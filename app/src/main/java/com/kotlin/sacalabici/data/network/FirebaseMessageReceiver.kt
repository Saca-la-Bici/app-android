package com.kotlin.sacalabici.data.network

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.AnnouncementApiService
import com.kotlin.sacalabici.framework.views.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Servicio para recibir mensajes de Firebase Cloud Messaging
class FirebaseMessageReceiver : FirebaseMessagingService() {
    // Instancia de FirebaseAuth para la autenticación
    val firebaseAuth = FirebaseAuth.getInstance()
    // Instancia de FirebaseTokenManager para manejar el token
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)

    // Método que se llama cuando se recibe un mensaje
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Verificar si el mensaje tiene una notificación y mostrarla
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Default Title"
            val body = notification.body ?: "Default Body"
            showNotification(title, body)
        }
    }

    // Método para crear y mostrar la notificación
    private fun showNotification(title: String?, message: String?) {
        // Intent para abrir MainActivity al hacer clic en la notificación
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Limpiar la pila de actividades al abrir
        // Crear un PendingIntent para manejar la acción de la notificación
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "default_channel_id"
        // Construir la notificación
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Obtener el servicio de notificaciones del sistema
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        // Mostrar la notificación
        notificationManager.notify(0, notificationBuilder.build())
    }
}