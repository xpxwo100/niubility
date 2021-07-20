package combookproductcontroller.controller;

import com.google.common.collect.Lists;
import combookproductcontroller.es.BookBean;
import combookproductcontroller.es.BookService;
import combookproductcontroller.es.ProductDao;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

}