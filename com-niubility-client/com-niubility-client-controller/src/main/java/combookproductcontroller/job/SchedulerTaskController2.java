package combookproductcontroller.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
@Slf4j
@Component
public class SchedulerTaskController2 {
    private static final SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
    //@Scheduled(fixedRate = 6000)
    //@Async
    public void currentTime(){
        log.info("中文:现在时间"+dateFormat.format(new Date()));
    }

    @Async
    public void currentTime2(){
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("中文222222222:现在时间"+dateFormat.format(new Date()));
    }

    @Async
    public void currentTime3(){
        log.info("中文33333333:现在时间"+dateFormat.format(new Date()));
    }

}
