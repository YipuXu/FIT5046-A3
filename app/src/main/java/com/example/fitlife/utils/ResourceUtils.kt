package com.example.fitlife.utils

import com.example.fitlife.R

/**
 * Resource tool class, providing a public method for obtaining resource IDs
 */
object ResourceUtils {
    /**
     * Method to safely obtain resource ID
     * @param name resource name
     * @param defaultId default resource ID, returned when resource cannot be found
     * @return resource ID
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