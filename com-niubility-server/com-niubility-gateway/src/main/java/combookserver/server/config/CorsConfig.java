/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package combookserver.server.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 跨域设置
 */
@Configuration
public class CorsConfig {
    @Autowired
    public NacosDiscoveryProperties nacosDiscoveryProperties;
    @Bean
    public  CorsWebFilter  CorsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }


    @PostConstruct
    public  void test() {
        if (nacosDiscoveryProperties == null) {
            return;
        }
        if (StrUtil.isNotBlank(nacosDiscoveryProperties.getServerAddr())) {
            String serverAddr1 = nacosDiscoveryProperties.getServerAddr();
            String serviceName = "com-niubility-client-controller";
            try {
                NamingService namingService = NacosFactory.createNamingService(serverAddr1);

                // 注册监听器
                namingService.subscribe(serviceName, (event) ->
                {
                    if (event instanceof NamingEvent) {
                        List<Instance> list = ((NamingEvent) event).getInstances();
                        for (Instance temp : list) {
                            System.out.println("========" + temp);
                        }
                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}