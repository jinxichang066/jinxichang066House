package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

}
