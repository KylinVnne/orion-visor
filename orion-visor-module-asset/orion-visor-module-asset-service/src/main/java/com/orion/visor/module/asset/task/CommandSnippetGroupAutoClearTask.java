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
package com.orion.visor.module.asset.task;

import com.orion.visor.framework.common.utils.LockerUtils;
import com.orion.visor.module.asset.service.CommandSnippetGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时清理未使用的命令片段分组
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/4/24 23:50
 */
@Slf4j
@Component
public class CommandSnippetGroupAutoClearTask {

    /**
     * 分布式锁名称
     */
    private static final String LOCK_KEY = "clear:csg:lock";

    @Resource
    private CommandSnippetGroupService commandSnippetGroupService;

    /**
     * 清理
     */
    @Scheduled(cron = "0 10 2 * * ?")
    public void clear() {
        log.info("CommandSnippetGroupAutoClearTask.clear start");
        // 获取锁并执行
        LockerUtils.tryLock(LOCK_KEY, commandSnippetGroupService::clearUnusedGroup);
        log.info("CommandSnippetGroupAutoClearTask.clear finish");
    }

}
