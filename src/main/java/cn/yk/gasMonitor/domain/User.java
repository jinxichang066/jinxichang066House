package cn.yk.gasMonitor.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Data
@TableName("g_user")
public class User {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String userName;

    private String password;

    private String realName;

    private String phone;

    private String email;

    private Type type;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date lastLoginTime;

    private DelFlag delFlag;

    private String remarks;

    private String roleId;

    @TableField(value = "createBy", fill = FieldFill.INSERT)
    private String createBy;

    @TableField(value = "createTime", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(value = "modifyBy", fill = FieldFill.INSERT_UPDATE)
    private String modifyBy;

    @TableField(value = "modifyTime", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    public enum Type {
        ROOT,
        CUSTOM
    }

    public enum DelFlag {
        NORMAL,
        DELETED
    }
}
