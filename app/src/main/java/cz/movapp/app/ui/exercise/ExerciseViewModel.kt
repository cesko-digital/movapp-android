package cz.movapp.app.ui.exercise

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.movapp.android.playSound
import cz.movapp.app.App
import cz.movapp.app.BuildConfig
import cz.movapp.app.data.DictionaryDatasource
import cz.movapp.app.data.LanguagePair
import cz.movapp.app.ui.dictionary.DictionaryTranslationsData
import cz.movapp.app.ui.exercise.play.AnswerState
import cz.movapp.app.ui.exercise.play.ExercisePlayItem
import timber.log.Timber
import kotlin.random.Random

class ExerciseViewModel : ViewModel() {

    private val _exerciseState = MutableLiveData<ExerciseState>()
    val exerciseState: LiveData<ExerciseState> = _exerciseState

    private val level: Level = levels[0]

    private lateinit var exercise: ExerciseData

    private var currentExerciseIndex: Int = 0
    private var currentExerciseType: ExerciseType = ExerciseType.random()

    // startup params
    private lateinit var selectedCategories: MutableList<String>
    private var numQuestions: Int = 0


    fun prepareExercise(
        selectedCategories: MutableList<String>,
        numQuestions: Int
    ) {
        // save for exercise restart
        this.selectedCategories = selectedCategories
        this.numQuestions = numQuestions

        val category = selectedCategories.random()

        val sections = DictionaryDatasource.loadSections(App.ctx, LanguagePair.getDefault())

        val section = sections.find { category.contentEquals(it.id) }

        val translations = DictionaryDatasource.loadTranslations(App.ctx, LanguagePair.getDefault())

        val phrases = mutableListOf<DictionaryTranslationsData>()
        fillPhrases(phrases, section?.phrases_ids, translations)

        Timber.d("numPhrases matching word limit ${phrases.size}")

        val numRequiredPhrases = level.choiceLimit * numQuestions
        if (phrases.size < numRequiredPhrases) {
            Timber.i("out of required phrases count ${phrases.size} < $numRequiredPhrases")

            val numberOfPhrasesToFill = numRequiredPhrases - phrases.size

            val fillPhrasesIDs = sections
                .filter { selectedCategories.contains(it.id) && it.id != category }
                .map { it.phrases_ids }
                .flatten()
                .shuffled()

            fillPhrases(phrases, fillPhrasesIDs, translations, numberOfPhrasesToFill)
        }

        val questions = mutableListOf<Question>()
        while (questions.size < numQuestions) {

            val selectedPhrases = mutableListOf<DictionaryTranslationsData>()
            while (selectedPhrases.size < level.choiceLimit) {
                val phrase = phrases.random()
                selectedPhrases.add(phrase)
                phrases.remove(phrase)
                Timber.d("Q${questions.size} loop ${selectedPhrases.size}/${level.choiceLimit} (remaining ${phrases.size})")
            }

            questions.add(
                Question(
                    Random.nextInt(0, selectedPhrases.size),
                    selectedPhrases
                )
            )
        }

        exercise = ExerciseData(
            numQuestions,
            questions
        )
    }

    private fun fillPhrases(phrases: MutableList<DictionaryTranslationsData>, phrasesIds: List<String>?, translations: List<DictionaryTranslationsData>, limit: Int? = null) {
        var addedPhrases = 0
        run loop@{
            phrasesIds?.forEach { phraseId ->
                translations.find {
                    phraseId.contentEquals(it.id)
                }?.let {
                    val numWords = it.main_translation.trim().count { it == ' ' } + 1
                    if (numWords >= level.wordLimitMin && numWords <= level.wordLimitMax) {
                        phrases.add(it)
                        addedPhrases++
                    }
                }

                limit?.takeIf { addedPhrases >= it }?.let {
                    return@loop
                }
            }
        }
    }

    fun startNewExercise() {
        currentExerciseIndex = 0
        currentExerciseType = ExerciseType.random()
        _exerciseState.value = ExerciseState.Exercise(
            currentExerciseType,
            exercise.questions[currentExerciseIndex]
        )
    }

    fun restartExercise() {
        prepareExercise(selectedCategories, numQuestions)
        startNewExercise()
    }

    fun onAnswerSelected(answer: ExercisePlayItem): AnswerState {
        val question = exercise.questions[currentExerciseIndex]
        val correctAnswer = question.phrases[question.phraseCorrectIndex]
        if (answer.id == correctAnswer.id) {
            _exerciseState.value = ExerciseState.Complete
            return AnswerState.CORRECT
        }
        return AnswerState.WRONG
    }

    fun onNextButtonClick() {
        currentExerciseIndex++
        if (currentExerciseIndex >= exercise.numExercises) {
            // end of game
            _exerciseState.value = ExerciseState.End
        } else {
            _exerciseState.value = ExerciseState.Exercise(
                currentExerciseType,
                exercise.questions[currentExerciseIndex]
            )
        }
    }

    fun playLocalSound(context: Context, localId: String, playbackSpeed: Float = 1.0f) {
        val question = exercise.questions[currentExerciseIndex]
        val phrase = question.phrases.firstOrNull { it.id == localId } ?: return
        val toPlay = phrase.source_sound_local
        Timber.d("toPlay $toPlay")
        playSound(context, toPlay, playbackSpeed)
    }
}