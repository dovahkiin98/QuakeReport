package net.inferno.quakereport.ui.settings

import android.Manifest
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import net.inferno.quakereport.R
import net.inferno.quakereport.compose.AmbientNavController
import net.inferno.quakereport.compose.registerForActivityResult
import net.inferno.quakereport.data.LocationProvider
import net.inferno.quakereport.theme.AppTheme

@Composable
fun Settings() {
    val navController = AmbientNavController.current
    val context = AmbientContext.current
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    var minMag by remember {
        mutableStateOf(prefs.getFloat(PreferencesKeys.MIN_MAG, 0f))
    }
    var selectedOrderByIndex by remember {
        mutableStateOf(
            orderValues.indexOf(prefs.getString(PreferencesKeys.ORDER_BY, "time"))
        )
    }
    var nearbyOnly by remember {
        mutableStateOf(
            prefs.getBoolean(PreferencesKeys.NEARBY_ONLY, false)
        )
    }

    val locationProvider = LocationProvider(context as AppCompatActivity)

    locationProvider.addOnSuccessListener {
        prefs.edit {
            putFloat(PreferencesKeys.LATITUDE, it.latitude.toFloat())
            putFloat(PreferencesKeys.LONGITUDE, it.longitude.toFloat())
        }
    }.addOnFailureListener {
        println(it)
    }

    AmbientLifecycleOwner.current.lifecycle.addObserver(locationProvider)

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it == true) {
            prefs.edit {
                putBoolean(PreferencesKeys.NEARBY_ONLY, true)
            }

            locationProvider.requestLocation()
        }
    }

    val preferencesChangeListener = { sharedPreferences: SharedPreferences, key: String ->
        when (key) {
            PreferencesKeys.MIN_MAG -> {
                minMag = sharedPreferences.getFloat(PreferencesKeys.MIN_MAG, 0f)
            }
            PreferencesKeys.ORDER_BY -> {
                selectedOrderByIndex = orderValues.indexOf(
                    sharedPreferences.getString(PreferencesKeys.ORDER_BY, "time")
                )
            }
            PreferencesKeys.NEARBY_ONLY -> {
                nearbyOnly = sharedPreferences.getBoolean(PreferencesKeys.NEARBY_ONLY, false)
            }
        }
    }

    var showOrderByDialog by remember { mutableStateOf(false) }
    var showMinMagnitudeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            stringResource(id = R.string.back),
                        )
                    }
                },
                title = {
                    Text(stringResource(id = R.string.title_activity_settings))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .verticalScroll(
                    rememberScrollState(),
                ),
        ) {
            ListItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.title_prefs),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 14.sp,
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            ListItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.minmag_title)
                    )
                },
                secondaryText = {
                    Text(
                        text = "$minMag"
                    )
                },
                modifier = Modifier
                    .clickable {
                        showMinMagnitudeDialog = true
                    }
                    .padding(horizontal = 16.dp)
            )

            ListItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.orderby_title)
                    )
                },
                secondaryText = {
                    Text(
                        text = orderText[selectedOrderByIndex]
                    )
                },
                modifier = Modifier
                    .clickable {
                        showOrderByDialog = true
                    }
                    .padding(horizontal = 16.dp)
            )

            ListItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.location_title)
                    )
                },
                secondaryText = {
                    Text(
                        text = stringResource(id = R.string.location_summary)
                    )
                },
                trailing = {
                    Switch(
                        checked = nearbyOnly,
                        onCheckedChange = {
                            if (!nearbyOnly) requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            else prefs.edit {
                                putBoolean(PreferencesKeys.NEARBY_ONLY, false)
                            }
                        },
                    )
                },
                singleLineSecondaryText = false,
                modifier = Modifier
                    .clickable {
                        if (!nearbyOnly) requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        else prefs.edit {
                            putBoolean(PreferencesKeys.NEARBY_ONLY, false)
                        }
                    }
                    .padding(horizontal = 16.dp)
            )
        }
    }

    if (showOrderByDialog) {
        OrderByDialog(
            onDismiss = {
                showOrderByDialog = false
            },
            onSelect = {
                prefs.edit {
                    putString(PreferencesKeys.ORDER_BY, orderValues[it])
                }
                showOrderByDialog = false
            },
            selectedItemIndex = selectedOrderByIndex,
        )
    }

    if (showMinMagnitudeDialog) {
        MinMagnitudeDialog(
            onDismiss = {
                showMinMagnitudeDialog = false
            },
            onSelect = {
                prefs.edit {
                    putFloat(PreferencesKeys.MIN_MAG, it)
                }
                showMinMagnitudeDialog = false
            },
            defaultValue = minMag,
        )
    }

    DisposableEffect(Unit) {
        prefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener)

        onDispose {
            prefs.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
        }
    }
}

@Composable
fun OrderByDialog(
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
    selectedItemIndex: Int = 0,
) {
    val orderText = orderText

    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                itemsIndexed(orderText) { index, text ->
                    ListItem(
                        text = {
                            Text(text)
                        },
                        icon = {
                            RadioButton(
                                selected = index == selectedItemIndex,
                                onClick = {
                                    onSelect(index)
                                }
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                onSelect(index)
                            }
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.orderby_title),
                style = MaterialTheme.typography.h6,
            )
        },
    )
}

@Composable
fun MinMagnitudeDialog(
    onDismiss: () -> Unit,
    onSelect: (Float) -> Unit,
    defaultValue: Float = 0f,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = defaultValue.toString())) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.minmag_title),
                style = MaterialTheme.typography.h6,
            )
        },
        buttons = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    onImeActionPerformed = { _, _ ->
                        onSelect(validateMinMagInput(textFieldValue.text))
                    },
                    onTextInputStarted = {
                        textFieldValue = textFieldValue.copy(
                            selection = TextRange(0, textFieldValue.text.length)
                        )
                    },
                    textStyle = AmbientTextStyle.current.copy(
                        fontSize = 16.sp,
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .onFocusChanged {
                            if (it == FocusState.Active) {
                                textFieldValue = textFieldValue.copy(
                                    selection = TextRange(0, textFieldValue.text.length)
                                )
                            }
                        }
                )

                Text(
                    text = stringResource(id = R.string.minmag_helper_text),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .padding(horizontal = 4.dp)
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                @OptIn(ExperimentalLayout::class)
                @Suppress("DEPRECATION")
                FlowRow(
                    mainAxisSize = SizeMode.Expand,
                    mainAxisAlignment = MainAxisAlignment.End,
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 12.dp
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = android.R.string.cancel))
                    }
                    TextButton(onClick = {
                        onSelect(validateMinMagInput(textFieldValue.text))
                    }) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                }
            }
        },
    )
}

fun validateMinMagInput(textValue: String) = (textValue.toFloatOrNull() ?: 0f).takeUnless {
    it > 10f || it < 0f
} ?: 0f

val orderValues = arrayOf(
    "time",
    "time-asc",
    "magnitude",
    "magnitude-asc",
)

@Composable
val orderText
    get() = stringArrayResource(id = R.array.order_text)

@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Page",
)
@Composable
fun SettingsPreview() {
    AppTheme {
        Settings()
    }
}

@Preview(
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Dialog",
)
@Composable
fun MinMagnitudeDialogPreview() {
    AppTheme {
        MinMagnitudeDialog(
            onDismiss = {

            },
            onSelect = {

            },
            defaultValue = 0f,
        )
    }
}