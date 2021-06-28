package combookproductcontroller.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author xpx
 * @email sunlightcs@gmail.com
 * @date 2021-06-10 14:04:21
 */
@Data
@TableName("test")
public class TestEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private String name;
	/**
	 * 
	 */
	private String code;
	/**
	 * 
	 */
	private Integer age;
	/**
	 * 
	 */
	private String neirong;
	/**
	 * 
	 */
	private Integer test2id;

}
