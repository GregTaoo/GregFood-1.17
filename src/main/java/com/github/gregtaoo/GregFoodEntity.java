package com.github.gregtaoo;

import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.*;

class SeashellEntity extends WaterCreatureEntity {

    public SeashellEntity(EntityType<? extends SeashellEntity> entityType, World world) {
        super(entityType, world);
    }

    public int getMaxAir() {
        return 1000;
    }

    protected void initGoals() {
        this.goalSelector.add(1, new EscapeLandGoal(this, 0.3D));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 0.3D));
        this.goalSelector.add(0, new LookAroundGoal(this));
        this.goalSelector.add(1, new WanderAroundGoal(this, 0.2D));
    }

    protected void tickWaterBreathingAir(int air) {
        if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
            this.setAir(air - 1);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.damage(DamageSource.DROWN, 0.5F);
            }
        } else {
            this.setAir(300);
        }

    }

    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return world.getFluidState(this.getBlockPos()).isIn(FluidTags.WATER);
    }
}

class SeashellEntityRenderer extends MobEntityRenderer<SeashellEntity, SeashellEntityModel> {

    public SeashellEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SeashellEntityModel(), 0.5f);
    }

    @Override
    public Identifier getTexture(SeashellEntity entity) {
        return new Identifier("gregfood", "textures/entity/seashell/seashell.png");
    }
}

class SeashellEntityModel extends EntityModel<SeashellEntity> {

    private final ModelPart base;

    public SeashellEntityModel() {
        List<ModelPart.Cuboid> list = new ArrayList<>();
        Map<String, ModelPart> map = new HashMap<>();
        list.add(new ModelPart.Cuboid(0,0,-4, 2, -4, 8, 4, 8,0.0F, 0.0F, 0.0F,false,64,32));
        base = new ModelPart(list, map);
    }

    @Override
    public void setAngles(SeashellEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        // translate model down
        matrices.translate(0, 1.125, 0);

        // render Seashell
        base.render(matrices, vertices, light, overlay);
    }
}
class EscapeLandGoal extends Goal {
    protected final PathAwareEntity mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;

    public EscapeLandGoal(PathAwareEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        if (!this.mob.world.getBlockState(this.mob.getBlockPos().up()).isOf(Blocks.WATER)) {
            BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob);
            if (blockPos != null) {
                this.targetX = blockPos.getX();
                this.targetY = blockPos.getY();
                this.targetZ = blockPos.getZ();
                return true;
            }
        }
        return false;
    }

    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
    }

    public void stop() {
        this.active = false;
    }

    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    @Nullable
    protected BlockPos locateClosestWater(BlockView blockView, Entity entity) {
        BlockPos blockPos = entity.getBlockPos();
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        float f = (float)(5 * 5 * 4 * 2);
        BlockPos blockPos2 = null;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int l = i - 5; l <= i + 5; ++l) {
            for(int m = j - 4; m <= j + 4; ++m) {
                for(int n = k - 5; n <= k + 5; ++n) {
                    mutable.set(l, m, n);
                    if (blockView.getFluidState(mutable).isIn(FluidTags.WATER)) {
                        float g = (float)((l - i) * (l - i) + (m - j) * (m - j) + (n - k) * (n - k));
                        if (g < f) {
                            f = g;
                            blockPos2 = new BlockPos(mutable);
                        }
                    }
                }
            }
        }

        return blockPos2;
    }
}
