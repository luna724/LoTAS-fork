package de.pfannekuchen.lotas.mixin;

import com.luna724.lotas.record.recording.RecordingSystem;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO : version conrtol
@Mixin(Window.class)
public class MixinWindow {
	@Inject(method = "updateDisplay", at = @At("HEAD"))
    private void onSwapBuffers(CallbackInfo ci) {
		RecordingSystem.INSTANCE.getManager().onFrameRendered();
    }
}
