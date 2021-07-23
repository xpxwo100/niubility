package combookproductcontroller.util.aspose;

import boot.util.Usual;

import java.util.HashMap;
import java.util.Map;

/**
 * PathUtil
 */
public class PathUtil
{

	/**
	 * WEB-INF
	 */
	public final static String mWebInfName="WEB-INF/";
	//
	/**
	 * getWebClassesPath
	 * @return
	 */
	public String getWebClassesPath()
	{
		String mPath=getClass().getClassLoader().getResource("/").getPath();
		//兼容resin服务器
		if(mPath.toLowerCase().indexOf("resin")>0 || mPath.toLowerCase().indexOf("wildfly")>0)
		{
			mPath=getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		}
		return Usual.getRealRoot(mPath);
		
		//Tomcat��,WebLogic,IBM WASΪ��ʱ·������·��
//		String mPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//		ȡ��·��Ϊ:
//		..\domains\base_domain\servers\AdminServer\tmp\_WL_user\ishitong.project.web\v1w0jy\war\WEB-INF\lib

//		String mPath = getClass().getClassLoader().getResource("").getPath();
//		ȡ��·��Ϊ:../domains/base_domain/
	}
	/**
	 * getWebInfPath
	 * @return
	 * @throws IllegalAccessException
	 */
	public String getWebInfPath() throws IllegalAccessException
	{
		String path = getWebClassesPath();
		if (path.indexOf(mWebInfName) > -1)
		{
			path = path.substring(0, path.indexOf(mWebInfName) + mWebInfName.length());
		}
		else
		{
			throw new IllegalAccessException("路径截取失败.");
		}
		return path;
	}
	/**
	 * get
	 * @return
	 * @throws IllegalAccessException
	 */
	public String getWebRoot() throws IllegalAccessException
	{
		String path = getWebClassesPath();
		if (path.indexOf(mWebInfName) > -1)
		{
			path = path.substring(0, path.indexOf(mWebInfName));
		}
		else
		{
			throw new IllegalAccessException("路径截取失败.");
		}
//		String mRootPath=Usual.mEmpty;
//		String mOSName = System.getProperty("os.name");
//		if (mOSName != null && mOSName.startsWith("Windows")) 
//		{ 
//			mRootPath=Usual.getRealRoot(path.replace("%20", Usual.mBlankSpace));
//		}
//		else
//		{
//			mRootPath=Usual.getRealRoot(path);
//		}
//		避免路径出现" "空格，兼容Tomcat,WebLogic
		String mRootPath=Usual.getRealRoot(path.replace("%20", Usual.mBlankSpace));
		return mRootPath;
	}
	/**
	 * getFilePath
	 * @param mWebPath
	 * @return
	 */
	public String getFilePath(String mWebPath)
	{
		String mRootPath=Usual.mEmpty;
		try
		{
			mRootPath = getWebRoot();
		}
		catch (IllegalAccessException e)
		{
		}
		if(mWebPath.startsWith("/") || mWebPath.startsWith("\\"))
		{
			mRootPath=mRootPath+mWebPath.substring(1);
		}
		else
		{
			mRootPath=mRootPath+mWebPath;
		}
		return mRootPath;
	}
	/**
	 * getRootPath4ConfigPath
	 * @param dir
	 * @return
	 * @throws IllegalAccessException
	 */
	public String getRootPath4ConfigPath(String dir) 
	throws IllegalAccessException
	{
		if (dir.indexOf(":") > -1)
		{
			return dir;
		}
		return getWebRoot() + "config/" + dir;
	}
	/**
	 * getDir
	 * @param file
	 * @return
	 */
	public static String getDir(String file)
	{
		return file.substring(0, file.lastIndexOf("/"));
	}
	public static Map<String, Object> URLRequest(String URL){
		Map<String, Object> mapRequest = new HashMap<String, Object>();

		String[] arrSplit = null;

		String strUrlParam = TruncateUrlPage(URL);
		if (strUrlParam == null) {
			return mapRequest;
		}
		// 每个键值为一组 
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;

	}
	 private static String TruncateUrlPage(String strURL){
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}
		return strAllParam;
	}

}
