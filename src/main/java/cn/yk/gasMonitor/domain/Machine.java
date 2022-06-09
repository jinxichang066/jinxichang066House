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
@TableName("g_machine")
public class Machine {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String machineId;

    private String machineName;

    private String machineUrl;

    private State state;

    private String machineDesc;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

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

    public enum State {
        OFFLINE,
        ONLINE
    }

}
