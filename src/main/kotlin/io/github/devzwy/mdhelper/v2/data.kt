package io.github.devzwy.mdhelper.v2

data class Config(
    /**
     * 读取应用信息
     */
    var appInfoUrl: String = "/api/v1/open/app/get",
    /**
     * 增加行记录
     */
    var addRowUrl: String = "/api/v2/open/worksheet/addRow",
    /**
     * 批量增加行记录
     */
    var addRowsUrl: String = "/api/v2/open/worksheet/addRows",
    /**
     * 表结构信息
     */
    var tableInfoUrl: String = "/api/v2/open/worksheet/getWorksheetInfo",
    /**
     * 编辑行记录
     */
    var editRowUrl: String = "/api/v2/open/worksheet/editRow",

    /**
     * 获取行记录详情
     */
    var getRowDetailUrl: String = "/api/v2/open/worksheet/getRowByIdPost",

    /**
     * 删除行
     */
    var deleteRowUrl: String = "/api/v2/open/worksheet/deleteRow",
    /**
     * 获取多行记录
     */
    var getRowsUrl: String = "/api/v2/open/worksheet/getFilterRows",

    /**
     * 查询应用列表
     */
    var appListUrl: String = "/v1/open/app/getByProject",

    /**
     * 获取单个应用鉴权的数据
     */
    var appAuthUrl: String = "/v1/open/app/getAppAuthorize",

    /**
     * 网络请求超时时间，单位ms
     */
    var httpTimeout: Int = 5000,

    /**
     * 日志打印开关
     */
    var debugEnabled: Boolean = false,

    /**
     * 移除请求路径前面的/api开头内容，兼容共有云明道,私有化部署的明道请求api前缀是/api，但是共有云不带这个前缀。调用共有云时这里传true，否则接口调用会报错
     * 其中 [appListUrl] [appAuthUrl] 这两个api不受此配置限制
     */
    var removePathUrlApiHead: Boolean = true
)

// 定义统一的返回包装类
data class ApiResponse<T>(
    val success: Boolean,
    val error_code: Int,
    val error_msg: String = "",
    val data: T?
)

data class AppSignData(
    /**
     * 组织编号
     */
    val projectId: String,
    /**
     * 应用Id
     */
    val appId: String,
    /**
     * appKey
     */
    val appKey: String,
    /**
     * 签名
     */
    val sign: String,
    /**
     * 1:全部、2:只读
     */
    val type: Int
)
