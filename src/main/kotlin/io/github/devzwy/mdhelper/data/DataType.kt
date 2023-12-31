package io.github.devzwy.mdhelper.data

//数据类型枚举
enum class DataType(val value: Int) {

    //文本
    TEXT(2),

    //邮箱
    EMAIL(5),

    //数值
    NUMBER(6),

    //金额
    AMOUNT(8),

    //金额
    OPTION(9),

    //多选
    OPTION_ML(10),

    //日期
    DATE(15),

    //日期时间
    DATETIME(16),

    //大写金额
    AMOUNT_UP(25),

    //关联记录
    ASS_REC(29),

    //他表字段
    OTHER_TABLE_FIELD(30);

}

//筛选类型
enum class FilterType(val value: Int) {

    //默认
    Default(0),

    //包含
    Like(1),

    //是（等于）
    Eq(2),

    //开头为
    Start(3),

    //结尾为
    End(4),

    //不包含
    NContain(5),

    //不是（不等于）
    Ne(6),

    //为空
    IsNull(7),

    //不为空
    HasValue(8),

    //在范围内
    Between(11),

    //不在范围内
    NBetween(12),

    // >
    Gt(13),

    // >=
    Gte(14),

    // <
    Lt(15),

    // <=
    Lte(16),

    // 日期是
    DateEq(17),

    // 日期不是
    NDateEq(18),

    // 数组等于
    ArrEq(26),

    // 数组不等于
    ArrNe(27),

    // 日期在范围内
    DateBetween(31),

    // 日期不在范围内
    DateNBetween(32),

    // 日期晚于
    DateGt(33),

    // 日期晚于等于
    DateGte(34),

    // 日期早于
    DateLt(35),

    // 日期早于等于
    DateLte(36)

}

enum class SpliceType(val value: Int) {
    AND(1), OR(2)
}

//返回的错误码
enum class ErrorCode(val value: Int) {
    //失败
    ERROR(0),

    //成功
    SUCCESS(1),

    //缺少参数
    NO_PRAM(10001),

    //参数值错误
    PARAM_ERR(10002),

    //数据操作无权限
    NO_PRI(10005),

    //数据不存在
    DATA_NOT_EXIST(10007),

    //请求令牌不存在
    TOKEN_NOT_EXIST(10101),

    //签名不合法
    SIGN_ERR(10102),

    //数据操作异常
    UN_KNOW_ERR(99999),
}