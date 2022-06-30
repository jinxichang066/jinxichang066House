package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.dao.UserMapper;
import cn.yk.gasMonitor.domain.User;
import cn.yk.gasMonitor.dto.UserDTO;
import cn.yk.gasMonitor.dto.UserSearchDTO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private TokenService tokenService;

    public IPage<User> searchCondition(UserSearchDTO userSearchDTO) {
        Page<User> page = new Page<>(userSearchDTO.getPageNum(), userSearchDTO.getPageSize());
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .like(!StringUtils.isEmpty(userSearchDTO.getName().trim()), User::getUserName, userSearchDTO.getName())
                .orderByAsc(User::getCreateTime)
                .orderByDesc(User::getType);
        return userMapper.selectPage(page, wrapper);
    }

    public PageResult findById(String id) {
        User user = userMapper.selectById(id);
        return new PageResult(true, StatusCode.OK, MessageConstant.USER_FIND_BY_ID_SUCCESS, user);

    }

    public PageResult add(User user) {
        // todo 如果手机号不为空 校验
        // todo 如果邮箱不为空 校验
        if (user.getUserName().trim().equals("")) {
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_LOSS_USER_NAME);
        }
        if (user.getRoleKey() == null) {
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_LOSS_ROLE_KEY);
        }

        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class).eq(User::getUserName, user.getUserName());
        if (userMapper.selectCount(wrapper) > 0) {
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_DUP_USER_NAME);
        } else {
            user.setType(User.Type.CREATE);
            user.setDelFlag(User.DelFlag.NORMAL);
            user.setCreateTime(new Date());

            userMapper.insert(user);
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_ADD_SUCCESS);
        }
    }

    public PageResult update(User user) {
        // todo 如果手机号不为空 校验
        // todo 如果邮箱不为空 校验
        if (user.getUserName().trim().equals("")) {
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_LOSS_USER_NAME);
        }
        if (user.getRoleKey() == null) {
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_LOSS_ROLE_KEY);
        }

        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class).eq(User::getUserName, user.getUserName()).ne(User::getId, user.getId());
        if (userMapper.selectCount(wrapper) > 0) {
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_DUP_USER_NAME_UPDATE);
        } else {
            user.setModifyTime(new Date());

            userMapper.updateById(user);
            return new PageResult(true, StatusCode.OK, MessageConstant.USER_UPDATE_SUCCESS);
        }
    }

    public PageResult setSelf(UserDTO userDTO) {
        String newPassword = userDTO.getNewPassword().trim();

        User userEx = userMapper.selectById(userDTO.getId());
        if (userEx != null) {
            if (!userEx.getPassword().equals(userDTO.getPassword())) {
                return new PageResult(true, StatusCode.OK, "用户密码错误!");
            } else {
                userEx.setRealName(userDTO.getRealName());
                userEx.setPhone(userDTO.getPhone());
                userEx.setEmail(userDTO.getEmail());
                // 如果输入了新密码 就修改并清除token
                if (org.apache.commons.lang3.StringUtils.isNotBlank(newPassword)) {
                    userEx.setPassword(newPassword);
                    tokenService.deleteTokenByUserId(userEx.getId());
                }

                userEx.setModifyTime(new Date());
                userMapper.updateById(userEx);
            }
        }
        return new PageResult(true, StatusCode.OK, org.apache.commons.lang3.StringUtils.isNotBlank(newPassword) ? "密码修改成功，请退出系统重新登录" : "修改成功");
    }

    public PageResult delete(List<String> ids) {
        userMapper.deleteBatchIds(ids);
        return new PageResult(true, StatusCode.OK, MessageConstant.USER_DELETE_SUCCESS);
    }

    public PageResult revertPassword(JSONObject jsonObject) {
        String id = jsonObject.getString("id");
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword("e10adc3949ba59abbe56e057f20f883e");
            user.setModifyTime(new Date());

            userMapper.updateById(user);

            tokenService.deleteTokenByUserId(id);
        }
        return new PageResult(true, StatusCode.OK, "");
    }

    public User getUser(String id) {
        return userMapper.selectById(id);
    }

}
