# SendGrid API 集成指南

## 简介
本文档介绍如何为FitLife应用配置SendGrid API以实现后台邮件发送功能。

## 步骤1：创建SendGrid账户
1. 访问 [SendGrid官网](https://sendgrid.com/) 并创建账户
2. 完成邮箱验证步骤
3. 在SendGrid控制台中导航至"Settings > API Keys"并创建新的API密钥

## 步骤2：配置域名和发件人验证
1. 在SendGrid控制台中，导航至"Settings > Sender Authentication"
2. 配置发件人验证以确保邮件发送成功率
3. 按照SendGrid提供的指南验证您的域名或单个发件人邮箱地址

## 步骤3：在应用中配置SendGrid API密钥
1. 打开 `app/src/main/java/com/example/fitlife/utils/AppConfig.kt` 文件
2. 将 `YOUR_SENDGRID_API_KEY` 替换为您在SendGrid控制台创建的实际API密钥：
```kotlin
const val SENDGRID_API_KEY = "SG.您的实际密钥..." // 替换为您的真实API密钥
```

## 步骤4：配置发件人邮箱
在 `AppConfig.kt` 文件中配置发件人邮箱信息：
```kotlin
object EmailConfig {
    const val ADMIN_EMAIL = "your-admin-email@example.com" // 替换为实际接收反馈的邮箱
    const val FROM_EMAIL = "noreply@yourdomain.com" // 替换为已验证的发件人邮箱
    const val FROM_NAME = "Your App Name" // 发件人显示名称
}
```

## 步骤5：测试邮件发送功能
1. 在应用中导航至"帮助与反馈"页面
2. 填写反馈内容并点击提交
3. 检查接收邮箱是否收到反馈邮件

## 故障排除
如果邮件发送失败，请检查：
1. API密钥是否正确配置
2. 发件人邮箱是否已在SendGrid中验证
3. 应用是否有网络连接
4. 查看Android Studio的Logcat日志，寻找邮件发送错误信息

## 重要说明
- 为安全起见，不要将API密钥直接提交到版本控制系统
- 在生产环境中，考虑使用更安全的方式存储API密钥，例如加密的SharedPreferences或远程配置
- 请遵守SendGrid的使用政策和电子邮件发送最佳实践 