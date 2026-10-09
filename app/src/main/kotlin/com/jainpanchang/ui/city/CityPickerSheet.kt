package com.jainpanchang.ui.city

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jainpanchang.data.repository.CityRepository
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.data.schema.CityFileEntry
import com.jainpanchang.engine.model.GeoLocation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityPickerDialog(
    cityRepository: CityRepository,
    settingsRepository: SettingsRepository,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val cities = remember(searchQuery) {
        cityRepository.searchCities(searchQuery)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(androidx.compose.ui.res.stringResource(com.jainpanchang.R.string.select_city))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = androidx.compose.ui.res.stringResource(com.jainpanchang.R.string.close))
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search city, state or country...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.height(350.dp)) {
                    items(cities) { city ->
                        CityListItem(
                            city = city,
                            onSelect = {
                                scope.launch {
                                    val geo = GeoLocation(
                                        name = city.name,
                                        latitude = city.latitude,
                                        longitude = city.longitude,
                                        timezoneId = city.timezoneId
                                    )
                                    settingsRepository.setSelectedCity(geo)
                                    cityRepository.saveCity(geo, city.state, city.country)
                                    onDismiss()
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun CityListItem(
    city: CityFileEntry,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            val sub = if (city.state.isNotEmpty()) "${city.state}, ${city.country}" else city.country
            Text(
                text = "$sub • ${city.timezoneId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
