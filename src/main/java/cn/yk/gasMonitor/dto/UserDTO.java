package cn.yk.gasMonitor.dto;

import cn.yk.gasMonitor.domain.User;
import lombok.Data;

/**
 * 描述
 *
 * @author Jin Xichang
 * @date 2022/7/1
 **/
@Data
public class UserDTO extends User {

    private String newPassword;

}
