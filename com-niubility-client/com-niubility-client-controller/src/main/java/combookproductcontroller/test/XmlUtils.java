package combookproductcontroller.test;

import boot.util.Usual;
import com.alibaba.fastjson.util.Base64;
import com.aspose.cells.PictureCollection;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import combookproductcontroller.dao.TestDao;
import combookproductcontroller.util.aspose.AsposeUtil;
import combookproductcontroller.util.aspose.PathUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.dom4j.DocumentException;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Map;


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
    public static void jdom() throws IOException, JDOMException {
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
            System.out.println("通过rollno获取属性值:" + child.getAttribute("Relation"));
            List<Attribute> attributes = child.getAttributes();
            //打印属性
            for (Attribute attr : attributes) {
                System.out.println(attr.getName() + ":" + attr.getValue());
            }
            List<Element> childrenList = child.getChildren();
            System.out.println("======获取子节点-start======");
            for (Element o : childrenList) {
                System.out.println("节点名:" + o.getName() + "---" + "节点值:" + o.getValue());
            }
            System.out.println("======获取子节点-end======");
        }
    }

    public static ResultVO sendPostRequest(String url, MultiValueMap<String, String> params) {
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

    public void setImgForPdf() throws Exception {
        AsposeUtil cellsUtil = new AsposeUtil(this.getClass().getResourceAsStream("/fileTem/printTemeplate.xlsx"));
        Workbook workBook = cellsUtil.getWorkBook();
        Worksheet sheet = workBook.getWorksheets().get(0);
        PictureCollection pictures = sheet.getPictures();

        ByteArrayOutputStream imageSignatureStream = new ByteArrayOutputStream();
        BufferedImage imageSignature = null;

        OutputStream out = null;
        byte[] b = null;
        try {
            String str = "";
            //Base64解码
            b = new Base64().decodeFast(str);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成图片
            PathUtil mPathUtil = new PathUtil();
            String mWebPath = mPathUtil.getWebRoot() + "signature.png"; //获取当前根目录
            out = new FileOutputStream(mWebPath);
            out.write(b);
            out.flush();
            out.close();
            File f = new File(mWebPath);
            imageSignature = ImageIO.read(f);
            /* 原始图像的宽度和高度 */
				/*	int width = imageSignature.getWidth();
					int height = imageSignature.getHeight();
					//压缩计算
					float resizeTimes = 0.4f;  这个参数是要转化成的倍数,如果是1就是转化成1倍
                    if(height > 700 || width > 700){
                    	 resizeTimes = 0.2f;
					}
					 调整后的图片的宽度和高度
					int toWidth = (int) (width * resizeTimes);
					int toHeight = (int) (height * resizeTimes);*/
            /* 新生成结果图片 */
					/*BufferedImage result = new BufferedImage(toWidth, toHeight,BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = result.createGraphics();
					result = g2d.getDeviceConfiguration().createCompatibleImage(toWidth,toHeight,Transparency.TRANSLUCENT);
					g2d.dispose();
	                g2d = result.createGraphics();
					Image image = imageSignature.getScaledInstance(toWidth, toHeight,
							Image.SCALE_AREA_AVERAGING);
					g2d.drawImage(image, 0, 0, null);
	                g2d.dispose(); */
            imageSignature = Thumbnails.of(f).size(200, 200).rotate(-90).asBufferedImage();
            //imageSignature = rotateImg(result, -90);
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            out.close();
        } finally {
            b = Usual.mEmptyBytes;
        }


        ImageIO.write(imageSignature, "png", imageSignatureStream);
        byte[] picDataFoot = imageSignatureStream.toByteArray();
        InputStream imageSignStream = new ByteArrayInputStream(picDataFoot);
        pictures.add(1, 1, imageSignStream);
        picDataFoot = Usual.mEmptyBytes;//清空byte数组
    }

    /**
     * 批量插入
     */
    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    public boolean insertCrossEvaluation(List<Object> members)
            throws Exception {
        // TODO Auto-generated method stub
        int result = 1;
        SqlSession batchSqlSession = null;
        try {
            batchSqlSession = sqlSessionTemplate
                    .getSqlSessionFactory()
                    .openSession(ExecutorType.BATCH, false);// 获取批量方式的sqlsession
            TestDao testDao = batchSqlSession.getMapper(TestDao.class);
            int batchCount = 1000;// 每批commit的个数
            int batchLastIndex = batchCount;// 每批最后一个的下标
            for (int index = 0; index < members.size(); ) {
                if (batchLastIndex >= members.size()) {
                    batchLastIndex = members.size();
                    result = result * batchSqlSession.insert("MutualEvaluationMapper.insertCrossEvaluation", members.subList(index, batchLastIndex));
                    batchSqlSession.commit();
                    batchSqlSession.clearCache();
                    System.out.println("index:" + index + " batchLastIndex:" + batchLastIndex);
                    break;// 数据插入完毕，退出循环
                } else {
                    result = result * batchSqlSession.insert("MutualEvaluationMapper.insertCrossEvaluation", members.subList(index, batchLastIndex));
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

    /**
     * 批量插入2
     *
     * @param list 要导入的数据集合
     */
    public void batch(SqlSession batchSqlSession, TestDao arResidualShoesCostsMapperBatch, List<Map<String, Object>> list) {
        int batchCount = 5000;// 每批commit的个数
        if (list != null) {
            int index = 0;
            for (int i = 0; i < list.size(); i++) {
                ++index;
                Map<String, Object> mMap = list.get(i);
                arResidualShoesCostsMapperBatch.insertToXD(mMap);
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

            String a = "64121,64413,64535,64620,64634,65028,65334,65489";
            String[] s = a.split(",");
            for (String id : s) {
                int idInt = Usual.f_getInteger(id);
            }
        }
    }
}
