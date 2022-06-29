package cn.yk.gasMonitor.controller;

import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.domain.Menu;
import cn.yk.gasMonitor.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @RequestMapping("/getMenuList")
    public PageResult getMenuList(@RequestParam String token) {
        List<Menu> menuList = menuService.getMenuList(token);
        return new PageResult(true, StatusCode.OK, MessageConstant.MENU_SEARCH_SUCCESS, menuList, (long) menuList.size());
    }

}
