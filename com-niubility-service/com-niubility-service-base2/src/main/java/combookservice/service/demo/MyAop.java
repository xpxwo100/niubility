package combookservice.service.demo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MyAop {
    String name() default "this is an annotation";

    String value() default "";

}
