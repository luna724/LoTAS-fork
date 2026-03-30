package com.luna724.lotas

import com.luna724.lotas.record.recording.EncoderManager
import com.luna724.lotas.record.recording.FileManager
import com.luna724.lotas.record.recording.RecordingSystem
import com.luna724.lotas.LoTASFork.Companion.LOGGER
import de.pfannekuchen.lotas.mods.SavestateMod
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.Vec3
import java.io.File

class SaveAdditionalStateMod {
    @Serializable
    data class Vec3S(
        val x: Double, val y: Double, val z: Double
    ) {
        companion object {
            fun fromVec3(v: Vec3): Vec3S {
                return Vec3S(v.x, v.y, v.z)
            }
        }
    }

    @Serializable
    data class SavedState(
        val yaw: Float, val pitch: Float,
        val prevYaw: Float, val prevPitch: Float,
        val attackTicker: Int,
        val moveVelocity: Vec3S,
        val fallDistance: Float,
//        val clientInput: movemaentInputs? = null
        val valid: Boolean = true
    ) {
        companion object {
            fun createEmpty(): SavedState {
                return SavedState(
                    0f, 0f, 0f, 0f,
                    0, Vec3S(0.0,0.0,0.0),
//                    clientInput = null,
                    0f,
                    valid =false
                )
            }
        }
    }

    @Serializable
    data class movementInputs(
        val w: Boolean, val a: Boolean, val s: Boolean, val d: Boolean,
        val shift: Boolean, val control: Boolean, 
        val left: Boolean, val right: Boolean, // is holding mouse?
        val valid: Boolean = true
    ) {
        companion object {
            fun createEmpty(): movementInputs {
                return movementInputs(
                    w = false,
                    a = false,
                    s = false,
                    d = false,
                    shift = false,
                    control = false,
                    left = false,
                    right = false,
                    valid=false
                )
            }
        }
    }


    companion object {
        var currentState: Int = 0
        private val onRAMStorage: MutableMap<String, Any> = mutableMapOf()

        private fun createSavedState(): SavedState {
            val mc = Minecraft.getInstance()
            val player = mc.player
                ?: return SavedState.createEmpty()

            return SavedState(
                yaw = player.yRot,
                pitch = player.xRot,
                prevYaw = player.yRotO,
                prevPitch = player.xRotO,
                attackTicker = player.attackStrengthTicker,
                moveVelocity = Vec3S.fromVec3(player.deltaMovement),
                fallDistance = player.fallDistance
//                clientInput = readClientInput(player)
            )
        }

//        private fun readClientInput(player: LocalPlayer): movementInputs {
//            try {
//                val input = player.input ?: throw NullPointerException("player.input is not valid field")
//                val options = Minecraft.getInstance().options
//
//                return movementInputs(
//                    w = input.up || options.keyUp.isDown,
//                    a = input.left || options.keyLeft.isDown,
//                    s = input.down || options.keyDown.isDown,
//                    d = input.right || options.keyRight.isDown,
//                    shift = readAccessibleField(input, arrayOf("shifyKeyDown", "sneaking"), default=false) as Boolean
//                            || readAccessibleField(options, arrayOf("keyShift", "keySneak"), default=false) as Boolean,
//                    control = options.keySprint.isDown,
//                    left = options.keyAttack.isDown,
//                    right = options.keyUse.isDown,
//                    valid = true
//                )
//            } catch (e: Exception) {
//                e.printStackTrace()
//                return movementInputs.createEmpty()
//            }
//        }

        /**
         * SavedStateを作成、保存する
         * SaveStateMod.savestate:ln72
         *
         */
        fun onSaveState(
            ssDir: File, ssNum: Int
        ) {
            val ss = createSavedState()
            val dir = File(ssDir, ssDir.name + ".json")
            if (!ssDir.exists()) { ssDir.mkdirs() }

            onRAMStorage[dir.name] = ss
            dir.writeText(Json.encodeToString(ss))
            LOGGER.info(
                "Savestate created to ${dir.absolutePath}"
            )
            currentState = ssNum
            RecordingSystem.manager.onSaveState(ssNum)
            logMismatch()
        }

        fun onLoadState(
            toState: Int
        ) {
            // TODO: restore SavedState
            LOGGER.info(
                "Loading state #${toState} (from: #${currentState})"
            )
            if (currentState >= toState) {
                for (s in currentState downTo toState) {
                    FileManager.destory(s)
                }
            }
            currentState = toState
            RecordingSystem.manager.onLoadState(toState)
            logMismatch()
        }

        fun logMismatch() {
            if (currentState != SavestateMod.TrackerFile.savestateCount) {
                LOGGER.error(
                    "StateCount mismatched! (EstimatedCurrent=${currentState} / ssCount: ${SavestateMod.TrackerFile.savestateCount})"
                )
            }
        }
    }
}