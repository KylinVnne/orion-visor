package com.orion.ops.module.infra.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.framework.mybatis.core.mapper.IMapper;
import com.orion.ops.module.infra.entity.domain.DataGroupDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据分组 Mapper 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023-11-7 18:44
 */
@Mapper
public interface DataGroupDAO extends IMapper<DataGroupDO> {

    /**
     * 查询最大排序
     *
     * @param parentId parentId
     * @param type     type
     * @return max(sort)
     */
    Integer selectMaxSort(@Param("parentId") Long parentId, @Param("type") String type);

    /**
     * 修改排序
     *
     * @param parentId  parentId
     * @param condition 条件
     * @param referSort 对比值
     * @param addition  自增步长
     * @return effect
     */
    Integer updateSort(@Param("parentId") Long parentId,
                       @Param("condition") String condition,
                       @Param("referSort") Integer referSort,
                       @Param("addition") Integer addition);

    /**
     * 通过 parentId 查询
     *
     * @param parentIdList parentIdList
     * @return rows
     */
    default List<DataGroupDO> selectByParentId(List<Long> parentIdList) {
        LambdaQueryWrapper<DataGroupDO> wrapper = this.lambda()
                .in(DataGroupDO::getParentId, parentIdList);
        return this.selectList(wrapper);
    }

    /**
     * 通过 userId 查询
     *
     * @param userId userId
     * @return rows
     */
    default List<DataGroupDO> selectByUserId(Long userId) {
        LambdaQueryWrapper<DataGroupDO> wrapper = this.lambda()
                .eq(DataGroupDO::getUserId, userId);
        return this.selectList(wrapper);
    }

}
