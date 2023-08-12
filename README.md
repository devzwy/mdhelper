# mdhelper
明道云Api封装



## 依赖
### [latest version](https://central.sonatype.com/artifact/io.github.devzwy/mdhelper)
- Maven
```
<dependency>
  <groupId>io.github.devzwy</groupId>
  <artifactId>mdhelper</artifactId>
  <version>2.1.5</version>
</dependency>
```

- Gradle
```
implementation("io.github.devzwy:mdhelper:2.1.5")
```

## [单元测试](https://github.com/devzwy/mdhelper/tree/main/src/test/kotlin/Test.kt)
![单元测试](https://github.com/devzwy/mdhelper/imgs/test.png)
## 开始使用
### 实例构造
```kotlin
MDHelper.Builder()
    //apiUrl
    .baseUrl(BASE_URL)
    //日志打印器 为空关闭日志输出
    .setLogger(log)
    //添加操作的应用
    .addConfig(APP_NAME, APP_KEY, APP_SIGN)
    .addConfig(APP_NAME2, APP_KEY2, APP_SIGN2)
    //网络超时 ms
    .httpTimeOut(5 * 1000)
    .build(false)
```
>__以下所有Api调用参数appName不传递时默认取最后一个配置，如果只有一个应用时可以不用传__
### 查询同名称的应用是否已配置
```kotlin
/**
 * 查询同名称的应用是否已配置
 * [appName] 应用名称
 */
fun appExists(appName: Any)
```

### 临时添加一个应用配置
```kotlin
/**
 * 临时添加一个操作应用数据 重复的应用名称不会被添加
 * [appName] 应用名称
 * [appKey] AppKey
 * [sign] Sign
 */
fun addConfig(appName: Any, appKey: String, sign: String)
```

### 根据应用名称修改原有配置
```kotlin
/**
 * 根据应用名称修改原有配置，找不到时不做处理
 * [appName] 应用名称
 * [appKey] 新的AppKey
 * [sign] 新的Sign
 */
fun updateConfig(appName: Any, appKey: String, sign: String)
```

### 根据应用名称删除配置
```kotlin
/**
 * 根据应用名称删除配置，找不到时不处理
 * [appName] 应用名称
 */
fun delConfig(appName: Any)
```

### 获取应用信息
```kotlin
/**
 * 获取应用信息
 * 查询失败时返回null
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * @return 成功时返回应用信息，否则返回空。
 */
fun getAppInfo(appName: Any? = null): AppInfo?
```

### 新建工作表
```kotlin
/**
 * 新建工作表
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [tableName] 工作表的名称，例如：用户表
 * [tableAlias] 工作表的别名，默认为空，例如：yhb
 * [filed] 表字段，可传入多个。注意构造时内部的type必须使用[io.github.devzwy.mdhelper.data.DataType]类进行构造，否则会出现找不到类型的错误
 * @return 创建成功时返回工作表的ID，否则返回空
 */
fun createTable(appName: Any? = null, tableName: String, tableAlias: String? = null, vararg filed: CreateTableData)
```

### 获取工作表结构信息
```kotlin
/**
 * 获取工作表结构信息
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [worksheetId] 获取的工作表的ID
 * @return 表结构数据 [io.github.devzwy.mdhelper.data.MDTableInfo]
 */
fun getTableInfo(appName: Any? = null, worksheetId: String): MDTableInfo?
```

### 获取列表
```kotlin
/**
 * 获取列表
 * [R] MDRowData<最终接收实体(可继承自[io.github.devzwy.mdhelper.data.MDRow]以获取父类字段)>
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [worksheetId] 工作表id
 * [pageSize] 分页数据 为空会拉取最大1000数据
 * [pageIndex] 分页数据 为空会拉取最大1000数据
 * [filter] 筛选配置 使用[io.github.devzwy.mdhelper.data.FilterBean.Builder]进行构造 为空时不筛选
 * @return 查询结果列表
 */
internal inline fun <reified R> getData(appName: Any? = null, worksheetId: String, pageSize: Int? = null, pageIndex: Int? = null, filter: FilterData? = null): R?
```

### 新建行记录
```kotlin
/**
 * 新建行记录
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [worksheetId] 工作表id
 * [triggerWorkflow] 是否触发工作流
 * [data] 写入的列数据
 * @return 成功时返回写入记录的行ID 否则为空
 */
fun insertRow(appName: Any? = null, worksheetId: String, triggerWorkflow: Boolean? = null, data: HashMap<String, Any?>): String?
```

### 获取行记录详情
```kotlin
/**
 * 获取行记录详情
 * [R] 最终接收实体(可继承自[io.github.devzwy.mdhelper.data.MDRow]以获取父类字段)
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [worksheetId] 工作表id
 * [rowId] 行记录ID
 * @return
 */
internal inline fun <reified R> getRow(appName: Any? = null, worksheetId: String, rowId: String): R?
```

### 更新行记录
```kotlin
/**
 * 更新行记录
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [worksheetId] 工作表id
 * [triggerWorkflow] 是否触发工作流
 * [data] 更新的列数据
 * [rowId] 更新的行记录ID
 * @return 成功时返回true，否则返回null
 */
fun updateRow(appName: Any? = null, worksheetId: String, rowId: String, triggerWorkflow: Boolean? = null, data: HashMap<String, Any?>): Boolean?
```

### 删除行记录
```kotlin
/**
 * 删除行记录
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [worksheetId] 工作表id
 * [rowId] 行记录ID
 * [triggerWorkflow] 是否触发工作流
 * @return 成功时返回true，否则返回null
 */
fun delRow(appName: Any? = null, worksheetId: String, rowId: String, triggerWorkflow: Boolean? = null): Boolean?
```

### 获取工作表总行数
```kotlin
/**
 * 获取工作表总行数
 * [appName] 应用名称，由添加配置时设定，为空时取最后一个添加到工具类的配置
 * [worksheetId] 工作表id
 * [filter] 筛选配置 使用[io.github.devzwy.mdhelper.data.FilterBean.Builder]进行构造 为空时不筛选
 * @return 成功时返回行数，否则返回null
 */
fun getWorksheetCount(appName: Any? = null, worksheetId: String, filter: FilterData? = null): Int?
```



