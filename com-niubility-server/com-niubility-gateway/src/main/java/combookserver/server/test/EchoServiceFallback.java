package combookserver.server.test;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EchoServiceFallback implements FallbackFactory<EchoService> {

    @Override
    public EchoService create(Throwable cause) {
        return new EchoService(){
            @Override
            public String hi() {
                return "sayHi fallback"+cause;
            }
        };
    }
}
