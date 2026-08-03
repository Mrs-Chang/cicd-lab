# cicd-lab

CI/CD 学习用的 Android 最小工程。

## 流水线

四道质量门禁，按「便宜的在前」排列：

1. ktlint 代码格式检查
2. 单元测试
3. Android Lint
4. 编译 Debug 包

失败时上传诊断报告，成功时上传 APK。

## 分支保护

main 受保护：必须走 PR、`build` 检查必须通过、分支必须与 main 同步、管理员同样受限。
