package com.heima.estatemanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 描述
 *
 * @author Jin Xichang
 * @date 2022/5/12
 **/
@Data
public class HistorySearchDTO {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private int scanMode;

    private String machineUrl;

    private int pageNum;

    private int pageSize;

}
