package com.github.yeetologist.storyapp.view.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.github.yeetologist.storyapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener{

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                when {
                    password.isBlank() -> setError(context.getString(R.string.error_pw_empty),null)
                    password.length < 8 -> setError(context.getString(R.string.error_pw_less_than_8),null)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }
}