package combookproductcontroller.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Autowired
    public LogCostFilter logCostFilter;
    @Bean
    public FilterRegistrationBean registFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(logCostFilter);
        registration.addUrlPatterns("/login");
        registration.setName("LogCostFilter");
        registration.setOrder(1);
        return registration;
    }
}
