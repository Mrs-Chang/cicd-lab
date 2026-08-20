package com.example.firstapplication

/**
 * 版本号比较工具。
 *
 * 纯 Kotlin 实现，**不依赖任何 Android framework 类**——因此可以放在 `src/test/` 里用
 * JVM 直接跑，秒级出结果，不需要模拟器。这是测试金字塔最底层该有的样子。
 *
 * 对比 [LocationHelper]：它依赖 LocationManager / Toast，在 `src/test/` 里跑会撞
 * `not mocked` —— 那类代码只能进 `src/androidTest/`，需要真机或模拟器，分钟级。
 */
object VersionUtils {
    /**
     * 比较两个点分十进制版本号，逐段按**数值**比较（不是字符串比较）。
     *
     * 段数不等时，缺失的段按 0 补齐："1.2" 与 "1.2.0" 相等。
     *
     * @return 负数表示 [a] < [b]，0 表示相等，正数表示 [a] > [b]
     * @throws IllegalArgumentException 版本号为空、含非数字段或含负数段
     */
    fun compare(
        a: String,
        b: String,
    ): Int {
        val left = parse(a)
        val right = parse(b)
        for (i in 0 until maxOf(left.size, right.size)) {
            val l = left.getOrElse(i) { 0 }
            val r = right.getOrElse(i) { 0 }
            if (l != r) return l.compareTo(r)
        }
        return 0
    }

    /** [candidate] 是否比 [current] 新。 */
    fun isNewer(
        candidate: String,
        current: String,
    ): Boolean = compare(candidate, current) > 0

    private fun parse(version: String): List<Int> {
        val trimmed = version.trim()
        require(trimmed.isNotEmpty()) { "版本号不能为空" }
        return trimmed.split(".").map { segment ->
            val value =
                segment.toIntOrNull()
                    ?: throw IllegalArgumentException("版本号含非数字段: \"$version\"")
            require(value >= 0) { "版本号含负数段: \"$version\"" }
            value
        }
    }
}

// 改一行源码，测「真实 PR」场景下 build cache 还能命中多少
// 第二次源码改动：同一场景取第二个样本，把 5s 的差别和噪音区分开

