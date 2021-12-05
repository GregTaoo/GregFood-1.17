package com.github.gregtaoo;


import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

class GregFoodSaplingBlock extends PlantBlock implements Fertilizable {
    public static final IntProperty STAGE;
    protected static final VoxelShape SHAPE;
    private final SaplingGenerator generator;

    public GregFoodSaplingBlock(SaplingGenerator generator, AbstractBlock.Settings settings) {
        super(settings);
        this.generator = generator;
        this.setDefaultState(this.stateManager.getDefaultState().with(STAGE, 0));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(pos.up()) >= 9 && random.nextInt(7) == 0) {
            this.generate(world, pos, state, random);
        }

    }

    public void generate(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, Random random) {
        if (blockState.get(STAGE) == 0) {
            serverWorld.setBlockState(blockPos, blockState.cycle(STAGE), 4);
        } else {
            this.generator.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), blockPos, blockState, random);
        }

    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return (double)world.random.nextFloat() < 0.45D;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        this.generate(world, pos, state, random);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    static {
        STAGE = Properties.STAGE;
        SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    }
}

class OrangeSaplingGenerator extends SaplingGenerator {
    @Nullable
    protected ConfiguredFeature<TreeFeatureConfig, ?> createTreeFeature(Random random, boolean bl) {
        return gregfood.Orange_tree_feature;
    }
}
class OrangeLeavesBlock extends LeavesBlock{
    public static final IntProperty AGE;
    public static final BooleanProperty FRUIT;

    public OrangeLeavesBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FRUIT,false).with(DISTANCE, 7).with(PERSISTENT, false).with(AGE,0));
    }
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        Random random = new Random();
        if(state.get(AGE) == 3)
          dropStack(world,pos,new ItemStack(gregfood.Orange,random.nextInt(2)+1));
    }
    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(PERSISTENT);
    }
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!(Boolean)state.get(PERSISTENT) && state.get(DISTANCE) == 7) {
            dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
        if(random.nextInt(15)==0 && state.get(FRUIT) && state.get(DISTANCE) < 7){
            int age = state.get(AGE);
            if(age < 3){
                world.setBlockState(pos,state.with(AGE,age+1));
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        int age = state.get(AGE);
        if(itemStack.isEmpty()){
            return ActionResult.PASS;
        }else if(itemStack.getItem() == gregfood.WEEDING_SHOVEL && age == 3){
            Random random = new Random();
            world.setBlockState(pos,state.with(AGE,0));
            ItemStack itemStack1 = new ItemStack(gregfood.Orange, random.nextInt(2)+1);
            dropStack(world,pos.up(),itemStack1);
            return ActionResult.SUCCESS;
        }else if(itemStack.getItem() == Items.BONE_MEAL && state.get(FRUIT) && state.get(DISTANCE) < 7 && age<3){
            if(!player.abilities.creativeMode){
                itemStack.decrement(1);
            }
            world.setBlockState(pos,state.with(AGE,age+1));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT, AGE, FRUIT);
    }

    static {
        AGE = Properties.AGE_3;
        FRUIT = Properties.ENABLED;
    }
}
