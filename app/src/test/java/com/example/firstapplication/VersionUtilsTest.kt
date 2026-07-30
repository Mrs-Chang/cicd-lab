package com.example.firstapplication

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * VersionUtils 的单元测试——CI 里 `testDebugUnitTest` 这道关卡守的就是它。
 *
 * 对比项目自带的 `assertEquals(4, 2 + 2)`：那测的是 JVM 的加法，不是我们的代码，
 * 属于「无效测试」——涨覆盖率但拦不住任何 bug。下面每条都是「真实输入 → 可观察输出」。
 */
class VersionUtilsTest {
    @Test
    fun `小版本号小于大版本号`() {
        assertTrue(VersionUtils.compare("1.2.3", "1.2.4") < 0)
    }

    @Test
    fun `大版本号大于小版本号`() {
        assertTrue(VersionUtils.compare("2.0.0", "1.9.9") > 0)
    }

    @Test
    fun `完全相同的版本号返回 0`() {
        assertEquals(0, VersionUtils.compare("1.2.3", "1.2.3"))
    }

    @Test
    fun `位数不等时缺失位按 0 补齐`() {
        assertEquals(0, VersionUtils.compare("1.2", "1.2.0"))
    }

    @Test
    fun `数字按数值比较而非字典序`() {
        // 字典序会认为 "9" > "10"，这是最常见的版本号比较 bug
        assertTrue(VersionUtils.compare("1.10.0", "1.9.0") > 0)
    }

    @Test
    fun `低于最低版本时需要强更`() {
        assertTrue(VersionUtils.needsForceUpdate(current = "3.1.0", minRequired = "3.2.0"))
    }

    @Test
    fun `等于最低版本时不需要强更`() {
        assertFalse(VersionUtils.needsForceUpdate(current = "3.2.0", minRequired = "3.2.0"))
    }

    @Test
    fun `高于最低版本时不需要强更`() {
        assertFalse(VersionUtils.needsForceUpdate(current = "3.3.0", minRequired = "3.2.0"))
    }
}
