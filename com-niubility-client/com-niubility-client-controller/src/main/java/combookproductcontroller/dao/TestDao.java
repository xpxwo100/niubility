package combookproductcontroller.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import combookproductcontroller.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author xpx
 * @email sunlightcs@gmail.com
 * @date 2021-06-10 14:04:21
 */
@Mapper
public interface TestDao extends BaseMapper<TestEntity> {
    public List<HashMap<String,Object>>  selectMapList(Map<String,Object> sql);

    void insertSysLog(Map<String, Object> mMap);
}
