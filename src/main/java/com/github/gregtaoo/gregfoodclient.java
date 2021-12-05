package com.github.gregtaoo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class gregfoodclient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.green_onion, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.soybean, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.tea, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.paddy, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.turnip, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.ginger, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.garlic, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.Orange_tree_leaves, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.Orange_tree_sapling, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(gregfood.Stove, RenderLayer.getCutout());
        EntityRendererRegistry.INSTANCE.register(gregfood.Seashell, SeashellEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(gregfood.Dough_Making_Table_Entity, DoughMakingTableEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(gregfood.Cutting_Board_Entity, CuttingBoardEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(gregfood.Wooden_Steamer_Entity, WoodenSteamerRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(gregfood.pot_Block_Entity, PotEntityRenderer::new);
    }

}
