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
package org.dromara.visor.framework.web.core.filter;

import org.dromara.visor.framework.common.meta.TraceIdHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * traceId 过滤器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/6/16 17:45
 */
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 获取 traceId
            String traceId = TraceIdHolder.createTraceId();
            // 设置 traceId 上下文
            TraceIdHolder.set(traceId);
            // 设置响应头
            response.setHeader(TraceIdHolder.TRACE_ID_HEADER, traceId);
            // 执行请求
            filterChain.doFilter(request, response);
        } finally {
            // 清空 traceId 上下文
            TraceIdHolder.remove();
        }
    }

}