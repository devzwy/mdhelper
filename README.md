# mdhelper

明道云Api封装，kotlin环境测试。

## [单元测试](https://github.com/devzwy/mdhelper/tree/main/src/test/kotlin/Test.kt)

![单元测试](https://github.com/devzwy/mdhelper/blob/main/imgs/test.png)

## 依赖

### [latest version](https://central.sonatype.com/artifact/io.github.devzwy/mdhelper)

### Android版本依赖参考

> 解决工具类在安卓端解析数据时引起的闪退问题。引入下面的依赖即可，不需要再引入Java/Kotlin依赖部分。

```
implementation 'com.alibaba.fastjson2:fastjson2:2.0.47.android5'

//明道云工具类
implementation('io.github.devzwy:mdhelper:3.1.3') {
        exclude group: 'com.alibaba.fastjson2', module: 'fastjson2'
}

```

### Java/kotlin 依赖

- Maven

```
<dependency>
  <groupId>io.github.devzwy</groupId>
  <artifactId>mdhelper</artifactId>
  <version>3.1.3</version>
</dependency>
```

- Gradle

```
implementation("io.github.devzwy:mdhelper:3.1.3")
implementation("com.alibaba.fastjson2:fastjson2:2.0.47")
```

## Api列表

### 实例构造

```
MDHelper.getInstance()
```

### 添加BaseUrl

> 针对多明道云配置不同的BaseUrl，如果全局仅有一个BaseUrl时，后续对明道应用的操作可以不传入该值，默认取第一个配置。

```
/**
 * 添加BaseUrl,内部判断所添加的url值，存在时将跳过添加，跳过添加时会有错误日志输出。
 * [key] 后续操作需要携带，用于找到对应的url
 * [url] url值
 */
fun addBaseUrl(key: String, url: String)
```

### 获取baseurl

```
/**
 * 获取baseurl,不填写key时会取第一个添加的baseurl,如果没有添加过baseurl时可能会抛出异常
 * [key] 调用[addBaseUrl]时的key
 */
fun getBaseUrl(key: String? = baseUrlHashMap.entries.firstOrNull()!!.key): String
```

### 移除baseurl

```
/**
 * 根据key移除baseurl
 * [key] 调用[addBaseUrl]时的key
 */
fun removeBaseUrlByKey(key: String)
```

### 移除全部baseurl

```
fun removeAllBaseUrls()
```

### 获取全部配置的baseurl

```
fun getAllBaseUrls():ConcurrentSkipListMap
```

### 添加应用配置

```
/**
 * 添加应用配置
 * [configKey] 配置的key，后续操作改应用时必填,
 * [appKey] 明道云的appKey
 * [sign] 明道云应用的Sign
 */
fun addAppConfig(configKey: String, appKey: String, sign: String)
```

### 删除应用配置

```
fun removeAppByConfigKey(configKey: String)
```

### 获取应用配置

```
/**
 * 获取应用配置,不填写key时会取第一个添加的应用配置,如果没有添加过应用时时可能会抛出异常
 * [appConfigKey] 调用[addApp]时的key
 */
fun getAppConfig(appConfigKey: String?): AppConfig
```

### 移除全部应用配置

```
fun removeAllAppConfigs()
```

### 获取全部应用配置

```
/**
 * 获取全部应用配置
 */
fun getAllAppConfigs():ConcurrentSkipListMap
```

### 获取列表

```
/**
 * 获取列表 传入接收单个row的实体，一些公用的参数(明道基础字段)封装在了[io.github.devzwy.mdhelper.data.Row]类，如有需要可以自行继承
 * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
 * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
 * [tableId] 操作的表ID，可以为别名或者明道生成的ID
 * [filter] 过滤条件，使用[MdFilterControl.Builder]构造多个
 * [pageSize] 行数
 * [pageIndex] 页码
 * [viewId] 视图ID
 * [sortId] 排序字段ID
 * [isAsc] 是否升序
 * [notGetTotal] 是否不统计总行数以提高性能(默认: false)
 * [useControlId] 是否只返回controlId(默认: false)
 * [clazz] 最终列表的每个实体的接收对象
 * @return 过滤后的数据[RowBaseResult]
 */
fun <T> getRows(
    baseUrlKey: String? = null,
    appConfigKey: String? = null,
    tableId: String,
    filter: MdFilterControl,
    pageSize: Int? = null,
    pageIndex: Int? = null,
    viewId: String? = null,
    sortId: String? = null,
    isAsc: Boolean? = null,
    notGetTotal: Boolean? = null,
    useControlId: Boolean? = null,
    clazz: Class<T>
): RowBaseResult<T>
```

### 删除行记录

```
/**
 * 删除行记录
 * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
 * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
 * [tableId] 操作的表ID，可以为别名或者明道生成的ID
 * [rowId] 删除行的Id
 * [triggerWorkflow] 是否触发工作流(默认: true)
 * @return 删除成功返回true 否则返回false
 */
fun deleteRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, triggerWorkflow: Boolean = true): Boolean
```

### 插入单行记录

```
/**
* 插入单行记录
* [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
* [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
* [tableId] 操作的表ID，可以为别名或者明道生成的ID
* [data] 写入的数据列，使用[MdDataControl.Builder]构造
* [triggerWorkflow] 是否触发工作流(默认: true)
* @return 写入成功后回传写入的行ID
*/
fun insertRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, data: MdDataControl, triggerWorkflow: Boolean = true): String
```

### 插入多行记录

```
/**
 * 插入多行记录，最大1000行
 * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
 * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
 * [tableId] 操作的表ID，可以为别名或者明道生成的ID
 * [dataList] 写入的数据列，使用[MdDataControl.Builder]构造多个
 * [triggerWorkflow] 是否触发工作流(默认: true)
 * @return 写入成功后回传写入成功的总行数
 */
fun insertRows(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, dataList: List<MdDataControl>, triggerWorkflow: Boolean = true): Int
```

### 编辑行记录

```
/**
 * 编辑行记录
 * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
 * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
 * [tableId] 操作的表ID，可以为别名或者明道生成的ID
 * [rowId] 行记录ID
 * [data] 更新的数据列，使用[MdDataControl.Builder]构造
 * [triggerWorkflow] 是否触发工作流(默认: true)
 * @return 编辑成功返回true，否则返回false
 */
fun updateRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, data: MdDataControl, triggerWorkflow: Boolean = true): Boolean
```

### 获取行记录详情

```
/**
 * 获取行记录详情
 * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
 * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
 * [tableId] 操作的表ID，可以为别名或者明道生成的ID
 * [rowId] 行记录ID
 * @return 行记录数据JSON
 */
fun <T> getRow(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String, rowId: String, clazz: Class<T>): T 
```

### 获取表结构信息

```
/**
 * 获取表结构信息
 * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
 * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
 * [tableId] 操作的表ID，可以为别名或者明道生成的ID
 * @return 表结构信息JSOn
 */
fun getTableInfo(baseUrlKey: String? = null, appConfigKey: String? = null, tableId: String): MdTableInfo
```

### 获取应用数据

```
/**
 * 获取应用数据
 * [baseUrlKey] baseUrl配置的Key，为空时取第一个添加的BaseUrl，如果未添加过BaseUrl时抛出异常
 * [appConfigKey] 应用的配置Key，为空时取第一个添加的应用配置，如果未添加过应用配置则抛出异常
 * @return 返回应用信息
 */
fun getAppInfo(baseUrlKey: String? = null, appConfigKey: String? = null): MdAppInfo
```

## 一些实体类

```

open class RowBaseResult<T> {
    var rows: List<T> = arrayListOf()
    var total: Int = 0
}

open class Row {
    var rowid: String = ""
    var ctime: String = ""
    var caid: UserInfo? = null
    var ownerid: UserInfo? = null
    var utime: String = ""
    var autoid: Int = 0
    var allowdelete: Boolean = false
    var controlpermissions: String = ""
}

open class UserInfo {
    var accountId: String = ""
    var fullname: String = ""
    var avatar: String = ""
    var status: Int = 0
}

open class MdTableInfo {
    var worksheetId: String = ""
    var name: String = ""
    var views: List<MdView> = arrayListOf()
    var controls: List<MdControl> = arrayListOf()
}

open class MdView{
    var viewId:String = ""
    var name:String = ""
}

open class MdControl{
    var controlId:String = ""
    var controlName:String = ""
    var type:Int = -1
    var attribute:Int = -1
    var row:Int = -1
    var col:Int = -1
    var userPermission:Int = -1
    var size:Int = -1
    var disabled:Boolean = false
    var checked:Boolean = false
    var controlPermissions:String = ""
    var alias:String = ""
}

open class MdAppInfo{
    var projectId:String = ""
    var appId:String = ""
    var name:String = ""
    var iconUrl:String = ""
    var color:String = ""
    var desc:String = ""
    var sections:List<MdSection> = arrayListOf()

}

open class MdSection{
    var sectionId:String = ""
    var name:String = ""
    var items:List<MdSectionItem> = arrayListOf()
}

open class MdSectionItem{
    var id:String = ""
    var name:String = ""
    var type:Int = -1
    var iconUrl:String = ""
    var status:Int = -1
    var alias:String = ""
}
```

## 过滤器

```
MdFilterControl.Build().xxxx.build()
```

### 添加过滤字段

```
/**
 * 添加过滤字段
 * [controlId] 字段
 * [value] 值
 * [filterType] 过滤类型 使用[FilterTypeEnum]构造
 * [dataType] 控件类型 使用[DataTypeEnum]构造，默认[DataTypeEnum.TEXT_SINGLE_LINE]文本类型
 * [spliceType] 拼接方式 使用[SpliceTypeEnum]构造，默认[SpliceTypeEnum.AND]
 */
fun addFilter(controlId: String, value: String, filterType: FilterTypeEnum, dataType: DataTypeEnum = DataTypeEnum.TEXT_SINGLE_LINE, spliceType: SpliceTypeEnum = SpliceTypeEnum.AND): Builder
```

## 数据添加器

```
MdDataControl.Build().xxxx.build()
```

### 添加普通的字段

```
/**
 * 添加普通的字段
 * [controlId] 键
 * [data] 值
 */
fun addControl(controlId: String, data: String?): Builder
```

### 添加附件字段

```
/**
 * 添加附件字段
 * [controlId] 键
 * [dataList] 值,可以为多个url或多个base64，例如：["url1","url2"...]，或["base641","base642"...]
 * [fileNames] 当[dataList]的值为Base64时这里的文件名称列表长度必须和[dataList]的长度一致，填写文件的后缀，例如["1.jpg","2.pdf"...]
 * [dataType] 值的类型 使用[MuliDataType]构造，默认[MuliDataType.URL]
 * [editType] 当前字段的编辑类型，使用[MuliEditType]构造，默认[MuliEditType.REPLACE]
 */
fun addMulti(
    controlId: String, dataList: List<String>?, fileNames: List<String>? = null, dataType: MuliDataType = MuliDataType.URL,
    editType: MuliEditType = MuliEditType.REPLACE
): Builder
```

### 添加选项卡字段

```
/**
 * 添加选项卡字段
 * [controlId] 键
 * [data] 值
 * [dataType] 使用[OptionDataType]构造，[OptionDataType.NOT_ADD]不增加选项，[OptionDataType.ADD]允许增加选项（默认为[OptionDataType.NOT_ADD]，为[OptionDataType.NOT_ADD]时匹配不到已有选项时传入空，为[OptionDataType.ADD]时，匹配不到时会创建新选项并写入）
 */
fun addOption(controlId: String, data: String?, dataType: OptionDataType = OptionDataType.NOT_ADD): Builder
```

## 一些常量

### 数据类型

```
/**
 * 数据类型
 */
enum class DataTypeEnum(val value: Int, val description: String, val controlType: String) {
    TEXT_SINGLE_LINE(2, "文本", "单行、多行"),
    PHONE_MOBILE(3, "电话", "手机"), PHONE_LANDLINE(
        4, "电话", "座机"
    ),
    EMAIL(5, "邮箱", ""),
    NUMERIC(6, "数值", ""), ID_CARD(7, "证件", ""),
    AMOUNT(8, "金额", ""),
    RADIO_TILE(
        9, "单选", "平铺"
    ),
    MULTI_SELECT(10, "多选", ""), RADIO_DROPDOWN(11, "单选", "下拉"), ATTACHMENT(
        14, "附件", ""
    ),
    DATE_YEAR_MONTH_DAY(15, "日期", "年-月-日"), DATE_YEAR_MONTH_DAY_HOUR_MINUTE(
        16, "日期", "年-月-日 时:分"
    ),
    REGION_PROVINCE(19, "地区", "省"), FREE_CONNECTION(21, "自由连接", ""), SEGMENT(
        22, "分段", ""
    ),
    REGION_PROVINCE_CITY(23, "地区", "省/市"), REGION_PROVINCE_CITY_COUNTY(
        24, "地区", "省/市/县"
    ),
    AMOUNT_UPPERCASE(25, "大写金额", ""), MEMBER(26, "成员", ""), DEPARTMENT(27, "部门", ""), LEVEL(
        28, "等级", ""
    ),
    RELATED_RECORD(29, "关联记录", ""), OTHER_TABLE_FIELD(30, "他表字段", ""), FORMULA_NUMERIC(
        31, "公式", "数字"
    ),
    TEXT_COMBINATION(32, "文本组合", ""), AUTO_NUMBER(33, "自动编号", ""), SUB_TABLE(
        34, "子表", ""
    ),
    CASCADE_SELECT(35, "级联选择", ""), CHECKBOX(36, "检查框", ""), SUMMARY(37, "汇总", ""), FORMULA_DATE(
        38, "公式", "日期"
    ),
    LOCATION(40, "定位", ""), RICH_TEXT(41, "富文本", ""), SIGNATURE(42, "签名", ""), EMBEDDED(
        45, "嵌入", ""
    ),
    NOTE(10010, "备注", "");

    companion object {
        fun fromCode(value: Int) = values().find { it.value == value }!!
    }
}
```

### 日期过滤类型

```
/**
 * 日期过滤类型
 */
enum class DateRangeEnum(val value: Int, val enumChar: String, val description: String) {
    DEFAULT(0, "Default", ""), TODAY(1, "Today", "今天"), YESTERDAY(2, "Yesterday", "昨天"), TOMORROW(
        3, "Tomorrow", "明天"
    ),
    THIS_WEEK(4, "ThisWeek", "本周"), LAST_WEEK(5, "LastWeek", "上周"), NEXT_WEEK(
        6, "NextWeek", "下周"
    ),
    THIS_MONTH(7, "ThisMonth", "本月"), LAST_MONTH(8, "LastMonth", "上月"), NEXT_MONTH(
        9, "NextMonth", "下月"
    ),
    LAST_ENUM(10, "LastEnum", "上.."), NEXT_ENUM(11, "NextEnum", "下.."), THIS_QUARTER(
        12, "ThisQuarter", "本季度"
    ),
    LAST_QUARTER(13, "LastQuarter", "上季度"), NEXT_QUARTER(14, "NextQuarter", "下季度"), THIS_YEAR(
        15, "ThisYear", "本年"
    ),
    LAST_YEAR(16, "LastYear", "去年"), NEXT_YEAR(17, "NextYear", "明年"), CUSTOMIZE(
        18, "Customize", "自定义"
    ),
    LAST_7_DAY(21, "Last7Day", "过去7天"), LAST_14_DAY(22, "Last14Day", "过去14天"), LAST_30_DAY(
        23, "Last30Day", "过去30天"
    ),
    NEXT_7_DAY(31, "Next7Day", "未来7天"), NEXT_14_DAY(32, "Next14Day", "未来14天"), NEXT_33_DAY(
        33, "Next33Day", "未来33天"
    );

    companion object {
        fun fromCode(value: Int) = values().find { it.value == value }!!
    }
}
```

### 回传状态码

```
/**
 * 回传状态码
 */
enum class ErrorCodeEnum(val code: Int, val description: String) {
    FAILURE(0, "失败"), SUCCESS(1, "成功"), MISSING_PARAMETER(10001, "缺少参数"), INVALID_PARAMETER_VALUE(
        10002, "参数值错误"
    ),
    NO_PERMISSION(10005, "数据操作无权限"), DATA_NOT_EXIST(10007, "数据不存在"), MISSING_TOKEN(
        10101, "请求令牌不存在"
    ),
    INVALID_SIGNATURE(10102, "签名不合法"), DATA_OPERATION_EXCEPTION(99999, "数据操作异常");

    companion object {
        fun fromCode(code: Int): ErrorCodeEnum {
            return values().find { it.code == code }!!
        }
    }
}
```

### 筛选方式

```
/**
 * 筛选方式
 */
enum class FilterTypeEnum(val value: Int, val enumChar: String, val description: String) {
    DEFAULT(0, "Default", ""), LIKE(1, "Like", "包含"), EQ(2, "Eq", "是（等于）"), START(3, "Start", "开头为"), END(
        4, "End", "结尾为"
    ),
    NCONTAIN(5, "NContain", "不包含"), NE(6, "Ne", "不是（不等于）"), ISNULL(7, "IsNull", "为空"), HASVALUE(
        8, "HasValue", "不为空"
    ),
    BETWEEN(11, "Between", "在范围内"), NBETWEEN(12, "NBetween", "不在范围内"), GT(13, "Gt", ">"), GTE(
        14, "Gte", ">="
    ),
    LT(15, "Lt", "<"), LTE(16, "Lte", "<="), DATE_ENUM(17, "DateEnum", "日期是"), NDATE_ENUM(
        18, "NDateEnum", "日期不是"
    ),
    MYSELF(21, "MySelf", "我拥有的"), UNREAD(22, "UnRead", "未读"), SUB(23, "Sub", "下属"), RCEQ(
        24, "RCEq", "关联控件是"
    ),
    RCNE(25, "RCNe", "关联控件不是"), ARREQ(26, "ArrEq", "数组等于"), ARRNE(
        27, "ArrNe", "数组不等于"
    ),
    DATE_BETWEEN(31, "DateBetween", "在范围内"), DATE_NBETWEEN(32, "DateNBetween", "不在范围内"), DATE_GT(
        33, "DateGt", ">"
    ),
    DATE_GTE(34, "DateGte", ">="), DATE_LT(35, "DateLt", "<"), DATE_LTE(36, "DateLte", "<="), NORMAL_USER(
        41, "NormalUser", "常规用户"
    ),
    PORTAL_USER(42, "PortalUser", "外部门户用户封装");

    companion object {
        fun fromCode(value: Int) = values().find { it.value == value }!!
    }
}

```

### 附件的类型

```
/**
 * 附件的类型
 */
enum class MuliDataType private constructor(val type:Int) {
    URL(1),
    BASE64(2)
}
```

### 附件的操作类型

```
/**
 * 附件的操作类型
 */
enum class MuliEditType private constructor(val type:Int) {
    REPLACE(0),
    ADD(1)
}
```

### 选项卡的操作类型

```
/**
 * 选项卡的操作类型
 */
enum class OptionDataType private constructor(val type:Int) {
    NOT_ADD(1),
    ADD(2)
}
```

### 拼接方式

```
/**
 * 拼接方式
 */
enum class SpliceTypeEnum(val value: Int, val description: String) {
    AND(1, "AND"), OR(2, "OR");
}
```