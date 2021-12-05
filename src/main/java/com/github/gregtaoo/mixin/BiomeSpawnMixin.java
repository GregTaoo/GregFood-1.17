package com.github.gregtaoo.mixin;

import com.github.gregtaoo.gregfood;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.SpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DefaultBiomeCreator.class)
public class BiomeSpawnMixin {
    @ModifyVariable(method = "createRiver(FFFIZ)Lnet/minecraft/world/biome/Biome;", at = @At("STORE"), ordinal = 0)
    private static SpawnSettings.Builder injectFunction1(SpawnSettings.Builder builder) {
        return builder.spawn(SpawnGroup.WATER_CREATURE,new SpawnSettings.SpawnEntry(gregfood.Seashell,6,1,5));
    }
    @ModifyVariable(method = "createWarmOcean()Lnet/minecraft/world/biome/Biome;", at = @At("STORE"), ordinal = 0)
    private static SpawnSettings.Builder injectFunction2(SpawnSettings.Builder builder) {
        return builder.spawn(SpawnGroup.WATER_CREATURE,new SpawnSettings.SpawnEntry(gregfood.Seashell,6,1,5));
    }
    @ModifyVariable(method = "createLukewarmOcean(Z)Lnet/minecraft/world/biome/Biome;", at = @At("STORE"), ordinal = 0)
    private static SpawnSettings.Builder injectFunction3(SpawnSettings.Builder builder) {
        return builder.spawn(SpawnGroup.WATER_CREATURE,new SpawnSettings.SpawnEntry(gregfood.Seashell,6,1,5));
    }
}