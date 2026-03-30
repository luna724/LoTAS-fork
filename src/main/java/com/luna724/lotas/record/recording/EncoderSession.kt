package com.luna724.lotas.record.recording

import java.io.OutputStream
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import com.luna724.lotas.record.recording.EncoderManager.FrameData
import com.luna724.lotas.LoTASFork.Companion.LOGGER
import java.io.File
import java.lang.System.nanoTime
import javax.annotation.Nonnull
import kotlin.concurrent.thread


/**
 * ffmpegへフレーム送信を行う
 *
 * 参照： https://github.com/daipenger/minema
 *
 */
class EncoderSession(
	val width: Int, val height: Int, val fp: File, val fps: Int = 60, val pSize: Int = 5,
) {
	val queue = ArrayBlockingQueue<FrameData>(pSize)
	val pool = ArrayBlockingQueue<FrameData>(pSize)

	private var ffmpeg: Process? = null
	@Volatile private var isRunning = false
	@Volatile private var isAborted = false

	init {
		// init pool
		val dataSize = width * height * 3
		for (i in 0 until pSize) {
            pool.offer(FrameData(ByteArray(dataSize)))
        }
	}

	fun start() {
		isRunning = true

		val pb = ProcessBuilder(
			"ffmpeg", "-y",
			"-f", "rawvideo",
			"-pix_fmt", "rgb24",
			"-s", "${width}x${height}",
			"-r", "$fps",
			"-i", "-",
			"-vf", "vflip", // opengl
			"-c:v", "h264_nvenc",
			"-preset", "p6",
			"-b:v", "50M",
			"-pix_fmt", "yuv420p",
			fp.absolutePath
		)
		pb.redirectError(ProcessBuilder.Redirect.INHERIT)
		ffmpeg = pb.start()
		val outputStream = ffmpeg?.outputStream ?: return

		thread(name = "LoTAS-EncoderWriter-${fp.name}") {
			val frameIntervalNs = 1_000_000_000L / fps
			var nextExpectedTimeNs = System.nanoTime()
			var lastSentFrame: ByteArray? = null

			try {
				while (isRunning || queue.isNotEmpty()) {
					val ts = nanoTime()
					if (ts < nextExpectedTimeNs) {
						Thread.sleep(1)
						continue
					}
					if (isAborted) break

					val peeked = queue.peek()
					if (peeked != null && peeked.timestampNs <= ts) {
						val frame = queue.poll()
						outputStream.write(frame.pixels)
						lastSentFrame = frame.pixels
						pool.offer(frame)
					} else {
						if (lastSentFrame != null) {
							outputStream.write(lastSentFrame)
						}
					}
					nextExpectedTimeNs += frameIntervalNs
				}
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				// cleanup
				try {
					outputStream.flush()
					outputStream.close()
				} catch (e: Exception) {
				}

				if (isAborted) {
					// ロードされて見捨てられた場合：FFmpegを強制キルし、失敗したファイルを消す
					ffmpeg?.destroyForcibly()
					if (fp.exists()) fp.delete()
					println("[Encoder] Session aborted & file deleted: ${fp.name}")
				} else {
					// 正常終了（ssや録画完了）の場合：エンコード完了を待つ
					ffmpeg?.waitFor()
					println("[Encoder] Session finished successfully: ${fp.name}")
				}
			}
		}
	}

	fun stopNormally() {
		isRunning = false
	}
	fun abort() {
		isRunning = false
		isAborted = true
	}
}