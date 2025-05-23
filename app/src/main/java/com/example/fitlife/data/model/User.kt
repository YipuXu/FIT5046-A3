package com.example.fitlife.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val firebaseUid: String, // 使用Firebase UID作为主键
    var name: String = "Xiao Ming", // 默认名称
    var email: String = "xiaoming@example.com", // 默认邮箱
    var avatarUri: String? = null, // 头像URI，可以为空
    var height: String = "178", // 身高，单位cm
    var weight: String = "70", // 体重，单位kg
    var fitnessGoal: String = "Muscle Gain & Fat Loss", // 健身目标
    var workoutFrequency: String = "4-5 Times Weekly", // 锻炼频率
    var fitnessTags: String = "Strength Training,Cardio" // 健身标签，逗号分隔
) 