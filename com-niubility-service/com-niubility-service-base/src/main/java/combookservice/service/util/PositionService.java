package combookservice.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PositionService {
    @Autowired
    private RedisUtils redisUtil;
    /**
     * 获取用户位置
     */
    public List<MapPosition> getGeoLocation(String key,String id) {
        log.info("username:{} delete geoLocation.",id);
        List<Point> points = redisUtil.getGeoLocation(key,id);
        List<MapPosition> positions = new ArrayList<>();
        for (Point point : points) {
            if (Objects.nonNull(point)) {
                positions.add(new MapPosition(id,point.getX(),point.getY()));
            }
        }
        return positions;
    }

    public Long addGeoLocation(String key,String username, double x, double y) {
        return redisUtil.addGeoLocation(key,username, x, y);
    }

    public Long deleteGeoLocation(String key,String username) {
        log.info("username:{} delete geoLocation.",username);
        return redisUtil.deleteGeoLocation(key,username);
    }

    public Distance getDistance(String key,String usernameOne, String usernameTwo, RedisGeoCommands.DistanceUnit unit) {
        return redisUtil.getDistance(key,usernameOne, usernameTwo, unit);
    }

    public List<MapNearby> getRadius(String key,String username, int radius, RedisGeoCommands.DistanceUnit unit, int limit) {
        GeoResults<RedisGeoCommands.GeoLocation<Object>> result = redisUtil.getRadius(key,username, radius, unit, limit);
        List<MapNearby> userNearbyList = new ArrayList<>();
        result.getContent().forEach((one) -> {
            userNearbyList.add(
                    new MapNearby(one.getDistance()
                            , new MapPosition((String) one.getContent().getName(),one.getContent().getPoint().getX(),one.getContent().getPoint().getY()))
            );
        });
        return userNearbyList;
    }

    /**
     *   private double lon;   // 经度
     *     private double lat;    // 纬度
     * @param key
     * @param username
     * @param radius
     * @param unit
     * @param limit
     * @return
     */
    public List<MapNearby> getRadiusByPoint(String key,double lon, double lat, int radius, RedisGeoCommands.DistanceUnit unit, int limit) {
        GeoResults<RedisGeoCommands.GeoLocation<Object>> result = redisUtil.getRadiusByPoint(key,lon,lat, radius, unit, limit);
        List<MapNearby> userNearbyList = new ArrayList<>();
        result.getContent().forEach((one) -> {
            userNearbyList.add(
                    new MapNearby(one.getDistance()
                            , new MapPosition((String) one.getContent().getName(),one.getContent().getPoint().getX(),one.getContent().getPoint().getY()))
            );
        });
        return userNearbyList;
    }
}
