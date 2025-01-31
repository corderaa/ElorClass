package com.example.elorclass.functionalities

import android.os.AsyncTask
import jakarta.mail.*
import jakarta.mail.internet.*
import java.util.Properties

class SendEmailTask(private val senderEmail: String, private val senderPassword: String, private val recipientEmail: String, private val subject: String, private val message: String) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        val session = Session.getDefaultInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(senderEmail, senderPassword)
            }
        })

        try {

            val mimeMessage = MimeMessage(session)
            mimeMessage.setFrom(InternetAddress(senderEmail))
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
            mimeMessage.subject = subject
            mimeMessage.setText(message)

            Transport.send(mimeMessage)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return null
    }
}