package cz.movapp.app.ui.exercise

object ExerciseConfig {
    val sizeDefault = 10
    val sizeList = arrayOf(10, 20, 30)
    val levelDefault = 0
    val levelMin = 0
    val levelMax = 1
    val levelDownTresholdScore = 50
    val levelUpTresholdScore = 100
}

data class Level (
    val wordLimitMin: Int,
    val wordLimitMax: Int,
    val choiceLimit: Int
)

val levels = arrayOf(
    Level(1, 2, 4),
    Level(2, 3, 5),
    Level(2, 3, 8),
)