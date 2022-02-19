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
    /**
     * limit -1 无限制数据
     * 获取某个用户 附近 n个距离单位m 内的附近z个用户
     * 返回固定半径内最近一条 sortAscending 正序排序 limit 为1 就可以
     */
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
     *  根据经纬度求一定范围内的数据 由近到远排序
     *  double lon;   // 经度
     *  double lat;    // 纬度
     * @param key
     * @param radius
     * @param unit
     * @param limit -1 无限制数据
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
