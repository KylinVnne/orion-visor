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
package com.orion.visor.framework.websocket.configuration;

import com.orion.visor.framework.common.constant.AutoConfigureOrderConst;
import com.orion.visor.framework.websocket.configuration.config.WebSocketConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * websocket 配置类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/6/25 19:45
 */
@EnableWebSocket
@AutoConfiguration
@AutoConfigureOrder(AutoConfigureOrderConst.FRAMEWORK_WEBSOCKET)
@EnableConfigurationProperties(WebSocketConfig.class)
public class OrionWebSocketAutoConfiguration {

    /**
     * @return websocket 缓冲区大小配置
     */
    @Bean
    public ServletServerContainerFactoryBean servletServerContainerFactoryBean(WebSocketConfig config) {
        ServletServerContainerFactoryBean factory = new ServletServerContainerFactoryBean();
        factory.setMaxBinaryMessageBufferSize(config.getBinaryBufferSize());
        factory.setMaxTextMessageBufferSize(config.getBinaryBufferSize());
        factory.setMaxSessionIdleTimeout(config.getSessionIdleTimeout());
        return factory;
    }

}
