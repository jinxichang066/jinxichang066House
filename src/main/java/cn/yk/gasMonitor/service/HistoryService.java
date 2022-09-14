package cn.yk.gasMonitor.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.yk.gasMonitor.common.MessageConstant;
import cn.yk.gasMonitor.common.PageResult;
import cn.yk.gasMonitor.common.StatusCode;
import cn.yk.gasMonitor.dao.MachineMapper;
import cn.yk.gasMonitor.domain.History;
import cn.yk.gasMonitor.domain.Machine;
import cn.yk.gasMonitor.domain.PointInfo;
import cn.yk.gasMonitor.dto.HistorySearchDTO;
import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.data.MiniTableRenderData;
import com.deepoove.poi.util.TableTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.*;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MINUTE_PATTERN;

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
    private static final String DB_URL = "jdbc:mysql://databaseIp:port/schema?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false";

    @Value("${jdbc.connection.port}")
    private String port;

    @Resource
    private MachineMapper machineMapper;

    public PageResult search(HistorySearchDTO historySearchDTO) {
        log.info("查询开始，查询历史信息");

        PageResult pageResult = new PageResult();

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接
            conn = getConnection(historySearchDTO.getId());

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
                sql1.append("order by dtime asc ");
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
                    history.setGasNames(history.getGasNames().substring(0, history.getGasNames().length() - 1));
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

    private Connection getConnection(String id) throws ClassNotFoundException, SQLException {
        Machine machine = machineMapper.selectById(id);

        // 注册 JDBC 驱动
        Class.forName(JDBC_DRIVER);

        String url = DB_URL.replace("databaseIp", machine.getDatabaseIp())
                .replace("port", port)
                .replace("schema", machine.getDatabaseName());

        // 打开链接
        log.info("连接数据库：{}", url);
        Connection conn = DriverManager.getConnection(url, machine.getDatabaseUser(), machine.getDatabasePassword());

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

    public void getImage(String id, String warningInfoId, String mode, HttpServletResponse response) {
        InputStream imageInputStream = getImageInputStream(id, warningInfoId, mode);
        // 写回response
        if (imageInputStream != null) {
            ServletUtil.write(response, imageInputStream);
        }
    }

    public PageResult getGasInfoTable(String id, String warningInfoId) {
        Connection conn = null;
        Statement stmt = null;

        List<JSONObject> list = new ArrayList<>();
        try {
            // 获取数据库连接
            conn = getConnection(id);

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT gasnames, gascolors from warninginfor where id=").append(warningInfoId);

            // 执行查询
            log.info("实例化Statement对象...");
            stmt = conn.createStatement();
            log.info("执行sql: {}", sql.toString());
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                //从warningInfo中得到气体与对应的颜色map
                String pointInfoGasIndex = rs.getString("gasnames");
                String pointInfoGasColors = rs.getString("gascolors");
                List<String> pointInfoGasIndexList = Arrays.asList(pointInfoGasIndex.split("\\|"));
                List<String> pointInfoGasColorsList = Arrays.asList(pointInfoGasColors.split("\\|"));
                for (int i = 0; i < pointInfoGasIndexList.size(); i++) {
                    JSONObject jsonObject = new JSONObject().fluentPut("gasName", pointInfoGasIndexList.get(i)).fluentPut("gasColor", pointInfoGasColorsList.get(i));
                    list.add(jsonObject);
                }
            }
            // 完成后关闭
            // 关闭warningInfo的查询
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

        PageResult pageResult = new PageResult();
        pageResult.setFlag(true);
        pageResult.setCode(StatusCode.OK);
        pageResult.setMessage(MessageConstant.HISTORY_SEARCH_SUCCESS);
        pageResult.setData(list);
        pageResult.setTotal((long) list.size());

        return pageResult;
    }

    private InputStream getImageInputStream(String id, String warningInfoId, String mode) {
        ByteArrayInputStream byteArrayInputStream = null;

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接
            conn = getConnection(id);

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT scanid, scanmode, imagevi, imageir, gasnum, gasindexs, gascolors from warninginfor where id=").append(warningInfoId);

            // 执行查询
            log.info("[{}]实例化Statement对象...", mode);
            stmt = conn.createStatement();
            log.info("[{}]执行sql: {}", mode, sql.toString());
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                //从warningInfo中得到气体与对应的颜色map
                Map<String, String> indexColorMap = new HashMap<>();
                String pointInfoGasIndex = rs.getString("gasindexs");
                String pointInfoGasColors = rs.getString("gascolors");
                List<String> pointInfoGasIndexList = Arrays.asList(pointInfoGasIndex.split("\\|"));
                List<String> pointInfoGasColorsList = Arrays.asList(pointInfoGasColors.split("\\|"));
                for (int i = 0; i < pointInfoGasIndexList.size(); i++) {
                    indexColorMap.put(pointInfoGasIndexList.get(i), pointInfoGasColorsList.get(i));
                }
                //扫描区域
                String scanid = rs.getString("scanid");
                //监测模式
                int scanMode = rs.getInt("scanmode");

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

                // 点位信息
                String pointInfoSql = "select id,waringid,pointx_0,pointy_0,fovx_0,fovy_0,pointx_1,pointy_1,fovx_1,fovy_1,gasindexs,gasnames, cons from pointinfor where waringid = " + warningInfoId;
                ResultSet pointInfoRs = stmt.executeQuery(pointInfoSql);
                List<PointInfo> pointInfoList = new ArrayList<>();
                while (pointInfoRs.next()) {
                    PointInfo pointInfo = new PointInfo();
                    pointInfo.setId(pointInfoRs.getInt("id"));
                    pointInfo.setWarningId(pointInfoRs.getInt("waringid"));
                    pointInfo.setPointx_0(pointInfoRs.getInt("pointx_0"));
                    pointInfo.setPointy_0(pointInfoRs.getInt("pointy_0"));
                    pointInfo.setFovx_0(pointInfoRs.getInt("fovx_0"));
                    pointInfo.setFovy_0(pointInfoRs.getInt("fovy_0"));
                    pointInfo.setPointx_1(pointInfoRs.getInt("pointx_1"));
                    pointInfo.setPointy_1(pointInfoRs.getInt("pointy_1"));
                    pointInfo.setFovx_1(pointInfoRs.getInt("fovx_1"));
                    pointInfo.setFovy_1(pointInfoRs.getInt("fovy_1"));
                    pointInfo.setGasindexs(pointInfoRs.getString("gasindexs"));
                    pointInfo.setGasnames(pointInfoRs.getString("gasnames"));

                    pointInfoList.add(pointInfo);
                }
                if (CollectionUtil.isNotEmpty(pointInfoList)) {
                    // 使用Graphics绘制矩形
                    Graphics g = image.getGraphics();

                    for (PointInfo pointInfo : pointInfoList) {
                        List<String> indexList = Arrays.asList(pointInfo.getGasindexs().split("\\|"));
                        for (String index : indexList) {
                            if (indexColorMap.containsKey(index)) {
                                String color = indexColorMap.get(index);
                                log.info("[{}]绘制的气体：index[{}],color[{}]", mode, index, color);

                                if (!StringUtils.isEmpty(color)) {
                                    Color rgbColor = new Color(Integer.parseInt(color));
                                    // 用户不按业界标准rgb颜色来，自己整了个bgr，所以要重新生成color
                                    g.setColor(new Color(rgbColor.getBlue(), rgbColor.getGreen(), rgbColor.getRed()));//画笔颜⾊
                                    // 用户说pointx、pointy不是点位左上角坐标，是中心点坐标
                                    if (mode.equals(VI)) {
                                        g.drawRect(pointInfo.getPointx_0() - pointInfo.getFovx_0() / 2, pointInfo.getPointy_0() - pointInfo.getFovy_0() / 2, pointInfo.getFovx_0(), pointInfo.getFovy_0());//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                                        g.fillRect(pointInfo.getPointx_0() - pointInfo.getFovx_0() / 2, pointInfo.getPointy_0() - pointInfo.getFovy_0() / 2, pointInfo.getFovx_0(), pointInfo.getFovy_0());
                                    } else {
                                        g.drawRect(pointInfo.getPointx_1() - pointInfo.getFovx_1() / 2, pointInfo.getPointy_1() - pointInfo.getFovy_1() / 2, pointInfo.getFovx_1(), pointInfo.getFovy_1());//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                                        g.fillRect(pointInfo.getPointx_1() - pointInfo.getFovx_1() / 2, pointInfo.getPointy_1() - pointInfo.getFovy_1() / 2, pointInfo.getFovx_1(), pointInfo.getFovy_1());
                                    }
                                }
                            }
                        }
                    }

                    g.dispose();
                }

                // 扇扫模式下，绘制扫描区域
                if (scanMode == 3) {
                    String scanInfoSql = "select * from scaninfor where id = " + scanid;
                    ResultSet scanInfoRs = stmt.executeQuery(scanInfoSql);
                    if (scanInfoRs.next()) {
                        int originvix = scanInfoRs.getInt("originvix");
                        int originviy = scanInfoRs.getInt("originviy");
                        int endvix = scanInfoRs.getInt("endvix");
                        int endviy = scanInfoRs.getInt("endviy");
                        int originirx = scanInfoRs.getInt("originirx");
                        int originiry = scanInfoRs.getInt("originiry");
                        int endirx = scanInfoRs.getInt("endirx");
                        int endiry = scanInfoRs.getInt("endiry");

                        // 使用Graphics绘制矩形
                        Graphics g = image.getGraphics();
                        g.setColor(Color.RED);//画笔颜⾊
                        if (mode.equals(VI)) {
                            g.drawRect(originvix, originviy, endvix - originvix, endviy - originviy);//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                        } else {
                            g.drawRect(originirx, originiry, endirx - originirx, endiry - originiry);//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                        }

                        g.dispose();
                    }
                }

                // 写到输出流
                ByteArrayOutputStream out = new ByteArrayOutputStream();//
                ImageIO.write(image, "jpeg", out);

                byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());

                // 关闭pointInfo的查询
                pointInfoRs.close();
            }
            // 完成后关闭
            // 关闭warningInfo的查询
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
        log.info("[{}]查询结束", mode);

        return byteArrayInputStream;
    }

    // 绘制扇扫模式的图片
    private InputStream getImageInputStreamForMode3(History history, String id, String warningInfoId, String mode, String gasIndex, Map<String, String> indexColorMap, List<String> consList) {
        ByteArrayInputStream byteArrayInputStream = null;

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接
            conn = getConnection(id);

            log.info("[{}]实例化Statement对象...", mode);
            stmt = conn.createStatement();

            // 获取图片的字节数组
            Blob blob;
            if (mode.equals(IR)) {
                blob = history.getImageIR();
            } else {
                blob = history.getImageVI();
            }
            byte[] array = blob.getBytes(1, (int) blob.length());

            // 使用ImageIO处理图像
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(array));

            String pointInfoSql = "select id,waringid,pointx_0,pointy_0,fovx_0,fovy_0,pointx_1,pointy_1,fovx_1,fovy_1,gasindexs,gasnames, cons from pointinfor where waringid = " + warningInfoId;
            ResultSet pointInfoRs = stmt.executeQuery(pointInfoSql);
            List<PointInfo> pointInfoList = new ArrayList<>();
            while (pointInfoRs.next()) {
                PointInfo pointInfo = new PointInfo();
                pointInfo.setId(pointInfoRs.getInt("id"));
                pointInfo.setWarningId(pointInfoRs.getInt("waringid"));
                pointInfo.setPointx_0(pointInfoRs.getInt("pointx_0"));
                pointInfo.setPointy_0(pointInfoRs.getInt("pointy_0"));
                pointInfo.setFovx_0(pointInfoRs.getInt("fovx_0"));
                pointInfo.setFovy_0(pointInfoRs.getInt("fovy_0"));
                pointInfo.setPointx_1(pointInfoRs.getInt("pointx_1"));
                pointInfo.setPointy_1(pointInfoRs.getInt("pointy_1"));
                pointInfo.setFovx_1(pointInfoRs.getInt("fovx_1"));
                pointInfo.setFovy_1(pointInfoRs.getInt("fovy_1"));
                pointInfo.setGasindexs(pointInfoRs.getString("gasindexs"));
                pointInfo.setGasnames(pointInfoRs.getString("gasnames"));
                pointInfo.setCons(pointInfoRs.getString("cons"));

                pointInfoList.add(pointInfo);
            }
            if (CollectionUtil.isNotEmpty(pointInfoList)) {
                // 使用Graphics绘制矩形
                Graphics g = image.getGraphics();

                for (PointInfo pointInfo : pointInfoList) {
                    List<String> indexList = Arrays.asList(pointInfo.getGasindexs().split("\\|"));
                    List<String> gasConsList = Arrays.asList(pointInfo.getCons().split(","));
                    for (int k = 0; k < indexList.size(); k++) {
                        String index = indexList.get(k);
                        // 只绘制单一气体点位
                        if (index.equals(gasIndex)) {
                            String cons = gasConsList.get(k);
                            consList.add(cons);

                            String color = indexColorMap.get(index);
                            log.info("[{}]绘制的气体：index[{}],color[{}]", mode, index, color);

                            if (!StringUtils.isEmpty(color)) {
                                Color rgbColor = new Color(Integer.parseInt(color));
                                g.setColor(new Color(rgbColor.getBlue(), rgbColor.getGreen(), rgbColor.getRed()));//画笔颜⾊
                                if (mode.equals(VI)) {
                                    g.drawRect(pointInfo.getPointx_0() - pointInfo.getFovx_0() / 2, pointInfo.getPointy_0() - pointInfo.getFovy_0() / 2, pointInfo.getFovx_0(), pointInfo.getFovy_0());//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                                    g.fillRect(pointInfo.getPointx_0() - pointInfo.getFovx_0() / 2, pointInfo.getPointy_0() - pointInfo.getFovy_0() / 2, pointInfo.getFovx_0(), pointInfo.getFovy_0());
                                } else {
                                    g.drawRect(pointInfo.getPointx_1() - pointInfo.getFovx_1() / 2, pointInfo.getPointy_1() - pointInfo.getFovy_1() / 2, pointInfo.getFovx_1(), pointInfo.getFovy_1());//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                                    g.fillRect(pointInfo.getPointx_1() - pointInfo.getFovx_1() / 2, pointInfo.getPointy_1() - pointInfo.getFovy_1() / 2, pointInfo.getFovx_1(), pointInfo.getFovy_1());
                                }
                            }
                        }
                    }
                }

                g.dispose();
            }

            // 扇扫模式下，绘制扫描区域
            String scanInfoSql = "select * from scaninfor where id = " + history.getScanId();
            ResultSet scanInfoRs = stmt.executeQuery(scanInfoSql);
            if (scanInfoRs.next()) {
                int originvix = scanInfoRs.getInt("originvix");
                int originviy = scanInfoRs.getInt("originviy");
                int endvix = scanInfoRs.getInt("endvix");
                int endviy = scanInfoRs.getInt("endviy");
                int originirx = scanInfoRs.getInt("originirx");
                int originiry = scanInfoRs.getInt("originiry");
                int endirx = scanInfoRs.getInt("endirx");
                int endiry = scanInfoRs.getInt("endiry");

                // 使用Graphics绘制矩形
                Graphics g = image.getGraphics();
                g.setColor(Color.RED);//画笔颜⾊
                if (mode.equals(VI)) {
                    g.drawRect(originvix, originviy, endvix - originvix, endviy - originviy);//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                } else {
                    g.drawRect(originirx, originiry, endirx - originirx, endiry - originiry);//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                }

                g.dispose();
            }

            // 写到输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();//
            ImageIO.write(image, "jpeg", out);

            byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());


            // 完成后关闭
            // 关闭warningInfo的查询
            pointInfoRs.close();
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
        log.info("[{}]查询结束", mode);

        return byteArrayInputStream;
    }

    private InputStream getImageInputStreamForModeOther(History history, String id, String warningInfoId, String mode, List<String> gasIndexList, Map<String, String> indexColorMap, Map<String, List<String>> consMap) {
        ByteArrayInputStream byteArrayInputStream = null;

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接
            conn = getConnection(id);

            log.info("[{}]实例化Statement对象...", mode);
            stmt = conn.createStatement();

            // 获取图片的字节数组
            Blob blob;
            if (mode.equals(IR)) {
                blob = history.getImageIR();
            } else {
                blob = history.getImageVI();
            }
            byte[] array = blob.getBytes(1, (int) blob.length());

            // 使用ImageIO处理图像
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(array));

            String pointInfoSql = "select id,waringid,pointx_0,pointy_0,fovx_0,fovy_0,pointx_1,pointy_1,fovx_1,fovy_1,gasindexs,gasnames, cons from pointinfor where waringid = " + warningInfoId;
            ResultSet pointInfoRs = stmt.executeQuery(pointInfoSql);
            List<PointInfo> pointInfoList = new ArrayList<>();
            while (pointInfoRs.next()) {
                PointInfo pointInfo = new PointInfo();
                pointInfo.setId(pointInfoRs.getInt("id"));
                pointInfo.setWarningId(pointInfoRs.getInt("waringid"));
                pointInfo.setPointx_0(pointInfoRs.getInt("pointx_0"));
                pointInfo.setPointy_0(pointInfoRs.getInt("pointy_0"));
                pointInfo.setFovx_0(pointInfoRs.getInt("fovx_0"));
                pointInfo.setFovy_0(pointInfoRs.getInt("fovy_0"));
                pointInfo.setPointx_1(pointInfoRs.getInt("pointx_1"));
                pointInfo.setPointy_1(pointInfoRs.getInt("pointy_1"));
                pointInfo.setFovx_1(pointInfoRs.getInt("fovx_1"));
                pointInfo.setFovy_1(pointInfoRs.getInt("fovy_1"));
                pointInfo.setGasindexs(pointInfoRs.getString("gasindexs"));
                pointInfo.setGasnames(pointInfoRs.getString("gasnames"));
                pointInfo.setCons(pointInfoRs.getString("cons"));

                pointInfoList.add(pointInfo);
            }
            if (CollectionUtil.isNotEmpty(pointInfoList)) {
                // 使用Graphics绘制矩形
                Graphics g = image.getGraphics();

                for (PointInfo pointInfo : pointInfoList) {
                    List<String> indexList = Arrays.asList(pointInfo.getGasindexs().split("\\|"));
                    List<String> gasConsList = Arrays.asList(pointInfo.getCons().split(","));
                    for (int k = 0; k < indexList.size(); k++) {
                        String index = indexList.get(k);
                        // 绘制全部气体点位
                        if (gasIndexList.contains(index)) {
                            List<String> list = consMap.get(index);
                            if (list != null) {
                                list.add(gasConsList.get(k));
                                consMap.put(index, list);
                            } else {
                                consMap.put(index, Arrays.asList(gasConsList.get(k)));
                            }

                            String color = indexColorMap.get(index);
                            log.info("[{}]绘制的气体：index[{}],color[{}]", mode, index, color);

                            if (!StringUtils.isEmpty(color)) {
                                Color rgbColor = new Color(Integer.parseInt(color));
                                g.setColor(new Color(rgbColor.getBlue(), rgbColor.getGreen(), rgbColor.getRed()));//画笔颜⾊
                                if (mode.equals(VI)) {
                                    g.drawRect(pointInfo.getPointx_0() - pointInfo.getFovx_0() / 2, pointInfo.getPointy_0() - pointInfo.getFovy_0() / 2, pointInfo.getFovx_0(), pointInfo.getFovy_0());//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                                    g.fillRect(pointInfo.getPointx_0() - pointInfo.getFovx_0() / 2, pointInfo.getPointy_0() - pointInfo.getFovy_0() / 2, pointInfo.getFovx_0(), pointInfo.getFovy_0());
                                } else {
                                    g.drawRect(pointInfo.getPointx_1() - pointInfo.getFovx_1() / 2, pointInfo.getPointy_1() - pointInfo.getFovy_1() / 2, pointInfo.getFovx_1(), pointInfo.getFovy_1());//矩形框(原点x坐标，原点y坐标，矩形的长，矩形的宽)
                                    g.fillRect(pointInfo.getPointx_1() - pointInfo.getFovx_1() / 2, pointInfo.getPointy_1() - pointInfo.getFovy_1() / 2, pointInfo.getFovx_1(), pointInfo.getFovy_1());
                                }
                            }
                        }
                    }
                }
                g.dispose();
            }

            // 写到输出流
            ByteArrayOutputStream out = new ByteArrayOutputStream();//
            ImageIO.write(image, "jpeg", out);

            byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());


            // 完成后关闭
            // 关闭warningInfo的查询
            pointInfoRs.close();
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
        log.info("[{}]查询结束", mode);

        return byteArrayInputStream;
    }

    private List<History> getHistoryList(HistorySearchDTO historySearchDTO) {
        log.info("导出开始，查询历史信息");
        List<History> historyList = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取数据库连接
            conn = getConnection(historySearchDTO.getId());

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT id, scanid, scanmode, dtime, gasindexs, gasnames, gascolors, imagevi, imageir, firstdtime, lastdtime FROM warninginfor where 1=1 ");
            assembleCondition(historySearchDTO, sql);
            //排序
            sql.append("order by dtime asc ");

            // 执行查询
            log.info("实例化Statement对象...");
            stmt = conn.createStatement();
            log.info("执行sql: {}", sql.toString());
            ResultSet rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                History history = new History();
                // 通过字段检索
                history.setId(rs.getInt("id"));
                history.setScanId(rs.getInt("scanid"));
                history.setScanMode(rs.getInt("scanmode"));
                history.setDTime(rs.getTimestamp("dtime"));
                history.setGasIndexes(rs.getString("gasindexs"));
                history.setGasNames(rs.getString("gasnames"));
                history.setGasColors(rs.getString("gascolors"));
                history.setImageVI(rs.getBlob("imagevi"));
                history.setImageIR(rs.getBlob("imageir"));
                history.setFirstDTime(rs.getTimestamp("firstdtime"));
                history.setLastDTime(rs.getTimestamp("lastdtime"));

                historyList.add(history);
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            log.error(se.getMessage(), se);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

        return historyList;
    }

    public void assembleWord(HttpServletResponse response, HistorySearchDTO historySearchDTO) throws IOException, InvalidFormatException {
        List<History> historyList = getHistoryList(historySearchDTO);

        XWPFDocument doc = new XWPFDocument();

        // 标题
        XWPFParagraph paragraph11 = doc.createParagraph();
        // 对齐方式
        paragraph11.setAlignment(ParagraphAlignment.CENTER);
        // 段落末尾创建XWPFRun
        XWPFRun run11 = paragraph11.createRun();
        run11.setText("危化气体监测记录");
        run11.setFontSize(24);
        run11.setBold(true);

        XWPFParagraph paragraphNull = doc.createParagraph();
        // 对齐方式
        paragraphNull.setAlignment(ParagraphAlignment.LEFT);
        // 段落末尾创建XWPFRun
        XWPFRun runNull = paragraphNull.createRun();
        runNull.setText("");

        XWPFParagraph paragraph22 = doc.createParagraph();
        // 对齐方式
        paragraph22.setAlignment(ParagraphAlignment.LEFT);
        // 段落末尾创建XWPFRun
        XWPFRun run22 = paragraph22.createRun();
        run22.setText(DateUtil.format(historySearchDTO.getStartTime(), NORM_DATETIME_MINUTE_PATTERN) + " - " + DateUtil.format(historySearchDTO.getEndTime(), NORM_DATETIME_MINUTE_PATTERN) + " " +
                "监测结果");
        run22.setFontSize(12);
        run22.setBold(true);

        XWPFParagraph paragraphNull1 = doc.createParagraph();
        // 对齐方式
        paragraphNull1.setAlignment(ParagraphAlignment.LEFT);
        // 段落末尾创建XWPFRun
        XWPFRun runNull1 = paragraphNull1.createRun();
        runNull1.setText("");

        // 表头
        XWPFTable table = doc.createTable(4, 6);
        table.getRow(0).getCell(0).setText("设备地点");
        table.getRow(0).getCell(4).setText("设备编号");
        //table.getRow(0).getCell(5).setText("设备编号");
        table.getRow(1).getCell(0).setText("监测人员");
        table.getRow(2).getCell(0).setText("汇总日期");
        table.getRow(2).getCell(1).setText(DateUtil.format(new Date(), NORM_DATETIME_MINUTE_PATTERN));
        table.getRow(3).getCell(0).setText("序号");
        table.getRow(3).getCell(1).setText("监测时间");
        table.getRow(3).getCell(2).setText("气体名称");
        table.getRow(3).getCell(3).setText("浓度程长积（ppm.m）");
        table.getRow(3).getCell(4).setText("监测模式");
        table.getRow(3).getCell(5).setText("监测图片");

        Machine machine = machineMapper.selectById(historySearchDTO.getId());
        if (machine != null) {
            table.getRow(0).getCell(5).setText(machine.getMachineId());
        }

        if (CollectionUtil.isNotEmpty(historyList)) {
            int rowIndex = 0;

            for (int i = 0; i < historyList.size(); i++) {
                History history = historyList.get(i);

                //从warningInfo中得到气体与对应的颜色map
                Map<String, String> indexColorMap = new HashMap<>();
                List<String> gasIndexList = Arrays.asList(history.getGasIndexes().split("\\|"));
                List<String> gasColorList = Arrays.asList(history.getGasColors().split("\\|"));
                for (int index = 0; index < gasIndexList.size(); index++) {
                    indexColorMap.put(gasIndexList.get(index), gasColorList.get(index));
                }

                //从warningInfo中得到气体与对应的名称map
                Map<String, String> indexNameMap = new HashMap<>();
                List<String> gasNameList = Arrays.asList(history.getGasNames().split("\\|"));
                for (int index = 0; index < gasIndexList.size(); index++) {
                    indexNameMap.put(gasIndexList.get(index), gasNameList.get(index));
                }

                // 如果是扇扫模式，图形分开绘制
                int scanMode = history.getScanMode();
                if (scanMode == 3) {
                    for (String gasIndex : gasIndexList) {
                        table.createRow();

                        // 序号按自然数
                        table.getRow(4 + rowIndex).getCell(0).setText(String.valueOf(i + 1));
                        String time = DateUtil.format(history.getFirstDTime(), "yyyy-MM-dd HH:mm:ss") + "--" + DateUtil.format(history.getLastDTime(), "yyyy-MM-dd HH:mm:ss");
                        table.getRow(4 + rowIndex).getCell(1).setText(time);
                        table.getRow(4 + rowIndex).getCell(2).setText(history.getGasNames());
                        table.getRow(4 + rowIndex).getCell(4).setText(getScanMode(scanMode));
                        XWPFParagraph p1 = table.getRow(4 + rowIndex).getCell(5).addParagraph();
                        XWPFRun run = p1.createRun();

                        List<String> consList = new ArrayList<>();
                        InputStream inputStream = getImageInputStreamForMode3(history, historySearchDTO.getId(), String.valueOf(history.getId()), VI, gasIndex, indexColorMap, consList);
                        if (inputStream != null) {
                            run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_JPEG, "Generated", Units.toEMU(64), Units.toEMU(34));
                        }
                        String consText = "";
                        if (CollectionUtils.isNotEmpty(consList)) {
                            String s = consList.toString();
                            consText = s.substring(1, s.length() - 1);
                        }

                        if (gasIndexList.size() > 1) {
                            consText = indexNameMap.get(gasIndex) + ":" + consText;
                        }
                        table.getRow(4 + rowIndex).getCell(3).setText(consText);

                        rowIndex++;
                    }
                    // 如果扇扫模式下有多个气体，合并单元格
                    if (gasIndexList.size() > 1) {
                        TableTools.mergeCellsVertically(table, 0, 4 + rowIndex - gasIndexList.size(), 4 + rowIndex - 1);
                        TableTools.mergeCellsVertically(table, 1, 4 + rowIndex - gasIndexList.size(), 4 + rowIndex - 1);
                        TableTools.mergeCellsVertically(table, 2, 4 + rowIndex - gasIndexList.size(), 4 + rowIndex - 1);
                        TableTools.mergeCellsVertically(table, 4, 4 + rowIndex - gasIndexList.size(), 4 + rowIndex - 1);
                    }
                } else { // 如果不是扇扫模式
                    table.createRow();

                    // 序号按自然数
                    table.getRow(4 + rowIndex).getCell(0).setText(String.valueOf(i + 1));
                    String time = DateUtil.format(history.getFirstDTime(), "yyyy-MM-dd HH:mm:ss");
                    table.getRow(4 + rowIndex).getCell(1).setText(time);
                    table.getRow(4 + rowIndex).getCell(2).setText(history.getGasNames());
                    table.getRow(4 + rowIndex).getCell(4).setText(getScanMode(scanMode));
                    XWPFParagraph p1 = table.getRow(4 + rowIndex).getCell(5).addParagraph();
                    XWPFRun run = p1.createRun();

                    Map<String, List<String>> consMap = new HashMap<>();
                    InputStream inputStream = getImageInputStreamForModeOther(history, historySearchDTO.getId(), String.valueOf(history.getId()), VI, gasIndexList, indexColorMap, consMap);
                    if (inputStream != null) {
                        run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_JPEG, "Generated", Units.toEMU(64), Units.toEMU(34));
                    }

                    String consText = "";
                    if (gasIndexList.size() > 1) {
                        for (String index : gasIndexList) {
                            List<String> consList = consMap.get(index);
                            String s = consList.toString();
                            String cons = s.substring(1, s.length() - 1);
                            consText += indexNameMap.get(index) + ":" + cons + ";";
                        }
                        consText = consText.substring(0, consText.length() - 1);
                    } else {
                        List<String> consList1 = consMap.get(gasIndexList.get(0));
                        String s1 = consList1.toString();
                        String cons1 = s1.substring(1, s1.length() - 1);
                        consText = cons1;
                    }
                    table.getRow(4 + rowIndex).getCell(3).setText(consText);

                    rowIndex++;
                }

            }
        }

        // 单元格格式
        TableTools.widthTable(table, MiniTableRenderData.WIDTH_A4_FULL, 10);
        // 表头合并
        TableTools.mergeCellsHorizonal(table, 0, 1, 3);
        TableTools.mergeCellsHorizonal(table, 1, 1, 5);
        TableTools.mergeCellsHorizonal(table, 2, 1, 5);

        response.setContentType("application/msword");
        doc.write(response.getOutputStream());
    }

    private String getScanMode(int mode) {
        String modeName = "";
        if (mode == 2) {
            modeName = "定点模式";
        }
        if (mode == 3) {
            modeName = "扇扫模式";
        }
        if (mode == 4) {
            modeName = "警戒模式";
        }
        return modeName;
    }

}