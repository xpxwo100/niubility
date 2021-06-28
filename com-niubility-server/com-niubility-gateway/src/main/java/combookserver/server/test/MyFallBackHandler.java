package combookserver.server.test;


import feign.hystrix.FallbackFactory;

public class MyFallBackHandler implements FallbackFactory {
    public static String getFallBack(String id,Throwable t){
        return id + ":=====>" + t.getMessage();
    }

    @Override
    public Object create(Throwable cause) {
        return null;
    }
}
