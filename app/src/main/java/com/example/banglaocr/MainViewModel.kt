package com.example.banglaocr

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream

@Serializable
data class sendResponse(
    val Ack_Msg: String,
    val recived: Int
)

@Serializable
data class ImageToTextResponse(
    val error: Boolean,
    val result: String? = null,
    val message: String? = null
)

class MainViewModel : ViewModel() {

    private val _bitmaps = MutableStateFlow<Bitmap?>(null)
    val bitmaps = _bitmaps.asStateFlow()

    private val _bitmapPlate = MutableStateFlow<Bitmap?>(null)
    val bitmapPlate = _bitmapPlate.asStateFlow()

    val _text= MutableStateFlow<String>("")
    val text = _text.asStateFlow()
    var immutableText = ""

    private val token = "af39d00bcf5045a8262245da6c9a5d9843ad6bd7"

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value = bitmap
    }

    fun convertToBitmap(outputByteArray: ByteArray) {
        _bitmapPlate.value =
            BitmapFactory.decodeByteArray(outputByteArray, 0, outputByteArray.size);
    }

    private fun cleanString(message: String): String {

        val newMessage = message.replace('1', '১')
            .replace('2', '২')
            .replace('3', '৩')
            .replace('4', '৪')
            .replace('5', '৫')
            .replace('6', '৬')
            .replace('7', '৭')
            .replace('8', '৮')
            .replace('9', '৯')
            .replace('0', '০')
            .replace("<br />","")
            .replace("\\n","")
        println(newMessage)
        return newMessage
    }

    fun extractText(imageByteArray: ByteArray) {
        viewModelScope.launch {

            try {
                //val base64 = encodeImage(imageBitmap)
                println("entered")
                println(token)

                val response = client.post("https://www.imagetotext.info/api/imageToText") {
                    header("Authorization", "Bearer $token")
                    setBody(MultiPartFormDataContent(
                        formData {
                            append("image", imageByteArray, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                            }
                            )
                        }
                    ))
                }
                println(response.toString())
                if (response.status.isSuccess()) {
                    val extractedText = response.body<ImageToTextResponse>()
                    if (extractedText.error) {
                        immutableText = extractedText.message.toString()
                    } else {
                        println(extractedText.result.toString())
                        immutableText = cleanString(extractedText.result.toString())
                    }
                    println(response.bodyAsText())
                } else {
                    immutableText = response.bodyAsText()
                }
                println(response.toString())
            } catch (e: Throwable) {
                println(e.message + "E Thr")
                immutableText = e.message.toString()
            }
            _text.value = immutableText
            println(text.value.length)
        }

    }
}