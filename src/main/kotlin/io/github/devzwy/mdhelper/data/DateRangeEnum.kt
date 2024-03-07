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
