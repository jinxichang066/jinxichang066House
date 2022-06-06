package com.heima.estatemanagement.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.heima.estatemanagement.common.MessageConstant;
import com.heima.estatemanagement.common.PageResult;
import com.heima.estatemanagement.common.StatusCode;
import com.heima.estatemanagement.domain.History;
import com.heima.estatemanagement.dto.HistorySearchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jin Xichang
 * @date 2022/5/12
 **/
@Slf4j
@Service
public class HistoryService {

    private static final String VI = "VI";
    private static final String IR = "IR";

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://machineUrl:port/schema?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false";

    @Value("${jdbc.connection.user}")
    private String user;
    @Value("${jdbc.connection.password}")
    private String password;
    @Value("${jdbc.connection.port}")
    private String port;
    @Value("${jdbc.connection.schema}")
    private String schema;

    public PageResult search(HistorySearchDTO historySearchDTO) {
        log.info("查询开始，查询历史信息");

        PageResult pageResult = new PageResult();

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接
            conn = getConnection(historySearchDTO.getMachineUrl());

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT count(1) as count from warninginfor where 1=1 ");
            assembleCondition(historySearchDTO, sql);

            // 执行查询
            log.info("实例化Statement对象...");
            stmt = conn.createStatement();
            log.info("执行sql: {}", sql.toString());
            ResultSet rs = stmt.executeQuery(sql.toString());
            int count = 0;
            while (rs.next()) {
                // 通过字段检索
                count = rs.getInt("count");
            }
            log.info("数据总条数为:{}", count);
            if (count == 0) {
                pageResult.setFlag(true);
                pageResult.setCode(StatusCode.OK);
                pageResult.setMessage(MessageConstant.HISTORY_SEARCH_SUCCESS);
                pageResult.setData(new ArrayList<>());
                pageResult.setTotal(0L);
            } else {
                StringBuilder sql1 = new StringBuilder();
                sql1.append("SELECT id, scanmode, dtime, gasnames, firstdtime, lastdtime FROM warninginfor where 1=1 ");
                assembleCondition(historySearchDTO, sql1);
                //排序
                sql1.append("order by dtime desc ");
                //分页
                sql1.append("limit ").append((historySearchDTO.getPageNum() - 1) * historySearchDTO.getPageSize()).append(",").append(historySearchDTO.getPageSize());

                log.info("执行sql: {}", sql1.toString());
                ResultSet rs1 = stmt.executeQuery(sql1.toString());
                List<History> historyList = new ArrayList<>();
                while (rs1.next()) {
                    History history = new History();
                    // 通过字段检索
                    history.setId(rs1.getInt("id"));
                    history.setScanMode(rs1.getInt("scanmode"));
                    history.setDTime(rs1.getTimestamp("dtime"));
                    history.setGasNames(rs1.getString("gasnames"));
                    history.setFirstDTime(rs1.getTimestamp("firstdtime"));
                    history.setLastDTime(rs1.getTimestamp("lastdtime"));

                    historyList.add(history);
                }
                pageResult.setFlag(true);
                pageResult.setCode(StatusCode.OK);
                pageResult.setMessage(MessageConstant.HISTORY_SEARCH_SUCCESS);
                pageResult.setData(historyList);
                pageResult.setTotal((long) count);
            }


            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            log.error(se.getMessage(), se);
            pageResult.setFlag(true);
            pageResult.setCode(StatusCode.ERROR);
            pageResult.setMessage(MessageConstant.HISTORY_SEARCH_FAIL_DB_CONNECT_FAIL);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            pageResult.setFlag(true);
            pageResult.setCode(StatusCode.ERROR);
            pageResult.setMessage(MessageConstant.HISTORY_SEARCH_FAIL_ERROR);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        log.info("查询结束");

        return pageResult;
    }

    private Connection getConnection(String machineUrl) throws ClassNotFoundException, SQLException {
        // 注册 JDBC 驱动
        Class.forName(JDBC_DRIVER);

        String url = DB_URL.replace("machineUrl", machineUrl)
                .replace("port", port)
                .replace("schema", schema);

        // 打开链接
        log.info("连接数据库：{}", url);
        Connection conn = DriverManager.getConnection(url, user, password);

        return conn;
    }


    private void assembleCondition(HistorySearchDTO historySearchDTO, StringBuilder sql) {
        if (historySearchDTO.getStartTime() != null) {
            sql.append("and dtime >= ").append("'").append(DateUtil.format(historySearchDTO.getStartTime(), "yyyy-MM-dd HH:mm:ss")).append("' ");
        }

        if (historySearchDTO.getEndTime() != null) {
            sql.append("and dtime <= ").append("'").append(DateUtil.format(historySearchDTO.getEndTime(), "yyyy-MM-dd HH:mm:ss")).append("' ");
        }
        if (historySearchDTO.getScanMode() != 0) {
            sql.append("and scanmode = ").append(historySearchDTO.getScanMode()).append(" ");
        }
        if (StrUtil.isNotEmpty(historySearchDTO.getGasnames())) {
            sql.append("and gasnames like '%").append(historySearchDTO.getGasnames()).append("%' ");
        }
    }

    public void getImage(String machineUrl, String warningInfoId, String mode, HttpServletResponse response) {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接
            conn = getConnection(machineUrl);

            StringBuilder sql = new StringBuilder();
            if (mode.equals(IR)) {
                sql.append("SELECT imageir from warninginfor where id=").append(warningInfoId);
            } else {
                sql.append("SELECT imagevi from warninginfor where id=").append(warningInfoId);
            }

            // 执行查询
            log.info("实例化Statement对象...");
            stmt = conn.createStatement();
            log.info("执行sql: {}", sql.toString());
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                // 获取图片的字节数组
                Blob blob;
                if (mode.equals(IR)) {
                    blob = rs.getBlob("imageir");
                } else {
                    blob = rs.getBlob("imagevi");
                }
                byte[] array = blob.getBytes(1, (int) blob.length());

                // 使用ImageIO处理图像
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(array));
                Graphics g = image.getGraphics();
                //g.setColor(new Color());
                g.setColor(Color.YELLOW);//画笔颜⾊
                g.drawRect(100, 100, 100, 100);//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                g.dispose();
                // 写到输出流
                ByteArrayOutputStream out = new ByteArrayOutputStream();//
                ImageIO.write(image, "jpeg", out);

                // 写回response
                ServletUtil.write(response, new ByteArrayInputStream(out.toByteArray()));
            }
            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            log.error(se.getMessage(), se);
            //pageResult.setFlag(true);
            //pageResult.setCode(StatusCode.ERROR);
            //pageResult.setMessage(MessageConstant.HISTORY_SEARCH_FAIL_DB_CONNECT_FAIL);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //pageResult.setFlag(true);
            //pageResult.setCode(StatusCode.ERROR);
            //pageResult.setMessage(MessageConstant.HISTORY_SEARCH_FAIL_ERROR);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        log.info("查询结束");

    }

}