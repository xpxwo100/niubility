package combookproductcontroller.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import java.io.IOException;
@Component
public class LogCostFilter implements Filter {

    @Autowired
    public RedisTemplate redisTemplate;
    @Override
    public void init(FilterConfig filterConfig){
        /*ServletContext servletContext = filterConfig.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        redisTemplate = ctx.getBean("redisTemplate", RedisTemplate.class);*/
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        chain.doFilter(request,response);
        System.out.println("Execute cost="+(System.currentTimeMillis()-start)+"rabbitTemplate="+redisTemplate);
    }

    @Override
    public void destroy() {

    }
}
