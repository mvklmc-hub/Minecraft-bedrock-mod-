package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModPackDao {
    @Query("SELECT * FROM mod_packs ORDER BY updatedAt DESC")
    fun getAllPacks(): Flow<List<ModPack>>

    @Query("SELECT * FROM mod_packs WHERE id = :id")
    fun getPackById(id: Long): Flow<ModPack?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPack(pack: ModPack): Long

    @Update
    suspend fun updatePack(pack: ModPack)

    @Query("DELETE FROM mod_packs WHERE id = :id")
    suspend fun deletePack(id: Long)
}

@Dao
interface BiomeDao {
    @Query("SELECT * FROM custom_biomes WHERE packId = :packId ORDER BY id ASC")
    fun getBiomesForPack(packId: Long): Flow<List<CustomBiome>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiome(biome: CustomBiome): Long

    @Update
    suspend fun updateBiome(biome: CustomBiome)

    @Query("DELETE FROM custom_biomes WHERE id = :id")
    suspend fun deleteBiome(id: Long)
}

@Dao
interface CreatureDao {
    @Query("SELECT * FROM magical_creatures WHERE packId = :packId ORDER BY id ASC")
    fun getCreaturesForPack(packId: Long): Flow<List<MagicalCreature>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreature(creature: MagicalCreature): Long

    @Update
    suspend fun updateCreature(creature: MagicalCreature)

    @Query("DELETE FROM magical_creatures WHERE id = :id")
    suspend fun deleteCreature(id: Long)
}
