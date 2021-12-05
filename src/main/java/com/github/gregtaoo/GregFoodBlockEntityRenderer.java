package com.github.gregtaoo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;


@Environment(EnvType.CLIENT)
class DoughMakingTableEntityRenderer implements BlockEntityRenderer<DoughMakingTableEntity> {

    public DoughMakingTableEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }
    public void render(DoughMakingTableEntity doughMakingTableEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        int k = (int)doughMakingTableEntity.getPos().asLong();
        ItemStack itemStack = doughMakingTableEntity.getItemStack(false);
        if (itemStack != ItemStack.EMPTY) {
            matrixStack.push();
            matrixStack.translate(0.25D, 0.25D, 0.25D);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            matrixStack.translate(0.25D, 0.25D, 0.0D);
            matrixStack.scale(0.75F, 0.75F, 0.75F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider, k + 1);
            matrixStack.pop();
        }
    }
}
@Environment(EnvType.CLIENT)
class CuttingBoardEntityRenderer implements BlockEntityRenderer<CuttingBoardEntity> {

    public CuttingBoardEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    public void render(CuttingBoardEntity cuttingBoardEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        int k = (int)cuttingBoardEntity.getPos().asLong();
        ItemStack itemStack = cuttingBoardEntity.getItemStack(false);
        if (itemStack != ItemStack.EMPTY) {
            matrixStack.push();
            matrixStack.translate(0.25D, 0.10D, 0.40D);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            matrixStack.translate(0.25D, 0.10D, 0.0D);
            matrixStack.scale(0.50F, 0.50F, 0.50F);
            MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider, k + 1);
            matrixStack.pop();
        }
    }
}
@Environment(EnvType.CLIENT)
class WoodenSteamerRenderer implements BlockEntityRenderer<WoodenSteamerEntity> {
    
    public WoodenSteamerRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    public void render(WoodenSteamerEntity woodenSteamerEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        DefaultedList<ItemStack> defaultedList = woodenSteamerEntity.downItems;
        int k = (int)woodenSteamerEntity.getPos().asLong();

        for(int l = 0; l < 4; ++l) {
            ItemStack itemStack = defaultedList.get(l);
            if (itemStack != ItemStack.EMPTY) {
                matrixStack.push();
                matrixStack.translate(0.5D, 0.1D, 0.5D);
                Direction direction2 = Direction.fromHorizontal(l % 4);
                float g = -direction2.asRotation();
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(g));
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                matrixStack.translate(-0.1D, -0.2D, 0.0D);
                matrixStack.scale(0.375F, 0.375F, 0.375F);
                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider, k + l);
                matrixStack.pop();
            }
        }

        DefaultedList<ItemStack> defaultedListUp = woodenSteamerEntity.upItems;
        k = (int)woodenSteamerEntity.getPos().asLong();

        for(int l = 0; l < 4; ++l) {
            ItemStack itemStack = defaultedListUp.get(l);
            if (itemStack != ItemStack.EMPTY) {
                matrixStack.push();
                matrixStack.translate(0.5D, 0.6D, 0.5D);
                Direction direction2 = Direction.fromHorizontal(l % 4);
                float g = -direction2.asRotation();
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(g));
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                matrixStack.translate(-0.2D, -0.2D, 0.0D);
                matrixStack.scale(0.375F, 0.375F, 0.375F);
                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider, k + l);
                matrixStack.pop();
            }
        }

    }
}
@Environment(EnvType.CLIENT)
class PotEntityRenderer implements BlockEntityRenderer<PotBlockEntity> {

    public PotEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    public void render(PotBlockEntity potBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        DefaultedList<ItemStack> itemStack = potBlockEntity.ingredients;
        for(int k = 0; k < itemStack.size(); ++i){
            if (itemStack.get(k) != ItemStack.EMPTY) {
                matrixStack.push();
                matrixStack.translate(0.25D, 0.10D, 0.40D);
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                matrixStack.translate(0.25D, 0.10D, 0.0D);
                matrixStack.scale(0.50F, 0.50F, 0.50F);
                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack.get(k), ModelTransformation.Mode.FIXED, i, j, matrixStack, vertexConsumerProvider, k + 1);
                matrixStack.pop();
            }
        }
    }
}
