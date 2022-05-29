package com.heima.estatemanagement.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("g_gas_spec")
public class GasSpec {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    private String gasName;

}
