package cz.movapp.app.ui.exercise

enum class ExerciseType {
    TEXT_IDENTIFICATION,
    AUDIO_IDENTIFICATION;

    companion object {
        fun random(): ExerciseType = values().random()
    }
}