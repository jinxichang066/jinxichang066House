package com.heima.estatemanagement.common;

/**
 * @Auth: zhuan
 * @Desc: 返回结果消息提示常量类
 */
public class MessageConstant {
    //---------------------------小区（Community）操作消息提示信息---------------------------------------
    public static final String COMMUNITY_SEARCH_SUCCESS = "查询小区列表信息成功！";
    public static final String COMMUNITY_ADD_SUCCESS = "新增小区信息成功！";
    public static final String COMMUNITY_UPDATE_SUCCESS = "修改小区信息成功！";
    public static final String COMMUNITY_DELETE_SUCCESS = "删除小区信息成功！";
    public static final String COMMUNITY_PIC_UPLOAD_SUCCESS = "小区缩略图上传成功！";
    public static final String COMMUNITY_PIC_DEL_SUCCESS = "小区缩略图删除成功！";
    public static final String COMMUNITY_FIND_BY_ID_SUCCESS = "根据主键获取小区对象成功！";
    public static final String COMMUNITY_UPDATE_STATUS_SUCCESS = "小区状态信息更新成功！";
    //---------------------------楼栋（Community）操作消息提示信息---------------------------------------
    //---------------------------系统提示信息----------------------------------------------------------
    public static final String SYSTEM_BUSY = "系统繁忙，请求稍后重试！";
    //---------------------------文件上传提示信息-------------------------------------------------------
    public static final String NO_FILE_SELECTED = "未选择上传的文件,请求选择后上传!";
    public static final String NO_WRITE_PERMISSION = "上传目录没有写权限!";
    public static final String INCORRECT_DIRECTORY_NAME = "目录名不正确!";
    public static final String SIZE_EXCEEDS__LIMIT = "上传文件大小超过限制!";
    public static final String FILE_TYPE_ERROR = "文件类型错误，只允许上传JPG/PNG/JPEG/GIF等图片类型的文件!";

    //---------------------------设备（Machine）操作消息提示信息---------------------------------------
    public static final String MACHINE_SEARCH_SUCCESS = "查询设备列表信息成功！";
    public static final String MACHINE_FIND_BY_ID_SUCCESS = "获取设备信息成功！";
    public static final String MACHINE_ADD_SUCCESS = "新增设备信息成功！";
    public static final String MACHINE_DUP_MACHINE_ID = "已存在相同设备ID，请勿重复添加";
    public static final String MACHINE_DUP_MACHINE_ID_UPDATE = "已存在相同设备ID，请检查后再修改";
    public static final String MACHINE_UPDATE_SUCCESS = "修改设备信息成功！";
    public static final String MACHINE_DELETE_SUCCESS = "删除设备信息成功！";

    //---------------------------设备（Machine）操作消息提示信息---------------------------------------
    public static final String HISTORY_SEARCH_SUCCESS = "查询历史记录信息成功！";
    public static final String HISTORY_SEARCH_FAIL_DB_CONNECT_FAIL = "查询历史记录失败！设备端数据库服务访问失败";
    public static final String HISTORY_SEARCH_FAIL_ERROR = "查询历史记录失败！服务内部错误";



}
