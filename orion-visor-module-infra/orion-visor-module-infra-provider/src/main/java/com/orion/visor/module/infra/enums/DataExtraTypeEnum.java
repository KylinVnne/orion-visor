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
package com.orion.visor.module.infra.enums;

/**
 * 数据拓展类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/12/18 21:51
 */
public enum DataExtraTypeEnum {

    /**
     * 主机
     */
    HOST,

    ;

    public static DataExtraTypeEnum of(String type) {
        if (type == null) {
            return null;
        }
        for (DataExtraTypeEnum value : values()) {
            if (value.name().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
