package com.kotlin.sacalabici.framework.views.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.databinding.FragmentActivityInfoBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddActivityInfoFragment: Fragment() {
    private var type: String? = null
    private lateinit var listener: OnFragmentInteractionListener
    private lateinit var viewModel: ActivitiesViewModel
    private var _binding: FragmentActivityInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var etTitle: EditText
    private lateinit var tvTitleWC: TextView
    val maxTitle = 50
    private lateinit var etUbi: EditText
    private lateinit var tvUbiWC: TextView
    val maxUbi = 150
    private lateinit var etDesc: EditText
    private lateinit var tvDescWC: TextView
    val maxDesc = 450

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    /*
    * Permite que el fragmento se comunique con la actividad
    * Le notificará cuando se seleccione el botón "Siguiente"
    * Llama a la función para recibir la información del formulario
    * */
    interface OnFragmentInteractionListener {
        fun onCloseClicked()
        fun onNextClicked(type: String)
        fun receiveInformation(title: String,
                               date: String,
                               hour: String,
                               minutes: String,
                               hourDur: String,
                               minutesDur: String,
                               ubi: String,
                               description: String,
                               image: Uri?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context debe implementar OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar el estado guardado o los argumentos
        type = savedInstanceState?.getString("type") ?: arguments?.getString("type")
        registerImagePicker()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guardar el tipo para evitar perderlo si el fragmento es recreado
        outState.putString("type", type)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ActivitiesViewModel::class.java)
        _binding = FragmentActivityInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // En caso de que se haya retrocedido, recuperar información
        viewModel.activityInfo.value?.let { info ->
            binding.etAddActivityTitle.setText(info.title)
            binding.BDate.text = info.date
            binding.hourSpinner.setSelection(info.hour.toInt())
            binding.minutesSpinner.setSelection(info.minutes.toInt())
            binding.hourSpinnerDur.setSelection(info.hourDur.toInt())
            binding.minutesSpinnerDur.setSelection(info.minutesDur.toInt())
            binding.etAddActivityUbi.setText(info.ubi)
            binding.etAddActivityDescription.setText(info.description)
        }

        // Variables de edit texts para conteo de caracteres
        etTitle = binding.etAddActivityTitle
        tvTitleWC = binding.tvTitleWordCount
        etUbi = binding.etAddActivityUbi
        tvUbiWC = binding.tvUbiWordCount
        etDesc = binding.etAddActivityDescription
        tvDescWC = binding.tvDescWordCount

        initializeListeners()
        initializeWordListeners()

        return root
    }

    private fun initializeListeners() {
        // Listener para cuando se seleccione la fecha en el DatePickerDialog
        val calendar = Calendar.getInstance()
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate(calendar)
        }

        // Evento para el botón de fecha
        binding.BDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Evento para cancelar el registro
        binding.ibClose.setOnClickListener {
            listener.onCloseClicked()
        }

        // Evento para subir imagen
        binding.imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        /*
        * Si el usuario ha terminado con esa vista, manda la información al viewModel
        * Si es rodada, llama al siguiente fragmento
        * */
        binding.btnNext.setOnClickListener {
            // Valida que los campos tengan contenido
            if (validateFields()) {
                // Captura los datos
                val title = binding.etAddActivityTitle.text.toString()
                val date = binding.BDate.text.toString()
                val hour = binding.hourSpinner.selectedItem.toString()
                val minutes = binding.minutesSpinner.selectedItem.toString()
                val hourDur = binding.hourSpinnerDur.selectedItem.toString()
                val minutesDur = binding.minutesSpinnerDur.selectedItem.toString()
                val ubi = binding.etAddActivityUbi.text.toString()
                val description = binding.etAddActivityDescription.text.toString()
                val image = selectedImageUri

                // Enviar información al ViewModel
                listener.receiveInformation(title, date, hour, minutes, hourDur, minutesDur, ubi, description, image)
                listener.onNextClicked(type.toString())
            }
        }
    }

    private fun initializeWordListeners() {
        etTitle.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Contar los caracteres
                val charCount = s?.length ?: 0

                // Actualizar el contador en el TextView
                tvTitleWC.text = getString(R.string.char_count, charCount, maxTitle)

                // Validar si se excede el límite de caracteres
                if (charCount > maxTitle) {
                    etTitle.error = "Has excedido el límite de $maxTitle caracteres."
                    // Prevenir que se sigan escribiendo caracteres
                    etTitle.setText(s?.substring(0, maxTitle))
                    etTitle.setSelection(maxTitle)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No aplica, pero debe llamarse
            }

            override fun afterTextChanged(s: Editable?) {
                // No aplica, pero debe llamarse
            }
        })

        etUbi.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Contar los caracteres
                val charCount = s?.length ?: 0

                // Actualizar el contador en el TextView
                tvUbiWC.text = getString(R.string.char_count, charCount, maxUbi)

                // Validar si se excede el límite de caracteres
                if (charCount > maxUbi) {
                    etUbi.error = "Has excedido el límite de $maxUbi caracteres."
                    // Prevenir que se sigan escribiendo caracteres
                    etUbi.setText(s?.substring(0, maxUbi))
                    etUbi.setSelection(maxUbi)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No aplica, pero debe llamarse
            }

            override fun afterTextChanged(s: Editable?) {
                // No aplica, pero debe llamarse
            }
        })

        etDesc.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Contar los caracteres
                val charCount = s?.length ?: 0

                // Actualizar el contador en el TextView
                tvDescWC.text = getString(R.string.char_count, charCount, maxDesc)

                // Validar si se excede el límite de caracteres
                if (charCount > maxDesc) {
                    etDesc.error = "Has excedido el límite de $maxDesc caracteres."
                    // Prevenir que se sigan escribiendo caracteres
                    etDesc.setText(s?.substring(0, maxDesc))
                    etDesc.setSelection(maxDesc) // Mover el cursor al final del texto permitido
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No aplica, pero debe llamarse
            }

            override fun afterTextChanged(s: Editable?) {
                // No aplica, pero debe llamarse
            }
        })
    }

    private fun updateDate(calendar: Calendar) {
        val dateFormat = "dd/MM/yyyy" // Formato de la fecha
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding.BDate.text = sdf.format(calendar.time)
    }

    private fun registerImagePicker() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.imageButton.setImageURI(selectedImageUri)
            }
        }
    }

    /*
    * Función para validar que el contenido de los campos sea adecuado para avanzar.
    * */
    private fun validateFields(): Boolean {
        var isValid = true

        if (binding.etAddActivityTitle.text.isNullOrEmpty()) {
            binding.etAddActivityTitle.error = "Por favor, introduce un título"
            isValid = false
        } else {
            binding.etAddActivityTitle.error = null
        }

        if (binding.hourSpinner.selectedItemPosition == 0 &&
            binding.minutesSpinner.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Por favor, selecciona una hora", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.hourSpinnerDur.selectedItemPosition == 0 &&
            binding.minutesSpinnerDur.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Por favor, selecciona una duración", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.BDate.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.etAddActivityUbi.text.isNullOrEmpty()) {
            binding.etAddActivityUbi.error = "Por favor, introduce una dirección"
            isValid = false
        } else {
            binding.etAddActivityUbi.error = null
        }

        if (binding.etAddActivityDescription.text.isNullOrEmpty()) {
            binding.etAddActivityDescription.error = "Por favor, introduce una descripción"
            isValid = false
        } else {
            binding.etAddActivityDescription.error = null
        }
        return isValid
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

}