package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "mod_packs")
data class ModPack(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val author: String = "Bedrock Creator",
    val version: String = "1.0.0",
    val minEngineVersion: String = "1.21.0",
    val behaviorUuid: String = UUID.randomUUID().toString(),
    val resourceUuid: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "custom_biomes",
    foreignKeys = [
        ForeignKey(
            entity = ModPack::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packId")]
)
data class CustomBiome(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packId: Long,
    val name: String,
    val identifier: String, // e.g. "magic:celestial_grove"
    val dimension: String = "overworld", // "overworld", "nether", "the_end"
    val temperature: Float = 0.7f,
    val downfall: Float = 0.8f,
    val skyColor: String = "#5B2C82", // Hex
    val fogColor: String = "#8AC926",
    val waterColor: String = "#00F5D4",
    val foliageColor: String = "#FF595E",
    val grassColor: String = "#70E000",
    val topBlock: String = "minecraft:moss_block",
    val midBlock: String = "minecraft:dirt",
    val foundationBlock: String = "minecraft:deepslate",
    val ambientParticle: String = "minecraft:falling_dust_spore",
    val ambientSound: String = "ambient.crystal_caverns.loop",
    val description: String = "A shimmering landscape shrouded in magical mist and glowing flora."
)

@Entity(
    tableName = "magical_creatures",
    foreignKeys = [
        ForeignKey(
            entity = ModPack::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packId")]
)
data class MagicalCreature(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packId: Long,
    val name: String,
    val identifier: String, // e.g. "magic:moonlit_pegasus"
    val archetype: String = "Pegasus", // "Pegasus", "Void Drake", "Spirit Fox", "Arcane Golem", "Ember Sprite", "Abyssal Serpent"
    val health: Int = 40,
    val attackDamage: Int = 8,
    val movementSpeed: Float = 0.32f,
    val canFly: Boolean = true,
    val canSwim: Boolean = false,
    val isTameable: Boolean = true,
    val tameItem: String = "minecraft:golden_apple",
    val specialAbility: String = "Healing Aura & Starlight Glide",
    val primaryColor: String = "#9D4EDD",
    val secondaryColor: String = "#72EFDD",
    val spawnBiomes: String = "magic:celestial_grove",
    val spawnWeight: Int = 30,
    val minGroupCount: Int = 1,
    val maxGroupCount: Int = 3,
    val lootDrops: String = "magic:starlight_feather (1-2), minecraft:glowstone_dust (2-4)",
    val description: String = "A celestial winged steed that glides down from high starlit peaks and blesses nearby adventurers."
)

data class ModPackWithDetails(
    val pack: ModPack,
    val biomes: List<CustomBiome>,
    val creatures: List<MagicalCreature>
)
