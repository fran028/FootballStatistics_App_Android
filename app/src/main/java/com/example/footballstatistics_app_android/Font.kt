package com.example.footballstatistics_app_android

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont

/*
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Lobster Two")

val fontFamily = FontFamily(
    Font(
        googleFont = fontName,
        fontProvider = provider,
        weight = FontWeight.Bold,
        style = FontStyle.Italic
    )
)
object AppFont {
    @Composable
    fun regular(): FontFamily {
        val context = LocalContext.current
        return FontFamily(
            Font(
                //loader = FontLoader(context),
                resId = R.font., // Your regular font resource ID
                weight = FontWeight.Normal,
                style = FontStyle.Normal
            )
        )
    }

    @Composable
    fun bold(): FontFamily {
        val context = LocalContext.current
        return FontFamily(
            Font(
               // loader = FontLoader(context),
                //resId = R.font.roboto_bold, // Your bold font resource ID
                weight = FontWeight.Bold,
                style = FontStyle.Normal
            )
        )
    }

    // Add other styles (italic, etc.) as needed
}*/