package combookserver.server.test;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    //@Value("${name}")
    private String useLocalCache;
    //@Value("${age}")
    private String age;
    @Autowired
    EchoService echoService;
    @Autowired
    RabbitBd rabbitBd;

    /*   @SentinelResource(value = "get",
               blockHandler = "handleException",
               //blockHandlerClass = {ExceptionUtil.class},
               fallback = "helloFallback"
               //fallbackClass = {ExceptionUtil.class})
               )*/
    @RequestMapping("/get")
    public String get() {
        Test t = new Test();
        t.code = "sdaa";
        for (int d=0;d<100;d++){
            t.state = d;
            rabbitBd.sendMsg(t);
        }
        return "rabbitBd.sendMsg();" ;
    }


    public String handleException(BlockException ex) {
        // Do some log here.
//        ex.printStackTrace();
        System.out.println("被限流，无法访问接口");
        return ("被限流，无法访问接口");
    }

    /**
     * 方法参数列表需要和原函数一致，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
     * fallback 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
     *
     * @return
     */
    public String helloFallback() {
        System.out.println("熔断功能被开启");
        return ("熔断功能被开启");
    }

    @RequestMapping(value = "/linkBaseService",method = RequestMethod.GET)
    public Object linkBaseService(@RequestParam String name){
        //Object value = baseService.getConfig(name);
        //Object value = baseService.getData("base:"+name);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sessionId", name);
        return map;
    }
}