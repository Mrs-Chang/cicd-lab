package com.example.firstapplication

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager

/**
 * 设备能力查询。
 *
 * ## 这个文件的来历（Day 2 任务 5）
 *
 * 最初这里是一段**故意埋的雷**：直接调用 API 31 的 [VibratorManager] 而不做版本判断，
 * 用来验证三个静态检查工具互不替代。实测结果：
 *
 * | 工具 | 结果 |
 * |---|---|
 * | Kotlin 编译器 | ✅ 通过（compileSdk = 35，这个类确实存在） |
 * | ktlint | ✅ 零意见（格式完全规范） |
 * | 单元测试 | ✅ 全绿（14 条，没一条覆盖它） |
 * | **Android Lint** | ❌ `Class requires API level 31 (current min is 26) [NewApi]` |
 *
 * 只有 Lint 抓得到——因为只有它同时知道 compileSdk（能不能编译）和 minSdk（会不会崩）。
 *
 * ## 正确的修法
 *
 * 不是删掉这段代码，也不是 `@SuppressLint("NewApi")`——那是把警报关掉，崩溃照旧发生。
 * 正确做法是**运行时判断系统版本 + 给旧版本一条降级路径**。
 * Lint 能识别 `Build.VERSION.SDK_INT >= ...` 这种守卫，识别到就不再报 NewApi。
 */
object DeviceCapabilities {
    /**
     * 设备是否有振动器。
     *
     * API 31+ 走 [VibratorManager]；更早的系统走已废弃的 [Context.VIBRATOR_SERVICE]。
     */
    fun hasVibrator(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java).defaultVibrator.hasVibrator()
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).hasVibrator()
        }
}
