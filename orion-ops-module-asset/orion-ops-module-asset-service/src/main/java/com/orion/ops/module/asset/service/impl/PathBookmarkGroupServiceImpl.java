package com.orion.ops.module.asset.service.impl;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Booleans;
import com.orion.ops.framework.common.constant.Const;
import com.orion.ops.framework.common.constant.ErrorMessage;
import com.orion.ops.framework.common.utils.Valid;
import com.orion.ops.framework.security.core.utils.SecurityUtils;
import com.orion.ops.module.asset.convert.PathBookmarkGroupConvert;
import com.orion.ops.module.asset.entity.request.path.PathBookmarkGroupCreateRequest;
import com.orion.ops.module.asset.entity.request.path.PathBookmarkGroupDeleteRequest;
import com.orion.ops.module.asset.entity.request.path.PathBookmarkGroupUpdateRequest;
import com.orion.ops.module.asset.entity.vo.PathBookmarkGroupVO;
import com.orion.ops.module.asset.service.PathBookmarkGroupService;
import com.orion.ops.module.asset.service.PathBookmarkService;
import com.orion.ops.module.infra.api.DataGroupApi;
import com.orion.ops.module.infra.api.DataGroupUserApi;
import com.orion.ops.module.infra.entity.dto.data.DataGroupCreateDTO;
import com.orion.ops.module.infra.entity.dto.data.DataGroupDTO;
import com.orion.ops.module.infra.entity.dto.data.DataGroupRenameDTO;
import com.orion.ops.module.infra.enums.DataGroupTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 路径标签分组 服务实现类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024-1-24 12:28
 */
@Slf4j
@Service
public class PathBookmarkGroupServiceImpl implements PathBookmarkGroupService {

    @Resource
    private DataGroupApi dataGroupApi;

    @Resource
    private DataGroupUserApi dataGroupUserApi;

    @Resource
    private PathBookmarkService pathBookmarkService;

    @Override
    public Long createPathBookmarkGroup(PathBookmarkGroupCreateRequest request) {
        Long userId = SecurityUtils.getLoginUserId();
        log.info("PathBookmarkGroupService-createPathBookmarkGroup request: {}", JSON.toJSONString(request));
        // 创建
        DataGroupCreateDTO create = PathBookmarkGroupConvert.MAPPER.to(request);
        create.setParentId(Const.ROOT_PARENT_ID);
        return dataGroupUserApi.createDataGroup(DataGroupTypeEnum.PATH_BOOKMARK, userId, create);
    }

    @Override
    public Integer updatePathBookmarkGroupById(PathBookmarkGroupUpdateRequest request) {
        Long id = Valid.notNull(request.getId(), ErrorMessage.ID_MISSING);
        log.info("PathBookmarkGroupService-updatePathBookmarkGroupById id: {}, request: {}", id, JSON.toJSONString(request));
        // 重命名
        DataGroupRenameDTO rename = PathBookmarkGroupConvert.MAPPER.to(request);
        return dataGroupApi.renameDataGroup(rename);
    }

    @Override
    public List<PathBookmarkGroupVO> getPathBookmarkGroupList() {
        Long userId = SecurityUtils.getLoginUserId();
        // 查询分组
        return dataGroupUserApi.getDataGroupList(DataGroupTypeEnum.PATH_BOOKMARK, userId)
                .stream()
                .sorted(Comparator.comparing(DataGroupDTO::getSort))
                .map(PathBookmarkGroupConvert.MAPPER::to)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deletePathBookmarkGroup(PathBookmarkGroupDeleteRequest request) {
        Long userId = SecurityUtils.getLoginUserId();
        Long id = request.getId();
        log.info("PathBookmarkGroupService-deletePathBookmarkGroupById id: {}", id);
        // 删除分组
        Integer effect = dataGroupApi.deleteDataGroupById(id);
        if (Booleans.isTrue(request.getDeleteItem())) {
            // 删除组内数据
            effect += pathBookmarkService.deleteByGroupId(userId, id);
        } else {
            // 移动到根节点
            effect += pathBookmarkService.setGroupNull(userId, id);
        }
        return effect;
    }

}
