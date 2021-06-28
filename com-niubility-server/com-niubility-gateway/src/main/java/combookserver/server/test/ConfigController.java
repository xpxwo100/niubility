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
        //String s = echoService.hi();
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        /*String endpoint = "https://oss-cn-beijing.aliyuncs.com";

// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
        String accessKeyId = "LTAI5tPfPjZ2Rx89f1ZEbVL2";
        String accessKeySecret = "rDWkSEC7iJGCWPnQaJolRgn7sz3CxI";
        String bucketName = "xpxwo100";
// <yourObjectName>从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        String objectName = "570_logo.png";


// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
// 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
        ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File("D:\\logs"));
// 关闭OSSClient。
        ossClient.shutdown();*/
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