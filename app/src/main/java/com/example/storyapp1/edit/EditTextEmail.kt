package com.example.storyapp1.edit

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

class EditTextEmail : TextInputEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val emailRegex = "^[A-Za-z0-9]+@[A-Za-z]+\\.com\$"

                if (!s.toString().matches(emailRegex.toRegex())) {
                    setError("Email tidak Valid", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }

        })
    }
}