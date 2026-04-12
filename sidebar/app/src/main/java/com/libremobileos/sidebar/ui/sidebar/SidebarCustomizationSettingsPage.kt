package com.libremobileos.sidebar.ui.sidebar

import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.settingslib.spa.framework.compose.LocalNavController
import com.android.settingslib.spa.framework.compose.NavControllerWrapper
import com.android.settingslib.spa.widget.scaffold.SettingsScaffold
import com.libremobileos.sidebar.R

@Composable
fun SidebarCustomizationSettingsPage(
    sharedPrefs: SharedPreferences,
    onBack: () -> Unit = {},
    onSettingChanged: () -> Unit = {},
) {
    BackHandler(onBack = onBack)
    var transparency by remember { mutableStateOf(sharedPrefs.getFloat("slider_transparency", 0.8f)) }
    var sliderLength by remember { mutableStateOf(sharedPrefs.getInt("slider_length", 200)) }
    var position by remember { mutableStateOf(sharedPrefs.getInt("sideline_position_x", 1)) }
    var columnCount by remember { mutableStateOf(sharedPrefs.getInt("sidebar_columns", 1)) }
    var sliderWidth by remember { mutableStateOf(sharedPrefs.getInt("slider_width", 100)) }
    var iconSize by remember { mutableStateOf(sharedPrefs.getInt("sidebar_icon_size", 40)) }
    var iconPadding by remember { mutableStateOf(sharedPrefs.getInt("sidebar_icon_padding", 7)) }
    var columnSpacing by remember { mutableStateOf(sharedPrefs.getInt("sidebar_column_spacing", 4)) }
    var cornerRadius by remember { mutableStateOf(sharedPrefs.getFloat("sidebar_corner_radius", 24f)) }
    var backgroundTransparency by remember {
        mutableStateOf(sharedPrefs.getFloat("sidebar_background_transparency", 0.8f))
    }
    var showShadow by remember { mutableStateOf(sharedPrefs.getBoolean("sidebar_show_shadow", true)) }
    var tapToOpen by remember { mutableStateOf(sharedPrefs.getBoolean("sidebar_tap_to_open", false)) }
    var swipeToOpen by remember { mutableStateOf(sharedPrefs.getBoolean("sidebar_swipe_to_open", true)) }
    var hideOnGameSpace by remember { mutableStateOf(sharedPrefs.getBoolean("sidebar_hide_on_gamespace", false)) }

    fun update(block: SharedPreferences.Editor.() -> Unit) {
        sharedPrefs.edit().apply(block).apply()
        onSettingChanged()
    }

    CompositionLocalProvider(LocalNavController provides remember {
        object : NavControllerWrapper {
            override fun navigate(route: String, popUpCurrent: Boolean) = Unit
            override fun navigateBack() = onBack()
        }
    }) {
        SettingsScaffold(title = stringResource(R.string.sidebar_customization_title)) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            ) {
                item {
                    CustomizationSection(stringResource(R.string.sidebar_section_slider)) {
                        SliderSetting(
                            stringResource(R.string.sidebar_transparency_value, transparency),
                            transparency,
                            0.1f..1f,
                        ) {
                            transparency = it
                            update { putFloat("slider_transparency", it) }
                        }
                        SliderSetting(
                            stringResource(R.string.sidebar_length_value, sliderLength),
                            sliderLength.toFloat(),
                            100f..500f,
                        ) {
                            sliderLength = it.toInt()
                            update { putInt("slider_length", sliderLength) }
                        }
                        SliderSetting(
                            stringResource(R.string.sidebar_width_value, sliderWidth),
                            sliderWidth.toFloat(),
                            20f..200f,
                        ) {
                            sliderWidth = it.toInt()
                            update { putInt("slider_width", sliderWidth) }
                        }
                        Text(stringResource(R.string.sidebar_position_label))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                position == -1,
                                { position = -1; update { putInt("sideline_position_x", position) } },
                                label = { Text(stringResource(R.string.sidebar_left)) },
                            )
                            FilterChip(
                                position == 1,
                                { position = 1; update { putInt("sideline_position_x", position) } },
                                label = { Text(stringResource(R.string.sidebar_right)) },
                            )
                        }
                    }
                }
                item {
                    CustomizationSection(stringResource(R.string.sidebar_section_layout)) {
                        SliderSetting(
                            pluralStringResource(R.plurals.sidebar_columns_value, columnCount, columnCount),
                            columnCount.toFloat(),
                            1f..3f,
                            1,
                        ) {
                            columnCount = it.toInt(); update { putInt("sidebar_columns", columnCount) }
                        }
                        SliderSetting(
                            stringResource(R.string.sidebar_icon_size_value, iconSize),
                            iconSize.toFloat(),
                            30f..80f,
                        ) {
                            iconSize = it.toInt(); update { putInt("sidebar_icon_size", iconSize) }
                        }
                        SliderSetting(
                            stringResource(R.string.sidebar_icon_padding_value, iconPadding),
                            iconPadding.toFloat(),
                            4f..20f,
                        ) {
                            iconPadding = it.toInt(); update { putInt("sidebar_icon_padding", iconPadding) }
                        }
                        if (columnCount > 1) SliderSetting(
                            stringResource(R.string.sidebar_column_spacing_value, columnSpacing),
                            columnSpacing.toFloat(),
                            1f..12f,
                        ) {
                            columnSpacing = it.toInt(); update { putInt("sidebar_column_spacing", columnSpacing) }
                        }
                        SliderSetting(
                            stringResource(R.string.sidebar_corner_radius_value, cornerRadius.toInt()),
                            cornerRadius,
                            0f..32f,
                        ) {
                            cornerRadius = it; update { putFloat("sidebar_corner_radius", it) }
                        }
                        SliderSetting(
                            stringResource(R.string.sidebar_bg_transparency_value, backgroundTransparency),
                            backgroundTransparency,
                            0.1f..1f,
                        ) {
                            backgroundTransparency = it; update { putFloat("sidebar_background_transparency", it) }
                        }
                    }
                }
                item {
                    CustomizationSection(stringResource(R.string.sidebar_section_visual)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(showShadow, {
                                showShadow = it
                                update { putBoolean("sidebar_show_shadow", it) }
                            })
                            Text(
                                stringResource(R.string.sidebar_drop_shadow_title),
                                modifier = Modifier.padding(start = 12.dp),
                            )
                        }
                    }
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                stringResource(R.string.sidebar_section_miscellaneous),
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(
                                    checked = tapToOpen,
                                    onCheckedChange = {
                                        tapToOpen = it
                                        update { putBoolean("sidebar_tap_to_open", it) }
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(stringResource(R.string.sidebar_tap_to_open_title))
                                    Text(
                                        text = stringResource(R.string.sidebar_tap_to_open_summary),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Switch(
                                    checked = swipeToOpen,
                                    onCheckedChange = {
                                        swipeToOpen = it
                                        update { putBoolean("sidebar_swipe_to_open", it) }
                                    },
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(stringResource(R.string.sidebar_swipe_to_open_title))
                                    Text(
                                        text = stringResource(R.string.sidebar_swipe_to_open_summary),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Switch(
                                    checked = hideOnGameSpace,
                                    onCheckedChange = {
                                        hideOnGameSpace = it
                                        update { putBoolean("sidebar_hide_on_gamespace", it) }
                                    },
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(stringResource(R.string.sidebar_hide_on_gamespace_title))
                                    Text(
                                        text = stringResource(R.string.sidebar_hide_on_gamespace_summary),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = {
                            transparency = 0.8f; sliderLength = 200; position = 1; columnCount = 1
                            sliderWidth = 100; iconSize = 40; iconPadding = 7; columnSpacing = 4
                            cornerRadius = 24f; backgroundTransparency = 0.8f; showShadow = true
                            tapToOpen = false
                            swipeToOpen = true
                            hideOnGameSpace = false
                            update {
                                putFloat("slider_transparency", transparency); putInt("slider_length", sliderLength)
                                putInt("sideline_position_x", position); putInt("sidebar_columns", columnCount)
                                putInt("slider_width", sliderWidth); putInt("sidebar_icon_size", iconSize)
                                putInt("sidebar_icon_padding", iconPadding); putInt("sidebar_column_spacing", columnSpacing)
                                putFloat("sidebar_corner_radius", cornerRadius)
                                putFloat("sidebar_background_transparency", backgroundTransparency)
                                putBoolean("sidebar_show_shadow", showShadow)
                                putBoolean("sidebar_tap_to_open", tapToOpen)
                                putBoolean("sidebar_swipe_to_open", swipeToOpen)
                                putBoolean("sidebar_hide_on_gamespace", hideOnGameSpace)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    ) { Text(stringResource(R.string.sidebar_reset_defaults)) }
                }
            }
        }
    }
}

@Composable
private fun CustomizationSection(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SliderSetting(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
) {
    Text(title)
    Slider(value, onValueChange, Modifier.fillMaxWidth(), valueRange = range, steps = steps)
}
