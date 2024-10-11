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
package com.orion.visor.framework.common.enums;

import com.orion.lang.utils.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 端点定义
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/6/21 19:15
 */
@Getter
@AllArgsConstructor
public enum EndpointDefine {

    /**
     * 上传临时分区
     */
    UPLOAD_SWAP("/upload/swap/{}"),

    /**
     * 批量执行日志
     */
    EXEC_LOG("/exec/{}/{}.log"),

    ;

    /**
     * 端点
     */
    private final String endpoint;

    /**
     * 格式化
     *
     * @param params params
     * @return path
     */
    public String format(Object... params) {
        return Strings.format(this.endpoint, params);
    }

}
