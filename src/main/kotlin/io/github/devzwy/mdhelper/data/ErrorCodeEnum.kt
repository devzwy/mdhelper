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
