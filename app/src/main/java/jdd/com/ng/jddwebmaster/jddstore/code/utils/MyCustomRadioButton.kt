package jdd.com.ng.jddwebmaster.jddstore.code.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MyCustomRadioButton (context: Context, attributeSet: AttributeSet): AppCompatRadioButton(context, attributeSet) {

    init {
        applyRadioButtinFont()
    }

    private fun applyRadioButtinFont() {
        val typeface = Typeface.createFromAsset(context.assets, "Roboto-Black.ttf")
        setTypeface(typeface)
    }
}