package com.test.storyapp.ui.customview
import com.test.storyapp.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View

class PasswordEdit : AppCompatEditText {
    private lateinit var formInput: Drawable
    private var length = 0

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
        formInput = ContextCompat.getDrawable(context, R.drawable.baseline_lock_person_24) as Drawable
        showIconFormInput()
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // do nothing
            }
            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                length = s.length
                if (!s.isNullOrEmpty() && length < 8) error = context.getString(R.string.password_validity)
            }
            override fun afterTextChanged(edt: Editable?) {

            }
        })
    }

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        context.apply {
            setTextColor(ContextCompat.getColor(this, R.color.black))
            setHintTextColor(ContextCompat.getColor(this, R.color.black))
            background = ContextCompat.getDrawable(this, R.drawable.user_form)
        }
        maxLines = 1
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        transformationMethod = PasswordTransformationMethod.getInstance()
    }
}