package combookservice.service.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test implements Serializable {

    public int state;
    public String code;


    @org.junit.jupiter.api.Test
    public void todo() {
        System.out.println(2111);
        String endpoint = "https://oss-cn-beijing.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
       
        String bucketName = "xpxwo100";
// <yourObjectName>从OSS下载文件时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        String objectName = "570_logo.png";
// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, "accessKeyId", "accessKeySecret");
        try {
            System.currentTimeMillis();
// 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
// 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
            // ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File("D:\\logs\\570_logo.png"));
            // 创建PutObjectRequest对象。
// 填写Bucket名称、Object完整路径和本地文件的完整路径。Object完整路径中不能包含Bucket名称。
// 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, "许培贤 java.pdf", new File("D:\\新建文件夹\\许培贤 java.pdf"));
// 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
// ObjectMetadata metadata = new ObjectMetadata();
// metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
// metadata.setObjectAcl(CannedAccessControlList.Private);
// putObjectRequest.setMetadata(metadata);

// 上传文件。
            ossClient.putObject(putObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
// 关闭OSSClient。
            ossClient.shutdown();
        }

    }

    @org.junit.jupiter.api.Test
    public void show() throws InterruptedException, ExecutionException {

      /*  FutureTask<Integer> futureTask = new FutureTask(new Callable() {
            @Override
            public Integer call() throws Exception {
                //Thread.sleep(10000);
                int a1 = 18;
                List a = new ArrayList<>();
                *//*while (true){
                    Thread.sleep(1);
                    a.add(10);
                    a.add(11);
                    a.add(12);
                    a.add(13);
                    a.add(14);

                }*//*
                a.add(11);
                return 1;
            }
        });*/
        /*new Thread(futureTask).start();
        System.out.println(futureTask.get());
        System.out.println("等待结果");*/
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("ObjectDto");
            return 2;
        }, executorService);
        Integer integer = integerCompletableFuture.get();
        System.out.println("等待结果"+integer);
        HashMap a = null;

    }

    public void setState(int state) {
        this.state = state;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Test{" +
                "state=" + state +
                ", code='" + code + '\'' +
                '}';
    }
}
