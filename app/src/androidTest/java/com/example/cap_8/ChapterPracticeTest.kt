package com.example.cap_8

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChapterPracticeTest {
    @Test
    fun validatesDateTimeAndDecimalUsingTheActualInputFields() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                fun validate(fieldId: Int, buttonId: Int, value: String): CharSequence? {
                    val field = activity.findViewById<EditText>(fieldId)
                    field.setText(value)
                    activity.findViewById<View>(buttonId).performClick()
                    return field.error
                }

                assertNotNull(validate(R.id.etDate, R.id.btnDate, "29/02/2025"))
                assertNull(validate(R.id.etDate, R.id.btnDate, "29/02/2024"))
                assertNotNull(validate(R.id.etTime, R.id.btnTime, "24:00"))
                assertNull(validate(R.id.etTime, R.id.btnTime, "23:59"))
                assertNotNull(validate(R.id.etNumberDecimal, R.id.btnDecimal, "12,345"))
                assertNull(validate(R.id.etNumberDecimal, R.id.btnDecimal, "12,50"))
                assertNull(validate(R.id.etNumberDecimal, R.id.btnDecimal, "12.50"))
                assertNotNull(validate(R.id.mactvTags, R.id.btnTags, "Kotlin, kotlin"))
                assertNull(validate(R.id.mactvTags, R.id.btnTags, "Android, Kotlin"))
            }
        }
    }

    @Test
    fun updatesTheCounterAndShowsRegistrationErrors() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<EditText>(R.id.etMultiline).setText("x".repeat(501))
                assertEquals(
                    "500 / 500 caracteres",
                    activity.findViewById<TextView>(R.id.tvCharacterCount).text.toString()
                )

                val registrationEmail = activity.findViewById<TextInputLayout>(R.id.tilEmail)
                activity.findViewById<View>(R.id.btnRegister).performClick()
                assertEquals("Requerido", registrationEmail.error.toString())

                registrationEmail.editText?.setText("ana@example.com")
                assertNull(registrationEmail.error)
            }
        }
    }
}
