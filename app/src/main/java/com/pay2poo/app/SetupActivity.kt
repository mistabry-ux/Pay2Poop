package com.pay2poo.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pay2poo.app.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private val viewModel: SetupViewModel by viewModels { SetupViewModel.Factory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.existingProfile.observe(this) { profile ->
            profile?.let {
                // Already set up — go straight to main
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        setupInputValidation()

        binding.btnGetStarted.setOnClickListener {
            val ageText = binding.etAge.text.toString().trim()
            val occupation = binding.etOccupation.text.toString().trim()
            val hoursText = binding.etHours.text.toString().trim()
            val salaryText = binding.etSalary.text.toString().trim()

            if (ageText.isEmpty() || occupation.isEmpty() || hoursText.isEmpty() || salaryText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageText.toIntOrNull()
            val hours = hoursText.toFloatOrNull()
            val salary = salaryText.replace(",", "").toDoubleOrNull()

            if (age == null || age < 16 || age > 100) {
                binding.tilAge.error = "Enter a valid age (16–100)"
                return@setOnClickListener
            }
            if (hours == null || hours < 1f || hours > 168f) {
                binding.tilHours.error = "Enter hours between 1 and 168"
                return@setOnClickListener
            }
            if (salary == null || salary <= 0) {
                binding.tilSalary.error = "Enter a valid salary"
                return@setOnClickListener
            }

            viewModel.saveProfile(age, occupation, hours, salary)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupInputValidation() {
        binding.etAge.addTextChangedListener(clearErrorWatcher { binding.tilAge.error = null })
        binding.etHours.addTextChangedListener(clearErrorWatcher { binding.tilHours.error = null })
        binding.etSalary.addTextChangedListener(clearErrorWatcher { binding.tilSalary.error = null })
    }

    private fun clearErrorWatcher(clear: () -> Unit) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { clear() }
        override fun afterTextChanged(s: Editable?) {}
    }
}
