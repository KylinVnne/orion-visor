/*
 * Copyright (c) 2023 - present Jiahang Li (visor.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orion.visor.module.asset.entity.request.exec;

import com.orion.visor.framework.desensitize.core.annotation.Desensitize;
import com.orion.visor.framework.desensitize.core.annotation.DesensitizeObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 执行模板 创建请求对象
 *
 * @author Jiahang Li
 * @version 1.0.1
 * @since 2024-3-7 18:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DesensitizeObject
@Schema(name = "ExecTemplateCreateRequest", description = "执行模板 创建请求对象")
public class ExecTemplateCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Size(max = 64)
    @Schema(description = "名称")
    private String name;

    @NotBlank
    @Desensitize(toEmpty = true)
    @Schema(description = "命令")
    private String command;

    @NotNull
    @Schema(description = "超时时间秒 0不超时")
    private Integer timeout;

    @NotNull
    @Schema(description = "是否使用脚本执行")
    private Integer scriptExec;

    @Schema(description = "参数定义")
    private String parameterSchema;

    @Schema(description = "模板主机")
    private List<Long> hostIdList;

}
