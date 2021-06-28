package combookservice.service.demo;


import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SampleClass {
    @MyAop(value = "aaa")
    public String test(String s,String a){
        System.out.println("test:"+s);
        return s;
    }

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SampleClass.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println("before method run...");
                System.out.println( method.getName());
                //获取注解
                Annotation[] annotations = method.getDeclaredAnnotations();
                for(Annotation annotations1 : annotations ){
                    if(annotations1 instanceof  MyAop){
                        MyAop myAop = (MyAop)annotations1;
                        System.out.println( myAop.name()+myAop.value());
                    }
                }
                //用工具
                MyAop myAop2 = (MyAop)AnnotationUtils.findAnnotation(method,MyAop.class);
                myAop2.name();
                System.out.println( myAop2.name()+myAop2.value());
                //放方法参数类型和值
                Map<Integer, Map<Class, Object>> paramsMap = new HashMap<>();
                //Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                //Type[] parameterTypes = method.getGenericParameterTypes();
                Class[] classes = method.getParameterTypes();
                for(int i= 0; i< classes.length ; i++){
                    Class classes1 = classes[i];
                    for(int x= 0; x< args.length ; x++){
                        Object objects = args[x];
                        if(i == x){
                            Map<Class, Object> paramsMap2 = new HashMap<>();
                            paramsMap2.put(classes1,objects);
                            paramsMap.put(i,paramsMap2);
                        }
                    }
                }
                System.out.println("paramsMap:"+paramsMap);
                Set<Map.Entry<Integer, Map<Class, Object>>> a = paramsMap.entrySet();
                for(Map.Entry<Integer, Map<Class, Object>> aa : a){
                    Integer c = aa.getKey();
                    Map<Class, Object> o = aa.getValue();
                    Set<Map.Entry<Class, Object>> a2 = o.entrySet();
                    for(Map.Entry<Class, Object> aa2 : a2){
                        Class cc = aa2.getKey();
                        Object o2 = aa2.getValue();
                        if("java.lang.String".equals(cc.getName())){
                            String value =  o2.toString();
                            System.out.println("val:"+value);
                        }
                    }
                }
                Object result = proxy.invokeSuper(obj, args);
                System.out.println(args);
                System.out.println("after method run...");
                return result;
            }
        });
        SampleClass sample = (SampleClass) enhancer.create();
        String s  = sample.test("222","888");
        System.out.println(s);
    }


    public void test2(){
        ApplicationContext applicationContext ;
    }
}
