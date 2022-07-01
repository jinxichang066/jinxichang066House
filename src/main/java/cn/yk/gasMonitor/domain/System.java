package cn.yk.gasMonitor.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Data
@TableName("g_system")
public class System {

    @TableId(value = "id")
    private String id;

    private String name;

    private byte[] logo;

    private String copyright;

    private Integer expire;

}
