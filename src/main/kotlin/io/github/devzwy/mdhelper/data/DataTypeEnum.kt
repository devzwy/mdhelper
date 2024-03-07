package io.github.devzwy.mdhelper.data

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