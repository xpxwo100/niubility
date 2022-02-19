package combookservice.service.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapPosition implements Serializable {
    private String id;    // 用户名
    private double lon;   // 经度
    private double lat;    // 纬度
}
