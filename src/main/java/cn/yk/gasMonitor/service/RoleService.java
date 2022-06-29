package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.dao.RoleMapper;
import cn.yk.gasMonitor.domain.Role;
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
public class RoleService {

    @Resource
    private RoleMapper roleMapper;

    public Role getRole(Role.RoleKey roleKey) {
        return roleMapper.selectOne(Wrappers.lambdaQuery(Role.class).eq(Role::getRoleKey, roleKey));
    }

}
