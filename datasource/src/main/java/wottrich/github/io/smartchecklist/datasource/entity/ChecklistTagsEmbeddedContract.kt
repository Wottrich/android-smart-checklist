package wottrich.github.io.smartchecklist.datasource.entity

import wottrich.github.io.smartchecklist.suggestion.TagContract

interface ChecklistTagsEmbeddedContract {
    val checklist: ChecklistContract
    val tags: List<TagContract>
}