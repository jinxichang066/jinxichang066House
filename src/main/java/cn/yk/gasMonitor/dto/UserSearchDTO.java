package cn.yk.gasMonitor.dto;

import lombok.Data;

/**
 * @author Jin Xichang
 * @date 2022/5/12
 **/
@Data
public class UserSearchDTO {

    private int pageNum;

    private int pageSize;

    private String name;

}
