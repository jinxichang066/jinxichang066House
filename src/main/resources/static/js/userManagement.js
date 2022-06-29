const roleKeyOptions = [{
    value: '系统管理员',
    label: '系统管理员',
    disabled: true
}, {
    value: '厂家管理员',
    label: '厂家管理员',
    disabled: true
}, {
    value: '系统用户',
    label: '系统用户'
}, {
    value: '游客用户',
    label: '游客用户'
}];

const userDefaultPassword = "123456";

// 此处一定要和后台Role.java中的枚举类定义一致
// 跟上边的roleKeyOptions也一样
const roleKeyList = [
    "系统管理员",
    "厂家管理员",
    "系统用户",
    "游客用户"
]