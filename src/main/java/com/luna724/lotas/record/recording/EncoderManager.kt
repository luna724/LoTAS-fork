package com.luna724.lotas.record.recording

import com.luna724.lotas.SaveAdditionalStateMod.Companion.currentState
import net.minecraft.client.Minecraft
import com.luna724.lotas.LoTASFork.Companion.LOGGER
import de.pfannekuchen.lotas.core.MCVer
import java.lang.System.gc
import java.lang.System.nanoTime

class EncoderManager {
	data class FrameData(
		val pixels: ByteArray,
		var timestampNs: Long = 0L
	)

	@Volatile private var currentSession: EncoderSession? = null
	private var capturer: PBOCapturer? = null
	private var currentLevelName: String? = null

	// ワールド開始時、SSが0なら呼ばれる
	fun onWorldStart() {
		LOGGER.info("starting record with onWorldStart..")
		try {
			val server = Minecraft.getInstance().singleplayerServer
			if (server != null) {
				currentLevelName = MCVer.getCurrentWorldFolder()
			}
			else{
				throw NullPointerException("Failed to get server instance for world name. currentLevelName is set to null.")
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}

		startSession(0)
	}

	fun onWorldUnload() {
		currentSession?.stopNormally()
		currentSession = null
		// TODO: 結合など後処理
		gc()
	}


	fun onSaveState(ss: Int) {
		LOGGER.info("starting record with onSaveState..")
		currentSession?.stopNormally()

		startSession(ss)
	}

	fun onLoadState(toState: Int? = null) {
		currentSession?.abort()

		val ss = toState ?: currentState
		startSession(ss)
	}

	fun startSession(ss: Int? = null) {
		val fp = FileManager.prepareFile(currentLevelName)
		val mc = Minecraft.getInstance()
		val fWidth = mc.window.screenWidth
        val fHeight = mc.window.screenHeight
		if (capturer == null || capturer!!.width != fWidth || capturer!!.height != fHeight) {
            capturer?.cleanup()
            capturer = PBOCapturer(fWidth, fHeight).apply { init() }
        }

		val session = EncoderSession(
			fWidth, fHeight, fp=fp
		)
		session.start()
		currentSession = session
	}

	// main
	fun onFrameRendered() {
		val session = currentSession ?: return
		val cap = capturer ?: return
		val frameData = session.pool.poll() ?: return
		if (!cap.capture(frameData.pixels)) {
			session.pool.offer(frameData)
			return
		}
		frameData.timestampNs = nanoTime()

		if (!session.queue.offer(frameData)) {
			session.pool.offer(frameData)
		}
	}
}

object RecordingSystem {
	val manager = EncoderManager()
}