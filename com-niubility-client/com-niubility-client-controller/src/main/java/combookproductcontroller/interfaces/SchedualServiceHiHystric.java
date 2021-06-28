package combookproductcontroller.interfaces;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class SchedualServiceHiHystric implements SchedualServiceHi {
    @Override
    public String sayHiFromClientOne(String name) {
        log.info("sayHiFromClientOne error");
        return "sorry "+name;
    }

    @Override
    public Object linkBaseService(String name) {
        log.info("linkBaseService error");
        return "linkBaseService error";
    }
}
