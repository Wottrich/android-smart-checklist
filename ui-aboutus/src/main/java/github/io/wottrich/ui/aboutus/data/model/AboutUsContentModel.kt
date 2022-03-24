package github.io.wottrich.ui.aboutus.data.model

data class AboutUsContentModel(
    val versionName: String,
    val versionNumber: String
) {
    fun getVersionApp(): String {
        return "$versionName($versionNumber)"
    }
}