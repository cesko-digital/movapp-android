package cz.movapp.app.ui.exercise

sealed class ExerciseState {

    /**
     * current exercise is active
     */
    class Exercise(
        val exerciseType: ExerciseType,
        val question: Question
    ) : ExerciseState()

    /**
     * current exercise solved
     */
    object Complete : ExerciseState()

    /**
     * all exercises in game done, game over
     */
    object End : ExerciseState()
}