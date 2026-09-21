package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomBiome
import kotlin.math.cos
import kotlin.math.sin

fun parseHexColor(hex: String, fallback: Color = Color.Gray): Color {
    return try {
        val clean = hex.removePrefix("#")
        when (clean.length) {
            6 -> Color(android.graphics.Color.parseColor("#$clean"))
            8 -> Color(android.graphics.Color.parseColor("#$clean"))
            else -> fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

@Composable
fun BiomeVoxelPreview(
    biome: CustomBiome,
    modifier: Modifier = Modifier,
    heightDp: Int = 200
) {
    val skyColor = remember(biome.skyColor) { parseHexColor(biome.skyColor, Color(0xFF4A154B)) }
    val fogColor = remember(biome.fogColor) { parseHexColor(biome.fogColor, Color(0xFF7B2CBF)) }
    val grassColor = remember(biome.grassColor) { parseHexColor(biome.grassColor, Color(0xFF70E000)) }
    val waterColor = remember(biome.waterColor) { parseHexColor(biome.waterColor, Color(0xFF00F5D4)) }
    val foliageColor = remember(biome.foliageColor) { parseHexColor(biome.foliageColor, Color(0xFFF72585)) }

    val infiniteTransition = rememberInfiniteTransition(label = "biomeAnimation")
    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("biome_voxel_preview_${biome.id}")
    ) {
        // Dynamic Sky & Voxel 2.5D Canvas
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Sky & Atmospheric Fog Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(skyColor, fogColor.copy(alpha = 0.85f), fogColor.copy(alpha = 0.4f)),
                    startY = 0f,
                    endY = canvasH * 0.7f
                )
            )

            // 2. Distant mountain voxel silhouette in fog
            val mountainPath = Path().apply {
                moveTo(0f, canvasH * 0.45f)
                lineTo(canvasW * 0.2f, canvasH * 0.32f)
                lineTo(canvasW * 0.35f, canvasH * 0.42f)
                lineTo(canvasW * 0.55f, canvasH * 0.28f)
                lineTo(canvasW * 0.75f, canvasH * 0.38f)
                lineTo(canvasW, canvasH * 0.30f)
                lineTo(canvasW, canvasH)
                lineTo(0f, canvasH)
                close()
            }
            drawPath(
                path = mountainPath,
                color = fogColor.copy(alpha = 0.35f)
            )

            // 3. Isometric 3D Voxel Ground Slice
            val originX = canvasW * 0.5f
            val originY = canvasH * 0.62f
            val blockW = canvasW * 0.085f
            val blockH = blockW * 0.55f

            // Ground grid layout (5x5 isometric stepped terrain with water pool)
            val heights = arrayOf(
                intArrayOf(1, 1, 2, 2, 3),
                intArrayOf(1, 0, 1, 2, 2),
                intArrayOf(1, 0, 1, 1, 2),
                intArrayOf(0, 0, 1, 1, 1),
                intArrayOf(0, 1, 1, 1, 1)
            )

            for (row in 0 until 5) {
                for (col in 0 until 5) {
                    val isWater = (row == 1 && col == 1) || (row == 2 && col == 1)
                    val h = heights[row][col]
                    val isoX = originX + (col - row) * (blockW * 0.95f)
                    val isoY = originY + (col + row) * (blockH * 0.95f) - (h * blockH * 0.9f)

                    if (isWater) {
                        drawIsoBlock(
                            isoX = isoX,
                            isoY = isoY + blockH * 0.4f,
                            w = blockW,
                            h = blockH,
                            topColor = waterColor.copy(alpha = 0.75f),
                            leftColor = waterColor.copy(alpha = 0.5f),
                            rightColor = waterColor.copy(alpha = 0.65f)
                        )
                    } else {
                        // Grass/Top block
                        val top = grassColor
                        val dirtSide = Color(0xFF5C4033)
                        drawIsoBlock(
                            isoX = isoX,
                            isoY = isoY,
                            w = blockW,
                            h = blockH,
                            topColor = top,
                            leftColor = dirtSide.copy(alpha = 0.9f),
                            rightColor = dirtSide
                        )

                        // If on peak, place a magical crystal / foliage sprout
                        if (row == 0 && col == 4) {
                            // Foliage / Crystal cluster
                            drawIsoBlock(
                                isoX = isoX,
                                isoY = isoY - blockH * 1.1f,
                                w = blockW * 0.75f,
                                h = blockH * 0.75f,
                                topColor = foliageColor,
                                leftColor = foliageColor.copy(alpha = 0.7f),
                                rightColor = foliageColor.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // 4. Floating Magical Spores / Wisps Animation
            val particleCount = 14
            for (i in 0 until particleCount) {
                val seed = i * 137.5f
                val baseX = (canvasW * 0.15f + ((seed * 73f) % (canvasW * 0.7f)))
                val floatProg = (particlePhase + (i / particleCount.toFloat())) % 1f
                val curY = canvasH * 0.85f - (floatProg * canvasH * 0.75f)
                val wobbleX = sin(floatProg * Math.PI.toFloat() * 4f + i) * 14f
                val radius = 3.5f + (i % 3) * 1.5f

                drawCircle(
                    color = foliageColor.copy(alpha = 0.85f * (1f - floatProg)),
                    radius = radius,
                    center = Offset(baseX + wobbleX, curY)
                )
                // Center glow
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f * (1f - floatProg)),
                    radius = radius * 0.45f,
                    center = Offset(baseX + wobbleX, curY)
                )
            }
        }

        // Overlay badges & info
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(grassColor, CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = biome.dimension.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Climate indicator pill
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = "Temperature",
                        tint = NetherEmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${(biome.temperature * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Downfall",
                        tint = LapisCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${(biome.downfall * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawIsoBlock(
    isoX: Float,
    isoY: Float,
    w: Float,
    h: Float,
    topColor: Color,
    leftColor: Color,
    rightColor: Color
) {
    val halfW = w * 0.5f
    val halfH = h * 0.5f

    // Top diamond face
    val topPath = Path().apply {
        moveTo(isoX, isoY - halfH)
        lineTo(isoX + halfW, isoY)
        lineTo(isoX, isoY + halfH)
        lineTo(isoX - halfW, isoY)
        close()
    }
    drawPath(topPath, topColor)

    // Left face
    val leftPath = Path().apply {
        moveTo(isoX - halfW, isoY)
        lineTo(isoX, isoY + halfH)
        lineTo(isoX, isoY + halfH + h)
        lineTo(isoX - halfW, isoY + h)
        close()
    }
    drawPath(leftPath, leftColor)

    // Right face
    val rightPath = Path().apply {
        moveTo(isoX, isoY + halfH)
        lineTo(isoX + halfW, isoY)
        lineTo(isoX + halfW, isoY + h)
        lineTo(isoX, isoY + halfH + h)
        close()
    }
    drawPath(rightPath, rightColor)
}

val NetherEmber = Color(0xFFFF9E00)
val LapisCyan = Color(0xFF00F5D4)
