package cz.movapp.app.ui.exercise

import cz.movapp.app.ui.dictionary.DictionaryTranslationsData

class ExerciseData (
    val numExercises: Int,
    val questions: List<Question>
)

class Question (
    /**
     * index of correct answer in phrases list
     */
    val phraseCorrectIndex: Int,
    val phrases: List<DictionaryTranslationsData>
)