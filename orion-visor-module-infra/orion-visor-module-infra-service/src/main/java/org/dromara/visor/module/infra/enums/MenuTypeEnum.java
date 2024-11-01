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
package org.dromara.visor.module.infra.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/7/18 10:01
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum {

    /**
     * 父菜单
     */
    PARENT_MENU(1),

    /**
     * 子菜单
     */
    SUB_MENU(2),

    /**
     * 功能
     */
    FUNCTION(3),

    ;

    private final Integer type;

    public static MenuTypeEnum of(Integer type) {
        if (type == null) {
            return null;
        }
        for (MenuTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}