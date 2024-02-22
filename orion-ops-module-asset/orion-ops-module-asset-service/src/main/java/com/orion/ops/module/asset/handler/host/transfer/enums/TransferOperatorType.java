package com.orion.ops.module.asset.handler.host.transfer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 传输操作类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/2/21 22:03
 */
@Getter
@AllArgsConstructor
public enum TransferOperatorType {

    /**
     * 开始上传
     */
    UPLOAD_START("uploadStart"),

    /**
     * 上传完成
     */
    UPLOAD_FINISH("uploadFinish"),

    /**
     * 上传失败
     */
    UPLOAD_ERROR("uploadError"),

    ;

    private final String type;

    public static TransferOperatorType of(String type) {
        if (type == null) {
            return null;
        }
        for (TransferOperatorType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}