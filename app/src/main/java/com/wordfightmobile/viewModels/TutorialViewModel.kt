package com.wordfightmobile.viewModels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.wordfightmobile.R
import com.wordfightmobile.data.Slide

class TutorialViewModel: ViewModel() {
    val open = mutableStateOf(false)
    val slide = mutableIntStateOf(0)
    val slides = mutableStateListOf<Slide>()
    val slideImages = mapOf<String,Int>("word" to R.drawable.wordslide,
        "color" to R.drawable.colorslide,
        "final" to R.drawable.finalslide,
        "points" to R.drawable.pointsslide)
    val current = derivedStateOf {
        if (slides.isNotEmpty())
            slides[slide.intValue]
        else
            null
    }
    private var db = Firebase.firestore

    fun getSlides() {
        slides.clear()
        db.collection("tutorial").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val data = task.result
                data.forEach {
                    val slide = it.toObject<Slide>(Slide::class.java)
                    slides.add(slide)
                }
                slides.sortBy { it.index }
            }
        }

    }

    fun close() {
        open.value = false
    }

    fun nextSlide() {
        if (slides.isNotEmpty())
            slide.intValue = (slide.intValue + 1) % slides.size
    }
    fun prevSlide() {
        if (slides.isNotEmpty() && slide.intValue != 0)
            slide.intValue -= 1
    }
}