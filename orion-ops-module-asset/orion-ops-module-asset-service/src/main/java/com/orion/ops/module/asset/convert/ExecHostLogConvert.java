package com.orion.ops.module.asset.convert;

import com.orion.ops.module.asset.entity.domain.ExecHostLogDO;
import com.orion.ops.module.asset.entity.request.exec.ExecHostLogQueryRequest;
import com.orion.ops.module.asset.entity.vo.ExecHostLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 批量执行主机日志 内部对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.1
 * @since 2024-3-11 14:05
 */
@Mapper
public interface ExecHostLogConvert {

    ExecHostLogConvert MAPPER = Mappers.getMapper(ExecHostLogConvert.class);

    ExecHostLogDO to(ExecHostLogQueryRequest request);

    ExecHostLogVO to(ExecHostLogDO domain);

    List<ExecHostLogVO> to(List<ExecHostLogDO> list);

}
