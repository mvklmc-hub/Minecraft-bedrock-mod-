package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CustomBiome
import com.example.data.MagicalCreature
import com.example.data.ModPack
import com.example.data.ModPackRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ModPackViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ModPackRepository

    val allPacks: StateFlow<List<ModPack>>

    private val _activePackId = MutableStateFlow<Long?>(null)
    val activePackId: StateFlow<Long?> = _activePackId.asStateFlow()

    val activePack: StateFlow<ModPack?>
    val activeBiomes: StateFlow<List<CustomBiome>>
    val activeCreatures: StateFlow<List<MagicalCreature>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ModPackRepository(
            modPackDao = db.modPackDao(),
            biomeDao = db.biomeDao(),
            creatureDao = db.creatureDao()
        )

        allPacks = repository.allPacks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activePack = _activePackId.flatMapLatest { id ->
            if (id != null) repository.getPack(id) else flowOf(null)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        activeBiomes = _activePackId.flatMapLatest { id ->
            if (id != null) repository.getBiomesForPack(id) else flowOf(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        activeCreatures = _activePackId.flatMapLatest { id ->
            if (id != null) repository.getCreaturesForPack(id) else flowOf(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Select first pack when available if none selected
        viewModelScope.launch {
            allPacks.collect { packs ->
                if (_activePackId.value == null && packs.isNotEmpty()) {
                    _activePackId.value = packs.first().id
                }
            }
        }
    }

    fun selectPack(id: Long?) {
        _activePackId.value = id
    }

    fun createPack(name: String, description: String, author: String = "Bedrock Creator") {
        viewModelScope.launch {
            val newPack = ModPack(
                name = name,
                description = description,
                author = author
            )
            val newId = repository.insertPack(newPack)
            _activePackId.value = newId

            // Add starter biome & creature
            repository.insertBiome(
                CustomBiome(
                    packId = newId,
                    name = "Enchanted Grove",
                    identifier = "magic:enchanted_grove",
                    dimension = "overworld",
                    temperature = 0.7f,
                    downfall = 0.8f,
                    skyColor = "#5B2C82",
                    fogColor = "#9B5DE5",
                    waterColor = "#00F5D4",
                    foliageColor = "#FF595E",
                    grassColor = "#70E000",
                    topBlock = "minecraft:moss_block",
                    description = "An enchanted grove with glowing starlight canopies and mystic flora."
                )
            )

            repository.insertCreature(
                MagicalCreature(
                    packId = newId,
                    name = "Moonlit Pegasus",
                    identifier = "magic:moonlit_pegasus",
                    archetype = "Pegasus",
                    health = 45,
                    attackDamage = 8,
                    canFly = true,
                    isTameable = true,
                    spawnBiomes = "magic:enchanted_grove"
                )
            )
        }
    }

    fun updatePack(pack: ModPack) {
        viewModelScope.launch {
            repository.updatePack(pack.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deletePack(id: Long) {
        viewModelScope.launch {
            repository.deletePack(id)
            if (_activePackId.value == id) {
                _activePackId.value = allPacks.value.firstOrNull { it.id != id }?.id
            }
        }
    }

    fun saveBiome(biome: CustomBiome) {
        viewModelScope.launch {
            if (biome.id == 0L) {
                repository.insertBiome(biome)
            } else {
                repository.updateBiome(biome)
            }
        }
    }

    fun deleteBiome(id: Long) {
        viewModelScope.launch {
            repository.deleteBiome(id)
        }
    }

    fun saveCreature(creature: MagicalCreature) {
        viewModelScope.launch {
            if (creature.id == 0L) {
                repository.insertCreature(creature)
            } else {
                repository.updateCreature(creature)
            }
        }
    }

    fun deleteCreature(id: Long) {
        viewModelScope.launch {
            repository.deleteCreature(id)
        }
    }

    fun addQuickPresetBiome(packId: Long) {
        viewModelScope.launch {
            val presets = listOf(
                CustomBiome(
                    packId = packId,
                    name = "Bioluminescent Spire",
                    identifier = "magic:bioluminescent_spire",
                    dimension = "the_end",
                    temperature = 0.5f,
                    downfall = 0.1f,
                    skyColor = "#0B090A",
                    fogColor = "#7209B7",
                    waterColor = "#4CC9F0",
                    foliageColor = "#F72585",
                    grassColor = "#4361EE",
                    topBlock = "minecraft:purpur_block",
                    midBlock = "minecraft:end_stone",
                    foundationBlock = "minecraft:obsidian",
                    ambientParticle = "minecraft:portal_reverse",
                    ambientSound = "ambient.cave.crystal_hum",
                    description = "End dimension floating towers radiating with violet energy and starlight motes."
                ),
                CustomBiome(
                    packId = packId,
                    name = "Solstice Meadow",
                    identifier = "magic:solstice_meadow",
                    dimension = "overworld",
                    temperature = 0.8f,
                    downfall = 0.6f,
                    skyColor = "#FFB703",
                    fogColor = "#FB8500",
                    waterColor = "#219EBC",
                    foliageColor = "#D90429",
                    grassColor = "#80ED99",
                    topBlock = "minecraft:grass_block",
                    midBlock = "minecraft:dirt",
                    foundationBlock = "minecraft:stone",
                    ambientParticle = "minecraft:falling_dust_spore",
                    ambientSound = "ambient.fairy_forest.loop",
                    description = "Sun-drenched golden meadows teeming with magical butterflies and radiant sunlight."
                )
            )
            repository.insertBiome(presets.random())
        }
    }

    fun addQuickPresetCreature(packId: Long) {
        viewModelScope.launch {
            val presets = listOf(
                MagicalCreature(
                    packId = packId,
                    name = "Astral Drake",
                    identifier = "magic:astral_drake",
                    archetype = "Void Drake",
                    health = 80,
                    attackDamage = 16,
                    canFly = true,
                    isTameable = true,
                    tameItem = "minecraft:chorus_fruit",
                    specialAbility = "Dimensional Rift & Mana Surge",
                    primaryColor = "#2B0938",
                    secondaryColor = "#E0AAFF"
                ),
                MagicalCreature(
                    packId = packId,
                    name = "Solstice Pixie",
                    identifier = "magic:solstice_pixie",
                    archetype = "Ember Sprite",
                    health = 20,
                    attackDamage = 6,
                    canFly = true,
                    isTameable = true,
                    tameItem = "minecraft:sweet_berries",
                    specialAbility = "Blinding Flash & Speed Gift",
                    primaryColor = "#FFB703",
                    secondaryColor = "#FB8500"
                )
            )
            repository.insertCreature(presets.random())
        }
    }
}
