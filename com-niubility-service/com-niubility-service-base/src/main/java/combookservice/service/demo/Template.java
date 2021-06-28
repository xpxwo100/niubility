package combookservice.service.demo;
/**
 * 模板方法
 */
public abstract class Template {
    //这是我们的模板方法
    public final void TemplateMethod(){
        PrimitiveOperation1();
        PrimitiveOperation2();
        PrimitiveOperation3();
    }

    protected void  PrimitiveOperation1(){
        //当前类实现
    }

    //被子类实现的方法
    protected abstract String PrimitiveOperation2();
    protected abstract void PrimitiveOperation3();
}
