package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MyCustomTextView(context: Context, attributeSet: AttributeSet): AppCompatTextView(context, attributeSet) {
    // initialize this class
    init {
        // call the font method
        applyRobotoFont()
    }

    private fun applyRobotoFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Roboto-Regular.ttf")
        setTypeface(typeface)
    }
}