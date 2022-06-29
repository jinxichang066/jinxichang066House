package cn.yk.gasMonitor.service;

import cn.yk.gasMonitor.dao.MenuMapper;
import cn.yk.gasMonitor.domain.Menu;
import cn.yk.gasMonitor.domain.Role;
import cn.yk.gasMonitor.domain.User;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@Service
public class MenuService {

    @Resource
    private MenuMapper menuMapper;
    @Resource
    private TokenService tokenService;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    /**
     * 根据token获取菜单列表
     * 也可以考虑在登录后将菜单信息存入前端token对象用于以后获取
     *
     * @param token token
     * @return 菜单列表
     */
    public List<Menu> getMenuList(String token) {
        String userId = tokenService.getUserId(token);
        User user = userService.getUser(userId);
        if (user != null) {
            Role.RoleKey roleKey = user.getRoleKey();
            Role role = roleService.getRole(roleKey);
            if (role != null) {
                List<String> menuIdList = role.getMenuIdList();
                List<Menu> menuList = menuMapper.selectList(Wrappers.lambdaQuery(Menu.class)
                        .in(Menu::getId, menuIdList)
                        .orderByAsc(Menu::getLevel)
                        .orderByAsc(Menu::getSortNum));

                return menuList;
            }
        }
        return new ArrayList<>();
    }


}
