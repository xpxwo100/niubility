package combookservice.service.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Distance;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapNearby implements Serializable {
    private Distance distance;
    private MapPosition position;
}
