package combookproductcontroller.interfaces;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignFallbackHandler implements FallbackFactory<SchedualServiceHi> {
    @Override
    public SchedualServiceHi create(Throwable cause) {
        return new SchedualServiceHi() {
            @Override
            public String sayHiFromClientOne(String name) {
                System.out.println("异常触发:" + cause.getMessage());
                return "1001";
            }

            @Override
            public Object linkBaseService(String name) {
                System.out.println("异常触发:" + cause.getMessage());
                return 1001;
            }
        };
    }
}
