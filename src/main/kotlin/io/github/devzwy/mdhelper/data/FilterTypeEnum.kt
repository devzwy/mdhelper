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
