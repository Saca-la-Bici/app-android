package com.kotlin.sacalabici.framework.viewholders

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
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
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
import java.util.Locale

class DetailsViewHolder(
    private val binding: ActivityDetailsBinding,
    private val viewModel: ActivitiesViewModel,
    private val activityID: String,
    private val permissions: List<String>
) {

    fun bind(activity: Activity) {
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(activity.date)
        val context = binding.root.context

        binding.tvActivityTitle.text = activity.title

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
        binding.tvActivityDuration.text = context.getString(R.string.activity_duration_list, activity.duration)
        binding.tvActivityLocation.text = context.getString(R.string.activity_location_list, activity.location)
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
            binding.tvActivityDistance.text = context.getString(R.string.activity_distance, activity.distancia)
            binding.tvActivityRenta.visibility = View.VISIBLE
            setupRentLink()
        } else {
            binding.tvActivityDistance.visibility = View.GONE
            binding.tvActivityRenta.visibility = View.GONE
        }

        setupStartButton(activity)
        setupRutaButton(activity)
    }

    private fun setupJoinButton(activity: Activity) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val firebaseUID = firebaseUser?.uid
        activity.id = activityID

        if (firebaseUID != null) {
            val usuarioInscrito = activity.register?.contains(firebaseUID) == true

            if (usuarioInscrito) {
                setButtonForUnsubscription(activity)
            } else {
                setButtonForSubscription(activity)
            }
        } else {
            Toast.makeText(binding.root.context, "No se ha autenticado ningún usuario.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setButtonForSubscription(activity: Activity) {
        binding.btnJoin.text = binding.root.context.getString(R.string.activity_join)
        binding.btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(binding.root.context, R.color.yellow))

        binding.btnJoin.setOnClickListener {
            binding.btnJoin.isEnabled = false

            viewModel.postInscribirActividad(activity.id, activity.type) { success: Boolean, message: String ->
                binding.btnJoin.isEnabled = true
                Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()

                if (success) {
                    // Actualizar el contador localmente
                    val currentCount = binding.tvPeopleCount.text.toString().toInt()
                    binding.tvPeopleCount.text = (currentCount + 1).toString()

                    setButtonForUnsubscription(activity)
                } else {
                    binding.btnJoin.text = binding.root.context.getString(R.string.activity_join)
                    binding.btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(binding.root.context, R.color.yellow))
                }
            }
        }
    }

    private fun setButtonForUnsubscription(activity: Activity) {
        binding.btnJoin.text = binding.root.context.getString(R.string.activity_unsubscribe)
        binding.btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(binding.root.context, R.color.gray))

        binding.btnJoin.setOnClickListener {
            binding.btnJoin.isEnabled = false

            viewModel.postCancelarInscripcion(activity.id, activity.type) { success: Boolean, message: String ->
                binding.btnJoin.isEnabled = true
                Toast.makeText(binding.root.context, message, Toast.LENGTH_SHORT).show()

                if (success) {
                    // Actualizar el contador localmente
                    val currentCount = binding.tvPeopleCount.text.toString().toInt()
                    binding.tvPeopleCount.text = (currentCount - 1).toString()

                    setButtonForSubscription(activity)
                } else {
                    binding.btnJoin.text = binding.root.context.getString(R.string.activity_unsubscribe)
                    binding.btnJoin.setBackgroundTintList(ContextCompat.getColorStateList(binding.root.context, R.color.gray))
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
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://rentabici.sacalabici.org/"))
                widget.context.startActivity(intent)
            }
        }

        spannableString.setSpan(clickableSpan, text.length, text.length + linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val colorSpan = ForegroundColorSpan(Color.parseColor("#7DA68D"))
        spannableString.setSpan(colorSpan, text.length, text.length + linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvActivityRenta.text = spannableString
        binding.tvActivityRenta.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupStartButton(activity: Activity) {
        if (permissions.contains("Iniciar rodada")) {
            binding.btnStart.visibility = View.VISIBLE
            binding.btnStart.setOnClickListener {
                val intent = Intent(binding.root.context, StartRouteActivity::class.java)
                intent.putExtra("ID",activity.id)
                intent.putExtra("IDRUTA",activity.idRouteBase)
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
                intent.putExtra("ID",activity.id)
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
