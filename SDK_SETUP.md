# Android SDK 设置指南

如果你在克隆或拉取项目后收到以下错误：

```
SDK location not found. Define a valid SDK location with an ANDROID_HOME environment variable or by setting the sdk.dir path in your project's local properties file at '.../local.properties'.
```

请按照以下步骤解决：

## 方法一：创建 local.properties 文件（推荐）

1. 在项目根目录下找到 `local.properties.template` 文件
2. 复制该文件并重命名为 `local.properties`
3. 打开 `local.properties` 文件，将 `sdk.dir=YOUR_SDK_PATH_HERE` 替换为你本地 Android SDK 的路径：

   **Windows 示例：**
   ```
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   ```

   **Mac 示例：**
   ```
   sdk.dir=/Users/YourUsername/Library/Android/sdk
   ```

   **Linux 示例：**
   ```
   sdk.dir=/home/YourUsername/Android/Sdk
   ```

## 方法二：设置环境变量

如果你不想创建 `local.properties` 文件，可以设置 `ANDROID_HOME` 环境变量：

### Windows:
1. 右键点击"此电脑"，选择"属性"
2. 点击"高级系统设置"
3. 点击"环境变量"
4. 在"用户变量"部分，点击"新建"
5. 变量名输入 `ANDROID_HOME`
6. 变量值输入你的 Android SDK 路径（例如：`C:\Users\YourUsername\AppData\Local\Android\Sdk`）
7. 点击"确定"保存

### Mac/Linux:
1. 打开终端
2. 编辑 `~/.bash_profile` 或 `~/.zshrc` 文件（取决于你使用的 shell）：
   ```
   nano ~/.bash_profile
   ```
   或
   ```
   nano ~/.zshrc
   ```
3. 添加以下行：
   ```
   export ANDROID_HOME=/Users/YourUsername/Library/Android/sdk
   ```
   （替换为你的实际 SDK 路径）
4. 保存并关闭文件（Ctrl+O, Enter, Ctrl+X）
5. 重载配置：
   ```
   source ~/.bash_profile
   ```
   或
   ```
   source ~/.zshrc
   ```

## 找到你的 Android SDK 路径

如果你不知道 Android SDK 的路径：

### 在 Android Studio 中查找：
1. 打开 Android Studio
2. 点击 File > Settings (Windows/Linux) 或 Android Studio > Preferences (Mac)
3. 展开 Appearance & Behavior > System Settings > Android SDK
4. 顶部的 "Android SDK Location" 字段显示了 SDK 路径

## 注意事项

- `local.properties` 文件**不应**提交到 Git 仓库，因为它包含特定于每个开发者本地环境的配置
- 该文件已被添加到 `.gitignore` 列表中，确保它不会被意外提交
- 每次从新环境克隆项目时，你都需要设置 `local.properties` 文件或环境变量 