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


   /* protected HashMap<String,Object> printSpecialHandle(AsposeUtil cellsUtil, Cells cells, List<HashMap<String, Object>> detailList, Integer minPrintRow, int minPrintRowIndex, int paginationRowIndex, int paginationColumnIndex, boolean printOtherInfo)throws Exception {
        HashMap<String,Object> value = new HashMap<>();
        Workbook workbook = cellsUtil.getWorkBook();
        List<List<HashMap<String,Object>>> rowLists = new ArrayList<>();//有分页的每页List

        Worksheet w = workbook.getWorksheets().get(0);
        PageSetup pageSetup = w.getPageSetup();
        double topMargin = pageSetup.getTopMargin();//上页边距
        double bottomMargin = pageSetup.getBottomMargin();//下页边距
        double zoom = pageSetup.getZoom();//页面缩放比例
        //复制页签1的内容到页签2
        int totalPage = 0 ;//总共几页
        boolean isSurplus = false;//是否有余数，不够整数页
        int size = detailList.size();//子表明细数量
        int rowIndex = minPrintRowIndex;//记录模板明细行的行号
        int maxDataRow = cells.getMaxDataRow();
        double divisor = 0.353357*(zoom/100);
        double allHeight = 0.0;
        double a4Height = 297-((topMargin+bottomMargin)*10);//264
        //1磅等于0.353357mm     A4 (210mm X 297mm)
        for(int r=0;r<cells.getMaxDataRow()+1;r++){
            allHeight += cells.getRowHeight(r); //181
        }
        double rowIndexHeight = cells.getRowHeight(rowIndex);
        double rowHeightMM = rowIndexHeight*divisor;//明细行高度mm
        double allHeightMM = allHeight*divisor;//111.2

        if(size % minPrintRow > 0){//有余数，+1页
            totalPage = (size/minPrintRow) + 1;
            isSurplus = true;
        }else{
            totalPage = size/minPrintRow;
        }
        //只有一页的不需要分页
        if(totalPage <= 1){
            setPageAndFont(totalPage, cells, paginationRowIndex, paginationColumnIndex,null,-1);
            value.put("intervalList", Lists.newArrayList());
            return value;
        }

        //根据页数从页签2复制表头表体模板到页签1
        List<Integer> intervalList = Lists.newArrayList();
        if(totalPage>1){
            double allDataHeightMM = (allHeightMM-rowHeightMM)+(minPrintRow*rowHeightMM);
            double nullMM = a4Height-allDataHeightMM;//138.162587

            int a = (int)Math.round(nullMM/(13.5*divisor));//28
            for(int k=0;k<totalPage;k++){
                //把数据根据页数进行分割
                List<HashMap<String, Object>> mainList = null;
                if(isSurplus && (k+1) == totalPage){
                    mainList = detailList.subList(k*minPrintRow, k*minPrintRow + (size % minPrintRow));
                }else{
                    mainList = detailList.subList(k*minPrintRow, (k+1)*minPrintRow);
                }
                rowLists.add(mainList);
            }
            for(int k=0;k<totalPage-1;k++){
                int interval=maxDataRow+2+a;//16
                intervalList.add(interval*(k+1));//[16, 32]
            }
            setPageAndFont(totalPage, cells, paginationRowIndex, paginationColumnIndex,null,-1);
            for(int k=0;k<intervalList.size();k++){
                cells.copyRows(cells, 0, intervalList.get(k), maxDataRow+1);

                setPageAndFont(totalPage, cells, paginationRowIndex, paginationColumnIndex,intervalList,k);
                //设置每页模板样式
                for(int r=0;r<cells.getMaxColumn()+1;r++){
                    int templateRow = 0;
                    templateRow = intervalList.get(k) + rowIndex;
                    com.aspose.cells.Cell templateCellRow = cells.get(templateRow,r);
                    if(templateCellRow.getValue()!=null){
                        String cellStr = templateCellRow.getValue().toString().replace("&=[mainMap]", "&=[mainMap"+(k+1)+"]");
                        templateCellRow.setValue(cellStr);
                    }
                }
            }
            //填充数据
            //是否打印图片
            if(!printOtherInfo){
                if(rowLists.size()>0){
                    for(int i=0;i<rowLists.size();i++){
                        HashMap<String,Object> maps = new HashMap<>();
                        if(i==0){
                            maps.put("mainMap", rowLists.get(i));
                        }else{
                            maps.put("mainMap"+i, rowLists.get(i));
                        }
                        cellsUtil.setDataSource(maps);
                    }
                }
            }
        }
        value.put("intervalList", intervalList);
        value.put("rowLists",rowLists);
        return value;
    }*/
}
