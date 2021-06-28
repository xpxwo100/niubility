package boot.nettyClient;

import boot.nettyRpcModel.ObjectDto;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CallBackAsyn {
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private ObjectDto result;
    public void setResult(ObjectDto result) {
        try {
            lock.lock();
            this.result = result;
            //唤醒等待客户端RPC线程
            condition.signal();
            System.out.println("result:signal"+result);
        } finally {
            lock.unlock();
        }
    }

    public ObjectDto getResult(){
        try {
            lock.lock();
            //Netty服务端链路没有建立完毕之前，先挂起等待
            if (result == null) {
                condition.await(40*1000, TimeUnit.MILLISECONDS);
            }
            System.out.println("result:"+result);
            return this.result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            return this.result;
        }
    }
}
