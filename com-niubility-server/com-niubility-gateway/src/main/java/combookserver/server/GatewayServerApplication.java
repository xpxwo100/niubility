package combookserver.server;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableRabbit
public class GatewayServerApplication {

    //test
    //GatewayServerApplication
    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);

    }

}
