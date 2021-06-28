package boot.nettyRpcModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ------------------------------------------------------ 
 * Date Author Description 2014-03-27 
 * Consam Kang Setting up the framework
 * ------------------------------------------------------
 */
public class DTO implements Serializable,Cloneable  {
	private static final long serialVersionUID = 20140411110230L;

	private Integer id;
	private long version;
	private Integer creatorID;
	private String creatorName;
	private Date createDate;
	private Integer modifierID;
	private String modifierName;
	private Date modifyDate;
	/**
	 * 明细删除
	 */
	private List<Map<String, String>> delList; 
	/**
	 * 明细修改
	 */
	private List<Map<String, Object>> modifyList; 
	/**
	 * 参照版本号控制--检查数据源
	 */
	private String checkSource;
	/**
	 * 参照版本号控制--检查数据源的编码
	 */
	private String checkSourceNo;
	/**
	 * 参照版本号控制--检查的数据
	 */
	private List<Map<String, String>> checkDatas;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public Integer getCreatorID() {
		return creatorID;
	}

	public void setCreatorID(Integer creatorID) {
		this.creatorID = creatorID;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getModifierID() {
		return modifierID;
	}

	public void setModifierID(Integer modifierID) {
		this.modifierID = modifierID;
	}

	public String getModifierName() {
		return modifierName;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}

	public List<Map<String, String>> getDelList() {
		return delList;
	}

	public void setDelList(List<Map<String, String>> delList) {
		this.delList = delList;
	}
	
	public List<Map<String, Object>> getModifyList() {
		return modifyList;
	}

	public void setModifyList(List<Map<String, Object>> modifyList) {
		this.modifyList = modifyList;
	}

	/**
	 * 参照版本号控制--检查数据源
	 * @return
	 */
	public String getCheckSource() {
		return checkSource;
	}
	/**
	 * 参照版本号控制--检查数据源
	 * @return
	 */
	public void setCheckSource(String checkSource) {
		this.checkSource = checkSource;
	}
	/**
	 * 参照版本号控制--检查数据源的编码
	 * @return
	 */
	public String getCheckSourceNo() {
		return checkSourceNo;
	}
	/**
	 * 参照版本号控制--检查数据源的编码
	 * @return
	 */
	public void setCheckSourceNo(String checkSourceNo) {
		this.checkSourceNo = checkSourceNo;
	}
	/**
	 * 参照版本号控制--检查的数据
	 * @return
	 */
	public List<Map<String, String>> getCheckDatas() {
		return checkDatas;
	}
	/**
	 * 参照版本号控制--检查的数据
	 * @return
	 */
	public void setCheckDatas(List<Map<String, String>> checkDatas) {
		this.checkDatas = checkDatas;
	}
}
