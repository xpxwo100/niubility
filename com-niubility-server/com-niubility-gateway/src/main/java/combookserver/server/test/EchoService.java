package combookserver.server.test;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "com-niubility-config-client",
        fallbackFactory = EchoServiceFallback.class)
public interface EchoService {
    @GetMapping(value = "/hi")
    String hi();
}
