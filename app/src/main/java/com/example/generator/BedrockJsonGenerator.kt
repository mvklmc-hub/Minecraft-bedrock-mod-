package com.example.generator

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.data.CustomBiome
import com.example.data.MagicalCreature
import com.example.data.ModPack
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object BedrockJsonGenerator {

    fun generateBehaviorManifest(pack: ModPack): String {
        return """
{
  "format_version": 2,
  "header": {
    "name": "${escape(pack.name)} (Behavior)",
    "description": "${escape(pack.description)}",
    "uuid": "${pack.behaviorUuid}",
    "version": [1, 0, 0],
    "min_engine_version": [1, 21, 0]
  },
  "modules": [
    {
      "type": "data",
      "uuid": "${pack.behaviorUuid.reversed()}",
      "version": [1, 0, 0]
    }
  ],
  "dependencies": [
    {
      "uuid": "${pack.resourceUuid}",
      "version": [1, 0, 0]
    }
  ]
}
        """.trimIndent()
    }

    fun generateResourceManifest(pack: ModPack): String {
        return """
{
  "format_version": 2,
  "header": {
    "name": "${escape(pack.name)} (Resources)",
    "description": "Visual textures and models for ${escape(pack.name)}",
    "uuid": "${pack.resourceUuid}",
    "version": [1, 0, 0],
    "min_engine_version": [1, 21, 0]
  },
  "modules": [
    {
      "type": "resources",
      "uuid": "${pack.resourceUuid.reversed()}",
      "version": [1, 0, 0]
    }
  ]
}
        """.trimIndent()
    }

    fun generateBiomeJson(biome: CustomBiome): String {
        val cleanId = biome.identifier.removePrefix("minecraft:")
        return """
{
  "format_version": "1.21.0",
  "minecraft:biome": {
    "description": {
      "identifier": "${biome.identifier}"
    },
    "components": {
      "minecraft:climate": {
        "downfall": ${biome.downfall},
        "temperature": ${biome.temperature},
        "snow_accumulation": [0.0, 0.125]
      },
      "minecraft:overworld_generation_rules": {
        "hills_transformation": [
          ["${biome.identifier}", 1]
        ],
        "mutate_transformation": "${biome.identifier}",
        "generate_for_climates": [
          ["medium", 2],
          ["warm", 1]
        ]
      },
      "minecraft:surface_parameters": {
        "top_material": "${biome.topBlock}",
        "mid_material": "${biome.midBlock}",
        "foundation_material": "${biome.foundationBlock}",
        "sea_floor_material": "minecraft:gravel",
        "sea_material": "minecraft:water",
        "sea_floor_depth": 7
      },
      "minecraft:sky_color": "${biome.skyColor}",
      "minecraft:fog_appearance": {
        "fog_identifier": "minecraft:fog_lush",
        "fog_color": "${biome.fogColor}"
      },
      "minecraft:water_appearance": {
        "surface_color": "${biome.waterColor}",
        "fog_color": "${biome.waterColor}"
      },
      "minecraft:foliage_appearance": {
        "color": "${biome.foliageColor}"
      },
      "minecraft:grass_appearance": {
        "color": "${biome.grassColor}"
      },
      "minecraft:ambient_sounds": {
        "ambient_sound": "${biome.ambientSound}",
        "mood_sound": "ambient.cave"
      },
      "minecraft:ambient_particles": {
        "particle_identifier": "${biome.ambientParticle}",
        "probability": 0.08
      }
    }
  }
}
        """.trimIndent()
    }

    fun generateCreatureBehaviorJson(creature: MagicalCreature): String {
        val cleanName = creature.identifier.substringAfter(":")
        val flyComponents = if (creature.canFly) {
            """
      "minecraft:can_fly": {},
      "minecraft:navigation.fly": {
        "can_path_over_water": true,
        "can_path_through_air": true
      },
      "minecraft:movement.fly": {
        "max_turn": 30.0
      },
      "minecraft:behavior.fly": {
        "priority": 4,
        "speed_multiplier": 1.2
      },
            """.trimIndent()
        } else {
            """
      "minecraft:navigation.walk": {
        "can_path_over_water": true,
        "avoid_damage_blocks": true
      },
      "minecraft:movement.basic": {
        "max_turn": 30.0
      },
            """.trimIndent()
        }

        val tameComponent = if (creature.isTameable) {
            """
      "minecraft:tameable": {
        "probability": 0.35,
        "tame_items": ["${creature.tameItem}"],
        "tame_event": {
          "event": "minecraft:on_tame",
          "target": "self"
        }
      },
      "minecraft:rideable": {
        "seat_count": 1,
        "family_types": ["player"],
        "seats": [
          { "position": [0.0, 1.2, 0.0] }
        ]
      },
            """.trimIndent()
        } else ""

        return """
{
  "format_version": "1.21.0",
  "minecraft:entity": {
    "description": {
      "identifier": "${creature.identifier}",
      "is_spawnable": true,
      "is_summonable": true,
      "is_experimental": false
    },
    "components": {
      "minecraft:type_family": {
        "family": ["mob", "magical", "${creature.archetype.lowercase().replace(" ", "_")}"]
      },
      "minecraft:health": {
        "value": ${creature.health},
        "max": ${creature.health}
      },
      "minecraft:attack": {
        "damage": ${creature.attackDamage}
      },
      "minecraft:movement": {
        "value": ${creature.movementSpeed}
      },
      "minecraft:collision_box": {
        "width": 1.1,
        "height": 1.8
      },
      "minecraft:experience_reward": {
        "on_death": "query.last_hit_by_player ? 15 : 0"
      },
      "minecraft:magic_aura": {
        "ability": "${creature.specialAbility}",
        "primary_color": "${creature.primaryColor}",
        "secondary_color": "${creature.secondaryColor}"
      },
$flyComponents
$tameComponent
      "minecraft:behavior.float": {
        "priority": 0
      },
      "minecraft:behavior.melee_attack": {
        "priority": 2,
        "speed_multiplier": 1.25,
        "track_target": true
      },
      "minecraft:behavior.hurt_by_target": {
        "priority": 1
      },
      "minecraft:behavior.nearest_attackable_target": {
        "priority": 3,
        "entity_types": [
          {
            "filters": {
              "test": "is_family",
              "subject": "other",
              "value": "monster"
            },
            "max_dist": 16.0
          }
        ]
      },
      "minecraft:behavior.random_stroll": {
        "priority": 6,
        "speed_multiplier": 0.8
      },
      "minecraft:behavior.look_at_player": {
        "priority": 7,
        "look_distance": 8.0
      },
      "minecraft:physics": {}
    }
  }
}
        """.trimIndent()
    }

    fun generateSpawnRulesJson(creature: MagicalCreature): String {
        return """
{
  "format_version": "1.21.0",
  "minecraft:spawn_rules": {
    "description": {
      "identifier": "${creature.identifier}",
      "population_control": "creature"
    },
    "conditions": [
      {
        "minecraft:spawns_on_surface": {},
        "minecraft:brightness_filter": {
          "min": 0,
          "max": 15,
          "adjust_for_weather": false
        },
        "minecraft:weight": {
          "default": ${creature.spawnWeight}
        },
        "minecraft:herd": {
          "min_size": ${creature.minGroupCount},
          "max_size": ${creature.maxGroupCount}
        },
        "minecraft:biome_filter": {
          "any_of": [
            {
              "test": "has_biome_tag",
              "operator": "==",
              "value": "${creature.spawnBiomes}"
            }
          ]
        }
      }
    ]
  }
}
        """.trimIndent()
    }

    fun exportPackToMcaddon(
        context: Context,
        pack: ModPack,
        biomes: List<CustomBiome>,
        creatures: List<MagicalCreature>
    ): File {
        val sanitizedName = pack.name.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val exportFile = File(context.cacheDir, "${sanitizedName}.mcaddon")
        if (exportFile.exists()) exportFile.delete()

        ZipOutputStream(FileOutputStream(exportFile)).use { zip ->
            // Behavior Pack
            val bpPrefix = "${sanitizedName}_BP/"
            addZipEntry(zip, "${bpPrefix}manifest.json", generateBehaviorManifest(pack))

            for (biome in biomes) {
                val biomeFile = "${biome.identifier.replace(":", "_")}.biome.json"
                addZipEntry(zip, "${bpPrefix}biomes/$biomeFile", generateBiomeJson(biome))
            }

            for (creature in creatures) {
                val entityFile = "${creature.identifier.replace(":", "_")}.json"
                addZipEntry(zip, "${bpPrefix}entities/$entityFile", generateCreatureBehaviorJson(creature))
                addZipEntry(zip, "${bpPrefix}spawn_rules/$entityFile", generateSpawnRulesJson(creature))
            }

            // Resource Pack
            val rpPrefix = "${sanitizedName}_RP/"
            addZipEntry(zip, "${rpPrefix}manifest.json", generateResourceManifest(pack))
        }

        return exportFile
    }

    fun savePackToDownloads(
        context: Context,
        pack: ModPack,
        biomes: List<CustomBiome>,
        creatures: List<MagicalCreature>
    ): Pair<Boolean, String> {
        val file = exportPackToMcaddon(context, pack, biomes, creatures)
        val sanitizedName = pack.name.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val filename = "${sanitizedName}.mcaddon"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        file.inputStream().use { input -> input.copyTo(out) }
                    }
                    return Pair(true, "Saved to Downloads/$filename")
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val target = File(downloadsDir, filename)
                file.copyTo(target, overwrite = true)
                return Pair(true, "Saved to Downloads/$filename")
            }
        } catch (e: Exception) {
            try {
                val extFiles = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                if (extFiles != null) {
                    val fallbackFile = File(extFiles, filename)
                    file.copyTo(fallbackFile, overwrite = true)
                    return Pair(true, "Saved to App Downloads/$filename")
                }
            } catch (_: Exception) {}
        }
        return Pair(true, "Ready in cache: $filename")
    }

    private fun addZipEntry(zip: ZipOutputStream, path: String, content: String) {
        val entry = ZipEntry(path)
        zip.putNextEntry(entry)
        zip.write(content.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun escape(text: String): String {
        return text.replace("\"", "\\\"").replace("\n", " ")
    }
}
