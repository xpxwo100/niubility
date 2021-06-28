package boot.nettyRpcModel;

import java.util.Map;

public class AppRunTimeException extends  RuntimeException {

	private static final long serialVersionUID = 20140424090130L;

	/** A wrapped Throwable */
	protected Throwable cause;
	protected String code;
	protected String msg;
	protected String detailMessage;
	protected Object[] args;
	
	protected Map<String, Object> data;// 附加信息

	public AppRunTimeException() {
		super("Error occurred in application.");
	}

	public AppRunTimeException(Exception e) {
		super(e);
	}
	
	public AppRunTimeException(String message) {
		super(message);
		this.msg = message;
	}

	public AppRunTimeException(String code, String message) {
		super(message);
		this.code = code;
		this.msg = message;
	}

	public AppRunTimeException(String message, Throwable cause) {
		this.msg = message;
		this.cause = cause;
	}

	public AppRunTimeException(String code, String message, Object[] args) {
		super(message);
		this.code = code;
		this.msg = message;
		this.args = args;
	}

	public AppRunTimeException(String code, String message, Object[] args, Throwable cause) {
		super(message);
		this.code = code;
		this.msg = message;
		this.args = args;
	}

	public AppRunTimeException(String code, String message, Object[] args, Map<String, Object> data, Throwable cause) {
		super(message);
		this.code = code;
		this.msg = message;
		this.args = args;
		this.data = data;
	}

	public AppRunTimeException(String code, String message, Throwable cause) {
		this.code = code;
		this.msg = message;
		this.cause = cause;
	}

	// Created to match the JDK 1.4 Throwable method.
	public Throwable initCause(Throwable cause) {
		this.cause = cause;
		return cause;
	}

	public Throwable getCause() {
		return cause;
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

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getDetailMessage() {
		return detailMessage;
	}

	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}
}
