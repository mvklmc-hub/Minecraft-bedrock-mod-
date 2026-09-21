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
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.MagicalCreature
import com.example.ui.components.CreatureVoxelPreview
import com.example.ui.components.parseHexColor

@Composable
fun CreatureEditorDialog(
    initialCreature: MagicalCreature?,
    packId: Long,
    availableBiomes: List<CustomBiome>,
    onDismiss: () -> Unit,
    onSave: (MagicalCreature) -> Unit
) {
    var name by remember { mutableStateOf(initialCreature?.name ?: "Starlight Phoenix") }
    var identifier by remember {
        mutableStateOf(initialCreature?.identifier ?: "magic:${name.lowercase().replace(" ", "_")}")
    }
    var archetype by remember { mutableStateOf(initialCreature?.archetype ?: "Pegasus") }
    var health by remember { mutableIntStateOf(initialCreature?.health ?: 40) }
    var attackDamage by remember { mutableIntStateOf(initialCreature?.attackDamage ?: 8) }
    var movementSpeed by remember { mutableFloatStateOf(initialCreature?.movementSpeed ?: 0.32f) }

    var canFly by remember { mutableStateOf(initialCreature?.canFly ?: true) }
    var canSwim by remember { mutableStateOf(initialCreature?.canSwim ?: false) }
    var isTameable by remember { mutableStateOf(initialCreature?.isTameable ?: true) }
    var tameItem by remember { mutableStateOf(initialCreature?.tameItem ?: "minecraft:golden_apple") }
    var specialAbility by remember {
        mutableStateOf(initialCreature?.specialAbility ?: "Starlight Glide & Healing Aura")
    }

    var primaryColor by remember { mutableStateOf(initialCreature?.primaryColor ?: "#9D4EDD") }
    var secondaryColor by remember { mutableStateOf(initialCreature?.secondaryColor ?: "#72EFDD") }

    var spawnBiomes by remember {
        mutableStateOf(
            initialCreature?.spawnBiomes
                ?: availableBiomes.firstOrNull()?.identifier
                ?: "magic:celestial_grove"
        )
    }
    var spawnWeight by remember { mutableIntStateOf(initialCreature?.spawnWeight ?: 30) }
    var minGroupCount by remember { mutableIntStateOf(initialCreature?.minGroupCount ?: 1) }
    var maxGroupCount by remember { mutableIntStateOf(initialCreature?.maxGroupCount ?: 3) }
    var lootDrops by remember {
        mutableStateOf(initialCreature?.lootDrops ?: "magic:starlight_feather (1-2), minecraft:glowstone_dust (2-4)")
    }
    var description by remember {
        mutableStateOf(initialCreature?.description ?: "A majestic starlight guardian that glides gracefully through custom biomes.")
    }

    val previewCreature = MagicalCreature(
        id = initialCreature?.id ?: 0,
        packId = packId,
        name = name,
        identifier = identifier,
        archetype = archetype,
        health = health,
        attackDamage = attackDamage,
        movementSpeed = movementSpeed,
        canFly = canFly,
        canSwim = canSwim,
        isTameable = isTameable,
        tameItem = tameItem,
        specialAbility = specialAbility,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor,
        spawnBiomes = spawnBiomes,
        spawnWeight = spawnWeight,
        minGroupCount = minGroupCount,
        maxGroupCount = maxGroupCount,
        lootDrops = lootDrops,
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
                .testTag("creature_editor_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialCreature == null) "Create Magical Creature" else "Edit Creature",
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
                        text = "Live 3D Voxel Model Preview",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(6.dp))
                    CreatureVoxelPreview(creature = previewCreature, heightDp = 175)

                    Spacer(Modifier.height(14.dp))

                    // Name & Identifier
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (initialCreature == null) {
                                identifier = "magic:${it.lowercase().trim().replace(" ", "_")}"
                            }
                        },
                        label = { Text("Creature Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("creature_name_input"),
                        singleLine = true
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = identifier,
                        onValueChange = { identifier = it },
                        label = { Text("Identifier (e.g. magic:void_drake)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("creature_identifier_input"),
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    // Archetype Picker
                    Text(
                        text = "Creature Archetype",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Pegasus", "Void Drake", "Spirit Fox", "Arcane Golem", "Ember Sprite").forEach { arch ->
                            FilterChip(
                                selected = archetype == arch,
                                onClick = {
                                    archetype = arch
                                    if (arch == "Void Drake" || arch == "Pegasus" || arch == "Ember Sprite") {
                                        canFly = true
                                    } else {
                                        canFly = false
                                    }
                                },
                                label = { Text(arch, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Health & Combat Stats
                    Text(
                        text = "Vitality & Combat: $health HP | $attackDamage ATK",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("Health: $health HP", style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = health.toFloat(),
                        onValueChange = { health = it.toInt() },
                        valueRange = 10f..250f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Attack Damage: $attackDamage Damage", style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = attackDamage.toFloat(),
                        onValueChange = { attackDamage = it.toInt() },
                        valueRange = 0f..35f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Movement Speed: ${(movementSpeed * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = movementSpeed,
                        onValueChange = { movementSpeed = it },
                        valueRange = 0.15f..0.55f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Capabilities Checkboxes
                    Text(
                        text = "Behaviors & Traits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = canFly, onCheckedChange = { canFly = it })
                            Text("Can Fly", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = canSwim, onCheckedChange = { canSwim = it })
                            Text("Can Swim", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isTameable, onCheckedChange = { isTameable = it })
                            Text("Tameable", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    if (isTameable) {
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = tameItem,
                            onValueChange = { tameItem = it },
                            label = { Text("Taming Item (e.g. minecraft:golden_apple)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = specialAbility,
                        onValueChange = { specialAbility = it },
                        label = { Text("Magical Special Ability") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(14.dp))

                    // Color Palettes
                    Text(
                        text = "Model Palette & Aura",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    ColorRow("Primary Body Color", primaryColor, listOf("#9D4EDD", "#2B0938", "#FF9E00", "#3D405B", "#FF5400", "#0077B6")) {
                        primaryColor = it
                    }
                    ColorRow("Aura & Wings Accent", secondaryColor, listOf("#72EFDD", "#E0AAFF", "#48CAE4", "#E07A5F", "#FFD60A", "#55FF99")) {
                        secondaryColor = it
                    }

                    Spacer(Modifier.height(14.dp))

                    // Spawning Biomes
                    Text(
                        text = "Spawn In Biome:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableBiomes.take(4).forEach { b ->
                            FilterChip(
                                selected = spawnBiomes == b.identifier,
                                onClick = { spawnBiomes = b.identifier },
                                label = { Text(b.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = lootDrops,
                        onValueChange = { lootDrops = it },
                        label = { Text("Loot Drops (e.g. magic:void_scale (2-3))") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Creature Lore & Ecology") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Footer
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
                                onSave(previewCreature)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("save_creature_button")
                    ) {
                        Text("Save Creature", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorRow(
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
