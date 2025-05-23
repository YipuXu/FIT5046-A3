package com.example.fitlife.ui.coach

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GeminiApiService(private val context: Context) {
    
    companion object {
        private const val TAG = "GeminiApiService"
        private const val API_KEY = "AIzaSyAQsgSlR1BjdxFQJFZqcPK3PtAB8DjnL0w"
        
        // Available model options
        private const val MODEL_GEMINI_FLASH = "gemini-1.5-flash" 
        private const val MODEL_GEMINI_PRO = "gemini-1.0-pro"    
        private const val MODEL_GEMINI_BASIC = "models/gemini-pro"
    }
    
    // Default to MODEL_GEMINI_FLASH
    private var currentModel = MODEL_GEMINI_FLASH
    
    // Dynamically create model instance
    private fun createModel(modelName: String): GenerativeModel {
        return GenerativeModel(
            modelName = modelName,
            apiKey = API_KEY,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 8192
            }
        )
    }
    
    // Get instance based on current model selection
    private fun getModel(): GenerativeModel {
        return createModel(currentModel)
    }
    
    // Chat history
    private val chatHistory = mutableListOf<Pair<String, String>>()
    
    // System prompt defining AI's role and behavior
    private val systemPrompt = """
        You are an AI fitness coach in the FitLife app, specializing in providing professional, personalized fitness advice and dietary guidance.
        
        Role:
        - You are an experienced fitness coach with extensive knowledge of fitness, nutrition, and healthy lifestyles.
        - You excel at creating personalized fitness plans and nutritional recommendations.
        - Your responses should be concise, clear, professional, and friendly.
        
        Response Format:
        - For fitness and nutrition plan requests, provide well-organized information with appropriate subheadings and bullet points.
        - Replies should be professional, encouraging, and practical.
        - Offer science-based advice, avoiding unfounded or unhealthy suggestions.
        - Use simple, clear language, avoiding overly technical jargon.
        
        Limitations:
        - Do not discuss topics unrelated to fitness, nutrition, and healthy living.
        - Do not provide medical diagnoses or treatment advice.
        - Do not recommend any potentially harmful fitness methods or diet plans.
    """.trimIndent()
    
    /**
     * Send a message to the Gemini API and get a response
     */
    suspend fun sendMessage(userMessage: String): Flow<String> = flow {
        // Try different models
        val modelOptions = listOf(currentModel, MODEL_GEMINI_FLASH, MODEL_GEMINI_PRO, MODEL_GEMINI_BASIC)
        var lastError: Exception? = null
        
        for (modelName in modelOptions.distinct()) {
            if (modelName != currentModel) {
                Log.d(TAG, "Trying fallback model: $modelName")
            }
            
            try {
                // Update the currently used model
                currentModel = modelName
                val model = getModel()
                
                Log.d(TAG, "Sending message using model $currentModel: $userMessage")
                
                // Build the prompt, including system prompt, history, and current message
                val prompt = buildPrompt(userMessage)
                
                // Send request and get response stream
                val responseStream = model.generateContentStream(prompt)
                
                // Collect and process the response stream
                var completeResponse = ""
                responseStream.collect { response ->
                    val chunk = response.text ?: ""
                    completeResponse += chunk
                    emit(completeResponse)
                }
                
                // Save to history
                if (completeResponse.isNotEmpty()) {
                    chatHistory.add(userMessage to completeResponse)
                    
                    // Log success with this model
                    Log.d(TAG, "Successfully used model $currentModel")
                    
                    // Exit loop, no need to try other models
                    return@flow
                }
            } catch (e: Exception) {
                Log.e(TAG, "Model $modelName error: ${e.message}", e)
                lastError = e
                
                // If it's the last model option and an error occurs, emit an error message
                if (modelName == modelOptions.last()) {
                    emit("Sorry, I encountered an issue and couldn't respond to your request. Please try again later.\nError: ${e.message}")
                }
                
                // Try the next model
                continue
            }
        }
        
        // If all models failed
        if (lastError != null) {
            Log.e(TAG, "All models failed", lastError)
            emit("Sorry, I encountered a technical issue and couldn't respond to your request. Please try again later.")
        }
    }
    
    /**
     * Build the full prompt including system prompt and history
     */
    private fun buildPrompt(userMessage: String): String {
        val sb = StringBuilder()
        
        // Add system prompt
        sb.append(systemPrompt)
        sb.append("\n\n")
        
        // Add historical conversation (max 5 rounds)
        val recentHistory = chatHistory.takeLast(5)
        for ((pastUserMsg, pastAiReply) in recentHistory) {
            sb.append("User: $pastUserMsg\n")
            sb.append("AI Coach: $pastAiReply\n\n")
        }
        
        // Add current user message
        sb.append("User: $userMessage\n")
        sb.append("AI Coach: ")
        
        return sb.toString()
    }
    
    /**
     * Parse structured content from AI response
     * Identify subheadings, list items, and paragraphs
     */
    fun parseStructuredContent(text: String): Message {
        val lines = text.lines()
        
        // Extract main content, including multiple paragraphs
        val mainContentBuilder = StringBuilder()
        var inMainContent = true
        val details = mutableListOf<String>()
        val sections = mutableMapOf<String, MutableList<String>>() // Changed to MutableList
        var currentSectionTitle = ""
        
        for (line in lines) {
            val trimmedLine = line.trim()
            
            // Check if it's a list item or section title
            val isListItem = trimmedLine.startsWith("- ") || trimmedLine.startsWith("• ")
            val isSectionTitle = "(.*?):\\s*".toRegex().matches(trimmedLine) && !isListItem
            
            if (inMainContent) {
                if (isListItem || isSectionTitle) {
                    // Main content ends, start parsing details and sections
                    inMainContent = false
                } else {
                    mainContentBuilder.append(line).append("\n")
                    continue // Continue processing main content line
                }
            }
            
            // Parse details and sections
            if (isSectionTitle) {
                currentSectionTitle = trimmedLine
                sections.putIfAbsent(currentSectionTitle, mutableListOf()) 
            } else if (isListItem) {
                val item = trimmedLine.replaceFirst("- ", "").replaceFirst("• ", "").trim()
                if (currentSectionTitle.isNotEmpty()) {
                    sections[currentSectionTitle]?.add(item)
                } else {
                    details.add(item)
                }
            } else if (currentSectionTitle.isNotEmpty() && trimmedLine.isNotEmpty()) {
                // If the current line is not a list item or section title, but belongs to a section, add it to that section
                sections[currentSectionTitle]?.add(trimmedLine)
            } else if (trimmedLine.isNotEmpty()) {
                // If it doesn't belong to any section and isn't a list item, add it as a normal paragraph to main content
                mainContentBuilder.append(line).append("\n") 
            }
        }
        
        // Clean up trailing newline characters from main content
        val mainContent = mainContentBuilder.toString().trimEnd()
        
        return Message(
            content = mainContent,
            isFromUser = false,
            details = details,
            // Ensure MutableList is converted to List
            sections = sections.mapValues { it.value.toList() }
        )
    }
    
    /**
     * Clear chat history
     */
    fun clearChatHistory() {
        chatHistory.clear()
    }
} 