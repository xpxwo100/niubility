package combookserver.server.test;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public class CustomerBlockHandler {
    public static String handleException(BlockException exe){
        return ("被限流，无法访问接口");
    }
}
