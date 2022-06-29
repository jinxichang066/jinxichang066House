package cn.yk.gasMonitor.common;


/**
 * 消息常量
 *
 * @author Jin Xichang
 * @date 2022/4/21
 **/
public class MessageConstant {
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
    public static final String MACHINE_URL_NOT_MATCH = "设备URL必须为IP地址格式！";

    //---------------------------历史记录（History）操作消息提示信息---------------------------------------
    public static final String HISTORY_SEARCH_SUCCESS = "查询历史记录信息成功！";
    public static final String HISTORY_SEARCH_FAIL_DB_CONNECT_FAIL = "查询历史记录失败！设备端数据库服务访问失败";
    public static final String HISTORY_SEARCH_FAIL_ERROR = "查询历史记录失败！服务内部错误";

    //---------------------------气体（GasSpec）操作消息提示信息---------------------------------------
    public static final String GAS_SPEC_NAME_LIST_SUCCESS = "查询气体名称列表信息成功！";

    //---------------------------登录操作消息提示信息---------------------------------------
    public static final String LOGIN_EXPIRED = "登录信息失效，请重新登陆！";
    public static final String LOGIN_SUCCESS = "登录成功！";
    public static final String LOGIN_USER_NOT_EXIST = "用户不存在！";
    public static final String LOGIN_PASSWORD_ERROR = "密码错误！";

    //---------------------------用户管理消息提示信息---------------------------------------
    public static final String USER_SEARCH_SUCCESS = "查询用户列表信息成功！";
    public static final String USER_FIND_BY_ID_SUCCESS = "获取用户信息成功！";
    public static final String USER_ADD_SUCCESS = "新增用户信息成功！";
    public static final String USER_DUP_USER_NAME = "已存在相同用户名，请勿重复添加";
    public static final String USER_DUP_USER_NAME_UPDATE = "已存在相同用户名，请检查后再修改";
    public static final String USER_UPDATE_SUCCESS = "修改用户信息成功！";
    public static final String USER_DELETE_SUCCESS = "删除用户信息成功！";

}
