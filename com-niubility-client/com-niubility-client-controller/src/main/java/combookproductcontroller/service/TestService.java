package combookproductcontroller.service;


import com.baomidou.mybatisplus.extension.service.IService;
import combookproductcontroller.entity.TestEntity;
import combookproductcontroller.util.PageUtils;

import java.util.Map;

/**
 * 
 *
 * @author xpx
 * @email sunlightcs@gmail.com
 * @date 2021-06-10 14:04:21
 */
public interface TestService extends IService<TestEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

