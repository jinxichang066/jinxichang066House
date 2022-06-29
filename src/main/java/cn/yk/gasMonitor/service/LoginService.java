package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.dao.UserMapper;
import cn.yk.gasMonitor.domain.Token;
import cn.yk.gasMonitor.domain.User;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@Service
public class LoginService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private TokenService tokenService;

    public PageResult login(String userName, String password) {
        PageResult result = new PageResult();
        User user = userMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getUserName, userName.trim()));

        // 用户名不存在
        if (user == null) {
            result.setCode(StatusCode.ERROR);
            result.setMessage(MessageConstant.LOGIN_USER_NOT_EXIST);
            return result;
        }
        // 密码不正确
        if (!user.getPassword().equals(password.trim())) {
            result.setCode(StatusCode.ERROR);
            result.setMessage(MessageConstant.LOGIN_PASSWORD_ERROR);
            return result;
        }

        // 生成token
        Token token = tokenService.createToken(user.getId());

        result.setCode(StatusCode.OK);
        result.setMessage(MessageConstant.LOGIN_SUCCESS);
        result.setData(token);
        return result;
    }

}
