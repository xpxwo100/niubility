package boot.nettyRpcModel;

import java.io.Serializable;
import java.util.Map;

/**
 * 存放异常信息
 * @author hasee
 *
 */
public class ErrorMsgException  implements Serializable{


	private static final long serialVersionUID = 20194342332190130L;

	protected String code;
	protected String msg;
	protected String detailMessage;
	protected Object[] args;
	

	public ErrorMsgException() {
	}

	
	public ErrorMsgException(String message) {
		this.msg = message;
	}

	public ErrorMsgException(String code, String message) {
		this.code = code;
		this.msg = message;
	}

	public ErrorMsgException(String message, Throwable cause) {
		this.msg = message;
	}

	public ErrorMsgException(String code, String message, Object[] args) {
		this.code = code;
		this.msg = message;
		this.args = args;
	}

	public ErrorMsgException(String code, String message, Object[] args, Throwable cause) {
		this.code = code;
		this.msg = message;
		this.args = args;
	}

	public ErrorMsgException(String code, String message, Object[] args, Map<String, Object> data, Throwable cause) {
		this.code = code;
		this.msg = message;
		this.args = args;
	}
	
	
	public ErrorMsgException( String code, String msg,
			String detailMessage, Object[] args) {
		this.code = code;
		this.msg = msg;
		this.detailMessage = detailMessage;
		this.args = args;
	}

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Object[] getArgs() {
		return args;
	}


	public String getDetailMessage() {
		return detailMessage;
	}

	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}

}
