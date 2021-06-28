package combookservice.service.demo;

import lombok.Data;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
@Data
public class Demo {
    private String  code;
    private long    expTime;
    public void test(){
        System.out.println("hello world");
    }

    public static void main(String[] args) {
        Enhancer enhancer =  new Enhancer();
        enhancer.setSuperclass(Demo.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("before method run...");
                Object result = methodProxy.invokeSuper(o,objects);
                System.out.println("after method run...");
                return result;
            }
        });
        Demo demo = (Demo) enhancer.create();
        demo.test();
    }
}
