package combookproductcontroller.util.aspose;

import boot.util.Usual;
import com.aspose.cells.PageSetup;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorkbookDesigner;
import com.aspose.cells.Worksheet;
import com.aspose.words.FontSettings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;

/**
 * Excel导出帮助工具
 * @author lcs
 *
 */
public class AsposeUtil
{
	private static final Log logger = LogFactory.getLog(AsposeUtil.class);
	
	private static com.aspose.cells.Licenses cellLic = null;
//	private static com.aspose.words.Licenses wordLic = null;
	private static com.aspose.words.License wordLic = null;
	private static com.aspose.pdf.Licenses pdfLic = null;
	private static com.aspose.slides.Licenses slidesLic = null;
	
	
	private Workbook workBook = null;
	private WorkbookDesigner designer = null;
	
	public  void LicAspose()
	{
		InputStream iStream =null;
		try
		{
			String trueTypeFontsPath="/fonts/";
			// aspose注册可使用Licenses
			if (cellLic == null)
			{
			    cellLic = new com.aspose.cells.Licenses();
			    com.aspose.cells.CellsHelper.setFontDir(trueTypeFontsPath);
			}
			if (pdfLic == null)
			{
				pdfLic = new com.aspose.pdf.Licenses();
				com.aspose.pdf.Document.setLocalFontPath(trueTypeFontsPath);
			}
			if (slidesLic == null)	//ppt
			{
				slidesLic = new com.aspose.slides.Licenses();
				com.aspose.slides.FontsLoader.loadExternalFonts(new String[]{trueTypeFontsPath});
			}
//			//Resin无法通过@Autowired反射
//			if (wordLic == null)
//			{
//				wordLic = new com.aspose.words.Licenses();
//			}
			if(wordLic == null)
			{
				iStream =this.getClass().getResourceAsStream("/lic/aspose.word.xml");
				wordLic = new com.aspose.words.License();
				wordLic.setLicense(iStream);
				iStream.close();
				//处理Aspose.word直接导出pdf中文乱码问题
//				FontSettings.setDefaultFontName("");
				FontSettings.setFontsFolder(trueTypeFontsPath, false);
			}

		}
		catch (Exception exp)
		{
			logger.info("Aspose注册失败",exp);
		}
		finally
		{
			if(iStream!=null)
			{
				iStream=null;
			}
		}
	}

	public AsposeUtil(InputStream resourceAsStream)
	{
		LicAspose();
		try {
			workBook = new Workbook(resourceAsStream);
		} catch (Exception e) {
			logger.info("创建AsopseCellsUtil异常", e);
		}
	}
	public AsposeUtil(String filePath)
	{
		LicAspose();
		try {
			workBook = new Workbook(filePath);
		} catch (Exception e) {
			logger.info("创建AsopseCellsUtil异常", e);
		}
	}

	/**
	 * 设置数据源
	 * dataMap中可以存储Bean,HashMap,List<Bean>,List<HashMap>
	 * @param dataMap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void setDataSource(Map<String,Object> dataMap) {
		if(dataMap==null){
			return;
		}
		try {
			if (designer == null) {
				designer = new WorkbookDesigner();
				designer.setWorkbook(workBook);
			}
			Set<String> keySet = dataMap.keySet();
			for (String key : keySet) {
				Object mData=dataMap.get(key);
				// 其他表体数据,循环显示
				if (mData instanceof List) {
					List<Object> mList = (List<Object>)mData;
					if (mList.size() > 0) {
						if (mList.get(0) instanceof HashMap|| mList.get(0) instanceof Map) {
							List<Map<String, Object>> mListTmp = (List<Map<String, Object>>)mData;
							HashMapDataTable dataTable = new HashMapDataTable();
							dataTable.addAll(mListTmp);
							designer.setDataSource(key, dataTable);
						} else {
							//传递List JavaBean
							designer.setDataSource(key, mList);
						}
					}
				}
				else if (mData instanceof HashMap || mData instanceof Map) {
					// 主要数据,单独显示
					Map<String, Object> map = (Map<String, Object>)mData;
					Set<String> mainKeySet = map.keySet();
					for (String mainKey : mainKeySet) {
						designer.setDataSource(mainKey, map.get(mainKey));
					}
				}
				else {
					//可传递标准JavaBean,HashMap
					designer.setDataSource(key,mData);
				}
			}
			designer.process(true);
		} catch (Exception e) {
			logger.info("设置数据源异常", e);
		}
	}
	/**
	 * 将Excel生成 输出流返回
	 * @param out
	 * @return
	 * @throws Exception
	 */
	public void sendReport(OutputStream out) {
		try {
			this.workBook.save(out, workBook.getFileFormat());
		} catch (Exception e) {
			logger.info("生成文件输出流异常", e);
		}
	}
	
	/**
	 * 设置excel所有页眉页脚的数据
	 * @param worksheet
	 * @param mainData
	 */
	public void setWorksheetHeaderAndFooterData(Worksheet worksheet, HashMap<String,Object> mainData){
		setWorksheetHeader(worksheet, mainData);
		setWorksheetFooter(worksheet, mainData);
	}
	/**
	 * 设置所有页眉数据
	 * @param worksheet
	 * @param mainData
	 */
	public void setWorksheetHeader(Worksheet worksheet, HashMap<String,Object> mainData){
		for(int i = 0; i < 3; i++){
			setWorksheetHeaderSite(i, worksheet, mainData);
		}
	}
	/**
	 * 设置所有页脚数据
	 * @param worksheet
	 * @param mainData
	 */
	public void setWorksheetFooter(Worksheet worksheet, HashMap<String,Object> mainData){
		for(int i = 0; i < 3; i++){
			setWorksheetFooterSite(i, worksheet, mainData);
		}
	}
	/**
	 * 设置指定页眉数据
	 * @param worksheet
	 * @param mainData
	 */
	public void setWorksheetHeaderSite(int index, Worksheet worksheet, HashMap<String,Object> mainData){
		PageSetup pageSetup = worksheet.getPageSetup();
		String source = pageSetup.getHeader(index);
		pageSetup.setHeader(index, replaceParameters(source, mainData));
	}
	/**
	 * 设置指定页脚数据
	 * @param worksheet
	 * @param mainData
	 */
	public void setWorksheetFooterSite(int index, Worksheet worksheet, HashMap<String,Object> mainData){
		PageSetup pageSetup = worksheet.getPageSetup();
		String source = pageSetup.getFooter(index);
		pageSetup.setFooter(index, replaceParameters(source, mainData));
	}
	/**
	 * 解析并替换参数
	 * @param source
	 * @param mainData
	 * @return
	 */
	private String replaceParameters(String source, HashMap<String,Object> mainData){
		if(!Usual.isNullOrEmpty(source) && source.indexOf("{") > -1 && source.indexOf("}") > -1){
			List<String> parameters = getParameters(source);
			for(String param : parameters){
				Object value = mainData.get(param);
				if(value != null){
					source = source.replace("{" + param + "}", value.toString());
				}
			}
		}
		return source;
	}
	/**
	 * 解析{}格式的参数
	 * @param sourceStr
	 * @return
	 */
	private List<String> getParameters(String sourceStr){
		List<String> list = new ArrayList<String>();
		String[] sourceStrs = sourceStr.split("\\{");
		for(String p : sourceStrs){
			if(p.indexOf("}") > 0){
				list.add(p.substring(0, p.indexOf("}")));
			}
		}
		return list;
	}
	/**
	 * 将Excel生成 文件到指定路径
	 * @param filePath 完整路径及文件名
	 * @return
	 * @throws Exception
	 */
	public void saveToFilePath(String filePath) {
		try {
			this.workBook.save(filePath);
		} catch (Exception e) {
			logger.info("生成文件输出流异常", e);
		}
	}
	
	public Workbook getWorkBook() {
		return workBook;
	}

	public void setWorkBook(Workbook workBook) {
		this.workBook = workBook;
	}

	public WorkbookDesigner getDesigner() {
		return designer;
	}

	public void setDesigner(WorkbookDesigner designer) {
		this.designer = designer;
	}
	
	public static void main(String[] args) throws IllegalAccessException, IOException {
		/*String s = "{creatorNames}\n打印次数:{printNum}";
		String[] s2 =s.split("\\{");
		for(String a : s2){
			System.out.println(a.indexOf("}"));
			if(a.indexOf("}") != -1){
				a = a.substring(0, a.indexOf("}"));
				s = s.replace("{"+a+"}", Math.random()+"");
				System.out.println(s);
			}
		}*/
		/*PathUtil mPathUtil=new PathUtil();
		String mWebPath=mPathUtil.getWebRoot();*/
		FileInputStream iStream = null;
		try {
			 iStream = new FileInputStream("com-niubility-common/src/main/java/boot/util/aspose/lic/aspose.word.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(iStream != null){
				iStream.close();
			}
		}

		System.out.println(new File(".").getAbsolutePath()+"lic/aspose.word.xml");
	}
}