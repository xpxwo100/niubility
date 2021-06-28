package combookservice.service.startListener;

import boot.nettyRpcModel.IBootStart;
import boot.nettyServer.NettyRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


//@WebListener
@Component
public  class ApplicationStart implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    public NettyRpcServer nettyRpcSnerver;
    @Autowired
    public IBootStart bootStart;
    public static int a = 0;
    /*@Override
    public void destroy() throws Exception {
        NettyRpcServer.unload();
        try {
            HashSet<String> serverNode = new HashSet<String>();
            List<String> z1 = zookeeper.getChildren("/server");
            if(z1.contains(ip+":"+post)){
                //zookeeper.setData("/server/"+ip+":"+post,"122");
                //zookeeper.deleteNode("/server/"+ip+":"+post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    /*@Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

        //NettyRpcServer.startUp();
        System.out.println("ssss222");
    }*/

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
       /* ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
        if (!applicationContext.getDisplayName().equals("FeignContext-com-niubility-config-client")) {
            bootStart.doNettyStart(contextRefreshedEvent);
            bootStart.doBootStart(contextRefreshedEvent);
        }*/
        if(a > 0){
            bootStart.doRegisteredStart(contextRefreshedEvent);
            bootStart.doBootStart(contextRefreshedEvent);
        }
        a++;
    }
}
