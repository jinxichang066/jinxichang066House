package cn.yk.gasMonitor.domain;

import lombok.Data;

/**
 * @author Jin Xichang
 * @date 2022/6/7
 **/
@Data
public class PointInfo {

    private int id;

    private int warningId;

    private int pointx_0;

    private int pointy_0;

    private int fovx_0;

    private int fovy_0;

    private int pointx_1;

    private int pointy_1;

    private int fovx_1;

    private int fovy_1;

    private String gasindexs;

    private String gasnames;

    private String cons;

}
