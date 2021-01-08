package net.inferno.quakereport.ui.quakes

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.compose.navigate
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import net.inferno.quakereport.R
import net.inferno.quakereport.compose.AmbientNavController
import net.inferno.quakereport.extension.isInternetException
import net.inferno.quakereport.theme.AppTheme
import net.inferno.quakereport.ui.main.SETTINGS_LABEL
import net.inferno.quakereport.view.RefreshDrawable

@Composable
fun QuakesList(
    viewModel: QuakesViewModel = viewModel(),
) {
    val earthQuakes = viewModel.requestData().collectAsLazyPagingItems()

    val context = AmbientContext.current
    val navController = AmbientNavController.current

    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = {
                        isRefreshing = true
                        earthQuakes.refresh()
                    }) {
                        Icon(Icons.Default.Refresh)
                    }

                    IconButton(onClick = {
                        navController.navigate(SETTINGS_LABEL)
                    }) {
                        Icon(Icons.Default.Settings)
                    }
                }
            )
        },
    ) {
        val pagingState = earthQuakes.loadState

        when (pagingState.refresh) {
            is LoadState.Loading -> {
                LoadingView()
            }
            is LoadState.Error -> {
                if ((pagingState.refresh as LoadState.Error).error.isInternetException) {
                    NetworkErrorView {
                        earthQuakes.retry()
                    }
                } else {
                    ErrorView {
                        earthQuakes.retry()
                    }
                }
            }
            is LoadState.NotLoading -> {
                isRefreshing = false

                if (earthQuakes.itemCount == 0) {
                    EmptyView()
                } else {
                    val lazyScrollState = rememberLazyListState()

                    LazyColumn(
                        state = lazyScrollState,
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        items(earthQuakes) {
                            QuakeItem(earthQuake = it!!)
                        }

                        when (pagingState.append) {
                            is LoadState.Loading -> item {
                                LoadingView()
                            }
                            is LoadState.Error -> item {
                                if ((pagingState.append as LoadState.Error).error.isInternetException) {
                                    NetworkErrorView {
                                        earthQuakes.retry()
                                    }
                                } else {
                                    ErrorView {
                                        earthQuakes.retry()
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            RefreshDrawable(
                enabled = isRefreshing,
            )
        }
    }
}

@Composable
fun ErrorView(retry: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.request_error),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
            )

            Button(
                onClick = retry,
            ) {
                Text(stringResource(id = R.string.retry))
            }
        }
    }
}

@Composable
fun NetworkErrorView(retry: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.request_network_error),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(8.dp)
            )

            Button(
                onClick = retry,
            ) {
                Text(stringResource(id = R.string.retry))
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(32.dp)
            .fillMaxSize(),
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
    ) {
        Text(
            text = stringResource(id = R.string.empty_list),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Page",
)
@Composable
fun QuakesListPreview() {
    AppTheme(isDarkTheme = false) {
        QuakesList(
            QuakesViewModel()
        )
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Page",
)
@Composable
fun QuakesListPreviewDark() {
    AppTheme(isDarkTheme = true) {
        QuakesList(
            QuakesViewModel()
        )
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4,
    group = "State",
)
@Composable
fun EmptyViewPreview() {
    EmptyView()
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4,
    group = "State",
)
@Composable
fun LoadingViewPreview() {
    LoadingView()
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4,
    group = "State",
)
@Composable
fun ErrorViewPreview() {
    ErrorView {}
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_4,
    group = "State",
)
@Composable
fun NetworkErrorViewPreview() {
    NetworkErrorView {}
}