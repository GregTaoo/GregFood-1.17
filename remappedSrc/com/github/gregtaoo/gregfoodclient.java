package com.github.gregtaoo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class gregfoodclient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.green_onion, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.soybean, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.tea,RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.paddy,RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.turnip, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.ginger,RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.garlic,RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.Orange_tree_leaves, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.Orange_tree_sapling, RenderLayer.getCutout());
        EntityRendererRegistry.INSTANCE.register(gregfood.Seashell, (dispatcher, context) -> new SeashellEntityRenderer(dispatcher));
    }
}