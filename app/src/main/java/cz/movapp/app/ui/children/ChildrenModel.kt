package cz.movapp.app.ui.children

data class ChildrenData(
    val id: String,

    val main_translation: String,
    val main_transcription: String,
    val main_sound_url: String,

    val source_translation: String,
    val source_transcription: String,
    val source_sound_url: String,

    val image_path: String
)