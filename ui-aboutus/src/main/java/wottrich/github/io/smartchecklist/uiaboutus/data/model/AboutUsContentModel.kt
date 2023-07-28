package wottrich.github.io.smartchecklist.uiaboutus.data.model

data class AboutUsContentModel(
    val versionName: String,
    val versionNumber: String
) {
    fun getVersionApp(): String {
        return "$versionName($versionNumber)"
    }
}