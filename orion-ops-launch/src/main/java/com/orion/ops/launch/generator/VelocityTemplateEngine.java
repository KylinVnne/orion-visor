/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orion.ops.launch.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.orion.lang.define.collect.MultiLinkedHashMap;
import com.orion.lang.utils.Enums;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.reflect.BeanMap;
import com.orion.lang.utils.reflect.Fields;
import com.orion.ops.framework.common.constant.Const;
import com.orion.ops.framework.common.constant.OrionOpsProConst;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 代码生成器 Velocity 引擎
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/20 10:33
 */
public class VelocityTemplateEngine extends AbstractTemplateEngine {

    private final Map<String, GenTable> tables;

    private VelocityEngine velocityEngine;

    public VelocityTemplateEngine(GenTable[] tables) {
        this.tables = Arrays.stream(tables)
                .collect(Collectors.toMap(GenTable::getTableName, Function.identity()));
    }

    {
        try {
            Class.forName("org.apache.velocity.util.DuckType");
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Velocity 1.x is outdated, please upgrade to 2.x or later.");
        }
    }

    @Override
    @NotNull
    public VelocityTemplateEngine init(@NotNull ConfigBuilder configBuilder) {
        if (velocityEngine == null) {
            Properties p = new Properties();
            p.setProperty(ConstVal.VM_LOAD_PATH_KEY, ConstVal.VM_LOAD_PATH_VALUE);
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, StringPool.EMPTY);
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            p.setProperty("file.resource.loader.unicode", StringPool.TRUE);
            this.velocityEngine = new VelocityEngine(p);
        }
        return this;
    }

    @Override
    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull File outputFile) throws Exception {
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             OutputStreamWriter ow = new OutputStreamWriter(fos, ConstVal.UTF8);
             BufferedWriter writer = new BufferedWriter(ow)) {
            template.merge(new VelocityContext(objectMap), writer);
        }
        LOGGER.debug("模板: " + templatePath + "; 文件: " + outputFile);
    }

    @Override
    @NotNull
    public String templateFilePath(@NotNull String filePath) {
        final String dotVm = ".vm";
        return filePath.endsWith(dotVm) ? filePath : filePath + dotVm;
    }

    @Override
    protected void outputCustomFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // 创建自定义文件副本文件
        customFiles = this.createCustomFilesBackup(customFiles, tableInfo);
        // 添加表元数据
        this.addTableMeta(tableInfo, objectMap);
        // 替换自定义包名
        this.replacePackageName(customFiles, tableInfo, objectMap);
        // 添加注释元数据
        this.addApiCommentMeta(tableInfo, objectMap);

        // 生成后端文件
        this.generatorServerFile(customFiles, tableInfo, objectMap);
        // 生成前端文件
        this.generatorVueFile(customFiles, tableInfo, objectMap);
    }

    /**
     * 创建自定义文件副本对象
     * <p>
     * - 根据类型进行移除不需要生成的模板
     *
     * @param originCustomerFile originCustomerFile
     * @param tableInfo          tableInfo
     * @return backup
     */
    private List<CustomFile> createCustomFilesBackup(@NotNull List<CustomFile> originCustomerFile,
                                                     @NotNull TableInfo tableInfo) {
        // 生成文件副本
        List<CustomFile> files = originCustomerFile.stream().map(s ->
                new CustomFile.Builder()
                        .enableFileOverride()
                        .templatePath(s.getTemplatePath())
                        .filePath(s.getFilePath())
                        .fileName(s.getFileName())
                        .packageName(s.getPackageName())
                        .build())
                .collect(Collectors.toList());
        // 获取 table
        GenTable table = tables.get(tableInfo.getName());
        // 不生成对外 api 文件
        if (!table.isGenProviderApi()) {
            files.removeIf(file -> this.isServerProviderFile(file.getTemplatePath()));
            // 不生成对外 api 单元测试文件
            if (table.isGenUnitTest()) {
                files.removeIf(file -> this.isServerProviderTestFile(file.getTemplatePath()));
            }
        }
        // 不生成单元测试文件
        if (!table.isGenUnitTest()) {
            files.removeIf(file -> this.isServerUnitTestFile(file.getTemplatePath()));
        }
        // 不生成 vue 文件
        if (!table.isGenVue()) {
            files.removeIf(file -> this.isVueFile(file.getTemplatePath()));
        }
        return files;
    }

    /**
     * 插入表元数据
     *
     * @param tableInfo tableInfo
     * @param objectMap objectMap
     */
    private void addTableMeta(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // http 注释标识
        objectMap.put("httpComment", "###");
        // 版本
        objectMap.put("since", OrionOpsProConst.VERSION);
        // 替换业务注释
        tableInfo.setComment(tables.get(tableInfo.getName()).getComment());
        // 实体名称
        String domainName = tableInfo.getEntityName();
        String mappingHyphen = objectMap.get("controllerMappingHyphen").toString();
        String entityName = domainName.substring(0, domainName.length() - 2);
        objectMap.put("type", entityName);
        objectMap.put("typeLower", Strings.firstLower(entityName));
        objectMap.put("typeHyphen", mappingHyphen.substring(0, mappingHyphen.length() - 3));
    }

    /**
     * 替换自定义文件包
     *
     * @param customFiles 自定义文件
     * @param tableInfo   tableInfo
     * @param objectMap   objectMap
     */
    private void replacePackageName(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // 替换包名
        customFiles.forEach(s -> {
            // 反射调用 setter 方法
            BiConsumer<String, Object> callSetter = (field, value) -> Fields.setFieldValue(s, field, value);
            String packageName = s.getPackageName();
            // 替换文件业务包名
            if (packageName.contains(Const.DOLLAR)) {
                Map<String, Object> meta = new HashMap<>(4);
                meta.put("bizPackage", tables.get(tableInfo.getName()).getBizPackage());
                // 调用 setter
                callSetter.accept("packageName", Strings.format(packageName, meta));
            }
        });

        // 包转换器
        Function<Predicate<String>, List<String>> packageConverter = conv ->
                customFiles.stream()
                        .filter(s -> conv.test(s.getTemplatePath()))
                        .map(CustomFile::getPackageName)
                        .map(s -> getConfigBuilder().getPackageConfig().getParent() + "." + s)
                        .distinct()
                        .collect(Collectors.toList());

        // 自定义 module 文件的包 (导入用)
        List<String> customModuleFilePackages = packageConverter.apply(s -> s.contains(".java.vm") && s.contains("orion-server-module"));
        objectMap.put("customModuleFilePackages", customModuleFilePackages);

        // 自定义 provider entity 文件的包 (导入用)
        List<String> customProviderEntityFilePackages = packageConverter.apply(s -> s.contains(".java.vm") && s.contains("orion-server-provider-entity"));
        objectMap.put("customProviderEntityFilePackages", customProviderEntityFilePackages);

        // 自定义 provider interface 文件的包 (导入用)
        List<String> customProviderFilePackages = packageConverter.apply(s -> s.contains(".java.vm") && s.contains("orion-server-provider"));
        objectMap.put("customProviderFilePackages", customProviderFilePackages);
    }

    /**
     * 插入 api 注释
     *
     * @param tableInfo tableInfo
     * @param objectMap objectMap
     */
    private void addApiCommentMeta(@NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        Map<String, String> map = new HashMap<>(12);
        objectMap.put("apiComment", map);
        String comment = tableInfo.getComment();
        map.put("create", "创建" + comment);
        map.put("updateById", "通过 id 更新" + comment);
        map.put("getById", "通过 id 查询" + comment);
        map.put("listByIdList", "通过 id 批量查询" + comment);
        map.put("listAll", "查询全部" + comment);
        map.put("queryCount", "查询" + comment + "数量");
        map.put("queryPage", "分页查询" + comment);
        map.put("deleteById", "通过 id 删除" + comment);
        map.put("batchDeleteByIdList", "通过 id 批量删除" + comment);
    }

    /**
     * 生成后端文件
     *
     * @param customFiles customFiles
     * @param tableInfo   tableInfo
     * @param objectMap   objectMap
     */
    private void generatorServerFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // 过滤文件
        customFiles = customFiles.stream()
                .filter(s -> this.isServerFile(s.getTemplatePath()))
                .collect(Collectors.toList());

        String parentPath = getPathInfo(OutputFile.parent);
        // 生成文件
        customFiles.forEach(file -> {
            // 获取 parent package
            String currentPackage = getConfigBuilder().getPackageConfig().getParent() + "." + file.getPackageName();
            // 设置当前包
            objectMap.put("currentPackage", currentPackage);

            // 文件路径
            String filePath = parentPath + File.separator + file.getPackageName()
                    .replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
            // 文件名称
            Map<String, Object> meta = new HashMap<>(4);
            meta.put("type", objectMap.get("type"));
            meta.put("tableName", tableInfo.getName());
            String fileName = filePath + File.separator + Strings.format(file.getFileName(), meta);
            // 渲染文件
            this.outputFile(Files1.newFile(fileName), objectMap, file.getTemplatePath(), file.isFileOverride());
        });
    }

    /**
     * 生成前端文件
     *
     * @param customFiles customFiles
     * @param tableInfo   tableInfo
     * @param objectMap   objectMap
     */
    private void generatorVueFile(@NotNull List<CustomFile> customFiles, @NotNull TableInfo tableInfo, @NotNull Map<String, Object> objectMap) {
        // 不生成 vue 文件
        if (!tables.get(tableInfo.getName()).isGenVue()) {
            return;
        }
        // 过滤文件
        customFiles = customFiles.stream()
                .filter(s -> this.isVueFile(s.getTemplatePath()))
                .collect(Collectors.toList());

        // 元数据
        String outPath = getConfigBuilder().getGlobalConfig().getOutputDir();
        GenTable table = tables.get(tableInfo.getName());
        BeanMap beanMap = BeanMap.create(table, "enums");
        // 模块名称首字母大写
        beanMap.put("moduleFirstUpper", Strings.firstUpper(table.getModule()));
        // 功能名称首字母大写
        beanMap.put("featureFirstUpper", Strings.firstUpper(table.getFeature()));
        // 功能名称全大写
        beanMap.put("featureAllUpper", table.getFeature().toUpperCase());
        // 枚举
        beanMap.put("enums", this.getEnumMap(table));
        objectMap.put("vue", beanMap);

        // 生成文件
        customFiles.forEach(file -> {
            // 文件路径
            String filePath = outPath
                    + "/" + Strings.format(file.getPackageName(), beanMap)
                    + "/" + Strings.format(file.getFileName(), beanMap);
            // 渲染文件
            this.outputFile(Files1.newFile(filePath), objectMap, file.getTemplatePath(), file.isFileOverride());
        });
    }

    /**
     * 是否为后端文件
     *
     * @param templatePath templatePath
     * @return 是否为后端文件
     */
    private boolean isServerFile(String templatePath) {
        return templatePath.contains("orion-server");
    }

    /**
     * 是否为后端 provider 文件
     *
     * @param templatePath templatePath
     * @return 是否为后端 provider 文件
     */
    private boolean isServerProviderFile(String templatePath) {
        return templatePath.contains("orion-server-provider");
    }

    /**
     * 是否为后端 provider 单元测试文件
     *
     * @param templatePath templatePath
     * @return 是否为后端 provider 单元测试文件
     */
    private boolean isServerProviderTestFile(String templatePath) {
        return templatePath.contains("orion-server-test-api");
    }

    /**
     * 是否为后端单元测试文件
     *
     * @param templatePath templatePath
     * @return 是否为后端单元测试文件
     */
    private boolean isServerUnitTestFile(String templatePath) {
        return templatePath.contains("orion-server-test");
    }

    /**
     * 是否为 vue 文件
     *
     * @param templatePath templatePath
     * @return 是否为 vue 文件
     */
    private boolean isVueFile(String templatePath) {
        return templatePath.contains("orion-vue-") ||
                templatePath.contains("orion-sql-menu.sql");
    }

    /**
     * 获取枚举 map
     *
     * @param table table
     * @return enums
     */
    private Object getEnumMap(GenTable table) {
        List<Class<? extends Enum<?>>> enums = table.getEnums();
        Map<String, MultiLinkedHashMap<String, String, Object>> enumMap = new LinkedHashMap<>();
        for (Class<? extends Enum<?>> e : enums) {
            MultiLinkedHashMap<String, String, Object> fieldValueMap = Enums.getFieldValueMap(e);
            enumMap.put(e.getSimpleName(), fieldValueMap);
        }
        return enumMap;
    }

}