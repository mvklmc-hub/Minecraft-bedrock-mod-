package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.CustomBiome
import com.example.data.MagicalCreature
import com.example.data.ModPack
import com.example.generator.BedrockJsonGenerator
import com.example.ui.ModPackViewModel
import com.example.ui.components.BedrockCodeViewer
import com.example.ui.components.BiomeVoxelPreview
import com.example.ui.components.CreatureVoxelPreview
import com.example.ui.components.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModPackMainScreen(
    viewModel: ModPackViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val packs by viewModel.allPacks.collectAsStateWithLifecycle()
    val activePack by viewModel.activePack.collectAsStateWithLifecycle()
    val biomes by viewModel.activeBiomes.collectAsStateWithLifecycle()
    val creatures by viewModel.activeCreatures.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showNewPackDialog by remember { mutableStateOf(false) }
    var editingBiome by remember { mutableStateOf<CustomBiome?>(null) }
    var showBiomeEditor by remember { mutableStateOf(false) }
    var editingCreature by remember { mutableStateOf<MagicalCreature?>(null) }
    var showCreatureEditor by remember { mutableStateOf(false) }
    var showPackDropdown by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showPackDropdown = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("pack_dropdown_selector")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activePack?.name ?: "Select Modpack",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "Switch Pack",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Minecraft Bedrock 1.21.0+",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showPackDropdown,
                            onDismissRequest = { showPackDropdown = false }
                        ) {
                            packs.forEach { p ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(p.name, fontWeight = FontWeight.Bold)
                                            Text("v${p.version} • by ${p.author}", style = MaterialTheme.typography.labelSmall)
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectPack(p.id)
                                        showPackDropdown = false
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val pack = activePack
                            if (pack != null) {
                                val res = BedrockJsonGenerator.savePackToDownloads(
                                    context = context,
                                    pack = pack,
                                    biomes = biomes,
                                    creatures = creatures
                                )
                                Toast.makeText(context, res.second, Toast.LENGTH_LONG).show()
                                selectedTab = 2
                            } else {
                                Toast.makeText(context, "Please select or create a modpack first", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("download_pack_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Modpack",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { showNewPackDialog = true },
                        modifier = Modifier.testTag("create_new_pack_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create New Modpack",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (activePack != null && selectedTab < 2) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (selectedTab == 0) {
                            editingBiome = null
                            showBiomeEditor = true
                        } else {
                            editingCreature = null
                            showCreatureEditor = true
                        }
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = {
                        Text(
                            text = if (selectedTab == 0) "New Biome" else "New Creature",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag(if (selectedTab == 0) "add_biome_fab" else "add_creature_fab")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_bedrock),
                    contentDescription = "Minecraft Bedrock Biomes & Magical Creatures Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "BEDROCK ADDON WORKSHOP",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Custom Biomes & Magical Creatures",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Landscape, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Biomes (${biomes.size})", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Creatures (${creatures.size})", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Addon JSON & Export", fontWeight = FontWeight.SemiBold) }
                )
            }

            // Tab Content
            activePack?.let { currentPack ->
                when (selectedTab) {
                    0 -> BiomesListTab(
                        pack = currentPack,
                        biomes = biomes,
                        onAddPreset = { viewModel.addQuickPresetBiome(currentPack.id) },
                        onEdit = {
                            editingBiome = it
                            showBiomeEditor = true
                        },
                        onDelete = { viewModel.deleteBiome(it.id) }
                    )
                    1 -> CreaturesListTab(
                        pack = currentPack,
                        creatures = creatures,
                        onAddPreset = { viewModel.addQuickPresetCreature(currentPack.id) },
                        onEdit = {
                            editingCreature = it
                            showCreatureEditor = true
                        },
                        onDelete = { viewModel.deleteCreature(it.id) }
                    )
                    2 -> BedrockCodeViewer(
                        pack = currentPack,
                        biomes = biomes,
                        creatures = creatures
                    )
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No mod pack selected. Tap + above to create one!")
            }
        }
    }

    // Dialogs
    if (showNewPackDialog) {
        NewPackDialog(
            onDismiss = { showNewPackDialog = false },
            onCreate = { name, desc, author ->
                viewModel.createPack(name, desc, author)
                showNewPackDialog = false
            }
        )
    }

    if (showBiomeEditor && activePack != null) {
        BiomeEditorDialog(
            initialBiome = editingBiome,
            packId = activePack!!.id,
            onDismiss = { showBiomeEditor = false },
            onSave = { biome ->
                viewModel.saveBiome(biome)
                showBiomeEditor = false
            }
        )
    }

    if (showCreatureEditor && activePack != null) {
        CreatureEditorDialog(
            initialCreature = editingCreature,
            packId = activePack!!.id,
            availableBiomes = biomes,
            onDismiss = { showCreatureEditor = false },
            onSave = { creature ->
                viewModel.saveCreature(creature)
                showCreatureEditor = false
            }
        )
    }
}

@Composable
private fun BiomesListTab(
    pack: ModPack,
    biomes: List<CustomBiome>,
    onAddPreset: () -> Unit,
    onEdit: (CustomBiome) -> Unit,
    onDelete: (CustomBiome) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Quick preset bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Custom Landscapes (${biomes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = onAddPreset,
                    modifier = Modifier.testTag("add_biome_preset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Add Preset Biome", fontSize = 12.sp)
                }
            }
        }

        if (biomes.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "No Custom Biomes Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Design enchanted forests, crystal caverns, or starlight spires with custom sky, fog, and foliage.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(biomes, key = { it.id }) { biome ->
            BiomeCard(
                biome = biome,
                onEdit = { onEdit(biome) },
                onDelete = { onDelete(biome) }
            )
        }

        item {
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
private fun BiomeCard(
    biome: CustomBiome,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("biome_card_${biome.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Interactive 2.5D Voxel Landscape Preview
            BiomeVoxelPreview(biome = biome, heightDp = 180)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = biome.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = biome.identifier,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Biome", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Biome", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = biome.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Palette pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Palette:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                listOf(
                    biome.skyColor to "Sky",
                    biome.fogColor to "Fog",
                    biome.waterColor to "Water",
                    biome.foliageColor to "Foliage",
                    biome.grassColor to "Grass"
                ).forEach { (colorHex, tip) ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(colorHex))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    )
                }

                Spacer(Modifier.weight(1f))
                Text(
                    text = "Top: ${biome.topBlock.removePrefix("minecraft:")}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CreaturesListTab(
    pack: ModPack,
    creatures: List<MagicalCreature>,
    onAddPreset: () -> Unit,
    onEdit: (MagicalCreature) -> Unit,
    onDelete: (MagicalCreature) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Magical Creatures (${creatures.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = onAddPreset,
                    modifier = Modifier.testTag("add_creature_preset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Add Preset Creature", fontSize = 12.sp)
                }
            }
        }

        if (creatures.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "No Magical Creatures Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sculpt winged Pegasi, Void Drakes, Spirit Foxes, and Arcane Golems that spawn in your custom biomes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(creatures, key = { it.id }) { creature ->
            CreatureCard(
                creature = creature,
                onEdit = { onEdit(creature) },
                onDelete = { onDelete(creature) }
            )
        }

        item {
            Spacer(Modifier.height(60.dp))
        }
    }
}

@Composable
private fun CreatureCard(
    creature: MagicalCreature,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("creature_card_${creature.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // 3D Voxel Preview
            CreatureVoxelPreview(creature = creature, heightDp = 180)

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = creature.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = creature.identifier,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Creature", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Creature", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = creature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spawns in: ${creature.spawnBiomes.substringAfter(":")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Drops: ${creature.lootDrops.take(24)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NewPackDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("Bedrock Explorer") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Bedrock Modpack", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Modpack Name (e.g. Celestial Odyssey)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_pack_name_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Creator / Author") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, description, author)
                    }
                },
                modifier = Modifier.testTag("confirm_create_pack_button")
            ) {
                Text("Create Pack")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
