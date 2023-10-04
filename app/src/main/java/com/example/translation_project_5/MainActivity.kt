package com.example.translation_project_5

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.translation_project_5.databinding.ActivityMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlin.random.Random

class MainActivity : AppCompatActivity() { //As the program is ran, checks what language is being translated to, from, and any updates in the input text
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) //initiates binding
        val view = binding.root
        setContentView(view)
        var initial = ""
        var trans = ""
        binding.source.setOnCheckedChangeListener{group, checkedId -> //checks what the user chose for source language and makes the initial language that
            when(checkedId){
                R.id.sourceEng -> initial = "English"
                R.id.sourceSpan -> initial = "Spanish"
                R.id.sourceGerm -> initial = "German"
                R.id.sourceDetect -> initial = "Auto"
            }
        }
        binding.transLang.setOnCheckedChangeListener{group, checkedId -> //checks what the user chose for translation language and makes the translation language that. contains rng for if user chose random translation.
            var rand = Random.nextInt(1,3)
            var randTwo = Random.nextInt(1,4)
            when(checkedId){
                R.id.transEng -> trans = "English"
                R.id.transSpan -> trans = "Spanish"
                R.id.transGerm -> trans = "German"
                R.id.transRand -> if(initial == "English" && rand == 1){
                    trans = "Spanish"
                }
                else if(initial == "English" && rand == 2){
                    trans = "German"
                }
                else if(initial == "Spanish" && rand == 1){
                    trans = "English"
                }
                else if(initial == "Spanish" && rand == 2){
                    trans = "German"
                }
                else if(initial == "German" && rand == 1){
                    trans = "English"
                }
                else if(initial == "German" && rand == 2){
                    trans = "Spanish"
                }
                else if(initial == "Auto" && rand == 1){
                    trans = "Spanish"
                }
                else if(initial == "Auto" && rand == 2){
                    trans = "German"
                }
                else{
                    trans = "English"
                }
            }
        }
        binding.enterText.addTextChangedListener(object : TextWatcher { //when text is changed, translates current text
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { //checks what the current text is and translates according to initial language and translation language
                var transOp: Translator
                var options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.SPANISH)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build()
                transOp = Translation.getClient(options)
                if (initial == "" || trans == "") {
                    binding.trans.text =
                        "Choose an Initial Language and a Translation Language first"
                } else if (initial == trans) {
                    binding.trans.text = "Cannot translate language to same language"
                } else {
                    if (initial == "Auto") { //if the user chose auto translate, system checks what language the user inputted and changes the input language to that
                        val languageIdentifier = LanguageIdentification.getClient()
                        languageIdentifier.identifyLanguage(binding.enterText.text.toString())
                            .addOnSuccessListener { languageCode ->
                                if (languageCode == "und") {
                                    initial = "Spanish"
                                } else {
                                    if (languageCode == "en") {
                                        initial = "English"
                                    } else if (languageCode == "es") {
                                        initial = "Spanish"
                                    } else if (languageCode == "de") {
                                        initial = "German"
                                    }
                                }
                            }
                            .addOnFailureListener {
                                //failed to identify language
                            }
                    }
                    //the following runs through every language combination and creates a translator for that
                    if (initial == "English" && trans == "German") {
                        options = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.ENGLISH)
                            .setTargetLanguage(TranslateLanguage.GERMAN)
                            .build()
                        transOp = Translation.getClient(options)
                    } else if (initial == "English" && trans == "Spanish") {
                        options = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.ENGLISH)
                            .setTargetLanguage(TranslateLanguage.SPANISH)
                            .build()
                        transOp = Translation.getClient(options)
                    } else if (initial == "Spanish" && trans == "English") {
                        options = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.SPANISH)
                            .setTargetLanguage(TranslateLanguage.ENGLISH)
                            .build()
                        transOp = Translation.getClient(options)
                    } else if (initial == "Spanish" && trans == "German") {
                        options = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.SPANISH)
                            .setTargetLanguage(TranslateLanguage.GERMAN)
                            .build()
                        transOp = Translation.getClient(options)
                    } else if (initial == "German" && trans == "Spanish") {
                        options = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.GERMAN)
                            .setTargetLanguage(TranslateLanguage.SPANISH)
                            .build()
                        transOp = Translation.getClient(options)
                    } else if (initial == "German" && trans == "English") {
                        options = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.GERMAN)
                            .setTargetLanguage(TranslateLanguage.ENGLISH)
                            .build()
                        transOp = Translation.getClient(options)
                    }
                    var conditions = DownloadConditions.Builder() //if language is not downloaded yet, downloades it
                        .requireWifi()
                        .build()
                    transOp.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            transOp.translate(binding.enterText.text.toString())
                                .addOnSuccessListener { translatedText ->
                                    binding.trans.text=translatedText
                                }
                                .addOnFailureListener { exception ->
                                    binding.trans.text="ERROR! Could not translate."
                                }
                        }
                        .addOnFailureListener { exception ->
                            binding.trans.text="ERROR! Could not download languages!"
                        }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }
}