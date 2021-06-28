package combookproductcontroller.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁 Redisson
 */
@Configuration
public class RedissonConfig {
   /* @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;
*/
    @Bean
    public RedissonClient getRedisson(){
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://" + host + ":" + port);
        config.useSentinelServers().setMasterName("mymaster")
                .addSentinelAddress("redis://127.0.0.1:26379", "redis://127.0.0.1:26380", "redis://127.0.0.1:26381");
        //添加主从配置 127.0.0.1:26379,127.0.0.1:26380,127.0.0.1:26381
        //config.useMasterSlaveServers().setMasterAddress("").setPassword("").addSlaveAddress(new String[]{"",""});

        return Redisson.create(config);
    }
}
