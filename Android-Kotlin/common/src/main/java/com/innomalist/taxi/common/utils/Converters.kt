package com.chatanoga.cab.common.utils

import androidx.databinding.InverseMethod

object Converters {
    fun genderIsMale(gender: String): Boolean {
        return gender == "male"
    }

    @InverseMethod("genderIsMale")
    fun genderFromIsMale(isMale: Boolean): String {
        return if (isMale) "male" else "female"
    }
}