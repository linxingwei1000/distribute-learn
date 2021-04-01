package cn.lxw.ss.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author ：楼兰
 * @date ：Created in 2021/1/4
 * @description:
 **/
@TableName("course")
public class Course {

    private Long cid;

    private String cname;

    @TableField(value = "user_id")
    private Long userId;

    private String cstatus;

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCstatus() {
        return cstatus;
    }

    public void setCstatus(String cstatus) {
        this.cstatus = cstatus;
    }

    @Override
    public String toString() {
        return "Course{" +
                "cid=" + cid +
                ", cname='" + cname + '\'' +
                ", userId=" + userId +
                ", cstatus='" + cstatus + '\'' +
                '}';
    }
}
