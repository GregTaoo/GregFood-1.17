package com.github.gregtaoo;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
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
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

class BiscuitBlock extends FallingBlock {
    public BiscuitBlock(Settings settings) {
        super(settings);
    }
}
class SaltBlock extends FallingBlock {
    public SaltBlock(Settings settings) {
        super(settings);
    }
}
class TeaRootedDirtBlock extends Block{

    public TeaRootedDirtBlock(Settings settings) {
        super(settings);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            if(itemStack.getItem() == gregfood.WEEDING_SHOVEL){
                Random random = new Random();
                dropStack(world,pos.up(),new ItemStack(gregfood.Tea_Root,random.nextInt(5)+1));
                world.setBlockState(pos,Blocks.DIRT.getDefaultState());
                world.playSound(null,pos,SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,SoundCategory.BLOCKS,1f,1f);
                return ActionResult.success(world.isClient);
            }
        }
        return ActionResult.PASS;
    }

}
class GingerRootedDirtBlock extends Block{

    public static final IntProperty AGE;

    public GingerRootedDirtBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE,0));
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        Random random = new Random();
        int age = state.get(AGE);
        dropStack(world,pos.up(),new ItemStack(gregfood.ginger,random.nextInt(age*3+1)+age+1));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            Random random = new Random();
            int age = state.get(AGE);
            if(itemStack.getItem() == gregfood.WEEDING_SHOVEL){
                dropStack(world,pos.up(),new ItemStack(gregfood.ginger,random.nextInt(age*3+1)+age+1));
                world.setBlockState(pos,Blocks.DIRT.getDefaultState());
                world.playSound(null,pos,SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,SoundCategory.BLOCKS,1f,1f);
                if(world.getBlockState(pos.up()).isOf(gregfood.ginger)){
                    world.breakBlock(pos.up(),true);
                }
                return ActionResult.success(world.isClient);
            }else if(itemStack.getItem() == Items.BONE_MEAL && world.getBlockState(pos.up()).isAir()){
                if(!player.abilities.creativeMode){
                    itemStack.decrement(1);
                }
                if(random.nextInt(10-age*3) == 0)
                  world.setBlockState(pos.up(),gregfood.ginger.getDefaultState());
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    static {
        AGE=Properties.AGE_3;
    }

}
class BrinerBlock extends Block {
    public static final IntProperty LEVEL;
    public static final BooleanProperty SALT;
    public static final BooleanProperty BRINED;
    private static final VoxelShape RAY_TRACE_SHAPE;
    public static final VoxelShape[] OUTLINE_SHAPE;

    public BrinerBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL,0).with(BRINED,false).with(SALT,false));
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        if(state.get(SALT)){
            if(state.get(BRINED)){
                dropStack(world,pos,new ItemStack(gregfood.Brined_brining_Ingredient,1));
            }else{
                dropStack(world,pos,new ItemStack(gregfood.Brining_Ingredient,1));
            }
        }
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(LEVEL);
        boolean hasSalt = state.get(SALT);
        if(i>0&&random.nextInt(50) == 0&&hasSalt&&!world.getBlockState(pos.up()).isAir()){
            world.setBlockState(pos,state.with(LEVEL,i).with(BRINED,true).with(SALT, true));
        }
    }

    public boolean hasRandomTicks(BlockState state) {
        return !state.get(BRINED);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE[0];
    }

    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAY_TRACE_SHAPE;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        ItemStack waterBottle = PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER);
        int i = state.get(LEVEL);
        boolean isOK = state.get(BRINED);
        boolean hasSalt = state.get(SALT);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        }else if(itemStack.getItem() == Items.WATER_BUCKET && i<8){
            setLevel(world,pos,state,i+4,false,1);
            if(!player.abilities.creativeMode){
                player.setStackInHand(hand,new ItemStack(Items.BUCKET));
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == Items.BUCKET && i>=4){
            setLevel(world,pos,state,i-4,true,0);
            itemStack.decrement(1);
            if (itemStack.isEmpty()) {
                player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
            } else if (!player.inventory.insertStack(new ItemStack(Items.WATER_BUCKET))) {
                player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == Items.GLASS_BOTTLE && i>0){
            setLevel(world,pos,state,i-1,true,2);
            itemStack.decrement(1);
            if (itemStack.isEmpty()) {
                player.setStackInHand(hand, waterBottle);
            } else if (!player.inventory.insertStack(waterBottle)) {
                player.dropItem(waterBottle, false);
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == Items.POTION && i<8){
            setLevel(world,pos,state,i+1,false,3);
            if(!player.abilities.creativeMode){
                itemStack.decrement(1);
                if (itemStack.isEmpty()) {
                    player.setStackInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                } else if (!player.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE))) {
                    player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
                }
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == gregfood.Brining_Ingredient && !hasSalt){
            world.setBlockState(pos,state.with(LEVEL,i).with(BRINED,isOK).with(SALT,true));
            if(!player.abilities.creativeMode){
                itemStack.decrement(1);
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == gregfood.WEEDING_SHOVEL && hasSalt){
            if(isOK){
                world.setBlockState(pos,state.with(LEVEL,Math.max(0,i-1)).with(BRINED, false).with(SALT,false));
                dropStack(world,pos,new ItemStack(gregfood.Brined_brining_Ingredient));
            }else{
                world.setBlockState(pos,state.with(LEVEL,i).with(BRINED, false).with(SALT,false));
                dropStack(world,pos,new ItemStack(gregfood.Brining_Ingredient));
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == gregfood.Tea_Root && hasSalt && i>0){
            Random random = new Random();
            if(!player.abilities.creativeMode){
                itemStack.decrement(1);
            }
            if(random.nextInt(10) == 0){
                world.setBlockState(pos,state.with(LEVEL,i).with(BRINED,true).with(SALT, true));
            }
            return ActionResult.success(world.isClient);
        }else if(isOK&&hasSalt){
            Inventory inventory = new SimpleInventory(itemStack);
            Optional<BriningRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Brining_Recipe_Type,inventory,world);
            ItemStack itemStack1 = itemRecipe.map((BriningRecipe) -> BriningRecipe.craft(inventory)).orElse(itemStack);
            if(itemStack.getItem() == itemStack1.getItem()){
                return ActionResult.PASS;
            }
            final float[] counts = {1};
            itemRecipe.ifPresent((recipe -> counts[0] = recipe.getCounts()));
            ItemStack itemStack2 = new ItemStack(itemStack1.getItem(), (int) counts[0]);
            dropStack(world,pos,itemStack2);
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world,pos,state,i-1,true,3);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public void setLevel(World world, BlockPos pos, BlockState state, int level, boolean enable, int soundType) {
        if(level>8){
            level=8;
        }else if(level<0){
            level=0;
        }
        boolean hasBrined = state.get(BRINED);
        boolean hasSalt = state.get(SALT);
        if(hasBrined){
            world.setBlockState(pos,state.with(LEVEL,level).with(BRINED,enable).with(SALT,hasSalt));
        }else{
            world.setBlockState(pos,state.with(LEVEL,level).with(BRINED, false).with(SALT,hasSalt));
        }
        world.updateComparators(pos, this);
        switch (soundType){
            case 0:{
                world.playSound(null,pos,SoundEvents.ITEM_BUCKET_FILL,SoundCategory.BLOCKS,1f,1f);
                break;
            }
            case 1:{
                world.playSound(null,pos,SoundEvents.ITEM_BUCKET_EMPTY,SoundCategory.BLOCKS,1f,1f);
                break;
            }
            case 2:{
                world.playSound(null,pos,SoundEvents.ITEM_BOTTLE_FILL,SoundCategory.BLOCKS,1f,1f);
                break;
            }
            case 3:{
                world.playSound(null,pos,SoundEvents.ITEM_BOTTLE_EMPTY,SoundCategory.BLOCKS,1f,1f);
                break;
            }
        }
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        int i = state.get(LEVEL);
        boolean hasBrined = state.get(BRINED);
        boolean hasSalt = state.get(SALT);
        if(hasSalt&&hasBrined){
            return i;
        }else{
            return 0;
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL,BRINED,SALT);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    static {
        LEVEL = Properties.LEVEL_8;
        BRINED = Properties.ENABLED;
        SALT = Properties.OCCUPIED;
        RAY_TRACE_SHAPE = VoxelShapes.fullCube();
        OUTLINE_SHAPE = Util.make(new VoxelShape[9], (voxelShapes) -> {
            for(int i = 0; i < 8; ++i) {
                voxelShapes[i] = VoxelShapes.combineAndSimplify(RAY_TRACE_SHAPE, Block.createCuboidShape(2.0D, Math.max(2, 1 + i * 2), 2.0D, 14.0D, 16.0D, 14.0D), BooleanBiFunction.ONLY_FIRST);
            }
            voxelShapes[8] = voxelShapes[7];
        });}
}
class KnifeTableBlock extends Block {
    private static final IntProperty KNIVES;
    protected static final VoxelShape SHAPE;

    public KnifeTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(KNIVES, 0));
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        if(state.get(KNIVES) == 15){
            dropStack(world,pos,new ItemStack(gregfood.Kitchen_Knife,1));
        }
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        int i = state.get(KNIVES);
        if (itemStack.isEmpty()) {
            if (i == 15) {
                setLevel(world, pos, state, 0, 0);
                if (!player.abilities.creativeMode) {
                    dropStack(world, pos, new ItemStack(gregfood.Kitchen_Knife));
                }
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        }
        if (itemStack.getItem() == gregfood.Kitchen_Knife && i == 0) {
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world, pos, state, 15, 2);
            return ActionResult.success(world.isClient);
        } else if (itemStack.getItem() == Items.IRON_INGOT && i != 0 && i < 15) {
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world, pos, state, i + 5, 1);
            return ActionResult.success(world.isClient);
        } else if ((itemStack.getItem() == Blocks.GRASS.asItem() || itemStack.getItem() == Blocks.FERN.asItem())&&i>0) {
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world, pos, state, i - 1, 0);
            Random random = new Random();
            switch (random.nextInt(10)) {
                case 0:
                case 4: {
                    dropStack(world, pos, new ItemStack(gregfood.Cumin, random.nextInt(5) + 1));
                    break;
                }
                case 1: {
                    dropStack(world, pos, new ItemStack(gregfood.soybean, random.nextInt(2) + 1));
                    break;
                }
                case 2: {
                    dropStack(world, pos, new ItemStack(gregfood.turnip, random.nextInt(2) + 1));
                    break;
                }
                case 3: {
                    dropStack(world, pos, new ItemStack(gregfood.tea, random.nextInt(2) + 1));
                    break;
                }
                case 5: {
                    dropStack(world, pos, new ItemStack(gregfood.paddy, random.nextInt(2) + 1));
                    break;
                }
                case 6: {
                    dropStack(world, pos, new ItemStack(gregfood.Orange_tree_sapling, 1));
                    break;
                }
                case 7: {
                    dropStack(world, pos, new ItemStack(gregfood.green_onion, random.nextInt(2)+1));
                    break;
                }
                case 8: {
                    dropStack(world, pos, new ItemStack(gregfood.garlic, random.nextInt(2)+1));
                    break;
                }
                case 9: {
                    dropStack(world, pos, new ItemStack(gregfood.ginger, random.nextInt(2)+1));
                    break;
                }
            }
            return ActionResult.success(world.isClient);
        }else if (i > 0) {
            Inventory inventory = new SimpleInventory(itemStack);
            Optional<CuttingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Cutting_Recipe_Type,inventory,world);
            ItemStack itemStack1 = itemRecipe.map((CuttingRecipe) -> CuttingRecipe.craft(inventory)).orElse(itemStack);
            if(itemStack.getItem() == itemStack1.getItem()){
                return ActionResult.PASS;
            }
            final float[] counts = {1};
            itemRecipe.ifPresent((recipe -> counts[0] = recipe.getCounts()));
            ItemStack itemStack2 = new ItemStack(itemStack1.getItem(), (int) counts[0]);
            dropStack(world,pos,itemStack2);
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world, pos, state, i - 1, 0);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && entity.getType() != EntityType.ITEM) {
            int i = state.get(KNIVES);
            entity.damage(DamageSource.CACTUS, i * 0.2F);
        }
    }

    public void setLevel(World world, BlockPos pos, BlockState state, int level, int soundType) {
        world.setBlockState(pos, state.with(KNIVES, MathHelper.clamp(level, 0, 15)));
        switch (soundType) {
            case 0: {
                world.playSound(null, pos, gregfood.Knife_Table_Cut_Event, SoundCategory.BLOCKS, 1f, 1f);
                break;
            }
            case 1: {
                world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1f);
                break;
            }
            case 2: {
                world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 1f);
                break;
            }
        }

    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(KNIVES);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(KNIVES);
    }

    static {
        KNIVES = Properties.LEVEL_15;
        SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    }
}
class GrinderBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty LIT;

    public GrinderBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(entity instanceof ItemEntity){
            ItemEntity itemEntity = (ItemEntity)entity;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof GrinderBlockEntity){
                GrinderBlockEntity grinderBlockEntity = (GrinderBlockEntity)blockEntity;
                ItemStack itemStack = itemEntity.getStack();
                boolean isSuccess = grinderBlockEntity.addItem(new ItemStack(itemStack.getItem(),1));
                if(isSuccess){
                    itemEntity.setStack(new ItemStack(itemStack.getItem(),itemStack.getCount()-1));
                }
            }
        }
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        if(blockEntity instanceof GrinderBlockEntity){
            GrinderBlockEntity grinderBlockEntity = (GrinderBlockEntity) blockEntity;
            ItemStack itemStack1 = grinderBlockEntity.pickItem(true);
            if(itemStack1!=ItemStack.EMPTY){
                dropStack(world,pos,itemStack1);
            }
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            ItemStack itemStack = player.getStackInHand(hand);
            if(blockEntity instanceof GrinderBlockEntity){
                GrinderBlockEntity grinderBlockEntity = (GrinderBlockEntity) blockEntity;
                if(!itemStack.isEmpty() && !state.get(LIT)){
                    if(itemStack.getItem() == gregfood.WEEDING_SHOVEL){
                        ItemStack itemStack1 = grinderBlockEntity.pickItem(false);
                        if(itemStack1!=ItemStack.EMPTY){
                            dropStack(world,pos.up(),itemStack1);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new GrinderBlockEntity();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(state.get(LIT)){
            return 1;
        }
        return 0;
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BrinerBlock.OUTLINE_SHAPE[7];
    }

    static {
        LIT=Properties.LIT;
    }

}
class SteamerBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty LIT;

    public SteamerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(entity instanceof ItemEntity){
            ItemEntity itemEntity = (ItemEntity)entity;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof SteamerBlockEntity){
                SteamerBlockEntity steamerBlockEntity = (SteamerBlockEntity)blockEntity;
                ItemStack itemStack = itemEntity.getStack();
                boolean isSuccess = steamerBlockEntity.addItem(new ItemStack(itemStack.getItem(),1));
                if(isSuccess){
                    itemEntity.setStack(new ItemStack(itemStack.getItem(),itemStack.getCount()-1));
                }
            }
        }
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        if(blockEntity instanceof SteamerBlockEntity){
            SteamerBlockEntity steamerBlockEntity = (SteamerBlockEntity) blockEntity;
            ItemStack itemStack1 = steamerBlockEntity.pickItem(true);
            if(itemStack1!=ItemStack.EMPTY){
                dropStack(world,pos,itemStack1);
            }
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            ItemStack itemStack = player.getStackInHand(hand);
            if(blockEntity instanceof SteamerBlockEntity){
                SteamerBlockEntity steamerBlockEntity = (SteamerBlockEntity) blockEntity;
                if(!itemStack.isEmpty() && !state.get(LIT)){
                    if(itemStack.getItem() == gregfood.WEEDING_SHOVEL){
                        ItemStack itemStack1 = steamerBlockEntity.pickItem(false);
                        if(itemStack1!=ItemStack.EMPTY){
                            dropStack(world,pos.up(),itemStack1);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new SteamerBlockEntity();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(state.get(LIT)){
            return 1;
        }
        return 0;
    }


    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BrinerBlock.OUTLINE_SHAPE[7];
    }

    static {
        LIT=Properties.LIT;
    }

}
class GarlicRootedDirtBlock extends Block{

    public static final IntProperty AGE;

    public GarlicRootedDirtBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE,0));
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        Random random = new Random();
        int age = state.get(AGE);
        dropStack(world,pos.up(),new ItemStack(gregfood.garlic,random.nextInt(age*3+1)+age+1));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            Random random = new Random();
            int age = state.get(AGE);
            if(itemStack.getItem() == gregfood.WEEDING_SHOVEL){
                dropStack(world,pos.up(),new ItemStack(gregfood.garlic,random.nextInt(age*3+1)+age+1));
                world.setBlockState(pos,Blocks.DIRT.getDefaultState());
                world.playSound(null,pos,SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,SoundCategory.BLOCKS,1f,1f);
                if(world.getBlockState(pos.up()).isOf(gregfood.garlic)){
                    world.breakBlock(pos.up(),true);
                }
                return ActionResult.success(world.isClient);
            }else if(itemStack.getItem() == Items.BONE_MEAL && world.getBlockState(pos.up()).isAir()){
                if(!player.abilities.creativeMode){
                    itemStack.decrement(1);
                }
                if(random.nextInt(10-age*3) == 0)
                    world.setBlockState(pos.up(),gregfood.garlic.getDefaultState());
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    static {
        AGE=Properties.AGE_3;
    }

}