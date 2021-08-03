package combookproductcontroller.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MongoTestC {
    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping(value="/test1")
    public void saveTest() throws Exception {
        /*MongoTest mgtest=new MongoTest();
        mgtest.setId(11);
        mgtest.setAge(33);
        mgtest.setName("ceshi");
        mtdao.saveTest(mgtest);*/
        readXmlforXml("D:\\扫码数据.xml");
    }
    public  void readXmlforXml(String url){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        org.w3c.dom.Document doc = null;
        String dbName = "test";
        String collName = "traceCode";
        MongoCollection<Document> coll = mongoTemplate.getCollection(collName);
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(url));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException | org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
        //Map<String,Object> dataMap = new HashMap<String,Object>();
        List<Map> list = new ArrayList<Map>();
        String batchNo = "";
        if(null != doc){
            NodeList links = doc.getElementsByTagName("Batch");
            for(int i = 0; i< links.getLength() ; i ++){
                Element node = (Element)links.item(i);
                Map map = new HashMap();
                batchNo = node.getAttribute("batchNo");
                map.put("batchNo", node.getAttribute("batchNo"));
                map.put("curCode", node.getAttribute("curCode"));
            }

            NodeList links2 = doc.getElementsByTagName("Code");
            List<Document> documents = new ArrayList<Document>();
            for(int i = 0; i< links2.getLength() ; i ++){
                Element node = (Element)links2.item(i);
              /*  Map map = new HashMap();
                map.put("curCode", node.getAttribute("curCode"));
                map.put("packLayer", node.getAttribute("packLayer"));
                map.put("parentCode", node.getAttribute("parentCode"));
                map.put("batchNo", batchNo);*/

               Document document = new Document("curCode", node.getAttribute("curCode")).
                        append("packLayer", node.getAttribute("packLayer")).
                        append("parentCode", node.getAttribute("parentCode")).
                        append("batchNo", batchNo);
                documents.add(document);
            }
            coll.insertMany(documents);

           // org.bson.Document curCode = coll.find(Filters.eq("curCode", "81892400108640417997")).first();
           /* while (cursor1.hasNext()) {
                  org.bson.Document _doc = (org.bson.Document) cursor1.next();
                  System.out.println(_doc.toString());
            }*/
        }
        System.out.println(list);
        System.out.println("list.size()="+list.size());
    }

    @GetMapping(value="/test2")
    public void saveTest2() throws Exception {
        /*MongoTest mgtest=new MongoTest();
        mgtest.setId(11);
        mgtest.setAge(33);
        mgtest.setName("ceshi");
        mtdao.saveTest(mgtest);*/
        //readXmlforXml("D:\\扫码数据.xml");
        String collName = "traceCode";
        MongoCollection<Document> coll = mongoTemplate.getCollection(collName);
        Document curCode = coll.find(Filters.eq("curCode", "81892400115565723240")).first();
        System.out.println(curCode.toJson());
        System.out.println(curCode.get("curCode"));
    }

}
