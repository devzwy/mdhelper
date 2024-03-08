# mdhelper
明道云Api封装，kotlin环境测试。

## [单元测试](https://github.com/devzwy/mdhelper/tree/main/src/test/kotlin/Test.kt)
![单元测试](https://github.com/devzwy/mdhelper/blob/main/imgs/test.png)

## 依赖
### [latest version](https://central.sonatype.com/artifact/io.github.devzwy/mdhelper)
- Maven
```
<dependency>
  <groupId>io.github.devzwy</groupId>
  <artifactId>mdhelper</artifactId>
  <version>3.0.5</version>
</dependency>
```

- Gradle
```
implementation("io.github.devzwy:mdhelper:3.0.5")
```

## 开始使用
### 实例构造
```kotlin
MDHelper.getInstance()
```
### 添加/移除一个baseurl
```
MDHelper.getInstance().addBaseUrl(key1, url1)
MDHelper.getInstance().removeBaseUrlByKey(key1)
```
### 添加移除应用配置
```
MDHelper.getInstance().addAppConfig(key1, appKey1, sign1)
MDHelper.getInstance().removeAppByConfigKey(key1)
```