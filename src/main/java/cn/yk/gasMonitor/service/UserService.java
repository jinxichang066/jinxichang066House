package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.dao.UserMapper;
import cn.yk.gasMonitor.domain.User;
import cn.yk.gasMonitor.dto.UserSearchDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public IPage<User> searchCondition(UserSearchDTO userSearchDTO) {
        Page<User> page = new Page<>(userSearchDTO.getPageNum(), userSearchDTO.getPageSize());
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .like(!StringUtils.isEmpty(userSearchDTO.getName().trim()), User::getUserName, userSearchDTO.getName())
                .orderByAsc(User::getCreateTime)
                .orderByDesc(User::getType);
        return userMapper.selectPage(page, wrapper);
    }

}
