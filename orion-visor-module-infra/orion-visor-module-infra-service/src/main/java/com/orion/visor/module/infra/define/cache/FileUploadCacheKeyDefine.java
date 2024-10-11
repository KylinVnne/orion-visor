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
package com.orion.visor.module.infra.define.cache;

import com.orion.lang.define.cache.key.CacheKeyBuilder;
import com.orion.lang.define.cache.key.CacheKeyDefine;
import com.orion.lang.define.cache.key.struct.RedisCacheStruct;
import com.orion.visor.module.infra.entity.dto.FileUploadTokenDTO;

import java.util.concurrent.TimeUnit;

/**
 * 文明上传缓存 key
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/8/31 11:48
 */
public interface FileUploadCacheKeyDefine {

    CacheKeyDefine FILE_UPLOAD = new CacheKeyBuilder()
            .key("file:upload:{}")
            .desc("文件上传信息 ${token}")
            .type(FileUploadTokenDTO.class)
            .struct(RedisCacheStruct.STRING)
            .timeout(3, TimeUnit.MINUTES)
            .build();

}
