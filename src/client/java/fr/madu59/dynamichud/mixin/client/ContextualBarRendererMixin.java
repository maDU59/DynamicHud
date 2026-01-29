package fr.madu59.dynamichud.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.dynamichud.DynamicHudClient;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@Mixin(ContextualBarRenderer.class)
public abstract interface ContextualBarRendererMixin {

    @Inject(method = "top", at = @At("RETURN"), cancellable = true)
    default void modifyTopPosition(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(info.getReturnValue() + 24 - (int)((DynamicHudClient.hotbarHeight + DynamicHudClient.aboveHotbarOffsetY) * DynamicHudClient.hotbarVisibility));
    }

    @ModifyVariable(method = "renderExperienceLevel", at = @At("STORE"), ordinal = 2)
	private static int modifyTextPosition(int y) {
		return y + 24 - (int)((DynamicHudClient.hotbarHeight + DynamicHudClient.aboveHotbarOffsetY) * DynamicHudClient.hotbarVisibility);
	}

    @Redirect(
		method = "renderExperienceLevel",
		at = @At(value = "INVOKE", 
				target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V")
	)
	private static void redirectXpLevelText(GuiGraphics instance, Font font, Component component, int i, int j, int k, boolean bl) {
		instance.drawString(font, component, i, j, Mth.floor(255.0F * DynamicHudClient.xpVisibility) << 24 | (k & 0x00FFFFFF), bl);
	}
}