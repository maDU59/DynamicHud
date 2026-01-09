package com.example.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.example.DynamicHudClient;

@Mixin(Gui.class)
public class CrosshairVisibilityMixin {

	@Inject(at = @At("HEAD"), method = "renderCrosshair", cancellable = true)
	private void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {

		if(Minecraft.getInstance().player == null) return;

		Item heldItem = Minecraft.getInstance().player.getMainHandItem().getItem();
		HitResult hit = Minecraft.getInstance().hitResult;

		if ((hit != null &&hit.getType() != Type.MISS) || heldItem instanceof ProjectileItem){
			return;
		}
		else{
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "renderItemHotbar", cancellable = true)
	private void renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
		if(false){
			return;
		}
		else{
			info.cancel();
		}
	}

	@ModifyVariable(method = "renderPlayerHealth", at = @At("STORE"), ordinal = 4)
	private int offsetPlayerInformationsY(int y) {
		return y + 39 - (10 + (int) (DynamicHudClient.hotbarHeight * DynamicHudClient.hotbarVisibility) + DynamicHudClient.xpHeight + DynamicHudClient.aboveHotbarOffsetY);
	}

	@ModifyVariable(method = "renderVehicleHealth", at = @At("STORE"), ordinal = 2)
	private int offsetVehicleHealthY(int y) {
		return y + 39 - (10 + (int) (DynamicHudClient.hotbarHeight * DynamicHudClient.hotbarVisibility) + DynamicHudClient.xpHeight + DynamicHudClient.aboveHotbarOffsetY);
	}

	// @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 1)
	// private int offsetHealthY(int y) {
	// 	return y + 39 - (hotbarOffsetY + xpOffsetY);
	// }

	// @ModifyVariable(method = "renderFood", at = @At("HEAD"), ordinal = 0)
	// private int offsetFoodY(int y) {
	// 	return y + 39 - (hotbarOffsetY + xpOffsetY);
	// }

	// @ModifyVariable(method = "renderVehicleHealth", at = @At("STORE"), ordinal = 2)
	// private int offsetAnimalHealthY(int y) {
	// 	return y + 39 - (hotbarOffsetY + xpOffsetY);
	// }

	// heldItem.useOn(useOnContext) == InteractionResult.SUCCESS
	// UseOnContext useOnContext = new UseOnContext(Minecraft.getInstance().player, InteractionHand.MAIN_HAND, getBlockHitResult());

	// private BlockHitResult getBlockHitResult(){
	// 	HitResult hit = Minecraft.getInstance().hitResult;

	// 	if (hit != null && hit.getType() != HitResult.Type.ENTITY) {
	// 		return (BlockHitResult) hit;
	// 	} else {
	// 		//return new BlockHitResult();
	// 	}
	// }
}