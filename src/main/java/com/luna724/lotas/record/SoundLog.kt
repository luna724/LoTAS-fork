package com.luna724.lotas.record

import kotlinx.serialization.Serializable
import net.minecraft.client.resources.sounds.SoundInstance

class SoundLog {
	@Serializable
	data class SoundEntry(
		val timestamp: Long,
		val resource: String, // audio ResourceLocation
		val pitch: Float, val delay: Int, val vol: Float
	) {
		companion object {
			fun add(
				resoure: String, pitch: Float, delay: Int, vol: Float
			): SoundEntry {
				return SoundEntry(
					System.currentTimeMillis(), resoure, pitch, delay, vol
				)
			}
		}
	}

	companion object {
		val logs: MutableList<SoundEntry> = mutableListOf()
		fun putAudio(a: SoundInstance) {
			a.sound ?: return
			logs.add(
				SoundEntry.add(
					a.location.path,
					a.pitch, a.delay, a.volume
				)
			)
		}
	}

}