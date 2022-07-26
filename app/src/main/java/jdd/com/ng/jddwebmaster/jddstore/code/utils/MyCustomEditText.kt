package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MyCustomEditText(context: Context, attributeSet: AttributeSet): AppCompatEditText(context, attributeSet) {
    // initiate the editText font
    init {
        applyEditTextFont()
    }

    private fun applyEditTextFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Roboto-Black.ttf")
        setTypeface(typeface)
    }
}