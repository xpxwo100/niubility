package combookproductcontroller.controller;

import com.google.common.collect.Lists;
import combookproductcontroller.dao.TestDao;
import combookproductcontroller.es.BookBean;
import combookproductcontroller.es.BookService;
import combookproductcontroller.es.ProductDao;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ElasticController {
    @Autowired
    private BookService bookService;
    @Autowired
    ProductDao productDao;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @RequestMapping("/book/{id}")
    @ResponseBody
    public List<BookBean> getBookById(@PathVariable String id) {
        Optional<BookBean> opt = bookService.findById(id);
        BookBean book = opt.get();
        System.out.println(book);
        //List<BookBean> a = bookService.findAll(0, 1);
        //相关度查询使用match，精确字段查询使用matchPhrase即可。
        MatchPhraseQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("title","ES入门教程167");
        Iterable<BookBean> bookBean = bookService.search(matchQueryBuilder);
        List<BookBean> bookBeans = Lists.newArrayList(bookBean);
        return  bookBeans;
    }

    @RequestMapping("/save")
    @ResponseBody
    public void Save() {
        for (int i = 0; i < 1000; i++) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            BookBean book = new BookBean(i+"", "ES入门教程" + i, "xpx" + UUID.randomUUID().toString(), df.format(new Date()));
            System.out.println(book);
            bookService.save(book);
        }
    }

    @Autowired
    public TestDao testDao;
    @Autowired
    public SqlSessionTemplate sqlSessionTemplate;
    @GetMapping("/in")
    public String insert() throws Exception {
        SqlSession batchSqlSession = sqlSessionTemplate
                .getSqlSessionFactory()
                .openSession(ExecutorType.BATCH, false);// 获取批量方式的sqlsession
        TestDao testDao = batchSqlSession.getMapper(TestDao.class);
        final  List<Map<String, Object>> list = new ArrayList<>();

        for(int i=0;i<100000;i++){
            Map<String, Object> objectObjectHashMap = new HashMap<>();
         /*   #{username,jdbcType=VARCHAR},
            #{operation,jdbcType=VARCHAR},
            #{method,jdbcType=VARCHAR},
            #{params,jdbcType=VARCHAR},
            #{time},
            #{ip,jdbcType=VARCHAR},*/
            objectObjectHashMap.put("username","aaaaa"+i);
            objectObjectHashMap.put("operation","保存菜单");
            objectObjectHashMap.put("method","update()");
            objectObjectHashMap.put("params","asdtfwgf6348578hbi");
            objectObjectHashMap.put("time", BigInteger.valueOf(i));
            objectObjectHashMap.put("ip","0:0:0:0:0:0:0:1");
            list.add(objectObjectHashMap);
        }
        long l = System.currentTimeMillis();
       // batch(batchSqlSession,testDao,list);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean b = insertCrossEvaluation(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean b = insertCrossEvaluation(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        long d = System.currentTimeMillis();
        return "sucess"+ (d-l);
    }

    /**
     * 批量插入2
     *@Transactional会对runtime异常进行自动回滚（异常没有被catch掉）
     * 如果try-catch，要想进行事务回滚，则throw抛出个异常，事务则会执行。否则事务将不会进行回滚
     * @Transactional(rollbackFor = Exception.class)，可以指定回滚异常，异常会对抛出指定异常时做事务回滚
     * @param list 要导入的数据集合
     */
    public void batch(SqlSession batchSqlSession, TestDao testDao, List<Map<String, Object>> list)  {
        int batchCount = 50000;// 每批commit的个数
        if (list != null) {
            int index = 0;
            for (int i = 0; i < list.size(); i++) {
                ++index;
                Map<String, Object> mMap = list.get(i);
                testDao.insertSysLog(mMap);
                if (index % batchCount == 0) {
                    try {
                        batchSqlSession.commit(true);
                        batchSqlSession.clearCache();
                    } catch (Exception e) {
                        batchSqlSession.rollback(true);
                        throw e;
                    }
                }
            }
            // 最后一次末尾提交
            if (index % batchCount != 0) {
                try {
                    batchSqlSession.commit(true);
                    batchSqlSession.clearCache();
                } catch (Exception e) {
                    batchSqlSession.rollback(true);
                    throw e; //抛出异常
                }
            }
            batchSqlSession.close();
        }
    }
    //set global max_allowed_packet = 100 * 1024 * 1024;
    public boolean insertCrossEvaluation(List<Map<String, Object>> members)
            throws Exception {
        // TODO Auto-generated method stub
        int result = 1;
        SqlSession batchSqlSession = null;
        try {
            batchSqlSession = sqlSessionTemplate
                    .getSqlSessionFactory()
                    .openSession(ExecutorType.BATCH, false);// 获取批量方式的sqlsession
            TestDao testDao = batchSqlSession.getMapper(TestDao.class);
            int batchCount = 50000;// 每批commit的个数
            int batchLastIndex = batchCount;// 每批最后一个的下标
            for (int index = 0; index < members.size(); ) {
                if (batchLastIndex >= members.size()) {
                    batchLastIndex = members.size();
                    //result = result * batchSqlSession.insert("TestDao.insertSysLogList", members.subList(index, batchLastIndex));

                    result = result *  testDao.insertSysLogList(members.subList(index, batchLastIndex));
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                    System.out.println("index:" + index + " batchLastIndex:" + batchLastIndex);
                    break;// 数据插入完毕，退出循环
                } else {
                   // result = result * batchSqlSession.insert("TestDao.insertSysLogList", members.subList(index, batchLastIndex));
                    result = result *  testDao.insertSysLogList(members.subList(index, batchLastIndex));
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                    System.out.println("index:" + index + " batchLastIndex:" + batchLastIndex);
                    index = batchLastIndex;// 设置下一批下标
                    batchLastIndex = index + (batchCount - 1);
                }
            }
            batchSqlSession.commit();
            batchSqlSession.clearCache();
        } finally {
            batchSqlSession.close();
        }
        return true;
    }
}