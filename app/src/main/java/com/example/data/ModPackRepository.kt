package com.example.data

import kotlinx.coroutines.flow.Flow

class ModPackRepository(
    private val modPackDao: ModPackDao,
    private val biomeDao: BiomeDao,
    private val creatureDao: CreatureDao
) {
    val allPacks: Flow<List<ModPack>> = modPackDao.getAllPacks()

    fun getPack(id: Long): Flow<ModPack?> = modPackDao.getPackById(id)

    fun getBiomesForPack(packId: Long): Flow<List<CustomBiome>> = biomeDao.getBiomesForPack(packId)

    fun getCreaturesForPack(packId: Long): Flow<List<MagicalCreature>> = creatureDao.getCreaturesForPack(packId)

    suspend fun insertPack(pack: ModPack): Long = modPackDao.insertPack(pack)

    suspend fun updatePack(pack: ModPack) = modPackDao.updatePack(pack)

    suspend fun deletePack(id: Long) = modPackDao.deletePack(id)

    suspend fun insertBiome(biome: CustomBiome): Long = biomeDao.insertBiome(biome)

    suspend fun updateBiome(biome: CustomBiome) = biomeDao.updateBiome(biome)

    suspend fun deleteBiome(id: Long) = biomeDao.deleteBiome(id)

    suspend fun insertCreature(creature: MagicalCreature): Long = creatureDao.insertCreature(creature)

    suspend fun updateCreature(creature: MagicalCreature) = creatureDao.updateCreature(creature)

    suspend fun deleteCreature(id: Long) = creatureDao.deleteCreature(id)
}
