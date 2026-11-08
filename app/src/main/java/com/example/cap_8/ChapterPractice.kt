package com.example.cap_8

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.cap_8.databinding.ChapterPracticeBinding
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

internal class ChapterPractice(
    private val activity: AppCompatActivity,
    private val binding: ChapterPracticeBinding
) {
    private val dateFormat = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ROOT)
        .withResolverStyle(ResolverStyle.STRICT)
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)
        .withResolverStyle(ResolverStyle.STRICT)

    init {
        configureContact()
        configureDateAndTime()
        configureNumbers()
        configureSuggestions()
        configureOptions()
        configureRegistration()
    }

    private fun configureContact() = with(binding) {
        btnPhone.setOnClickListener {
            val phone = etPhone.value().replace(Regex("[\\s()\\-]"), "")
            val digits = phone.removePrefix("+51")
            if (digits.matches(Regex("[0-9]{9}"))) {
                val formatted = digits.chunked(3).joinToString(" ")
                etPhone.setText("+51 $formatted")
                report(etPhone, null, "Teléfono válido: +51 $formatted")
            } else {
                report(etPhone, "Escribe 9 dígitos, con +51 opcional", "")
            }
        }
        btnAddress.setOnClickListener {
            val error = if (etPostalAddress.value().length < 10) "Mínimo 10 caracteres" else null
            report(etPostalAddress, error, "Dirección válida")
        }
        etMultiline.doOnTextChanged { text, _, _, _ ->
            tvCharacterCount.text = "${text?.length ?: 0} / 500 caracteres"
        }
    }

    private fun configureDateAndTime() = with(binding) {
        btnPickTime.setOnClickListener {
            val time = readTime(etTime.value()) ?: LocalTime.now()
            TimePickerDialog(activity, { _, hour, minute ->
                etTime.setText(LocalTime.of(hour, minute).format(timeFormat))
                etTime.error = null
            }, time.hour, time.minute, true).show()
        }
        btnTime.setOnClickListener {
            val time = readTime(etTime.value())
            if (time == null) {
                report(etTime, "Usa HH:MM entre 00:00 y 23:59", "")
            } else {
                val minutes = time.hour * 60 + time.minute
                report(etTime, null, "Hora válida: $time ($minutes min desde medianoche)")
            }
        }
        btnPickDate.setOnClickListener {
            val date = readDate(etDate.value()) ?: LocalDate.now()
            DatePickerDialog(activity, { _, year, month, day ->
                etDate.setText(LocalDate.of(year, month + 1, day).format(dateFormat))
                etDate.error = null
            }, date.year, date.monthValue - 1, date.dayOfMonth).show()
        }
        btnDate.setOnClickListener {
            val date = readDate(etDate.value())
            if (date == null) {
                report(etDate, "Ingresa una fecha real con formato DD/MM/AAAA", "")
            } else {
                report(etDate, null, "Fecha válida: ${date.format(dateFormat)}")
            }
        }
    }

    private fun readTime(text: String): LocalTime? =
        runCatching { LocalTime.parse(text, timeFormat) }.getOrNull()

    private fun readDate(text: String): LocalDate? =
        runCatching { LocalDate.parse(text, dateFormat) }.getOrNull()?.takeIf { it.year > 0 }

    private fun configureNumbers() = with(binding) {
        btnNumber.setOnClickListener {
            val quantity = etNumber.value().toIntOrNull()
            val error = if (quantity != null && quantity in 1..999) null else "Ingresa de 1 a 999"
            report(etNumber, error, "Cantidad válida: $quantity")
        }
        btnSigned.setOnClickListener {
            val temperature = etNumberSigned.value().toIntOrNull()
            if (temperature == null || temperature !in -50..60) {
                report(etNumberSigned, "Ingresa de -50 a 60 °C", "")
            } else {
                val description = when {
                    temperature < 0 -> "Congelante"
                    temperature < 10 -> "Frío"
                    temperature < 20 -> "Templado"
                    else -> "Cálido"
                }
                report(etNumberSigned, null, "$temperature °C · $description")
            }
        }
        btnDecimal.setOnClickListener {
            val text = etNumberDecimal.value()
            val price = text.replace(',', '.').toBigDecimalOrNull()
            if (!text.matches(Regex("[0-9]+([.,][0-9]{1,2})?")) ||
                price == null || price.signum() <= 0
            ) {
                report(etNumberDecimal, "Ingresa un precio positivo con hasta 2 decimales", "")
            } else {
                val formatted = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-PE"))
                    .format(price)
                report(etNumberDecimal, null, "Precio válido: $formatted")
            }
        }
    }

    private fun configureSuggestions() = with(binding) {
        val row = android.R.layout.simple_dropdown_item_1line
        val resources = activity.resources
        actvPais.setAdapter(ArrayAdapter(activity, row, resources.getStringArray(R.array.countries_array)))
        actvPais.setOnItemClickListener { parent, _, position, _ ->
            actvPais.error = null
            showMessage("País seleccionado: ${parent.getItemAtPosition(position)}")
        }
        btnCountry.setOnClickListener {
            val country = actvPais.value()
            val error = if (country.isEmpty()) "Escribe un país" else null
            report(actvPais, error, "País: $country")
        }

        mactvTags.setAdapter(ArrayAdapter(activity, row, resources.getStringArray(R.array.tags_array)))
        mactvTags.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        btnTags.setOnClickListener {
            val tags = mactvTags.value().split(',').map(String::trim).filter(String::isNotEmpty)
            val error = when {
                tags.isEmpty() -> "Agrega al menos una etiqueta"
                tags.size > 10 -> "Máximo 10 etiquetas"
                tags.distinctBy { it.lowercase(Locale.ROOT) }.size != tags.size ->
                    "Quita las etiquetas repetidas"
                else -> null
            }
            report(mactvTags, error, tags.joinToString(prefix = "Etiquetas: "))
        }
    }

    private fun configureOptions() = with(binding) {
        val options = activity.resources.getStringArray(R.array.tools_array)
        val registry = activity.savedStateRegistry
        val stateKey = "chapter_practice_options"
        val restored = registry.consumeRestoredStateForKey(stateKey)
        var checked = restored?.getBooleanArray("checked")?.copyOf(options.size)
            ?: BooleanArray(options.size)
        registry.registerSavedStateProvider(stateKey) {
            Bundle().apply { putBooleanArray("checked", checked) }
        }
        fun showSelection() {
            val selected = options.filterIndexed { index, _ -> checked[index] }
            tvOptions.text = selected.joinToString().ifEmpty { "Ninguna herramienta seleccionada" }
        }
        showSelection()
        btnOptions.setOnClickListener {
            val draft = checked.copyOf()
            // AlertDialog usa filas CheckedTextView para esta selección múltiple.
            AlertDialog.Builder(activity)
                .setTitle("Herramientas")
                .setMultiChoiceItems(options, draft) { _, index, isChecked -> draft[index] = isChecked }
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Confirmar") { _, _ ->
                    checked = draft
                    showSelection()
                }
                .show()
        }
    }

    private fun configureRegistration() = with(binding) {
        tieNombre.doAfterTextChanged { tilNombre.error = nameError() }
        tieEmail.doAfterTextChanged { tilEmail.error = emailError() }
        tiePassword.doAfterTextChanged { tilPassword.error = passwordError() }
        tieBio.doAfterTextChanged { tilBio.error = bioError() }
        tieEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_DONE) return@setOnEditorActionListener false
            tilEmail.error = emailError()
            tilEmail.error != null
        }
        btnRegister.setOnClickListener {
            tilNombre.error = nameError()
            tilEmail.error = emailError()
            tilPassword.error = passwordError()
            tilBio.error = bioError()
            val invalid = listOf(tilNombre, tilEmail, tilPassword, tilBio)
                .firstOrNull { it.error != null }
            if (invalid == null) {
                showMessage("¡Formulario válido!")
                tiePassword.text?.clear()
                tilPassword.error = null
            } else {
                invalid.editText?.requestFocus()
            }
        }
    }

    private fun nameError(): String? =
        if (binding.tieNombre.value().isEmpty()) "Requerido" else null

    private fun emailError(): String? {
        val email = binding.tieEmail.value()
        return when {
            email.isEmpty() -> "Requerido"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Correo no válido"
            else -> null
        }
    }

    private fun passwordError(): String? = when (binding.tiePassword.text?.length ?: 0) {
        0 -> "Requerido"
        in 1..5 -> "Mínimo 6 caracteres"
        else -> null
    }

    private fun bioError(): String? =
        if ((binding.tieBio.text?.length ?: 0) > 200) "Máximo 200 caracteres" else null

    private fun EditText.value(): String = text.toString().trim()

    private fun report(field: EditText, error: String?, success: String) {
        field.error = error
        if (error == null) showMessage(success) else field.requestFocus()
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
