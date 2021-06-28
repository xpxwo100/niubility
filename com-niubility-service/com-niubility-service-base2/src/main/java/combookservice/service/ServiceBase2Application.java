package combookservice.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement//开启事务
//@ServletComponentScan
@MapperScan("combookservice.service.mapper")
public class ServiceBase2Application {

    public static void main(String[] args) {
        SpringApplication.run(ServiceBase2Application.class, args);
       /* ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationfile.xml");
        BaseService BaseService = context.getBean(BaseService.class);
         System.out.println(BaseService);*/
    }

}
