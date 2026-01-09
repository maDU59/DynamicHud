package com.example.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.example.DynamicHudClient;

import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;

@Mixin(ContextualBarRenderer.class)
public interface ContextualBarRendererMixin {

    @Inject(method = "top", at = @At("RETURN"), cancellable = true)
    default void modifyTopPosition(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(info.getReturnValue() + 22 - DynamicHudClient.hotbarHeight);
    }

    @ModifyVariable(method = "renderExperienceLevel", at = @At("STORE"), ordinal = 2)
	private static int modifyTextPosition(int y) {
		return y + 22 - DynamicHudClient.hotbarHeight;
	}
}