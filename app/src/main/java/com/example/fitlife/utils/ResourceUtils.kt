package com.example.fitlife.utils

import com.example.fitlife.R

/**
 * 资源工具类，提供获取资源ID的公共方法
 */
object ResourceUtils {
    /**
     * 安全获取资源ID的方法
     * @param name 资源名称
     * @param defaultId 默认资源ID，当找不到资源时返回
     * @return 资源ID
     */
    fun getResourceId(name: String, defaultId: Int): Int {
        return try {
            val resourceField = R.drawable::class.java.getDeclaredField(name)
            resourceField.getInt(null)
        } catch (e: Exception) {
            defaultId
        }
    }
} 