package combookserver.server.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class SchedulerTaskController {
    private static final SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
    private int count=0;
    @Scheduled(cron="*/6 * * * * ?")
    @Async("threadPoolTaskExecutor")
    public void process(){
        log.info("英文:this is scheduler task runing "+(count++));
    }

    @Scheduled(fixedRate = 6000)
    @Async("threadPoolTaskExecutor")
    public void currentTime(){
        log.info("中文:现在时间"+dateFormat.format(new Date()));
    }

}
