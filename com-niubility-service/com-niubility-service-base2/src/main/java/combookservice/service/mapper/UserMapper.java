package combookservice.service.mapper;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface UserMapper {
    public List<HashMap<String,Object>>  selectMapList(HashMap<String,Object> sql);

    public void insetLog(String s);
}
