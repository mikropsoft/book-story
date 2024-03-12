package ua.acclorite.book_story.presentation.components.custom_navigation_rail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.acclorite.book_story.R
import ua.acclorite.book_story.domain.model.NavigationItem
import ua.acclorite.book_story.presentation.data.Navigator
import ua.acclorite.book_story.presentation.data.Screen
import ua.acclorite.book_story.ui.elevation

/**
 * Custom Navigation Rail. It is used to be shown on Tablets.
 */
@Composable
fun CustomNavigationRail(
    navigator: Navigator
) {
    var currentScreen: Screen? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        navigator.getCurrentScreen().collect {
            if (it == Screen.LIBRARY || it == Screen.HISTORY || it == Screen.BROWSE) {
                currentScreen = it
            }
        }
    }

    NavigationRail(
        modifier = Modifier
            .width(80.dp)
            .background(MaterialTheme.elevation())
            .padding(horizontal = 12.dp),
        containerColor = MaterialTheme.elevation()
    ) {
        Column(
            Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomNavigationRailItem(
                item = NavigationItem(
                    stringResource(id = R.string.library_screen),
                    selectedIcon = painterResource(id = R.drawable.library_screen_filled),
                    unselectedIcon = painterResource(id = R.drawable.library_screen_outlined)
                ),
                isSelected = currentScreen == Screen.LIBRARY
            ) {
                navigator.navigate(Screen.LIBRARY, false)
            }
            CustomNavigationRailItem(
                item = NavigationItem(
                    stringResource(id = R.string.history_screen),
                    selectedIcon = painterResource(id = R.drawable.history_screen_filled),
                    unselectedIcon = painterResource(id = R.drawable.history_screen_outlined)
                ),
                isSelected = currentScreen == Screen.HISTORY
            ) {
                navigator.navigate(Screen.HISTORY, false)
            }
            CustomNavigationRailItem(
                item = NavigationItem(
                    stringResource(id = R.string.browse_screen),
                    selectedIcon = painterResource(id = R.drawable.browse_screen_filled),
                    unselectedIcon = painterResource(id = R.drawable.browse_screen_outlined)
                ),
                isSelected = currentScreen == Screen.BROWSE
            ) {
                navigator.navigate(Screen.BROWSE, false)
            }
        }
    }
}