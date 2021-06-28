package combookserver.server.test;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class MyRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization","lixue:123456");
        System.out.println("MyRequestInterceptor feign 拦截器");
    }
}
