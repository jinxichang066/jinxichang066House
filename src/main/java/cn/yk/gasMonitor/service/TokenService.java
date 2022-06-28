package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.dao.TokenMapper;
import cn.yk.gasMonitor.domain.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@Service
public class TokenService {

    @Resource
    private TokenMapper tokenMapper;

    public Token createToken(String userId) {
        Date loginTime = new Date();
        Token token = new Token();
        token.setLoginTime(loginTime);
        token.setOperateTime(loginTime);
        token.setMenuIdList(new ArrayList<>());
        token.setRoleIdList(new ArrayList<>());
        token.setUserId(userId);

        tokenMapper.insert(token);

        return token;
    }

}
