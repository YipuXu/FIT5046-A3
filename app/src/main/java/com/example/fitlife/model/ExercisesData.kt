package com.example.fitlife.model

data class ExercisesData(
    val previousPage: String?,      // 上一页链接
    val nextPage: String?,          // 下一页链接
    val totalPages: Int,            // 总页数
    val totalExercises: Int,        // 总练习数
    val exercises: List<Exercise>   // 真正的练习数组
)