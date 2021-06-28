package boot.nettyRpcModel;

import java.io.Serializable;
import java.util.Arrays;

public class ParamsData implements Serializable {
	private static final long serialVersionUID = 2015534444051785L;
	public String beanName; // required
	public String methodName; // required
	public Object[] params;
	public String id;
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object... params) {
		this.params = params;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ParamsData{" +
				"beanName='" + beanName + '\'' +
				", methodName='" + methodName + '\'' +
				", params=" + Arrays.toString(params) +
				'}';
	}
}
