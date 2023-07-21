package cz.movapp.app.ui.exercise.play

sealed class ExercisePlayItem(
    val id: String,
    val name: String,
    var answerState: AnswerState
)

class TextExercisePlayItem(
    id: String,
    name: String,
    answerState: AnswerState = AnswerState.UNANSWERED
) : ExercisePlayItem(id, name, answerState)

class AudioExercisePlayItem(
    id: String,
    name: String,
    answerState: AnswerState = AnswerState.UNANSWERED
) : ExercisePlayItem(id, name, answerState)