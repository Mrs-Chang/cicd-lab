package com.example.firstapplication

import android.content.Context
import android.os.VibratorManager

/**
 * 设备能力查询。
 *
 * ⚠️ 这是 Day 2 任务 5 的**故意埋雷**，不要修复它——它的作用是证明三个静态检查工具互不替代。
 *
 * 这段代码：
 * - Kotlin 编译器：✅ 通过（compileSdk = 35，VibratorManager 这个类确实存在）
 * - ktlint：✅ 零意见（格式完全规范）
 * - 单元测试：✅ 全绿（没有任何测试覆盖它，而且它依赖 Android framework，JVM 上也测不了）
 * - **Android Lint：❌ NewApi**
 *
 * 因为 `VibratorManager` 是 **API 31** 引入的，而本项目 `minSdk = 26`。
 * 装了 Android 8.0 ~ 11 的手机上根本没有这个类，运行到这里直接
 * `NoClassDefFoundError` / `NoSuchMethodError` 崩溃。
 *
 * 👉 这就是「编译期正确 ≠ 运行期正确」：编译用的是 compileSdk，运行用的是用户手机的系统版本。
 */
object DeviceCapabilities {
    /** 设备是否有振动器。 */
    fun hasVibrator(context: Context): Boolean {
        val manager = context.getSystemService(VibratorManager::class.java)
        return manager.defaultVibrator.hasVibrator()
    }
}
