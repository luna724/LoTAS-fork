package com.luna724.lotas.record.recording

import com.luna724.lotas.SaveAdditionalStateMod.Companion.currentState
import de.pfannekuchen.lotas.core.MCVer
import com.luna724.lotas.LoTASFork.Companion.LOGGER
import de.pfannekuchen.lotas.mods.SavestateMod
import net.minecraft.client.Minecraft
import java.io.File

class FileManager {
	companion object {
		val mc = Minecraft.getInstance()

		fun getFp(levelName: String? = null): File {
			val level = levelName ?: MCVer.getCurrentWorldFolder()
			return File(
				mc.gameDirectory, "lotas/frameDump/" + level
			)
		}

		fun prepareFile(levelName: String? = null): File {
			val fp = getFp(levelName)
			if (!fp.exists()) {
				fp.mkdirs()
			}
			return File(fp, "/savestates-${currentState}.mp4")
		}

		// Delete mp4 by savestate-num (on-loadstate)
		fun destory(ss: Int) {
			val fp = getFp()
			if (!fp.exists()) {
				return
			}
			val file = File(fp, "savestates-${ss}.mp4")
			if (file.exists()) {
				try {
					file.delete()
					LOGGER.info("Deleted state record: ${file.relativeTo(mc.gameDirectory).name}")
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}
	}
}