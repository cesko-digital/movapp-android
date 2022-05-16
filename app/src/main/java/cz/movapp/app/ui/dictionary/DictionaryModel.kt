package cz.movapp.app.ui.dictionary

data class DictionarySectionsData(val id: String, val main: String, val source: String, val phrases_ids: List<String>)

data class DictionaryTranslationsData(
    val id: String,

    val main_translation: String,
    val main_transcription: String,
    val main_stripped: String,
    val main_sound_url: String,
    val main_sound_local: String,

    val source_translation: String,
    val source_transcription: String,
    val source_stripped: String,
    val source_sound_url: String,
    val source_sound_local: String
)
