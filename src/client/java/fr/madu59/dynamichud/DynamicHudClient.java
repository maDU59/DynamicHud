package fr.madu59.dynamichud;

import fr.madu59.dynamichud.config.Option;
import fr.madu59.dynamichud.config.SettingsManager;
import fr.madu59.dynamichud.helpers.EasingFunctions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

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
	private static float armorLinear = 1.0f;
	private static long lastArmorState = System.nanoTime();

	public static float hotbarVisibility = 1.0f;
	public static boolean hotbarState = true;
	private static float hotbarLinear = 1.0f;
	private static long lastHotbarState = System.nanoTime();

	public static float healthVisibility = 1.0f;
	public static boolean healthState = true;
	private static float healthLinear = 1.0f;
	private static long lastHealthState = System.nanoTime();

	public static float foodVisibility = 1.0f;
	public static boolean foodState = true;
	private static float foodLinear = 1.0f;
	private static long lastFoodState = System.nanoTime();

	public static float vehicleHealthVisibility = 1.0f;
	public static boolean vehicleHealthState = true;
	private static float vehicleHealthLinear = 1.0f;
	private static long lastVehicleHealthState = System.nanoTime();

	public static float contextualBarVisibility = 1.0f;
	public static boolean contextualBarState = true;
	private static float contextualBarLinear = 1.0f;
	private static long lastContextualBarState = System.nanoTime();

	public static boolean canHit = false;
	public static DynamicHudClient.Action action = DynamicHudClient.Action.NULL;
	public static float projectileHitTimer = 0;

	public static float xpVisibility = 1.0f;
	public static boolean xpState = true;
	private static float xpLinear = 1.0f;
	private static long lastXpState = System.nanoTime();

	private int slot;
	private float armorValue;
	private int foodLevel;
	private float experienceProgress;
	private int experienceLevel;

	@Override
	public void onInitializeClient() {

		HudRenderCallback.EVENT.register((drawContext, delta) -> {

				float deltaTime = this.minecraft.getDeltaTracker().getGameTimeDeltaTicks() * 0.05f;
				if(projectileHitTimer>0) projectileHitTimer -= deltaTime;

				action = UpdateCrosshair();
				UpdateHotbar(deltaTime);
				UpdateHealth(deltaTime);
				UpdateArmor(deltaTime);
				UpdateFood(deltaTime);
				UpdateVehicleHealth(deltaTime);
				UpdateContextualBar(deltaTime);
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

	private void UpdateHealth(float deltaTime){
		if (this.minecraft.player == null) return;

		float time = SettingsManager.SHOWN_DURATION.getValue();
		float fadingDuration = SettingsManager.FADING_DURATION.getValue();
		Option.ElementState healthStateSetting = SettingsManager.HEALTH_STATE.getValue();

		if (this.minecraft.player.getHealth() < this.minecraft.player.getMaxHealth()){
			lastHealthState = System.nanoTime();
		}

		if ((lastHealthState + (time * 1000000000L) > System.nanoTime() && healthStateSetting != Option.ElementState.DISABLED) || healthStateSetting == Option.ElementState.ENABLED){
			healthState = true;
		} else {
			healthState = false;
		}

		healthLinear = lerp(healthLinear, healthState, fadingDuration, deltaTime);
		healthVisibility = EasingFunctions.ease(healthLinear, SettingsManager.EASING_FUNCTION.getValue(), healthState, healthVisibility, deltaTime, fadingDuration);
	}

	private void UpdateVehicleHealth(float deltaTime){
		if (this.minecraft.player == null) return;

		float time = SettingsManager.SHOWN_DURATION.getValue();
		float fadingDuration = SettingsManager.FADING_DURATION.getValue();
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

		vehicleHealthLinear = lerp(vehicleHealthLinear, vehicleHealthState, fadingDuration, deltaTime);
		vehicleHealthVisibility = EasingFunctions.ease(vehicleHealthLinear, SettingsManager.EASING_FUNCTION.getValue(), vehicleHealthState, vehicleHealthVisibility, deltaTime, fadingDuration);
	}

	private void UpdateFood(float deltaTime){
		if (this.minecraft.player == null) return;

		float time = SettingsManager.SHOWN_DURATION.getValue();
		float fadingDuration = SettingsManager.FADING_DURATION.getValue();
		Option.ElementState foodStateSetting = SettingsManager.FOOD_STATE.getValue();

		float threshold = SettingsManager.DYNAMIC_FOOD_BAR_MINIMUM.getValue() * 2;
		int foodLevel = this.minecraft.player.getFoodData().getFoodLevel();

		if (foodLevel <= threshold || 
			foodLevel != this.foodLevel || 
			(action == DynamicHudClient.Action.FOOD && SettingsManager.DYNAMIC_FOOD_BAR_HOLDING.getValue() == true)
		){
			lastFoodState = System.nanoTime();
			this.foodLevel = foodLevel;
		}

		if ((lastFoodState + (time * 1000000000L) > System.nanoTime() && foodStateSetting != Option.ElementState.DISABLED) || foodStateSetting == Option.ElementState.ENABLED){
			foodState = true;
		} else {
			foodState = false;
		}

		foodLinear = lerp(foodLinear, foodState, fadingDuration, deltaTime);
		foodVisibility = EasingFunctions.ease(foodLinear, SettingsManager.EASING_FUNCTION.getValue(), foodState, foodVisibility, deltaTime, fadingDuration);
	}

	private void UpdateArmor(float deltaTime){
		if (this.minecraft.player == null) return;

		float time = SettingsManager.SHOWN_DURATION.getValue();
		float fadingDuration = SettingsManager.FADING_DURATION.getValue();
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

		armorLinear = lerp(armorLinear, armorState, fadingDuration, deltaTime);
		armorVisibility = EasingFunctions.ease(armorLinear, SettingsManager.EASING_FUNCTION.getValue(), armorState, armorVisibility, deltaTime, fadingDuration);
	}

	private void UpdateContextualBar(float deltaTime){

		if (this.minecraft.player == null) return;

		float time = SettingsManager.SHOWN_DURATION.getValue();
		float fadingDuration = SettingsManager.FADING_DURATION.getValue();
		Option.ElementState contextualBarStateSetting = SettingsManager.CONTEXTUAL_BAR_STATE.getValue();

		ContextualInfo info = getContextualInfoState();
		int experienceLevel = this.minecraft.player.experienceLevel;
		if (info == DynamicHudClient.ContextualInfo.EMPTY) return;
		else if(info == DynamicHudClient.ContextualInfo.EXPERIENCE){
			float experienceProgress = this.minecraft.player.experienceProgress;

			if((this.experienceLevel < experienceLevel && SettingsManager.DYNAMIC_XP_BAR_MODE.value == Option.xpBarState.ON_LEVEL) || (this.experienceProgress != experienceProgress && SettingsManager.DYNAMIC_XP_BAR_MODE.value == Option.xpBarState.ON_GAIN) || this.experienceProgress + this.experienceLevel > experienceProgress + experienceLevel || this.minecraft.screen instanceof EnchantmentScreen){
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

		contextualBarLinear = lerp(contextualBarLinear, contextualBarState, fadingDuration, deltaTime);
		xpLinear = lerp(xpLinear, xpState, fadingDuration, deltaTime);
		contextualBarVisibility = EasingFunctions.ease(contextualBarLinear, SettingsManager.EASING_FUNCTION.getValue(), contextualBarState, contextualBarVisibility, deltaTime, fadingDuration);
		xpVisibility = EasingFunctions.ease(xpLinear, SettingsManager.EASING_FUNCTION.getValue(), xpState, xpVisibility, deltaTime, fadingDuration);
	}

	private void UpdateHotbar(float deltaTime){

		if (this.minecraft.player == null) return;

		float time = SettingsManager.SHOWN_DURATION.getValue();
		float fadingDuration = SettingsManager.FADING_DURATION.getValue();
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

		hotbarLinear = lerp(hotbarLinear, hotbarState, fadingDuration, deltaTime);
		hotbarVisibility = EasingFunctions.ease(hotbarLinear, SettingsManager.EASING_FUNCTION.getValue(), hotbarState, hotbarVisibility, deltaTime, fadingDuration);
	}

	private DynamicHudClient.Action UpdateCrosshair(){

		if (this.minecraft.player == null) return DynamicHudClient.Action.NULL;

		for (InteractionHand interactionHand : InteractionHand.values()){
			ItemStack itemStack = this.minecraft.player.getItemInHand(interactionHand);
			if (!itemStack.isItemEnabled(this.minecraft.level.enabledFeatures())) {
				return DynamicHudClient.Action.NULL;
			}

			if (this.minecraft.hitResult != null){
				if (this.minecraft.hitResult.getType() == Type.ENTITY){
					canHit = true;
				}
				if (this.minecraft.hitResult.getType() == Type.BLOCK && this.minecraft.hitResult instanceof BlockHitResult blockHitResult ){
					if(!itemStack.isEmpty()){
						if(itemStack.getItem() instanceof BlockItem){
							BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(this.minecraft.player, interactionHand, blockHitResult));
							if(context.canPlace()) return DynamicHudClient.Action.PLACE;
						}
						if(getToolInteraction(itemStack.getItem(), this.minecraft.level.getBlockState(blockHitResult.getBlockPos()))) return DynamicHudClient.Action.TOOL_INTERACTION;
					}
				}
			}
			if(!itemStack.isEmpty()){
				if(itemStack.get(DataComponents.FOOD) != null && itemStack.get(DataComponents.FOOD).nutrition() > 0 && this.minecraft.player.getFoodData().needsFood()){
					return DynamicHudClient.Action.FOOD;
				}
			}
		}
		return DynamicHudClient.Action.NULL;
	}

	private boolean getToolInteraction(Item item, BlockState blockState){
		if (item instanceof AxeItem) {
			return AxeItem.STRIPPABLES.containsKey(blockState.getBlock());
		}
		
		if (item instanceof ShovelItem) {
			return ShovelItem.FLATTENABLES.containsKey(blockState.getBlock());
		}
		
		if (item instanceof HoeItem) {
			return HoeItem.TILLABLES.containsKey(blockState.getBlock());
		}

		return false;
	}

	private float lerp(float value, boolean bool, float duration, float deltaTimeSeconds){
		if (bool){
			if (value < 1.0f){
				value += deltaTimeSeconds/duration;
			}
			if (value > 1.0f || SettingsManager.FADEIN_TYPE.getValue() == EasingFunctions.FadingType.INSTANT){
				value = 1.0f;
			}
		} else {
			if (value > 0.0f){
				value -= deltaTimeSeconds/duration;
			}
			if (value < 0.0f || SettingsManager.FADEOUT_TYPE.getValue() == EasingFunctions.FadingType.INSTANT){
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

	public static void projectileHit(){
		projectileHitTimer = 0.5f;
	}

	public static enum Action{
		NULL,
		FOOD,
		PLACE,
		TOOL_INTERACTION
	}

	static enum ContextualInfo {
		EMPTY,
		EXPERIENCE,
		LOCATOR,
		JUMPABLE_VEHICLE;
	}
}