package cz.movapp.app.ui.dictionary

data class DictionarySectionsData(val id: String, val from: String, val to: String, val translation_ids: List<String>)

data class DictionaryTranslationsData(val id: String, val translation_from: String, val transcription_from: String, val translation_to: String, val transcription_to: String, val stripped_from: String, val stripped_to: String)
