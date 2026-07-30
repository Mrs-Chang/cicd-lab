package com.example.firstapplication

import android.content.Context

/**
 * 故意的 Android Lint 违规靶子。
 *
 * `Context.attributionTag` 是 API 30 才有的属性，而本项目 minSdk = 26。
 * 在 26~29 的设备上调用它会直接抛 NoSuchMethodError 崩溃——
 * 这类问题编译期完全不报错，**只有 Android Lint 的 NewApi 检查能发现**。
 *
 * 👉 这正好说明为什么 ktlint 替代不了 Android Lint：
 *    ktlint 只看格式，它对这段代码毫无意见；
 *    而这是一个会让线上崩溃的真实 bug。
 *
 * 用于验证 CI 的关卡 3。演练结束后删除。
 */
object NewApiUsage {
    fun attributionTag(context: Context): String? = context.attributionTag
}
