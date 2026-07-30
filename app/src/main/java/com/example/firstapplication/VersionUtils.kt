package com.example.firstapplication

/**
 * 语义化版本号比较工具。
 *
 * 存在意义（不只是为了给 CI 一个测试靶子）：客户端「强制更新 / 灰度放量」都要判断
 * 「用户当前版本 是否 低于 服务端下发的最低版本」。这段逻辑写错的后患很大——
 * 要么该强更的没强更，要么把好用户挡在门外，所以它必须有单元测试守着。
 */
object VersionUtils {
    /**
     * 比较两个版本号。
     *
     * @return 负数表示 [left] < [right]，0 表示相等，正数表示 [left] > [right]
     *
     * 支持位数不等的比较："1.2" 与 "1.2.0" 视为相等（缺失位按 0 补齐）。
     */
    fun compare(
        left: String,
        right: String,
    ): Int {
        val a = parse(left)
        val b = parse(right)
        val size = maxOf(a.size, b.size)
        for (i in 0 until size) {
            val diff = a.getOrElse(i) { 0 } - b.getOrElse(i) { 0 }
            if (diff != 0) return diff
        }
        return 0
    }

    /** [current] 是否需要强制更新到 [minRequired]。 */
    fun needsForceUpdate(
        current: String,
        minRequired: String,
    ): Boolean = compare(current, minRequired) < 0

    // 🐛 故意植入的真实 bug：把数值比较改成了字典序比较。
    //    这是版本号比较最经典的错误——字典序下 "9" > "10"，
    //    于是 1.10.0 会被判定为小于 1.9.0，用户永远收不到 1.10 的强更。
    //    用于验证 CI 的关卡 2（单元测试）能否拦住它。演练结束后回滚。
    private fun parse(version: String): List<Int> =
        version.trim()
            .split(".")
            .map { part -> part.trim().firstOrNull()?.code ?: 0 }
}
