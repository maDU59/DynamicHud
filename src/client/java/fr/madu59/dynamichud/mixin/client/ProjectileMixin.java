package fr.madu59.dynamichud.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.dynamichud.DynamicHudClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {

    @Inject( at = @At("HEAD"), method = "onHit")
    private void onHit(HitResult hitResult, CallbackInfo ci){
        HitResult.Type type = hitResult.getType();
        if (type == Type.ENTITY){
            Projectile projectile = (Projectile) (Object) this;
            Entity owner = projectile.getOwner();
            if (owner != null && owner.getId() == Minecraft.getInstance().player.getId()){
                DynamicHudClient.projectileHit();
            }
        }
    }
}
