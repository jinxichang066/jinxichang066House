package cn.yk.gasMonitor.domain;

import cn.yk.gasMonitor.handler.JsonStringArrayTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Data
@TableName("g_token")
public class Token {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateTime;

    private String userId;

    @TableField(typeHandler = JsonStringArrayTypeHandler.class)
    private List<String> menuIdList;

    @TableField(typeHandler = JsonStringArrayTypeHandler.class)
    private List<String> roleIdList;

    private String user;

}
