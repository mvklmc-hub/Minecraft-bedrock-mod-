package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Shield
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
import com.example.data.MagicalCreature
import kotlin.math.sin

@Composable
fun CreatureVoxelPreview(
    creature: MagicalCreature,
    modifier: Modifier = Modifier,
    heightDp: Int = 190
) {
    val primaryColor = remember(creature.primaryColor) {
        parseHexColor(creature.primaryColor, Color(0xFF9D4EDD))
    }
    val secondaryColor = remember(creature.secondaryColor) {
        parseHexColor(creature.secondaryColor, Color(0xFF72EFDD))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "creatureAnimation")
    val bobbing by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing"
    )

    val wingFlap by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wingFlap"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141822))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .testTag("creature_voxel_preview_${creature.id}")
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Background radial magical glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(secondaryColor.copy(alpha = 0.28f), Color.Transparent),
                    center = Offset(canvasW * 0.5f, canvasH * 0.52f),
                    radius = canvasW * 0.45f
                )
            )

            // Pedestal / Rune circle on the floor
            val floorY = canvasH * 0.82f
            drawOval(
                color = primaryColor.copy(alpha = 0.35f),
                topLeft = Offset(canvasW * 0.22f, floorY - 14f),
                size = androidx.compose.ui.geometry.Size(canvasW * 0.56f, 28f)
            )
            drawOval(
                color = secondaryColor.copy(alpha = 0.7f),
                topLeft = Offset(canvasW * 0.32f, floorY - 8f),
                size = androidx.compose.ui.geometry.Size(canvasW * 0.36f, 16f)
            )

            // Creature Model Rendering based on Archetype
            val cx = canvasW * 0.5f
            val cy = canvasH * 0.50f + bobbing
            val scale = canvasW * 0.0035f

            when (creature.archetype) {
                "Pegasus" -> {
                    drawVoxelPegasus(
                        cx = cx,
                        cy = cy,
                        scale = scale,
                        primary = primaryColor,
                        secondary = secondaryColor,
                        wingOffset = wingFlap
                    )
                }
                "Void Drake" -> {
                    drawVoxelDrake(
                        cx = cx,
                        cy = cy,
                        scale = scale,
                        primary = primaryColor,
                        secondary = secondaryColor,
                        wingOffset = wingFlap
                    )
                }
                "Spirit Fox" -> {
                    drawVoxelFox(
                        cx = cx,
                        cy = cy + 10f,
                        scale = scale,
                        primary = primaryColor,
                        secondary = secondaryColor,
                        tailWag = bobbing
                    )
                }
                "Arcane Golem" -> {
                    drawVoxelGolem(
                        cx = cx,
                        cy = cy + 12f,
                        scale = scale,
                        primary = primaryColor,
                        secondary = secondaryColor
                    )
                }
                else -> {
                    // Ember Sprite or default magical wisp
                    drawVoxelSprite(
                        cx = cx,
                        cy = cy,
                        scale = scale,
                        primary = primaryColor,
                        secondary = secondaryColor,
                        flutter = wingFlap
                    )
                }
            }

            // Floating magical aura motes
            for (i in 0 until 8) {
                val angle = (i * 45f) * (Math.PI / 180f).toFloat()
                val dist = (canvasW * 0.28f) + sin((bobbing + i * 20f) * 0.1f) * 12f
                val px = cx + kotlin.math.cos(angle) * dist
                val py = cy + sin(angle) * (dist * 0.5f)
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.8f),
                    radius = 3.5f,
                    center = Offset(px, py)
                )
            }
        }

        // Top badges: Archetype & Flying / Tameable
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Surface(
                color = primaryColor.copy(alpha = 0.25f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.6f))
            ) {
                Text(
                    text = creature.archetype.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = secondaryColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            if (creature.canFly) {
                Spacer(Modifier.width(6.dp))
                Surface(
                    color = secondaryColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "FLYING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = secondaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }

            if (creature.isTameable) {
                Spacer(Modifier.width(6.dp))
                Surface(
                    color = Color(0xFF00D166).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "TAMEABLE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF55FF99),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bottom stats bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color(0xFF0E1117).copy(alpha = 0.90f),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Health",
                    tint = Color(0xFFFF595E),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${creature.health} HP",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Attack",
                    tint = Color(0xFFFFCA3A),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${creature.attackDamage} ATK",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Special Ability",
                    tint = secondaryColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = creature.specialAbility.take(22) + if (creature.specialAbility.length > 22) "..." else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryColor,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

// Voxel Drawing helpers
private fun DrawScope.drawVoxelBox(
    x: Float,
    y: Float,
    w: Float,
    h: Float,
    color: Color
) {
    drawRect(
        color = color,
        topLeft = Offset(x - w * 0.5f, y - h * 0.5f),
        size = androidx.compose.ui.geometry.Size(w, h)
    )
    // Darker outline for voxel feel
    drawRect(
        color = color.copy(alpha = 0.4f),
        topLeft = Offset(x - w * 0.5f, y - h * 0.5f),
        size = androidx.compose.ui.geometry.Size(w, h),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawVoxelPegasus(
    cx: Float,
    cy: Float,
    scale: Float,
    primary: Color,
    secondary: Color,
    wingOffset: Float
) {
    val u = 10f * scale
    // Body
    drawVoxelBox(cx, cy, u * 4.5f, u * 2.8f, primary)
    // Neck & Head
    drawVoxelBox(cx + u * 2.2f, cy - u * 2f, u * 1.8f, u * 2.8f, primary)
    drawVoxelBox(cx + u * 3.2f, cy - u * 2.6f, u * 2.2f, u * 1.5f, primary)
    // Glowing Horn
    drawVoxelBox(cx + u * 3.8f, cy - u * 4.2f, u * 0.8f, u * 2.2f, secondary)
    // Legs
    drawVoxelBox(cx - u * 1.4f, cy + u * 2.8f, u * 0.9f, u * 3f, primary.copy(alpha = 0.9f))
    drawVoxelBox(cx + u * 1.4f, cy + u * 2.8f, u * 0.9f, u * 3f, primary.copy(alpha = 0.9f))
    // Animated Wings
    drawVoxelBox(cx - u * 0.5f, cy - u * 2.2f + wingOffset, u * 4f, u * 1.4f, secondary)
    drawVoxelBox(cx - u * 1.5f, cy - u * 3.2f + wingOffset * 1.2f, u * 3f, u * 1.2f, secondary.copy(alpha = 0.85f))
    // Glowing eyes
    drawVoxelBox(cx + u * 3.6f, cy - u * 2.8f, u * 0.5f, u * 0.5f, Color.White)
}

private fun DrawScope.drawVoxelDrake(
    cx: Float,
    cy: Float,
    scale: Float,
    primary: Color,
    secondary: Color,
    wingOffset: Float
) {
    val u = 10f * scale
    // Dragon Torso
    drawVoxelBox(cx, cy, u * 5f, u * 2.5f, primary)
    // Serrated Spines
    drawVoxelBox(cx - u * 1.5f, cy - u * 1.8f, u * 1f, u * 1f, secondary)
    drawVoxelBox(cx, cy - u * 1.8f, u * 1f, u * 1f, secondary)
    // Serpentine Neck and Jaw
    drawVoxelBox(cx + u * 2.8f, cy - u * 1.5f, u * 2.2f, u * 1.8f, primary)
    drawVoxelBox(cx + u * 4f, cy - u * 2.4f, u * 2.5f, u * 1.6f, primary)
    // Long Horns
    drawVoxelBox(cx + u * 4.2f, cy - u * 3.8f, u * 0.8f, u * 2f, secondary)
    // Claws
    drawVoxelBox(cx - u * 1.6f, cy + u * 2.4f, u * 1.2f, u * 2.2f, primary)
    drawVoxelBox(cx + u * 1.6f, cy + u * 2.4f, u * 1.2f, u * 2.2f, primary)
    // Large Wyrm Wings
    drawVoxelBox(cx - u * 0.8f, cy - u * 2.5f + wingOffset, u * 5.5f, u * 1.6f, secondary)
    drawVoxelBox(cx - u * 2.2f, cy - u * 4f + wingOffset * 1.3f, u * 4.2f, u * 1.4f, secondary.copy(alpha = 0.85f))
    // Glowing eye
    drawVoxelBox(cx + u * 4.6f, cy - u * 2.6f, u * 0.6f, u * 0.6f, secondary)
}

private fun DrawScope.drawVoxelFox(
    cx: Float,
    cy: Float,
    scale: Float,
    primary: Color,
    secondary: Color,
    tailWag: Float
) {
    val u = 10f * scale
    // Body
    drawVoxelBox(cx, cy, u * 3.8f, u * 2.2f, primary)
    // Head & Pointed ears
    drawVoxelBox(cx + u * 2f, cy - u * 1.4f, u * 2.2f, u * 2f, primary)
    drawVoxelBox(cx + u * 1.6f, cy - u * 3f, u * 0.8f, u * 1.4f, primary)
    drawVoxelBox(cx + u * 2.6f, cy - u * 3f, u * 0.8f, u * 1.4f, primary)
    // White muzzle
    drawVoxelBox(cx + u * 3.1f, cy - u * 0.9f, u * 1.2f, u * 1.1f, Color.White)
    // Spirit Tails (Multi-plume)
    drawVoxelBox(cx - u * 2.8f + tailWag, cy - u * 1.2f, u * 2.2f, u * 3.4f, primary)
    drawVoxelBox(cx - u * 3.4f + tailWag * 1.2f, cy - u * 2.4f, u * 1.6f, u * 2.2f, secondary)
    // Legs
    drawVoxelBox(cx - u * 1.2f, cy + u * 2f, u * 0.8f, u * 2f, primary)
    drawVoxelBox(cx + u * 1.2f, cy + u * 2f, u * 0.8f, u * 2f, primary)
}

private fun DrawScope.drawVoxelGolem(
    cx: Float,
    cy: Float,
    scale: Float,
    primary: Color,
    secondary: Color
) {
    val u = 10f * scale
    // Massive Runic Torso
    drawVoxelBox(cx, cy - u * 0.5f, u * 5f, u * 4.2f, primary)
    // Glowing Runic Core in Chest
    drawVoxelBox(cx, cy - u * 0.5f, u * 2f, u * 2f, secondary)
    // Heavy Shoulder Plates
    drawVoxelBox(cx - u * 3.2f, cy - u * 1.8f, u * 2.2f, u * 2.2f, primary)
    drawVoxelBox(cx + u * 3.2f, cy - u * 1.8f, u * 2.2f, u * 2.2f, primary)
    // Heavy Fists
    drawVoxelBox(cx - u * 3.4f, cy + u * 1.5f, u * 1.8f, u * 3.5f, primary)
    drawVoxelBox(cx + u * 3.4f, cy + u * 1.5f, u * 1.8f, u * 3.5f, primary)
    // Head with slit eyes
    drawVoxelBox(cx, cy - u * 3.4f, u * 2.4f, u * 1.8f, primary)
    drawVoxelBox(cx, cy - u * 3.4f, u * 1.4f, u * 0.4f, secondary)
    // Sturdy Legs
    drawVoxelBox(cx - u * 1.4f, cy + u * 3f, u * 1.6f, u * 3f, primary)
    drawVoxelBox(cx + u * 1.4f, cy + u * 3f, u * 1.6f, u * 3f, primary)
}

private fun DrawScope.drawVoxelSprite(
    cx: Float,
    cy: Float,
    scale: Float,
    primary: Color,
    secondary: Color,
    flutter: Float
) {
    val u = 10f * scale
    // Central fairy orb body
    drawVoxelBox(cx, cy, u * 3f, u * 3f, primary)
    drawVoxelBox(cx, cy, u * 1.8f, u * 1.8f, Color.White)
    // Fluttering crystalline wings
    drawVoxelBox(cx - u * 2.6f, cy - u * 1.2f + flutter, u * 3f, u * 1.6f, secondary)
    drawVoxelBox(cx + u * 2.6f, cy - u * 1.2f - flutter, u * 3f, u * 1.6f, secondary)
    drawVoxelBox(cx - u * 2f, cy + u * 1.6f + flutter * 0.8f, u * 2f, u * 1.2f, secondary.copy(alpha = 0.7f))
    drawVoxelBox(cx + u * 2f, cy + u * 1.6f - flutter * 0.8f, u * 2f, u * 1.2f, secondary.copy(alpha = 0.7f))
}
