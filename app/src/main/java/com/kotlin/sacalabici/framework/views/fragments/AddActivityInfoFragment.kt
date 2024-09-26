package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kotlin.sacalabici.databinding.FragmentActivityInfoBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ActivitiesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddActivityInfoFragment: Fragment() {
    private var type: String? = null
    private lateinit var listener: OnFragmentInteractionListener
    private lateinit var viewModel: ActivitiesViewModel
    private var _binding: FragmentActivityInfoBinding? = null
    private val binding get() = _binding!!

    /*
    * Permite que el fragmento se comunique con la actividad
    * Le notificará cuando sea una Rodada y se seleccione "Siguiente"
    * */
    interface OnFragmentInteractionListener {
        fun onNextClicked(type: String)
        fun receiveInformation(title: String,
                               date: String,
                               hour: String,
                               minutes: String,
                               hourDur: String,
                               minutesDur: String,
                               ubi: String,
                               description: String)
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

        Log.d("RegisterActivityInfoFragment", "Tipo en onCreate: $type")
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

        initializeListeners()

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

                // Enviar información al ViewModel
                listener.receiveInformation(title, date, hour, minutes, hourDur, minutesDur, ubi, description)
                listener.onNextClicked(type.toString())
            }
        }
    }

    private fun updateDate(calendar: Calendar) {
        val dateFormat = "dd/MM/yyyy" // Formato de la fecha
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        binding.BDate.text = sdf.format(calendar.time)
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