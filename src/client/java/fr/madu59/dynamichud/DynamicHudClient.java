package fr.madu59.dynamichud;

import fr.madu59.dynamichud.config.Option;
import fr.madu59.dynamichud.config.SettingsManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.food.FoodProperties;

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

	public static float foodVisibility = 1.0f;
	public static boolean foodState = true;
	private static long lastFoodState = System.nanoTime();

	public static float vehicleHealthVisibility = 1.0f;
	public static boolean vehicleHealthState = true;
	private static long lastVehicleHealthState = System.nanoTime();

	public static float contextualBarVisibility = 1.0f;
	public static boolean contextualBarState = true;
	private static long lastContextualBarState = System.nanoTime();

	public static float xpVisibility = 1.0f;
	public static boolean xpState = true;
	private static long lastXpState = System.nanoTime();

	private int slot;
	private float armorValue;
	private int foodLevel;
	private float experienceProgress;
	private int experienceLevel;

	@Override
	public void onInitializeClient() {

		HudRenderCallback.EVENT.register((drawContext, delta) -> {
				UpdateHotbar();
				UpdateHealth();
				UpdateArmor();
				UpdateFood();
				UpdateVehicleHealth();
				UpdateContextualBar();
			}
		);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			lastHotbarState = System.nanoTime();
			lastHealthState = System.nanoTime();
			lastArmorState = System.nanoTime();
			lastFoodState = System.nanoTime();
			lastVehicleHealthState = System.nanoTime();
			lastContextualBarState = System.nanoTime();
		});
	}

	private void UpdateHealth(){
		if (this.minecraft.player == null) return;

		float time = 3.0f;
		Option.ElementState healthStateSetting = SettingsManager.HEALTH_STATE.getValue();

		if (this.minecraft.player.getHealth() < this.minecraft.player.getMaxHealth()){
			lastHealthState = System.nanoTime();
		}

		if ((lastHealthState + (time * 1000000000L) > System.nanoTime() && healthStateSetting != Option.ElementState.DISABLED) || healthStateSetting == Option.ElementState.ENABLED){
			healthState = true;
		} else {
			healthState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		healthVisibility = lerp(healthVisibility, healthState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private void UpdateVehicleHealth(){
		if (this.minecraft.player == null) return;

		float time = 3.0f;
		Option.ElementState mountHealthStateSetting = SettingsManager.MOUNT_HEALTH_STATE.getValue();

		Entity vehicle = this.minecraft.player.getVehicle();

		if (vehicle != null && vehicle instanceof LivingEntity e && e.getHealth() < e.getMaxHealth()){
			lastVehicleHealthState = System.nanoTime();
		}

		if ((lastVehicleHealthState + (time * 1000000000L) > System.nanoTime() && mountHealthStateSetting != Option.ElementState.DISABLED) || mountHealthStateSetting == Option.ElementState.ENABLED){
			vehicleHealthState = true;
		} else {
			vehicleHealthState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		vehicleHealthVisibility = lerp(vehicleHealthVisibility, vehicleHealthState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private void UpdateFood(){
		if (this.minecraft.player == null) return;

		float time = 3.0f;
		Option.ElementState foodStateSetting = SettingsManager.FOOD_STATE.getValue();

		float threshold = 10.0f;
		int foodLevel = this.minecraft.player.getFoodData().getFoodLevel();

		FoodProperties mainHandFood = this.minecraft.player.getMainHandItem().get(DataComponents.FOOD);

		if (foodLevel < threshold || 
			foodLevel != this.foodLevel || 
			(mainHandFood != null && mainHandFood.nutrition() > 0 && this.minecraft.player.getFoodData().needsFood())
		){
			lastFoodState = System.nanoTime();
			this.foodLevel = foodLevel;
		}

		if ((lastFoodState + (time * 1000000000L) > System.nanoTime() && foodStateSetting != Option.ElementState.DISABLED) || foodStateSetting == Option.ElementState.ENABLED){
			foodState = true;
		} else {
			foodState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		foodVisibility = lerp(foodVisibility, foodState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private void UpdateArmor(){
		if (this.minecraft.player == null) return;

		float time = 3.0f;
		Option.ElementState armorStateSetting = SettingsManager.ARMOR_STATE.getValue();

		float armorValue = this.minecraft.player.getArmorValue() + this.minecraft.player.getArmorCoverPercentage() * 0.1f;
		DamageSource damageSource = this.minecraft.player.getLastDamageSource();
		boolean isHurt = this.minecraft.player.hurtTime > 0 && !damageSource.is(DamageTypeTags.BYPASSES_ARMOR);

		if(this.armorValue != armorValue || isHurt){
			lastArmorState = System.nanoTime();
			this.armorValue = armorValue;
		}

		if ((lastArmorState + (time * 1000000000L) > System.nanoTime() && armorStateSetting != Option.ElementState.DISABLED) || armorStateSetting == Option.ElementState.ENABLED){
			armorState = true;
		} else {
			armorState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		armorVisibility = lerp(armorVisibility, armorState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private void UpdateContextualBar(){

		if (this.minecraft.player == null) return;

		float time = 3.0f;
		Option.ElementState contextualBarStateSetting = SettingsManager.CONTEXTUAL_BAR_STATE.getValue();

		ContextualInfo info = getContextualInfoState();
		int experienceLevel = this.minecraft.player.experienceLevel;
		if (info == DynamicHudClient.ContextualInfo.EMPTY) return;
		else if(info == DynamicHudClient.ContextualInfo.EXPERIENCE){
			float experienceProgress = this.minecraft.player.experienceProgress;

			if(this.experienceLevel != experienceLevel || this.experienceProgress != experienceProgress || this.minecraft.screen instanceof EnchantmentScreen){
				lastContextualBarState = System.nanoTime();
				lastXpState = System.nanoTime();
				this.experienceLevel = experienceLevel;
				this.experienceProgress = experienceProgress;
			}
		} else if(info == DynamicHudClient.ContextualInfo.LOCATOR){
			lastContextualBarState = System.nanoTime();

			if(this.experienceLevel != experienceLevel){
				lastXpState = System.nanoTime();
				this.experienceLevel = experienceLevel;
			}
		} else if(info == DynamicHudClient.ContextualInfo.JUMPABLE_VEHICLE){
			if (this.minecraft.player.jumpableVehicle().getJumpCooldown() > 0){
				lastContextualBarState = System.nanoTime();
			}
			else if (this.minecraft.player.getJumpRidingScale() > 0.0F){
				lastContextualBarState = System.nanoTime();
			}

			if(this.experienceLevel != experienceLevel){
				lastXpState = System.nanoTime();
				this.experienceLevel = experienceLevel;
			}
		}

		if ((lastContextualBarState + (time * 1000000000L) > System.nanoTime() && contextualBarStateSetting != Option.ElementState.DISABLED) || contextualBarStateSetting == Option.ElementState.ENABLED){
			contextualBarState = true;

			if (lastXpState + (time * 1000000000L) > System.nanoTime() || contextualBarStateSetting == Option.ElementState.ENABLED){
				xpState = true;
			} else {
				xpState = false;
			}
		} else {
			contextualBarState = false;
			xpState = false;
		}

		float deltaTicks = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks();

		contextualBarVisibility = lerp(contextualBarVisibility, contextualBarState, time * 0.1f, 1/(deltaTicks * 0.05f));
		xpVisibility = lerp(xpVisibility, xpState, time * 0.1f, 1/(deltaTicks * 0.05f));
	}

	private void UpdateHotbar(){

		if (this.minecraft.player == null) return;

		float time = 3.0f;
		Option.ElementState hotbarStateSetting = SettingsManager.HOTBAR_STATE.getValue();

		int slot = this.minecraft.player.getInventory().getSelectedSlot();
		if(this.slot != slot){
			lastHotbarState = System.nanoTime();
			this.slot = slot;
		}

		if ((lastHotbarState + (time * 1000000000L) > System.nanoTime() && hotbarStateSetting != Option.ElementState.DISABLED) || hotbarStateSetting == Option.ElementState.ENABLED){
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

	private ContextualInfo getContextualInfoState(){
		boolean bl = this.minecraft.player.connection.getWaypointManager().hasWaypoints();
		boolean bl2 = this.minecraft.player.jumpableVehicle() != null;
		boolean bl3 = this.minecraft.gameMode.hasExperience();
		if (bl) {
			if (bl2 && (this.minecraft.player.getJumpRidingScale() > 0.0F || (Integer)Optionull.mapOrDefault(this.minecraft.player.jumpableVehicle(), PlayerRideableJumping::getJumpCooldown, 0) > 0)) {
				return DynamicHudClient.ContextualInfo.JUMPABLE_VEHICLE;
			} else {
				return bl3 && (this.minecraft.player.experienceDisplayStartTick + 100 > this.minecraft.player.tickCount) ? DynamicHudClient.ContextualInfo.EXPERIENCE : DynamicHudClient.ContextualInfo.LOCATOR;
			}
		} else if (bl2) {
			return DynamicHudClient.ContextualInfo.JUMPABLE_VEHICLE;
		} else {
			return bl3 ? DynamicHudClient.ContextualInfo.EXPERIENCE : DynamicHudClient.ContextualInfo.EMPTY;
		}
	}

	static enum ContextualInfo {
		EMPTY,
		EXPERIENCE,
		LOCATOR,
		JUMPABLE_VEHICLE;
	}
}