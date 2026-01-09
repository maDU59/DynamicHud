package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;

public class DynamicHudClient implements ClientModInitializer {

	//Hotbar is 22 pixels tall (23 with the selector)
	//2 pixels between hotbar and xp bar
	//Xp bar is 5 pixel tall
	//Hearts are 9 pixel tall (and spaced by 1 pixel above)
	//Hearts are rendered at 39 pixels high

	public static int heartHeight = 10;
	public static int armorHeight = 10;
	public static int foodHeight = 10;
	public static int xpHeight = 5;
	public static int aboveHotbarOffsetY = 2;
	public static int hotbarHeight = 22;

	public static float hotbarVisibility = 1.0f;
	public static boolean hotbarState = true;
	private static long lastHotbarState = System.nanoTime();
	private int slot;

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((drawContext, delta) -> {
				UpdateHotbar();
			}
		);
	}

	private void UpdateHotbar(){

		if (Minecraft.getInstance().player == null) return;

		float time = 3.0f;

		int slot = Minecraft.getInstance().player.getInventory().getSelectedSlot();
		if(this.slot != slot){
			lastHotbarState = System.nanoTime();
			this.slot = slot;
		}

		if (lastHotbarState + (time * 1000000000L) > System.nanoTime()){
			hotbarState = true;
		} else {
			hotbarState = false;
		}

		// This gives you the change in ticks since the last frame
		float deltaTicks = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks();

		// To convert ticks to seconds (since 1 tick = 0.05 seconds):
		float deltaTimeSeconds = 1/(deltaTicks * 0.05f);

		if (hotbarState){
			if (hotbarVisibility < 1.0f){
				hotbarVisibility += 1f/(time * 0.1f * deltaTimeSeconds);
			}
			if (hotbarVisibility > 1.0f){
				hotbarVisibility = 1.0f;
			}
		} else {
			if (hotbarVisibility > 0.0f){
				hotbarVisibility -= 1f/(time * 0.1f * deltaTimeSeconds);
			}
			if (hotbarVisibility < 0.0f){
				hotbarVisibility = 0.0f;
			}
		}
	}
}