package cn.yk.gasMonitor.domain;

import cn.yk.gasMonitor.handler.JsonStringArrayTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Data
@TableName("g_role")
public class Role {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String roleName;

    private RoleKey roleKey;

    private State state;

    private int sortNum;

    private DelFlag delFlag;

    private String remarks;

    @TableField(typeHandler = JsonStringArrayTypeHandler.class)
    private List<String> menuIdList;

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

    public enum RoleKey {
        ADMIN,
        ROOT,
        USER,
        VISITOR
    }

    public enum State {
        NORMAL,
        ABNORMAL
    }

    public enum DelFlag {
        NORMAL,
        DELETED
    }
}
