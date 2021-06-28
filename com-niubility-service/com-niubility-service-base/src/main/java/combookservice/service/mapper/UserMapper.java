package combookservice.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper extends BaseMapper {
    public List<HashMap<String,Object>>  selectMapList(HashMap<String,Object> sql);

    public void insetLog(String s);
    void selectForwardOnly(Map<String, Object> paramMap,ResultHandler<HashMap<String,Object>> handler);
}

