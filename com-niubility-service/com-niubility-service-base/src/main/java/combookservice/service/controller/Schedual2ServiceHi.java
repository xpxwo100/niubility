package combookservice.service.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "com-niubility-config-client")
public interface Schedual2ServiceHi {
    @RequestMapping(value = "/hi2",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);


    @RequestMapping(value = "/getConfig",method = RequestMethod.GET)
    Object getConfig(@RequestParam(value = "name") String name);
}
