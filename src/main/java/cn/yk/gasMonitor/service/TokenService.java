package cn.yk.gasMonitor.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.yk.gasMonitor.dao.TokenMapper;
import cn.yk.gasMonitor.domain.Token;
import cn.yk.gasMonitor.domain.User;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${custom.system.setting.expireTime}")
    private Long expiredTime;

    public Token createToken(User user) {
        Date loginTime = new Date();
        Token token = new Token();
        token.setLoginTime(loginTime);
        token.setOperateTime(loginTime);
        token.setMenuIdList(new ArrayList<>());
        token.setRoleIdList(new ArrayList<>());
        token.setUserId(user.getId());
        token.setUser(JSONObject.toJSONString(user));

        tokenMapper.insert(token);

        return token;
    }

    /**
     * token验证
     * todo 此处最好用redis验证，为了不给客户引入额外组件，暂时用数据库实现
     */
    public boolean validToken(String gasToken, boolean updateFlag) {
        log.info("[验证token]:{}", gasToken);
        Token tokenEx = tokenMapper.selectOne(Wrappers.lambdaQuery(Token.class).eq(Token::getId, gasToken));

        // 未登录
        if (tokenEx == null) {
            log.info("[验证token]验证失败,登陆信息为空");
            return false;
        }

        Date operateTime = tokenEx.getOperateTime();
        Date now = new Date();
        long between = DateUtil.between(operateTime, now, DateUnit.SECOND);

        // 登录已过期
        if (between > expiredTime) {
            log.info("[验证token]验证失败,登陆信息过期");
            tokenMapper.deleteById(tokenEx.getId());
            return false;
        } else { // 未过期
            log.info("[验证token]验证成功");
            if (updateFlag) {
                tokenEx.setOperateTime(now);
                tokenMapper.updateById(tokenEx);
            }
            return true;
        }
    }

    public void logout(String token) {
        tokenMapper.deleteById(token);
    }

    public String getUserId(String token) {
        Token tokenObj = tokenMapper.selectById(token);
        if (tokenObj != null) {
            return tokenObj.getUserId();
        } else {
            return "";
        }
    }

    public void deleteTokenByUserId(String userId) {
        tokenMapper.delete(Wrappers.lambdaQuery(Token.class).eq(Token::getUserId, userId));
    }

}
