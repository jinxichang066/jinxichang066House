package cn.yk.gasMonitor.service;

import cn.hutool.extra.servlet.ServletUtil;
import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.dao.SystemMapper;
import cn.yk.gasMonitor.domain.System;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Jin Xichang
 * @date 2022/4/20
 **/
@Slf4j
@Service
public class SystemService {

    @Resource
    private SystemMapper systemMapper;

    public PageResult uploadImage(MultipartFile file) throws IOException {
        // todo 校验图片格式是否为svg 或者是交给前端校验
        System system = getSystem();
        system.setLogo(file.getBytes());

        systemMapper.updateById(system);
        return new PageResult(true, StatusCode.OK, MessageConstant.SYSTEM_LOGO_UPLOAD_SUCCESS);
    }

    public void getImage(HttpServletResponse response) {
        System system = getSystem();
        byte[] bytes = system.getLogo();
        if (bytes != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            // contentType设置成svg 否则前端显示不出
            response.setContentType("image/svg+xml");
            ServletUtil.write(response, inputStream);
        }
    }

    private System getSystem() {
        System system = systemMapper.selectOne(new QueryWrapper<>());
        return system;
    }

}
