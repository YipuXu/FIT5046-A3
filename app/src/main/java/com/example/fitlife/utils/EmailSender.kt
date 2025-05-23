package com.example.fitlife.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmailSender {
    companion object {
        private const val TAG = "EmailSender"

        /**
         * Use SendGrid API to send feedback emails in the background
         * @param context context
         * @param feedback user feedback content
         * @param onSuccess send success callback
         * @param onError send failure callback
         */
        suspend fun sendFeedbackEmail(
            context: Context, 
            feedback: String,
            onSuccess: () -> Unit = {},
            onError: (String) -> Unit = {}
        ) {
            return withContext(Dispatchers.IO) {
                try {
                    val from = Email(AppConfig.EmailConfig.FROM_EMAIL, AppConfig.EmailConfig.FROM_NAME)
                    val to = Email(AppConfig.EmailConfig.ADMIN_EMAIL)
                    val subject = "FitLife App Feedback"
                    val content = Content("text/plain", "User Feedback:\n\n$feedback")
                    
                    val mail = Mail(from, subject, to, content)
                    val sg = SendGrid(AppConfig.SENDGRID_API_KEY)
                    val request = Request()
                    
                    request.method = Method.POST
                    request.endpoint = "mail/send"
                    request.body = mail.build()
                    
                    val response = sg.api(request)
                    
                    withContext(Dispatchers.Main) {
                        if (response.statusCode in 200..299) {
                            Log.d(TAG, "Email sent successfully")
                            onSuccess()
                        } else {
                            Log.e(TAG, "Failed to send email: ${response.body}")
                            onError("Failed to send email (${response.statusCode})")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception when sending email", e)
                    withContext(Dispatchers.Main) {
                        onError("Error: ${e.localizedMessage ?: "Unknown error"}")
                    }
                }
            }
        }
    }
} 