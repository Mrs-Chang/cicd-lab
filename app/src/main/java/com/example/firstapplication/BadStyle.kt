package com.example.firstapplication

import java.util.*

/**
 * 故意的 ktlint 违规靶子：通配符 import + 错误缩进 + 多余空行。
 * 用于验证 CI 的关卡 1 能否拦住格式问题。演练结束后删除。
 */
object BadStyle {
      fun formatTimestamp( millis : Long ) : String {
            val date = Date(millis)
        return date.toString()
      }



    fun unusedHelper() : Int { return 42 }
}
