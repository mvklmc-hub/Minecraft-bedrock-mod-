package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ModPack::class, CustomBiome::class, MagicalCreature::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modPackDao(): ModPackDao
    abstract fun biomeDao(): BiomeDao
    abstract fun creatureDao(): CreatureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bedrock_modpack_maker.db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with inspiring default packs
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            seedDefaultPacks(database)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDefaultPacks(database: AppDatabase) {
            val packDao = database.modPackDao()
            val biomeDao = database.biomeDao()
            val creatureDao = database.creatureDao()

            val pack1Id = packDao.insertPack(
                ModPack(
                    name = "Mythic Realms & Beasts",
                    description = "Custom fantastical biomes paired with mythical creatures designed for Bedrock Edition explorers.",
                    author = "Aetheria Studio",
                    version = "1.0.0",
                    minEngineVersion = "1.21.0"
                )
            )

            // Biomes for Pack 1
            biomeDao.insertBiome(
                CustomBiome(
                    packId = pack1Id,
                    name = "Celestial Grove",
                    identifier = "magic:celestial_grove",
                    dimension = "overworld",
                    temperature = 0.65f,
                    downfall = 0.85f,
                    skyColor = "#4A154B",
                    fogColor = "#7B2CBF",
                    waterColor = "#00F5D4",
                    foliageColor = "#F72585",
                    grassColor = "#70E000",
                    topBlock = "minecraft:moss_block",
                    midBlock = "minecraft:dirt",
                    foundationBlock = "minecraft:deepslate",
                    ambientParticle = "minecraft:falling_dust_spore",
                    ambientSound = "ambient.crystal_caverns.loop",
                    description = "Ancient enchanted woodlands illuminated by falling stardust motes and luminous flora."
                )
            )

            biomeDao.insertBiome(
                CustomBiome(
                    packId = pack1Id,
                    name = "Crystal Caverns",
                    identifier = "magic:crystal_caverns",
                    dimension = "overworld",
                    temperature = 0.4f,
                    downfall = 0.3f,
                    skyColor = "#1D3557",
                    fogColor = "#457B9D",
                    waterColor = "#A8DADC",
                    foliageColor = "#B5179E",
                    grassColor = "#3A0CA3",
                    topBlock = "minecraft:amethyst_block",
                    midBlock = "minecraft:calcite",
                    foundationBlock = "minecraft:tuff",
                    ambientParticle = "minecraft:portal_reverse",
                    ambientSound = "ambient.cave.crystal_hum",
                    description = "Underground crystalline grottos with towering amethyst geodes echoing mana frequencies."
                )
            )

            biomeDao.insertBiome(
                CustomBiome(
                    packId = pack1Id,
                    name = "Sunken Twilight Abyss",
                    identifier = "magic:sunken_twilight",
                    dimension = "overworld",
                    temperature = 0.8f,
                    downfall = 1.0f,
                    skyColor = "#0B090A",
                    fogColor = "#161A1D",
                    waterColor = "#0077B6",
                    foliageColor = "#06D6A0",
                    grassColor = "#118AB2",
                    topBlock = "minecraft:prismarine",
                    midBlock = "minecraft:dark_prismarine",
                    foundationBlock = "minecraft:sea_lantern",
                    ambientParticle = "minecraft:bubble_pop",
                    ambientSound = "ambient.underwater.whispers",
                    description = "Submerged mystical abyss where ancient sunken ruins glow under phosphorescent sea life."
                )
            )

            // Creatures for Pack 1
            creatureDao.insertCreature(
                MagicalCreature(
                    packId = pack1Id,
                    name = "Moonlit Pegasus",
                    identifier = "magic:moonlit_pegasus",
                    archetype = "Pegasus",
                    health = 45,
                    attackDamage = 8,
                    movementSpeed = 0.35f,
                    canFly = true,
                    canSwim = false,
                    isTameable = true,
                    tameItem = "minecraft:golden_apple",
                    specialAbility = "Healing Aura & Starlight Glide",
                    primaryColor = "#9D4EDD",
                    secondaryColor = "#72EFDD",
                    spawnBiomes = "magic:celestial_grove",
                    spawnWeight = 35,
                    minGroupCount = 1,
                    maxGroupCount = 3,
                    lootDrops = "magic:starlight_feather (1-2), minecraft:glowstone_dust (2-4)",
                    description = "A celestial winged equine with starlight feathers that heals allies within 8 blocks."
                )
            )

            creatureDao.insertCreature(
                MagicalCreature(
                    packId = pack1Id,
                    name = "Void Drake",
                    identifier = "magic:void_drake",
                    archetype = "Void Drake",
                    health = 75,
                    attackDamage = 14,
                    movementSpeed = 0.38f,
                    canFly = true,
                    canSwim = false,
                    isTameable = true,
                    tameItem = "minecraft:chorus_fruit",
                    specialAbility = "Dimensional Phase & Mana Breath",
                    primaryColor = "#2B0938",
                    secondaryColor = "#E0AAFF",
                    spawnBiomes = "magic:crystal_caverns, magic:celestial_grove",
                    spawnWeight = 15,
                    minGroupCount = 1,
                    maxGroupCount = 1,
                    lootDrops = "magic:void_scale (2-3), minecraft:ender_pearl (1-3)",
                    description = "A majestic serpentine dragon that teleports short distances and breathes arcane fire."
                )
            )

            creatureDao.insertCreature(
                MagicalCreature(
                    packId = pack1Id,
                    name = "Whisper Fox",
                    identifier = "magic:whisper_fox",
                    archetype = "Spirit Fox",
                    health = 24,
                    attackDamage = 5,
                    movementSpeed = 0.40f,
                    canFly = false,
                    canSwim = true,
                    isTameable = true,
                    tameItem = "minecraft:sweet_berries",
                    specialAbility = "Spirit Cloak & Speed Blessing",
                    primaryColor = "#FF9E00",
                    secondaryColor = "#48CAE4",
                    spawnBiomes = "magic:celestial_grove",
                    spawnWeight = 45,
                    minGroupCount = 2,
                    maxGroupCount = 4,
                    lootDrops = "magic:spirit_essence (1-3), minecraft:leather (1-2)",
                    description = "A multi-tailed ethereal forest fox that grants Swiftness to crouching players."
                )
            )

            creatureDao.insertCreature(
                MagicalCreature(
                    packId = pack1Id,
                    name = "Arcane Golem",
                    identifier = "magic:arcane_golem",
                    archetype = "Arcane Golem",
                    health = 110,
                    attackDamage = 18,
                    movementSpeed = 0.22f,
                    canFly = false,
                    canSwim = false,
                    isTameable = true,
                    tameItem = "minecraft:iron_block",
                    specialAbility = "Earthquake Slam & Runic Ward",
                    primaryColor = "#3D405B",
                    secondaryColor = "#E07A5F",
                    spawnBiomes = "magic:crystal_caverns",
                    spawnWeight = 20,
                    minGroupCount = 1,
                    maxGroupCount = 2,
                    lootDrops = "magic:runic_core (1), minecraft:iron_ingot (3-5)",
                    description = "A hulking animated rune-stone automaton that defends nearby settlements."
                )
            )

            // Pack 2: Chrono Nether & Sprites
            val pack2Id = packDao.insertPack(
                ModPack(
                    name = "Chrono Mire & Sprites",
                    description = "Distorted temporal biomes with flickering ember fairies for surreal dimension travel.",
                    author = "NetherCrafters",
                    version = "1.0.0",
                    minEngineVersion = "1.21.0"
                )
            )

            biomeDao.insertBiome(
                CustomBiome(
                    packId = pack2Id,
                    name = "Chrono Mire",
                    identifier = "magic:chrono_mire",
                    dimension = "nether",
                    temperature = 1.8f,
                    downfall = 0.0f,
                    skyColor = "#FFB703",
                    fogColor = "#FB8500",
                    waterColor = "#D90429",
                    foliageColor = "#9D0208",
                    grassColor = "#6A040F",
                    topBlock = "minecraft:soul_sand",
                    midBlock = "minecraft:soul_soil",
                    foundationBlock = "minecraft:blackstone",
                    ambientParticle = "minecraft:flame",
                    ambientSound = "ambient.nether.crimson_forest",
                    description = "A time-warped soul valley where warm amber embers drift in low gravity."
                )
            )

            creatureDao.insertCreature(
                MagicalCreature(
                    packId = pack2Id,
                    name = "Ember Sprite",
                    identifier = "magic:ember_sprite",
                    archetype = "Ember Sprite",
                    health = 16,
                    attackDamage = 4,
                    movementSpeed = 0.42f,
                    canFly = true,
                    canSwim = false,
                    isTameable = true,
                    tameItem = "minecraft:blaze_powder",
                    specialAbility = "Pyromancy & Flash Step",
                    primaryColor = "#FF5400",
                    secondaryColor = "#FFD60A",
                    spawnBiomes = "magic:chrono_mire",
                    spawnWeight = 60,
                    minGroupCount = 3,
                    maxGroupCount = 6,
                    lootDrops = "minecraft:blaze_powder (1-2), minecraft:glowstone_dust (1-3)",
                    description = "Tiny floating fire fairies that illuminate dark tunnels and ignite enemy targets."
                )
            )
        }
    }
}
