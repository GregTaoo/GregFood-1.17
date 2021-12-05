package com.github.gregtaoo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

class OnionCrop extends CropBlock {
    public OnionCrop(Settings settings) {
        super(settings);
    }
    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return gregfood.green_onion;
    }
}
class SoybeanCrop extends CropBlock {
    public SoybeanCrop(Settings settings) {
        super(settings);
    }
    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return gregfood.soybean;
    }
}
class TeaBlock extends PlantBlock implements Fertilizable {
    public static final IntProperty AGE;
    private static final VoxelShape SMALL_SHAPE;
    private static final VoxelShape LARGE_SHAPE;

    public TeaBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(gregfood.tea);
    }
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(AGE) == 0) {
            return SMALL_SHAPE;
        } else {
            return state.get(AGE) < 3 ? LARGE_SHAPE : super.getOutlineShape(state, world, pos, context);
        }
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(gregfood.tea) || floor.isOf(gregfood.TeaRooted_dirt) || floor.isOf(Blocks.GRASS_BLOCK) || floor.isOf(Blocks.DIRT) || floor.isOf(Blocks.COARSE_DIRT) || floor.isOf(Blocks.PODZOL) || floor.isOf(Blocks.FARMLAND);
    }

    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) < 3;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(AGE);
        boolean haveDirt = false;
        for (int j=1; j<=2; ++j) {
            if(world.getBlockState(pos.down(j)).isOf(gregfood.TeaRooted_dirt)){
                haveDirt = true;
                break;
            }
        }
        if(!haveDirt) {
            if (i < 3 && random.nextInt(20) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
                world.setBlockState(pos, state.with(AGE, i + 1), 2);
                if (random.nextInt(10) == 4 && !world.getBlockState(pos.down()).isOf(gregfood.tea)) {
                    world.setBlockState(pos.down(), gregfood.TeaRooted_dirt.getDefaultState(), 2);
                }
            }
        } else {
            if (i < 3 && random.nextInt(10) == 0) {
                world.setBlockState(pos, state.with(AGE, i + 1), 2);
            }
            if (random.nextInt(3) == 0){
                if(world.getBlockState(pos.up()).isAir()){
                    world.setBlockState(pos.up(),gregfood.tea.getDefaultState().with(AGE,0),1);
                }
            }
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(AGE);
        boolean bl = i == 3;
        if (!bl && player.getStackInHand(hand).getItem() == Items.BONE_MEAL) {
            return ActionResult.PASS;
        } else if (i > 1) {
            int j = 1 + world.random.nextInt(2);
            dropStack(world, pos, new ItemStack(gregfood.tea, j + (bl ? 1 : 0)));
            if(world.random.nextInt(3) == 0 && i > 2){
                dropStack(world, pos, new ItemStack(gregfood.Tea_Seed, world.random.nextInt(5) + 1));
            }
            world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlockState(pos, state.with(AGE, 1), 2);
            return ActionResult.success(world.isClient);
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 3;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = state.get(AGE);
        boolean haveDirt = false;
        for (int j=1; j<=2; ++j) {
            if(world.getBlockState(pos.down(j)).isOf(gregfood.TeaRooted_dirt)){
                haveDirt = true;
                break;
            }
        }
        if(!haveDirt) {
            if (i < 3) {
                world.setBlockState(pos, state.with(AGE, i + 1), 2);
                if (random.nextInt(10) == 4 && !world.getBlockState(pos.down()).isOf(gregfood.tea)) {
                    world.setBlockState(pos.down(), gregfood.TeaRooted_dirt.getDefaultState(), 2);
                }
            }
        } else {
            if (i < 3) {
                world.setBlockState(pos, state.with(AGE, i + 1), 2);
            }
        }
    }

    static {
        AGE = Properties.AGE_3;
        SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
        LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    }
}
class GingerBlock extends PlantBlock implements Fertilizable {
    public static final IntProperty AGE;
    private static final VoxelShape SMALL_SHAPE;
    private static final VoxelShape LARGE_SHAPE;

    public GingerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
    }
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(gregfood.GingerRooted_dirt) || floor.isOf(Blocks.GRASS_BLOCK) || floor.isOf(Blocks.DIRT) || floor.isOf(Blocks.COARSE_DIRT) || floor.isOf(Blocks.PODZOL) || floor.isOf(Blocks.FARMLAND);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(gregfood.ginger);
    }
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(AGE) <=1) {
            return SMALL_SHAPE;
        } else {
            return state.get(AGE) <= 3 ? LARGE_SHAPE : super.getOutlineShape(state, world, pos, context);
        }
    }

    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) <= 3;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(!world.getBlockState(pos.down()).isOf(gregfood.GingerRooted_dirt)){
            world.setBlockState(pos.down(),gregfood.GingerRooted_dirt.getDefaultState());
        }
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(AGE);
        if (i < 3 && random.nextInt(10) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            grow(world,random,pos,state);
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(AGE);
        boolean bl = i == 3;
        if (!bl && player.getStackInHand(hand).getItem() == Items.BONE_MEAL) {
            return ActionResult.PASS;
        } else if (i <= 2) {
            if(player.getStackInHand(hand).getItem() == Items.BONE_MEAL){
                if(!player.getAbilities().creativeMode){
                    player.getStackInHand(hand).decrement(1);
                }
                grow((ServerWorld) world, world.random, pos,state);
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 3;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.get(AGE) + 1);
        world.setBlockState(pos, state.with(AGE, i));
        BlockState blockState = world.getBlockState(pos.down());
        if(blockState.isOf(gregfood.GingerRooted_dirt)){
            if(blockState.get(GingerRootedDirtBlock.AGE) < i){
                world.setBlockState(pos.down(), gregfood.GingerRooted_dirt.getDefaultState()
                        .with(GingerRootedDirtBlock.AGE,i));
            }
        }else {
            world.setBlockState(pos.down(),gregfood.GingerRooted_dirt.getDefaultState().with(AGE,1));
        }
    }

    static {
        AGE = Properties.AGE_3;
        SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
        LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    }
}
class TurnipCrop extends CropBlock {
    public TurnipCrop(Settings settings) {
        super(settings);
    }
    @Environment(EnvType.CLIENT)
    protected ItemConvertible getSeedsItem() {
        return gregfood.turnip;
    }
}
class PaddyBlock extends PlantBlock implements Fertilizable, Waterloggable {
    public static final IntProperty AGE;
    private static final VoxelShape SMALL_SHAPE;
    private static final VoxelShape LARGE_SHAPE;
    public static final BooleanProperty WATERLOGGED;

    public PaddyBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(WATERLOGGED,false));
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(gregfood.paddy);
    }
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(AGE) <=1) {
            return SMALL_SHAPE;
        } else {
            return state.get(AGE) <= 3 ? LARGE_SHAPE : super.getOutlineShape(state, world, pos, context);
        }
    }

    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) <= 3;
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(AGE);
        if (i < 3 && random.nextInt(10) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            world.setBlockState(pos, state.with(AGE, i + 1), 2);
        }
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down()),blockState1 = world.getBlockState(pos.up());
        return blockState.isOf(getBlockFromItem(Items.CLAY)) && !blockState1.isOf(Blocks.WATER.getDefaultState().getBlock());
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(AGE);
        boolean bl = i == 3;
        if (!bl && player.getStackInHand(hand).getItem() == Items.BONE_MEAL) {
            return ActionResult.PASS;
        } else if (i < 2) {
            if(player.getStackInHand(hand).getItem() == Items.BONE_MEAL){
                world.setBlockState(pos, state.with(AGE, i+1), 2);
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        } else if(i == 2) {
            if(player.getStackInHand(hand).getItem() == Items.BONE_MEAL){
                world.setBlockState(pos, state.with(AGE, i+1), 2);
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE,WATERLOGGED);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 3;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return !world.getBlockState(pos.up()).isOf(Blocks.WATER.getDefaultState().getBlock());
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.get(AGE) + 1);
        world.setBlockState(pos, state.with(AGE, i), 2);
    }
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            boolean bl = state.get(WATERLOGGED);
            if (bl) {
                if (!world.isClient()) {
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }

            world.setBlockState(pos, state.with(WATERLOGGED, true),3);
            world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return true;
        } else {
            return false;
        }
    }

    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return true;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
    static {
        AGE = Properties.AGE_3;
        WATERLOGGED = Properties.WATERLOGGED;
        SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
        LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    }
}
class GarlicBlock extends PlantBlock implements Fertilizable {
    public static final IntProperty AGE;
    private static final VoxelShape SMALL_SHAPE;
    private static final VoxelShape LARGE_SHAPE;

    public GarlicBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
    }
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(gregfood.GarlicRooted_dirt) || floor.isOf(Blocks.GRASS_BLOCK) || floor.isOf(Blocks.DIRT) || floor.isOf(Blocks.COARSE_DIRT) || floor.isOf(Blocks.PODZOL) || floor.isOf(Blocks.FARMLAND);
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        Random random = new Random();
        int age = state.get(AGE);
        if(age != 0)
          dropStack(world,pos,new ItemStack(gregfood.garlic_leaf,age+random.nextInt(age+1)));
    }

    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(gregfood.ginger);
    }
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(AGE) <=1) {
            return SMALL_SHAPE;
        } else {
            return state.get(AGE) <= 3 ? LARGE_SHAPE : super.getOutlineShape(state, world, pos, context);
        }
    }

    public boolean hasRandomTicks(BlockState state) {
        return state.get(AGE) <= 3;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(!world.getBlockState(pos.down()).isOf(gregfood.GarlicRooted_dirt)){
            world.setBlockState(pos.down(),gregfood.GarlicRooted_dirt.getDefaultState());
        }
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(AGE);
        if (i < 3 && random.nextInt(10) == 0 && world.getBaseLightLevel(pos.up(), 0) >= 9) {
            grow(world,random,pos,state);
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = state.get(AGE);
        boolean bl = i == 3;
        if (!bl && player.getStackInHand(hand).getItem() == Items.BONE_MEAL) {
            return ActionResult.PASS;
        } else if (i <= 2) {
            if(player.getStackInHand(hand).getItem() == Items.BONE_MEAL){
                if(!player.getAbilities().creativeMode){
                    player.getStackInHand(hand).decrement(1);
                }
                grow((ServerWorld) world, world.random, pos,state);
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        } else {
            return super.onUse(state, world, pos, player, hand, hit);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        return state.get(AGE) < 3;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.get(AGE) + 1);
        world.setBlockState(pos, state.with(AGE, i));
        BlockState blockState = world.getBlockState(pos.down());
        if(blockState.isOf(gregfood.GarlicRooted_dirt)){
            if(blockState.get(GingerRootedDirtBlock.AGE) < i){
                world.setBlockState(pos.down(), gregfood.GarlicRooted_dirt.getDefaultState()
                        .with(GingerRootedDirtBlock.AGE,i));
            }
        }else {
            world.setBlockState(pos.down(),gregfood.GarlicRooted_dirt.getDefaultState().with(AGE,1));
        }
    }

    static {
        AGE = Properties.AGE_3;
        SMALL_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
        LARGE_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    }
}