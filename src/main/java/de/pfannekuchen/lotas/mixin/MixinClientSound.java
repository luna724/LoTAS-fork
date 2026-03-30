package de.pfannekuchen.lotas.mixin;

import com.luna724.lotas.record.SoundLog;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * プレイヤーに聞こえるseを全取得
 * -> l7.lotas.record.AudioLog
 */

@Mixin(SoundManager.class)
public class MixinClientSound {
	// TODO : version compat
	//#if MC>=11700
	@Inject(method = "play", at = @At("HEAD"))
	private void onPlay(SoundInstance sound, CallbackInfo ci) {
		SoundLog.Companion.putAudio(sound);
	}
	//#endif
}
