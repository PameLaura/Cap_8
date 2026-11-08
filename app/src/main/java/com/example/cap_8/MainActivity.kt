package com.example.cap_8

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.example.cap_8.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 1. Declarar la variable de binding recomendada en el manual
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Inflar el diseño usando View Binding[cite: 1]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Ajuste de bordes (Edge to Edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val visibleInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(visibleInsets.left, visibleInsets.top, visibleInsets.right, visibleInsets.bottom)
            insets
        }

        ChapterPractice(this, binding.practica)

        // Subcapítulo 1.4: TextView[cite: 1]
        binding.tvTitulo.text = "Nuevo contenido"
        binding.tvTitulo.setTextColor(0xFFD32F2F.toInt())
        binding.tvTitulo.setOnClickListener {
            Toast.makeText(this, "Clic en el título", Toast.LENGTH_SHORT).show()
        }

        // Subcapítulo 1.5.4: Validación en tiempo real del Plain Text[cite: 1]
        binding.etUsuario.doOnTextChanged { text, _, _, _ ->
            Log.d("MI_TAG", "Longitud del usuario: ${text?.length ?: 0}")
            binding.etUsuario.error = if (text.isNullOrBlank()) "Campo vacío" else null
        }
        binding.etUsuario.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                return@setOnEditorActionListener false
            }
            val usuario = binding.etUsuario.text.toString().trim()
            if (usuario.isEmpty()) {
                binding.etUsuario.error = "Campo vacío"
                return@setOnEditorActionListener true
            }
            Toast.makeText(this, "Usuario ingresado: $usuario", Toast.LENGTH_SHORT).show()
            false
        }
        binding.etEjemplo.doOnTextChanged { text, _, _, _ ->
            binding.etEjemplo.error = if (text.isNullOrBlank()) "Campo vacío" else null
        }

        // Subcapítulo 1.6.4: Eventos y validación de Password en el botón[cite: 1]
        // Prueba de inicio de sesión: usuario rsaquel y contraseña de ejemplo Demo123.
        // Resultado esperado: «¡Bienvenido rsaquel!».
        binding.btnLogin.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val password = binding.etPassword.text.toString()

            binding.tilPassword.error = when {
                password.isEmpty() -> "La contraseña no puede estar vacía"
                password.length < 6 -> "Mínimo 6 caracteres"
                password.none { it in '0'..'9' } -> "Debe contener al menos un número"
                else -> null
            }
            when {
                usuario.isEmpty() -> {
                    binding.etUsuario.error = "Requerido"
                }
                binding.tilPassword.error != null -> binding.etPassword.requestFocus()
                else -> {
                    Toast.makeText(this, "¡Bienvenido $usuario!", Toast.LENGTH_SHORT).show()
                    // Limpiar el campo tras éxito[cite: 1]
                    binding.etUsuario.setText("")
                    binding.etUsuario.error = null
                    binding.etPassword.setText("")
                    binding.etUsuario.requestFocus()
                }
            }
        }
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
        }

        // Eventos y validación de PIN y Correo (Subcapítulos 1.7 y 1.8)[cite: 1]
        binding.btnValidarExtra.setOnClickListener {
            val pin = binding.etPin.text.toString()
            val email = binding.etEmail.text.toString().trim()

            // 1. Validar PIN (debe tener exactamente 4 dígitos)[cite: 1]
            when {
                pin.isEmpty() -> binding.etPin.error = "Ingresa tu PIN"
                pin.length != 4 || pin.any { !it.isDigit() } ->
                    binding.etPin.error = "El PIN debe tener 4 dígitos"
                pin == "0000" || pin == "1234" -> binding.etPin.error = "PIN demasiado obvio" // Regla de oro de seguridad[cite: 1]
                else -> binding.etPin.error = null
            }

            // 2. Validar Correo Electrónico usando la herramienta oficial de Android[cite: 1]
            when {
                email.isEmpty() -> binding.etEmail.error = "Requerido"
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.etEmail.error = "Email no válido"
                }
                else -> {
                    binding.etEmail.error = null

                    // Si ambos son válidos (no tienen errores)
                    if (binding.etPin.error == null && binding.etEmail.error == null) {
                        Toast.makeText(this, "PIN y Correo correctos", Toast.LENGTH_SHORT).show()
                        // Limpiar campos[cite: 1]
                        binding.etPin.setText("")
                        binding.etEmail.setText("")
                    }
                }
            }
        }
    }
}
