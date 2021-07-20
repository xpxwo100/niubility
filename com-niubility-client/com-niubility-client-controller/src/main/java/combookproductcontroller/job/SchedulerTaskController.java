package combookproductcontroller.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Slf4j
@Component
public class SchedulerTaskController {
    @Autowired
    SchedulerTaskController2 SchedulerTaskController2;
    private static final SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
    private int count=0;
    @Scheduled(cron="*/6 * * * * ?")
    @Async
    public void process(){
        SchedulerTaskController2.currentTime2();
        SchedulerTaskController2.currentTime3();
        log.info("英文:this is scheduler task runing "+(count++));
    }


    @Async
    public void process2(){
        log.info("英文2222222222:this is scheduler task runing "+(count++));
    }
}
