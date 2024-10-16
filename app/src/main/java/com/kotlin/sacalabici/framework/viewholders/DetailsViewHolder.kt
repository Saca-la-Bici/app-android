package com.kotlin.sacalabici.framework.viewholders

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.activities.Activity
import com.kotlin.sacalabici.databinding.ActivityDetailsBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.activities.LookRouteActivity
import com.kotlin.sacalabici.framework.views.activities.StartRouteActivity



import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailsViewHolder(
    private val binding: ActivityDetailsBinding,
    private val viewModel: ActivitiesViewModel,
    private val activityID: String,
    private val permissions: List<String>
) {

    @SuppressLint("SimpleDateFormat")
    fun bind(activity: Activity) {
        // Formatear solo la parte de la fecha (día, mes, año)
        val formattedDate =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(activity.date)
        val context = binding.root.context

        binding.tvActivityTitle.text = activity.title

        // Mostrar el nivel si está presente
        if (activity.nivel != null) {
            binding.tvActivityLevel.visibility = View.VISIBLE
            binding.tvActivityLevel.text = activity.nivel

            val levelColor = when (activity.nivel) {
                "Nivel 1" -> ContextCompat.getColor(context, R.color.level1)
                "Nivel 2" -> ContextCompat.getColor(context, R.color.level2)
                "Nivel 3" -> ContextCompat.getColor(context, R.color.level3)
                "Nivel 4" -> ContextCompat.getColor(context, R.color.level4)
                "Nivel 5" -> ContextCompat.getColor(context, R.color.level5)
                else -> ContextCompat.getColor(context, R.color.gray)
            }
            val background = binding.tvActivityLevel.background as GradientDrawable
            background.setColor(levelColor)
        } else {
            binding.tvActivityLevel.visibility = View.GONE
        }

        binding.tvPeopleCount.text = activity.peopleEnrolled.toString()

        if (activity.imageURL != null) {
            binding.ivActivityImage.visibility = View.VISIBLE
            getActivityImage(activity.imageURL, binding.ivActivityImage)
        } else {
            binding.ivActivityImage.visibility = View.GONE
        }

        binding.tvActivityDate.text = context.getString(R.string.activity_date_list, formattedDate)
        binding.tvActivityTime.text = context.getString(R.string.activity_time_list, activity.time)
        binding.tvActivityDuration.text =
            context.getString(R.string.activity_duration_list, activity.duration)
        binding.tvActivityLocation.text =
            context.getString(R.string.activity_location_list, activity.location)
        binding.tvActivityDescription.text = activity.description

        setupJoinButton(activity)

        binding.btnCopyLocation.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Ubicación", activity.location)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Ubicación copiada al portapapeles", Toast.LENGTH_SHORT).show()
        }

        if (activity.type == "Rodada") {
            binding.tvActivityDistance.visibility = View.VISIBLE
            binding.tvActivityDistance.text =
                context.getString(R.string.activity_distance, activity.distancia)
            binding.tvActivityRenta.visibility = View.VISIBLE
            setupRentLink()
            binding.materialesRow.visibility = View.VISIBLE
            binding.btnMateriales.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sacalabucket.s3.us-east-2.amazonaws.com/decalogos/Decalogo+%231.jpeg"))
                context.startActivity(intent)
            }
        } else {
            binding.tvActivityDistance.visibility = View.GONE
            binding.tvActivityRenta.visibility = View.GONE
            binding.materialesRow.visibility = View.GONE
        }

        setupStartButton(activity)
        setupRutaButton(activity)
    }

    private fun setupJoinButton(activity: Activity) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val firebaseUID = firebaseUser?.uid
        activity.id = activityID

        // Combinar la fecha y la hora utilizando Calendar
        val calendar = Calendar.getInstance().apply {
            time = activity.date
            val timeParts = activity.time.split(":")
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
        }

        // Obtener la fecha y hora actual
        val currentTime = Calendar.getInstance()

        // Verificar si ha pasado más de una hora desde la hora de la actividad
        if (currentTime.timeInMillis > calendar.timeInMillis + 3600000) {
            // Ocultar el botón si ha pasado más de una hora
            binding.btnJoin.visibility = View.GONE
            return
        }

        if (firebaseUID != null) {
            val usuarioInscrito = activity.register?.contains(firebaseUID) == true

            if (usuarioInscrito) {
                setButtonForUnsubscription(activity)
                setupValidateAttendanceButton(
                    activity,
                    true
                ) // Mostrar el botón si ya está inscrito
            } else {
                setButtonForSubscription(activity)
                setupValidateAttendanceButton(
                    activity,
                    false
                ) // Ocultar el botón si no está inscrito
            }
        } else {
            Toast.makeText(
                binding.root.context,
                "No se ha autenticado ningún usuario.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupValidateAttendanceButton(activity: Activity, show: Boolean) {
        if (show && activity.type == "Rodada" && permissions.contains("Iniciar rodada")) {
            animateButtonVisibility(binding.btnCheckCode, true)
            binding.btnCheckCode.setOnClickListener {
                activity.code?.let { it1 -> showDialogCode(it1) }
                animateButtonVisibility(binding.btnValidateAttendance, false)
            }
        } else if (show && activity.type == "Rodada") {
            animateButtonVisibility(binding.btnValidateAttendance, true)
            binding.btnValidateAttendance.setOnClickListener {
                showValidationDialog()
                animateButtonVisibility(binding.btnCheckCode, false)
            }
        } else {
            animateButtonVisibility(binding.btnValidateAttendance, false)
            animateButtonVisibility(binding.btnCheckCode, false)
        }
    }


    private fun showValidationDialog() {
        val context = binding.root.context
        val dialogView = View.inflate(context, R.layout.dialog_validate_attendance, null)
        val editText = dialogView.findViewById<TextView>(R.id.etValidationCode)

        val dialog = android.app.AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<View>(R.id.btnConfirm).setOnClickListener {
            val code = editText.text.toString()
            if (code.length == 4) {
                dialog.dismiss()
                val codeInt = code.toInt() // Convertir a Int
                handleValidationCode(codeInt)
            } else {
                Toast.makeText(
                    context,
                    "Por favor ingrese un código de 4 números.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }
    private fun showDialogCode(code: Int) {
        val context = binding.root.context
        val dialogViewCode = View.inflate(context, R.layout.dialog_code, null)
        val codeTextView = dialogViewCode.findViewById<TextView>(R.id.tvValidationCode)

        codeTextView.text = code.toString()

        val dialog = android.app.AlertDialog.Builder(context)
            .setView(dialogViewCode)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogViewCode.findViewById<View>(R.id.btnValidateAttendance).setOnClickListener {
            dialog.dismiss()  // Cierra el diálogo
            handleValidationCode(code)
        }
        dialog.show()
    }


    private fun handleValidationCode(code: Int) {
        Toast.makeText(binding.root.context, "Código ingresado: $code", Toast.LENGTH_SHORT).show()

        // Llamada a la función del ViewModel para validar la asistencia
        viewModel.validateAttendance(activityID, code) { success: Boolean, message: String ->
            if (success) {
                Toast.makeText(
                    binding.root.context,
                    "Asistencia validada con éxito.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    binding.root.context,
                    "Error:$message",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("errer", "$message")
            }
        }
    }


    private fun animateButtonVisibility(button: View, show: Boolean) {
        if (show) {
            button.visibility = View.VISIBLE
            val fadeIn = AlphaAnimation(0.0f, 1.0f)
            fadeIn.duration = 300
            button.startAnimation(fadeIn)
        } else {
            val fadeOut = AlphaAnimation(1.0f, 0.0f)
            fadeOut.duration = 300
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    button.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            button.startAnimation(fadeOut)
        }
    }

    private fun setButtonForSubscription(activity: Activity) {
        binding.btnJoin.text = binding.root.context.getString(R.string.activity_join)
        binding.btnJoin.setBackgroundTintList(
            ContextCompat.getColorStateList(
                binding.root.context,
                R.color.yellow
            )
        )

        binding.btnJoin.setOnClickListener {
            binding.btnJoin.isEnabled = false

            viewModel.postInscribirActividad(
                activity.id,
                activity.type
            ) { success: Boolean, message: String ->
                binding.btnJoin.isEnabled = true
                Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()

                if (success) {
                    val currentCount = binding.tvPeopleCount.text.toString().toInt()
                    binding.tvPeopleCount.text = (currentCount + 1).toString()
                    setButtonForUnsubscription(activity)
                    setupValidateAttendanceButton(activity, true)
                } else {
                    binding.btnJoin.text = binding.root.context.getString(R.string.activity_join)
                    binding.btnJoin.setBackgroundTintList(
                        ContextCompat.getColorStateList(
                            binding.root.context,
                            R.color.yellow
                        )
                    )
                }
            }
        }
    }

    private fun setButtonForUnsubscription(activity: Activity) {
        binding.btnJoin.text = binding.root.context.getString(R.string.activity_unsubscribe)
        binding.btnJoin.setBackgroundTintList(
            ContextCompat.getColorStateList(
                binding.root.context,
                R.color.gray
            )
        )

        binding.btnJoin.setOnClickListener {
            binding.btnJoin.isEnabled = false

            viewModel.postCancelarInscripcion(
                activity.id,
                activity.type
            ) { success: Boolean, message: String ->
                binding.btnJoin.isEnabled = true
                Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()

                if (success) {
                    val currentCount = binding.tvPeopleCount.text.toString().toInt()
                    binding.tvPeopleCount.text = (currentCount - 1).toString()
                    setButtonForSubscription(activity)
                    setupValidateAttendanceButton(activity, false)
                } else {
                    binding.btnJoin.text =
                        binding.root.context.getString(R.string.activity_unsubscribe)
                    binding.btnJoin.setBackgroundTintList(
                        ContextCompat.getColorStateList(
                            binding.root.context,
                            R.color.gray
                        )
                    )
                }
            }
        }
    }

    private fun setupRentLink() {
        val text = binding.root.context.getString(R.string.activity_rent) + " "
        val linkText = "Clic aquí"
        val spannableString = SpannableString(text + linkText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://rentabici.sacalabici.org/"))
                widget.context.startActivity(intent)
            }
        }

        spannableString.setSpan(
            clickableSpan,
            text.length,
            text.length + linkText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val colorSpan = ForegroundColorSpan(Color.parseColor("#7DA68D"))
        spannableString.setSpan(
            colorSpan,
            text.length,
            text.length + linkText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvActivityRenta.text = spannableString
        binding.tvActivityRenta.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupStartButton(activity: Activity) {
        if ((activity.type == "Rodada") && permissions.contains("Iniciar rodada")) {
            binding.btnStart.visibility = View.VISIBLE
            binding.btnStart.setOnClickListener {
                val intent = Intent(binding.root.context, StartRouteActivity::class.java)
                intent.putExtra("ID", activity.id)
                intent.putExtra("IDRUTA", activity.idRouteBase)
                binding.root.context.startActivity(intent)
            }
        } else {
            binding.btnStart.visibility = View.GONE
        }
    }

    private fun setupRutaButton(activity: Activity) {
        if (activity.type == "Rodada") {
            binding.btnRuta.visibility = View.VISIBLE
            binding.btnRuta.setOnClickListener {
                val intent = Intent(binding.root.context, LookRouteActivity::class.java)
                intent.putExtra("ID", activity.id)
                binding.root.context.startActivity(intent)
            }
        } else {
            binding.btnRuta.visibility = View.GONE
        }
    }

    private fun getActivityImage(url: String, imageView: ImageView) {
        val requestOptions = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .priority(Priority.HIGH)

        Glide.with(binding.root.context)
            .load(url)
            .apply(requestOptions)
            .into(imageView)
    }

}
