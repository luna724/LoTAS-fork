package com.luna724.lotas.record.recording

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL21

// @Copilot
class PBOCapturer(val width: Int, val height: Int) {
    private val pboIds = IntArray(2)
    private var index = 0
    private var nextIndex = 1
    private val dataSize = width * height * 3 // RGB24

    fun init() {
        pboIds[0] = GL15.glGenBuffers()
        pboIds[1] = GL15.glGenBuffers()

        for (id in pboIds) {
            GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, id)
            // GL_STREAM_READ: VRAMからRAMへ毎フレーム読み出す用途に最適化するフラグ
            GL15.glBufferData(GL21.GL_PIXEL_PACK_BUFFER, dataSize.toLong(), GL15.GL_STREAM_READ)
        }
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0)
    }

    /**
     * @param targetArray プールから借りてきた空のByteArray
     * @return 読み込みに成功したかどうか
     */
    fun capture(targetArray: ByteArray): Boolean {
        // ★重要: OpenGLのバイトアライメント設定。
        // デフォルト(4)のままだと、横幅が4の倍数でない時に画像が斜めに歪むため、1バイト単位に強制する。
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)

        // --- 1. [次のバッファ] へGPUからの転送を命令（非同期） ---
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, pboIds[nextIndex])
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, 0L)

        // --- 2. [現在のバッファ] からCPUへデータを吸い出す ---
        var success = false
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, pboIds[index])

        // GPUメモリをByteBufferとしてマップする
        val byteBuffer = GL15.glMapBuffer(GL21.GL_PIXEL_PACK_BUFFER, GL15.GL_READ_ONLY, null)

        if (byteBuffer != null) {
            // Bufferから提供されたByteArrayへ一括コピー
            byteBuffer.get(targetArray)
            GL15.glUnmapBuffer(GL21.GL_PIXEL_PACK_BUFFER)
            success = true
        }

        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0)

        // インデックスの入れ替え
        index = (index + 1) % 2
        nextIndex = (nextIndex + 1) % 2

        return success
    }

    fun cleanup() {
        GL15.glDeleteBuffers(pboIds[0])
        GL15.glDeleteBuffers(pboIds[1])
    }
}