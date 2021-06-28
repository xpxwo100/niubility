package boot.nettyRpcModel;

import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by lzhijun on 2019/9/29.
 */
public interface IBootStart {
    public void doBootStart(ContextRefreshedEvent contextRefreshedEvent);

    public void doRegisteredStart(ContextRefreshedEvent contextRefreshedEvent);
}
