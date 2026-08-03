package com.example.firstapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [VersionUtils] 的单元测试。
 *
 * 等价类划分（每条测试对应下表一格，不重不漏）：
 *
 * | 维度 | 等价类 | 覆盖的测试 |
 * |---|---|---|
 * | 数值关系 | a < b / a == b / a > b | lessThan / equal / greaterThan |
 * | 段内位数 | 单位数 / 多位数（10 > 9 但 "10" < "9"） | multiDigitSegment |
 * | 段数 | 相等 / 不等（缺段补 0） | shorterVersionPadsWithZero / trailingSegmentMakesNewer |
 * | 格式噪音 | 前导零 / 前后空格 | leadingZero / surroundingWhitespace |
 * | 非法输入 | 空串 / 纯空白 / 非数字段 / 负数段 | 四条 throws 测试 |
 *
 * 边界值：位数进位处 9 → 10；段数边界 2 段 vs 3 段。
 */
class VersionUtilsTest {
    @Test
    fun `相同版本号返回 0`() {
        assertEquals(0, VersionUtils.compare("1.2.3", "1.2.3"))
    }

    @Test
    fun `主版本号小的排在前面`() {
        assertTrue(VersionUtils.compare("1.9.9", "2.0.0") < 0)
    }

    @Test
    fun `主版本号大的排在后面`() {
        assertTrue(VersionUtils.compare("2.0.0", "1.9.9") > 0)
    }

    /** 关键用例：按字符串比较 "1.2.10" < "1.2.9"，按数值比较才是对的。 */
    @Test
    fun `多位数段按数值比较而不是字符串比较`() {
        assertTrue(VersionUtils.compare("1.2.10", "1.2.9") > 0)
    }

    @Test
    fun `段数不等时缺失的段补 0`() {
        assertEquals(0, VersionUtils.compare("1.2", "1.2.0"))
    }

    @Test
    fun `段数不等且尾段非 0 时更新`() {
        assertTrue(VersionUtils.compare("1.2.1", "1.2") > 0)
    }

    @Test
    fun `前导零不影响比较结果`() {
        assertEquals(0, VersionUtils.compare("1.02.3", "1.2.3"))
    }

    @Test
    fun `前后空格被忽略`() {
        assertEquals(0, VersionUtils.compare("  1.2.3  ", "1.2.3"))
    }

    @Test
    fun `isNewer 在更新时返回 true`() {
        assertTrue(VersionUtils.isNewer("1.3.0", "1.2.9"))
    }

    @Test
    fun `isNewer 在版本相同时返回 false`() {
        assertFalse(VersionUtils.isNewer("1.2.3", "1.2.3"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `空串抛异常`() {
        VersionUtils.compare("", "1.0.0")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `纯空白抛异常`() {
        VersionUtils.compare("   ", "1.0.0")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `含非数字段抛异常`() {
        VersionUtils.compare("1.2.x", "1.2.3")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `含负数段抛异常`() {
        VersionUtils.compare("1.-2.3", "1.2.3")
    }
}
