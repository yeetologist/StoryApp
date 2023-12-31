package com.github.yeetologist.storyapp.view.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.github.yeetologist.storyapp.R
import com.github.yeetologist.storyapp.data.Result
import com.github.yeetologist.storyapp.data.remote.response.LoginResponse
import com.github.yeetologist.storyapp.databinding.ActivityLoginBinding
import com.github.yeetologist.storyapp.util.Preference
import com.github.yeetologist.storyapp.view.ui.ViewModelFactory
import com.github.yeetologist.storyapp.view.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokenDataStore")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()
        playAnimation()
    }

    private fun processLogin(body: LoginResponse) {
        if (body.error) {
            showSnackbar(body.message)
        } else {
            val preferences = Preference.getInstance(dataStore)
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.yeah))
                setMessage(getString(R.string.login_successful))
                setPositiveButton(getString(R.string.next)) { _, _ ->
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_TOKEN, body.loginResult.token)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    CoroutineScope(Main).launch {
                        preferences.saveToken(body.loginResult.token)
                    }
                    finish()
                }
                create()
                show()
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(bool: Boolean) {
        binding.vLayer.visibility = if (bool) View.VISIBLE else View.GONE
        binding.progressBar.visibility = if (bool) View.VISIBLE else View.GONE
    }

    private fun setupAction() {

        binding.loginButton.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            loginViewModel.login(email, password).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            processLogin(it.data)
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showSnackbar(it.error)
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(50)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(50)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(50)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(50)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(50)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(50)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(50)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 50
        }.start()
    }
}