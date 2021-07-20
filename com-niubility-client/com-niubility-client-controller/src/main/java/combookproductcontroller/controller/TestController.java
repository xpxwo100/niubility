package combookproductcontroller.controller;

import boot.nettyClient.NettyRpcUtil;
import boot.nettyRpcModel.ObjectDto;
import boot.util.R;
import combookproductcontroller.dao.TestDao;
import combookproductcontroller.entity.TestEntity;
import combookproductcontroller.interfaces.SchedualServiceHi;
import combookproductcontroller.service.TestService;
import combookproductcontroller.util.PageUtils;
import combookproductcontroller.util.RedisUtils;
import combookproductcontroller.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author xpx
 * @email sunlightcs@gmail.com
 * @date 2021-06-10 14:04:21
 */
@Slf4j
@RestController
@RequestMapping("product/category")
@RefreshScope
public class TestController {
    @Autowired
    private TestService testService;
    @Autowired
    private TestDao testDao;
    @Autowired
    private RedisUtils redisUtils;
  /*  @Value("${fuck}")
    String fuck;*/
    @Autowired
    SchedualServiceHi schedualServiceHi;
    @Autowired
    RedissonUtil redissonUtil;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("com-niubility-client:test:list")
    public R list(@RequestParam Map<String, Object> params) {
       /* PageUtils page = testService.queryPage(params);
        return R.ok().put("page", page);*/

        RLock lock = redissonUtil.getRLock("com-niubility-client:test:list");
        try {
            lock.lock(10, TimeUnit.SECONDS);
            Object linkBaseService = schedualServiceHi.linkBaseService("com-niubility-client:test:list");
            log.info("linkBaseService=" + linkBaseService.toString());
            //Thread.sleep(3000);
        } catch (Exception ex) {
            log.error("下单失败", ex);
        } finally {
            lock.unlock();
        }

        int limit = 10;
        int page = 1;
        if (params != null && params.size() > 0) {
            limit = Integer.parseInt((String) params.get("limit"));
            page = Integer.parseInt((String) params.get("page"));
        }
        Object daad = redisUtils.get("product/category/list/" + limit);
        if (redisUtils.get("product/category/list/" + limit) != null) {
            PageUtils pages = (PageUtils) redisUtils.get("product/category/list/" + limit);
            log.info("来自缓存");
            return R.ok().put("page", pages);
        }
        params.put("page", page);
        params.put("limit", limit);
        List<HashMap<String, Object>> list = testDao.selectMapList(params);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("page", page);
        map2.put("limit", limit);
        ObjectDto objectDto = NettyRpcUtil.load(true, "com-niubility-service-base", "baseService", "testA", map2, "fuck");
        List<String> list2 = list.stream().map(s -> (String) s.get("name") + s.get("code")).collect(Collectors.toList());

        list.forEach(e -> {
            Object sad = objectDto!=null?objectDto.getObj():"objectDto id null";
            e.put("code", (String) e.get("code") + sad + LocalDateTime.now());
        });
        log.info(list2.toString());
        PageUtils pages = new PageUtils(list, list.size(), 10, 0);
        redisUtils.set("product/category/list/" + limit, pages, (long) (600 + new Random().nextInt(100)));
        return R.ok().put("page", pages);
    }

    @Test
    public void dadad() {
        long dsad = (long) (600 + new Random().nextInt(100));
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("aa", 1);
        map2.put("aa1", 11);
        map2.put("aa2", 12);
        map2.put("aa3", 13);
        map2.forEach((a, b) -> {
            System.out.println(a);
            System.out.println(b);
        });


    }

    @RequestMapping("/test")
    public R test(@RequestBody HashMap<String, Object> paramMap) {
        return R.ok().put("page", paramMap);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("com-niubility-client:test:info")
    public R info(@PathVariable("id") Integer id) {
        TestEntity test = testService.getById(id);

        return R.ok().put("test", test);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("com-niubility-client:test:save")
    public R save(@RequestBody TestEntity test) {
        testService.save(test);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("com-niubility-client:test:update")
    public R update(@RequestBody TestEntity test) {
        testService.updateById(test);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("com-niubility-client:test:delete")
    public R delete(@RequestBody Integer[] ids) {
        testService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
