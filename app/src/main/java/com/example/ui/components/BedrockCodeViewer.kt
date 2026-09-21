package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.CustomBiome
import com.example.data.MagicalCreature
import com.example.data.ModPack
import com.example.generator.BedrockJsonGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class BedrockFileItem(
    val filename: String,
    val folder: String,
    val content: String
)

@Composable
fun BedrockCodeViewer(
    pack: ModPack,
    biomes: List<CustomBiome>,
    creatures: List<MagicalCreature>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val files = remember(pack, biomes, creatures) {
        val list = mutableListOf<BedrockFileItem>()
        // BP Manifest
        list.add(
            BedrockFileItem(
                filename = "BP/manifest.json",
                folder = "Behavior Pack",
                content = BedrockJsonGenerator.generateBehaviorManifest(pack)
            )
        )
        // RP Manifest
        list.add(
            BedrockFileItem(
                filename = "RP/manifest.json",
                folder = "Resource Pack",
                content = BedrockJsonGenerator.generateResourceManifest(pack)
            )
        )
        // Biome files
        for (b in biomes) {
            val name = b.identifier.substringAfter(":").ifEmpty { "custom_biome" }
            list.add(
                BedrockFileItem(
                    filename = "biomes/$name.biome.json",
                    folder = "Biomes",
                    content = BedrockJsonGenerator.generateBiomeJson(b)
                )
            )
        }
        // Creature entities
        for (c in creatures) {
            val name = c.identifier.substringAfter(":").ifEmpty { "creature" }
            list.add(
                BedrockFileItem(
                    filename = "entities/$name.json",
                    folder = "Entities",
                    content = BedrockJsonGenerator.generateCreatureBehaviorJson(c)
                )
            )
            list.add(
                BedrockFileItem(
                    filename = "spawn_rules/$name.json",
                    folder = "Spawn Rules",
                    content = BedrockJsonGenerator.generateSpawnRulesJson(c)
                )
            )
        }
        list
    }

    var selectedIndex by remember { mutableStateOf(0) }
    val currentFile = files.getOrNull(selectedIndex) ?: files.firstOrNull()
    var isCopied by remember { mutableStateOf(false) }
    var downloadStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("bedrock_code_viewer")
    ) {
        // Header & Quick Export Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bedrock Addon Files",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${files.size} JSON definitions ready for Minecraft",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val result = BedrockJsonGenerator.savePackToDownloads(
                            context = context,
                            pack = pack,
                            biomes = biomes,
                            creatures = creatures
                        )
                        downloadStatus = result.second
                        Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()

                        // Try to prompt opening in Minecraft if installed
                        try {
                            val file = BedrockJsonGenerator.exportPackToMcaddon(context, pack, biomes, creatures)
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/octet-stream")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            if (viewIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(Intent.createChooser(viewIntent, "Open with Minecraft Bedrock"))
                            }
                        } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("download_mcaddon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download Bedrock Addon",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Download .mcaddon", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        try {
                            val file = BedrockJsonGenerator.exportPackToMcaddon(
                                context = context,
                                pack = pack,
                                biomes = biomes,
                                creatures = creatures
                            )
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/zip"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_SUBJECT, "${pack.name}.mcaddon")
                                putExtra(Intent.EXTRA_TEXT, "Bedrock Modpack: ${pack.name}\n${biomes.size} biomes & ${creatures.size} magical creatures.")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share .mcaddon Mod Pack"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Exported .mcaddon file (${pack.name})", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("export_mcaddon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Bedrock Addon",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Download Status / Install Guide Banner
        if (downloadStatus != null) {
            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF003822),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D166))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF55FF99),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = downloadStatus ?: "Downloaded",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80FFDB)
                        )
                        Text(
                            text = "Tap file in Downloads folder to import directly into Minecraft Bedrock Edition.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        } else {
            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Download .mcaddon packs both Behavior & Resource packs for 1-tap Minecraft import.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // File tabs selector
        val tabScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tabScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            files.forEachIndexed { index, file ->
                FilterChip(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    label = {
                        Text(
                            text = file.filename,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Code Box Container
        currentFile?.let { file ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                color = Color(0xFF0F1218)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Code bar header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF181D26))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = file.filename,
                                style = MaterialTheme.typography.labelMedium,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(file.filename, file.content)
                                clipboard.setPrimaryClip(clip)
                                isCopied = true
                                coroutineScope.launch {
                                    delay(2000)
                                    isCopied = false
                                }
                            },
                            modifier = Modifier.testTag("copy_json_button")
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy JSON",
                                tint = if (isCopied) Color(0xFF55FF99) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isCopied) "Copied!" else "Copy JSON",
                                fontSize = 12.sp,
                                color = if (isCopied) Color(0xFF55FF99) else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Code text scrollable
                    val codeScrollState = rememberScrollState()
                    val horizScrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                            .verticalScroll(codeScrollState)
                            .horizontalScroll(horizScrollState)
                    ) {
                        Text(
                            text = file.content,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = Color(0xFF80FFDB),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
