package combookserver.server;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.List;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableRabbit
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
        new Thread(new Runnable() {
            @Override
            public void run() {
                test();
            }
        }).start();
    }
    public static void test() {
        String serverAddr = "localhost:8848";
        String serviceName = "com-niubility-client-controller";
        try {
            NamingService namingService = NacosFactory.createNamingService(serverAddr);

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
