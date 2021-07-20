package combookproductcontroller.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class TaskScheduleConfig {
    private static final int corePoolSize = 10;       		// 默认线程数
    private static final int maxPoolSize = 100;			    // 最大线程数
    private static final int keepAliveTime = 10;			// 允许线程空闲时间（单位：默认为秒）,十秒后就把线程关闭
    private static final int queueCapacity = 200;			// 缓冲队列数
    private static final String threadNamePrefix = "it-is-threaddemo-"; // 线程池名前缀

    @Bean("threadPoolTaskExecutor") // bean的名称，默认为首字母小写的方法名
    public ThreadPoolTaskExecutor getDemoThread(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(keepAliveTime);
        executor.setKeepAliveSeconds(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);

        //线程池拒绝任务的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化
        executor.initialize();

        return executor;
    }
}
