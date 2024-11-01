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
package org.dromara.visor.module.infra.service;

import java.util.List;

/**
 * 提示服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/10/8 17:53
 */
public interface TipsService {

    /**
     * 设置为已提示
     *
     * @param tippedKey tippedKey
     */
    void tipped(String tippedKey);

    /**
     * 获取所有已提示的 key
     *
     * @return keys
     */
    List<String> getTippedKeys();

}