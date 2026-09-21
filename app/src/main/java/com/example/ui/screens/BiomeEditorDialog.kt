package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CustomBiome
import com.example.ui.components.BiomeVoxelPreview
import com.example.ui.components.parseHexColor

@Composable
fun BiomeEditorDialog(
    initialBiome: CustomBiome?,
    packId: Long,
    onDismiss: () -> Unit,
    onSave: (CustomBiome) -> Unit
) {
    var name by remember { mutableStateOf(initialBiome?.name ?: "Astral Glade") }
    var identifier by remember {
        mutableStateOf(initialBiome?.identifier ?: "magic:${name.lowercase().replace(" ", "_")}")
    }
    var dimension by remember { mutableStateOf(initialBiome?.dimension ?: "overworld") }
    var temperature by remember { mutableFloatStateOf(initialBiome?.temperature ?: 0.7f) }
    var downfall by remember { mutableFloatStateOf(initialBiome?.downfall ?: 0.8f) }

    var skyColor by remember { mutableStateOf(initialBiome?.skyColor ?: "#5B2C82") }
    var fogColor by remember { mutableStateOf(initialBiome?.fogColor ?: "#9B5DE5") }
    var waterColor by remember { mutableStateOf(initialBiome?.waterColor ?: "#00F5D4") }
    var foliageColor by remember { mutableStateOf(initialBiome?.foliageColor ?: "#FF595E") }
    var grassColor by remember { mutableStateOf(initialBiome?.grassColor ?: "#70E000") }

    var topBlock by remember { mutableStateOf(initialBiome?.topBlock ?: "minecraft:moss_block") }
    var midBlock by remember { mutableStateOf(initialBiome?.midBlock ?: "minecraft:dirt") }
    var foundationBlock by remember { mutableStateOf(initialBiome?.foundationBlock ?: "minecraft:deepslate") }

    var ambientParticle by remember { mutableStateOf(initialBiome?.ambientParticle ?: "minecraft:falling_dust_spore") }
    var ambientSound by remember { mutableStateOf(initialBiome?.ambientSound ?: "ambient.crystal_caverns.loop") }
    var description by remember {
        mutableStateOf(initialBiome?.description ?: "An ethereal biome featuring glowing canopy and starlit mist.")
    }

    val previewBiome = CustomBiome(
        id = initialBiome?.id ?: 0,
        packId = packId,
        name = name,
        identifier = identifier,
        dimension = dimension,
        temperature = temperature,
        downfall = downfall,
        skyColor = skyColor,
        fogColor = fogColor,
        waterColor = waterColor,
        foliageColor = foliageColor,
        grassColor = grassColor,
        topBlock = topBlock,
        midBlock = midBlock,
        foundationBlock = foundationBlock,
        ambientParticle = ambientParticle,
        ambientSound = ambientSound,
        description = description
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(680.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("biome_editor_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialBiome == null) "Create Custom Biome" else "Edit Biome",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(Modifier.height(8.dp))

                    // Live Interactive Voxel Preview
                    Text(
                        text = "Live Bedrock Landscape Preview",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(6.dp))
                    BiomeVoxelPreview(biome = previewBiome, heightDp = 170)

                    Spacer(Modifier.height(16.dp))

                    // Biome Name & Identifier
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (initialBiome == null) {
                                identifier = "magic:${it.lowercase().trim().replace(" ", "_")}"
                            }
                        },
                        label = { Text("Biome Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("biome_name_input"),
                        singleLine = true
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Identifier (e.g. magic:astral_glade)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("biome_identifier_input"),
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    // Dimension selection
                    Text(
                        text = "Dimension",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("overworld", "nether", "the_end").forEach { dim ->
                            FilterChip(
                                selected = dimension == dim,
                                onClick = { dimension = dim },
                                label = { Text(dim.replace("_", " ").uppercase()) }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Climate Sliders
                    Text(
                        text = "Climate: Temperature (${(temperature * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 0.0f..2.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Precipitation / Downfall (${(downfall * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Slider(
                        value = downfall,
                        onValueChange = { downfall = it },
                        valueRange = 0.0f..1.0f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Color Palettes
                    Text(
                        text = "Biome Color Palette",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ColorPickerRow("Sky Atmosphere", skyColor, listOf("#5B2C82", "#1D3557", "#0B090A", "#4A154B", "#0077B6", "#FFB703")) {
                        skyColor = it
                    }

                    ColorPickerRow("Horizon Fog", fogColor, listOf("#9B5DE5", "#7B2CBF", "#457B9D", "#161A1D", "#06D6A0", "#FB8500")) {
                        fogColor = it
                    }

                    ColorPickerRow("Water Tint", waterColor, listOf("#00F5D4", "#A8DADC", "#0077B6", "#72EFDD", "#F72585", "#D90429")) {
                        waterColor = it
                    }

                    ColorPickerRow("Foliage / Canopy", foliageColor, listOf("#FF595E", "#F72585", "#B5179E", "#06D6A0", "#9D0208", "#FFCA3A")) {
                        foliageColor = it
                    }

                    ColorPickerRow("Grass Turf Tint", grassColor, listOf("#70E000", "#3A0CA3", "#118AB2", "#55FF99", "#6A040F", "#9D4EDD")) {
                        grassColor = it
                    }

                    Spacer(Modifier.height(14.dp))

                    // Surface Blocks
                    Text(
                        text = "Surface Generation Blocks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))

                    BlockSelectorRow("Top Block", topBlock, listOf(
                        "minecraft:moss_block",
                        "minecraft:grass_block",
                        "minecraft:amethyst_block",
                        "minecraft:prismarine",
                        "minecraft:soul_sand",
                        "minecraft:sculk"
                    )) { topBlock = it }

                    BlockSelectorRow("Subsurface", midBlock, listOf(
                        "minecraft:dirt",
                        "minecraft:calcite",
                        "minecraft:dark_prismarine",
                        "minecraft:soul_soil",
                        "minecraft:sand"
                    )) { midBlock = it }

                    BlockSelectorRow("Foundation", foundationBlock, listOf(
                        "minecraft:deepslate",
                        "minecraft:stone",
                        "minecraft:tuff",
                        "minecraft:sea_lantern",
                        "minecraft:blackstone"
                    )) { foundationBlock = it }

                    Spacer(Modifier.height(14.dp))

                    // Particles & Ambience
                    Text(
                        text = "Magical Particle Ambience",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "minecraft:falling_dust_spore" to "Spore Motes",
                            "minecraft:portal_reverse" to "Portal Spark",
                            "minecraft:bubble_pop" to "Aquatic Wisps",
                            "minecraft:flame" to "Fireflies"
                        ).forEach { (part, label) ->
                            FilterChip(
                                selected = ambientParticle == part,
                                onClick = { ambientParticle = part },
                                label = { Text(label) }
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Biome Description / Lore") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(previewBiome)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("save_biome_button")
                    ) {
                        Text("Save Biome", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorPickerRow(
    label: String,
    currentColor: String,
    presets: List<String>,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(currentColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            presets.forEach { hex ->
                val col = parseHexColor(hex)
                val isSelected = currentColor.equals(hex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(col)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onSelect(hex) }
                )
            }
        }
    }
}

@Composable
private fun BlockSelectorRow(
    label: String,
    currentBlock: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ${currentBlock.removePrefix("minecraft:")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.take(4).forEach { block ->
                val name = block.removePrefix("minecraft:").replace("_", " ")
                FilterChip(
                    selected = currentBlock == block,
                    onClick = { onSelect(block) },
                    label = { Text(name, fontSize = 11.sp) }
                )
            }
        }
    }
}
