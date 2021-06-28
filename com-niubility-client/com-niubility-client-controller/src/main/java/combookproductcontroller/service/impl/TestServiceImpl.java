package combookproductcontroller.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import combookproductcontroller.dao.TestDao;
import combookproductcontroller.entity.TestEntity;
import combookproductcontroller.service.TestService;
import combookproductcontroller.util.PageUtils;
import combookproductcontroller.util.Query;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("testService")
public class TestServiceImpl extends ServiceImpl<TestDao, TestEntity> implements TestService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TestEntity> page = this.page(
                new Query<TestEntity>().getPage(params),
                new QueryWrapper<TestEntity>()
        );
        return new PageUtils(page);
    }

}