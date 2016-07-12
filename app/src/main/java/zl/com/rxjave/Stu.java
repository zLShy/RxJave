package zl.com.rxjave;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/11.
 */
public class Stu implements Serializable {

    private String name;

    public Stu(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
