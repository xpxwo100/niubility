package boot.util;

import java.io.Serializable;

public class MsgDto implements Serializable,Cloneable {
    private static final long serialVersionUID = 2012452154834L;
    private Object obj;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
