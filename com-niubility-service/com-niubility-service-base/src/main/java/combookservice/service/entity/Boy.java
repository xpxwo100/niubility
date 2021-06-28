package combookservice.service.entity;

import org.hibernate.annotations.Entity;

import java.io.Serializable;

public class Boy implements Serializable {
    private static final long serialVersionUID=1L;
    private int id;
    private String name;
    private int age;
    @Override
    public String toString() {
        return "Boy{" +
                "age=" + age +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    //此处省略getter 和setter 方法

    public Boy(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    public Boy() {
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
