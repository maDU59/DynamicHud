package com.example;

import java.util.Iterator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.server.ServerInfo;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DynamicHudClient implements ClientModInitializer {

	private final Minecraft minecraft = Minecraft.getInstance();

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

	public static float armorVisibility = 1.0f;
	public static boolean armorState = true;
	private static long lastArmorState = System.nanoTime();

	public static float hotbarVisibility = 1.0f;
	public static boolean hotbarState = true;
	private static long lastHotbarState = System.nanoTime();

	public static float healthVisibility = 1.0f;
	public static boolean healthState = true;
	private static long lastHealthState = System.nanoTime();

	private int slot;
	private float armorValue;

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((drawContext, delta) -> {
				UpdateHotbar();
				UpdateHealth();
				UpdateArmor();
			}
		);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			lastHotbarState = System.nanoTime();
			lastHealthState = System.nanoTime();
			lastArmorState = System.nanoTime();
		});
	}

	private void UpdateHealth(){
		if (this.minecraft.player == null) return;

		if (this.minecraft.player.getHealth() < this.minecraft.player.getMaxHealth()){
			lastHealthState = System.nanoTime();
		}

		float time = 3.0f;

		if (lastHealthState + (time * 1000000000L) > System.nanoTime()){
			healthState = true;
		} else {
			healthState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		healthVisibility = lerp(healthVisibility, healthState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private void UpdateArmor(){
		if (this.minecraft.player == null) return;

		float armorValue = this.minecraft.player.getArmorValue() + this.minecraft.player.getArmorCoverPercentage() * 0.1f;
		DamageSource damageSource = this.minecraft.player.getLastDamageSource();
		boolean isHurt = this.minecraft.player.hurtTime > 0 && (!damageSource.is(DamageTypeTags.BYPASSES_ARMOR) || damageSource.is(DamageTypeTags.IS_FALL));

		if(this.armorValue != armorValue || isHurt){
			lastArmorState = System.nanoTime();
			this.armorValue = armorValue;
		}

		float time = 3.0f;

		if (lastArmorState + (time * 1000000000L) > System.nanoTime()){
			armorState = true;
		} else {
			armorState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		armorVisibility = lerp(armorVisibility, armorState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private void UpdateHotbar(){

		if (this.minecraft.player == null) return;

		float time = 3.0f;

		int slot = this.minecraft.player.getInventory().getSelectedSlot();
		if(this.slot != slot){
			lastHotbarState = System.nanoTime();
			this.slot = slot;
		}

		if (lastHotbarState + (time * 1000000000L) > System.nanoTime()){
			hotbarState = true;
		} else {
			hotbarState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		hotbarVisibility = lerp(hotbarVisibility, hotbarState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private float lerp(float value, boolean bool, float duration, float deltaTimeSeconds){
		if (bool){
			if (value < 1.0f){
				value += 1f/(duration * deltaTimeSeconds);
			}
			if (value > 1.0f){
				value = 1.0f;
			}
		} else {
			if (value > 0.0f){
				value -= 1f/(duration * deltaTimeSeconds);
			}
			if (value < 0.0f){
				value = 0.0f;
			}
		}
		return value;
	}

	private int getTotalArmorDamage(){
		if (this.minecraft.player == null) return 0;

		int damageValue = 0;
		Iterator var3 = EquipmentSlotGroup.ARMOR.iterator();

		while(var3.hasNext()) {
			EquipmentSlot equipmentSlot = (EquipmentSlot)var3.next();
			if (equipmentSlot.getType() == Type.HUMANOID_ARMOR) {
				ItemStack itemStack = this.minecraft.player.getItemBySlot(equipmentSlot);
				if (!itemStack.isEmpty() && !(itemStack.is(Items.ELYTRA))) {
					damageValue += itemStack.getDamageValue();
				}
			}
		}

		return damageValue;
	}
}