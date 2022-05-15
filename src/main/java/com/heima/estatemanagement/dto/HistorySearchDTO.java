package com.heima.estatemanagement.dto;

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

    private Date startTime;

    private Date endTime;

    private int scanMode;

    private String machineUrl;

}
