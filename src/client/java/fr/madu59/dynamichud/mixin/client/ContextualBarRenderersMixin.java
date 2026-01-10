package fr.madu59.dynamichud.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import fr.madu59.dynamichud.DynamicHudClient;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

@Mixin(targets = {"net.minecraft.client.gui.contextualbar.ExperienceBarRenderer",
                  "net.minecraft.client.gui.contextualbar.LocatorBarRenderer",
                  "net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer"})
public class ContextualBarRenderersMixin {

    @Redirect(
		method = {"renderBackground", "render"},
		at = @At(value = "INVOKE", 
				target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V")
	)
	private void redirectHotbarBlit(GuiGraphics instance, RenderPipeline pipeline, Identifier id, int x, int y, int width, int height) {
		instance.blitSprite(pipeline, id, x, y, width, height, DynamicHudClient.contextualBarVisibility);
	}

    @Redirect(
		method = {"renderBackground", "render"},
		at = @At(value = "INVOKE", 
				target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V")
	)
	private void redirectHotbarBlit(GuiGraphics instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l, int m, int n, int o, int p) {
		instance.blitSprite(renderPipeline, identifier, i, j, k, l, m, n, o, p, ARGB.white(DynamicHudClient.contextualBarVisibility));
	}
}