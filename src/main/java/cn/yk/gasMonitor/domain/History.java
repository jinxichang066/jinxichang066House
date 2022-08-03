package cn.yk.gasMonitor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Blob;
import java.util.Date;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Data
public class History {

    private int id;

    private int scanId;

    private int scanMode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dTime;

    private String gasIndexes;

    private String gasNames;

    private String gasColors;

    private Blob imageVI;

    private Blob imageIR;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date firstDTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastDTime;

    private String picture;

}
