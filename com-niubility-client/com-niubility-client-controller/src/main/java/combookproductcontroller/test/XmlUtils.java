package combookproductcontroller.test;

import org.dom4j.DocumentException;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Singleton
public class XmlUtils {
   /* public static Document parseXmlString(String xmlStr){

        try{
            FileInputStream fileInputStream = new FileInputStream("D:\\test.xml");

            InputSource is = new InputSource(fileInputStream);

            //InputSource is = new InputSource(new StringReader(xmlStr));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,Object> getXmlBodyContext(String bodyXml){

        Map<String,Object> dataMap = new HashMap<String,Object>();

        Document doc = parseXmlString(bodyXml);
        if(null != doc){
            NodeList rootNode = doc.getElementsByTagName("Document");
            if(rootNode != null){

                Node root = rootNode.item(0);
                NodeList nodes = root.getChildNodes();
                for(int i = 0;i < nodes.getLength(); i++){
                    Node node = nodes.item(i);
                    dataMap.put(node.getNodeName(), node.getTextContent());
                }
            }
        }
        return dataMap;
    }*/



    public static void main(String[] args) throws IOException, DocumentException, JDOMException {

      /*  String xmlStr = "<xml><AppId></AppId><CreateTime>1413192605</CreateTime><InfoType></InfoType><ComponentVerifyTicket></ComponentVerifyTicket></xml>";
        Map<String, Object> map = XmlUtils.getXmlBodyContext(xmlStr);
        System.out.println(map);



        FileInputStream fileInputStream = new FileInputStream("D:\\test.xml");

        new SAXReader().read(fileInputStream);
*/
        //readXml("D:\\test.xml");
        //readXmlforXml("D:\\test.xml");
        jdom();
    }
    /**
     * 读取XML
     *
     * @param filename
     * @return
     */
 /*   public static List<Map> readXml(String filename){
        String xmlStr = "";
        InputSource is = new InputSource(new StringReader(xmlStr));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();


           // doc = builder.parse(new File(filename));

            doc = builder.parse(is);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NodeList links = doc.getElementsByTagName("Batch");
        List<Map> list = new ArrayList<Map>();
        for(int i = 0; i< links.getLength() ; i ++){
            Element node = (Element)links.item(i);
            Map map = new HashMap();
            map.put(node.getAttribute("batchNo"), node.getAttribute("lineName"));
            list.add(map);
        }
        NodeList links2 = doc.getElementsByTagName("Code");
        List<Map> list2 = new ArrayList<Map>();
        for(int i = 0; i< links2.getLength() ; i ++){
            Element node = (Element)links2.item(i);
            Map map = new HashMap();

            map.put("curCode", node.getAttribute("curCode"));
            map.put("packLayer", node.getAttribute("packLayer"));
            map.put("parentCode", node.getAttribute("parentCode"));
            list2.add(map);
        }


        System.out.println(list);
        System.out.println(list2);
        return list;
    }


    public static Map readXmlforString(String xmlStr){
        InputSource is = new InputSource(new StringReader(xmlStr));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,Object> dataMap = new HashMap<String,Object>();
        if(null != doc){
            NodeList rootNode = doc.getElementsByTagName("Batch");
            if(rootNode != null){

                Node root = rootNode.item(0);
                NodeList nodes = root.getChildNodes();
                for(int i = 0;i < nodes.getLength(); i++){
                    Node node = nodes.item(i);
                    dataMap.put(node.getNodeName(), node.getTextContent());
                }
            }
        }

        System.out.println(dataMap);
        return dataMap;
    }

    public static Map readXmlforXml(String url){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new File(url));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,Object> dataMap = new HashMap<String,Object>();
        if(null != doc){
            NodeList rootNode = doc.getElementsByTagName("Batch");
            if(rootNode != null){

                Node root = rootNode.item(0);
                NodeList nodes = root.getChildNodes();
                for(int i = 0;i < nodes.getLength(); i++){
                    Node node = nodes.item(i);

                    System.out.println(node.getNodeValue());
                    System.out.println(node.getTextContent());
                    System.out.println(node.getAttributes());
                    NamedNodeMap attributes = node.getAttributes();


                    dataMap.put(node.getNodeName(), node.getTextContent());
                }
            }
        }

        System.out.println(dataMap);
        return dataMap;
    }*/


    public static void  jdom() throws IOException, JDOMException {
        //1.创建SAXBuilder对象
        SAXBuilder saxBuilder = new SAXBuilder();
        //2.创建输入流
        InputStream is = new FileInputStream(new File("D:\\test.xml"));
        //3.将输入流加载到build中
        org.jdom2.Document document = saxBuilder.build(is);
        //4.获取根节点
        Element rootElement = document.getRootElement();
        //5.获取子节点
        List<Element> children = rootElement.getChildren();
        for (Element child : children) {
            System.out.println("通过rollno获取属性值:"+child.getAttribute("Relation"));
            List<Attribute> attributes = child.getAttributes();
            //打印属性
            for (Attribute attr : attributes) {
                System.out.println(attr.getName()+":"+attr.getValue());
            }
            List<Element> childrenList = child.getChildren();
            System.out.println("======获取子节点-start======");
            for (Element o : childrenList) {
                System.out.println("节点名:"+o.getName()+"---"+"节点值:"+o.getValue());
            }
            System.out.println("======获取子节点-end======");
        }
    }
    public static ResultVO sendPostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<ResultVO> response = client.exchange(url, method, requestEntity, ResultVO.class);

        return response.getBody();
    }

}
