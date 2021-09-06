package wottrich.github.io.tools

import androidx.navigation.compose.NamedNavArgument

interface BaseNavigationModel {
    val route: String
    val routeWithArgument: String
    val arguments: List<NamedNavArgument>
    val titleRes: Int?
}

abstract class BaseNavigationModelImpl(
    override val route: String,
    override val routeWithArgument: String = route,
    override val arguments: List<NamedNavArgument> = emptyList(),
    override val titleRes: Int? = null
) : BaseNavigationModel