import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import com.kotlin.sacalabici.R

class TermsAndConditionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.TVAdvertisement)
        val spannableString = SpannableString("Al registrarte estás aceptando los Términos y Política de Privacidad de Saca la Bici")

        // Enlace para los Términos
        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openUrl("https://www.google.com") // Cambia el enlace por la URL de los términos
            }
        }

        // Enlace para la Política de Privacidad
        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openUrl("https://www.google.com") // Cambia el enlace por la URL de la política de privacidad
            }
        }

        // Índices de las palabras que queremos hacer clicables
        val termsStartIndex = spannableString.indexOf("Términos")
        val termsEndIndex = termsStartIndex + "Términos".length
        val privacyStartIndex = spannableString.indexOf("Política de Privacidad")
        val privacyEndIndex = privacyStartIndex + "Política de Privacidad".length

        // Aplicar el color azul a los enlaces
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), termsStartIndex, termsEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), privacyStartIndex, privacyEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Asignar los ClickableSpan
        spannableString.setSpan(termsClickableSpan, termsStartIndex, termsEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyClickableSpan, privacyStartIndex, privacyEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Configurar el TextView para que los enlaces sean clicables
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    // Función para abrir una URL en el navegador
    private fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
