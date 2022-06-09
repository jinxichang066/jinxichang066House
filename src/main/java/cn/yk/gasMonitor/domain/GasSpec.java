package cn.yk.gasMonitor.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("g_gas_spec")
public class GasSpec {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String gasName;

}
