package com.test.storyapp.ui.customview
import com.test.storyapp.R
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

class EmailEdit : AppCompatEditText {
    private lateinit var formInput: Drawable

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }

    private fun init() {
        formInput = ContextCompat.getDrawable(context, R.drawable.baseline_alternate_email_24) as Drawable
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showIconFormInput()
                error = if (s.isNotEmpty()) {
                    if (!s.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                        context.getString(R.string.email_validity)
                    } else null
                } else null
            }

            override fun afterTextChanged(s: Editable) {} }) }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun showIconFormInput() {
        setButtonDrawables(startOfTheText = formInput)
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        context.apply {
            setTextColor(ContextCompat.getColor(this, R.color.black))
            setHintTextColor(ContextCompat.getColor(this, R.color.black))
            background = ContextCompat.getDrawable(this, R.drawable.user_form)
        }
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}