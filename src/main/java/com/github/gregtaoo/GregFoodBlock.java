package com.github.gregtaoo;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

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
                dropStack(world,pos.up(),new ItemStack(gregfood.Tea_Root,random.nextInt(5) + 1));
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
        dropStack(world,pos.up(),new ItemStack(gregfood.ginger,random.nextInt(age + 1) + age + 1));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            Random random = new Random();
            int age = state.get(AGE);
            if(itemStack.getItem() == gregfood.WEEDING_SHOVEL){
                dropStack(world,pos.up(),new ItemStack(gregfood.ginger,random.nextInt(age + 1) + age + 1));
                world.setBlockState(pos,Blocks.DIRT.getDefaultState());
                world.playSound(null,pos,SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,SoundCategory.BLOCKS,1f,1f);
                if(world.getBlockState(pos.up()).isOf(gregfood.ginger)){
                    world.breakBlock(pos.up(),true);
                }
                return ActionResult.success(world.isClient);
            }else if(itemStack.getItem() == Items.BONE_MEAL && world.getBlockState(pos.up()).isAir()){
                if(!player.getAbilities().creativeMode){
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
        if(i > 0 && random.nextInt(50) == 0 && hasSalt && !world.getBlockState(pos.up()).isAir()){
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
            if(!player.getAbilities().creativeMode){
                player.setStackInHand(hand,new ItemStack(Items.BUCKET));
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == Items.BUCKET && i>=4){
            setLevel(world,pos,state,i-4,true,0);
            itemStack.decrement(1);
            if (itemStack.isEmpty()) {
                player.setStackInHand(hand, new ItemStack(Items.WATER_BUCKET));
            } else if (!player.getInventory().insertStack(new ItemStack(Items.WATER_BUCKET))) {
                player.dropItem(new ItemStack(Items.WATER_BUCKET), false);
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == Items.GLASS_BOTTLE && i>0){
            setLevel(world,pos,state,i-1,true,2);
            itemStack.decrement(1);
            if (itemStack.isEmpty()) {
                player.setStackInHand(hand, waterBottle);
            } else if (!player.getInventory().insertStack(waterBottle)) {
                player.dropItem(waterBottle, false);
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == Items.POTION && i<8){
            setLevel(world,pos,state,i+1,false,3);
            if(!player.getAbilities().creativeMode){
                itemStack.decrement(1);
                if (itemStack.isEmpty()) {
                    player.setStackInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
                } else if (!player.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE))) {
                    player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
                }
            }
            return ActionResult.success(world.isClient);
        }else if(itemStack.getItem() == gregfood.Brining_Ingredient && !hasSalt){
            world.setBlockState(pos,state.with(LEVEL,i).with(BRINED,isOK).with(SALT,true));
            if(!player.getAbilities().creativeMode){
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
            if(!player.getAbilities().creativeMode){
                itemStack.decrement(1);
            }
            if(random.nextInt(10) == 0){
                world.setBlockState(pos,state.with(LEVEL,i).with(BRINED,true).with(SALT, true));
            }
            return ActionResult.success(world.isClient);
        }else if(isOK && hasSalt && !world.isClient()){
            Inventory inventory = new SimpleInventory(itemStack);
            Optional<BriningRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Brining_Recipe_Type,inventory,world);
            ItemStack itemStack1 = itemRecipe.map((BriningRecipe) -> BriningRecipe.craft(inventory)).orElse(itemStack);
            if(itemStack.getItem() == itemStack1.getItem()){
                return ActionResult.PASS;
            }
            dropStack(world,pos,itemStack1);
            if (!player.getAbilities().creativeMode) {
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
        switch (soundType) {
            case 0 -> world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1f, 1f);
            case 1 -> world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
            case 2 -> world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1f, 1f);
            case 3 -> world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
        }
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        int i = state.get(LEVEL);
        boolean hasBrined = state.get(BRINED);
        boolean hasSalt = state.get(SALT);
        if(hasSalt && hasBrined){
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
                if (!player.getAbilities().creativeMode) {
                    dropStack(world, pos, new ItemStack(gregfood.Kitchen_Knife));
                }
                return ActionResult.success(world.isClient);
            }
            return ActionResult.PASS;
        }
        if (itemStack.getItem() == gregfood.Kitchen_Knife && i == 0) {
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world, pos, state, 15, 2);
            return ActionResult.success(world.isClient);
        } else if (itemStack.getItem() == Items.IRON_INGOT && i != 0 && i < 15) {
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world, pos, state, i + 5, 1);
            return ActionResult.success(world.isClient);
        } else if ((itemStack.getItem() == Blocks.GRASS.asItem() || itemStack.getItem() == Blocks.FERN.asItem())&&i>0) {
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            setLevel(world, pos, state, i - 1, 0);
            Random random = new Random();
            switch (random.nextInt(10)) {
                case 0, 1, 6 -> dropStack(world, pos, new ItemStack(gregfood.Cumin, random.nextInt(5) + 1));
                case 2 -> dropStack(world, pos, new ItemStack(gregfood.soybean, random.nextInt(2) + 1));
                case 3 -> dropStack(world, pos, new ItemStack(gregfood.turnip, random.nextInt(2) + 1));
                case 4 -> dropStack(world, pos, new ItemStack(gregfood.tea, random.nextInt(2) + 1));
                case 5 -> dropStack(world, pos, new ItemStack(gregfood.paddy, random.nextInt(2) + 1));
                case 7 -> dropStack(world, pos, new ItemStack(gregfood.green_onion, random.nextInt(2) + 1));
                case 8 -> dropStack(world, pos, new ItemStack(gregfood.garlic, random.nextInt(2) + 1));
                case 9 -> dropStack(world, pos, new ItemStack(gregfood.ginger, random.nextInt(2) + 1));
            }
            return ActionResult.success(world.isClient);
        }else if (i > 0 && !world.isClient()) {
            Inventory inventory = new SimpleInventory(itemStack);
            Optional<KnifeCuttingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Cutting_Recipe_Type,inventory,world);
            ItemStack itemStack1 = itemRecipe.map((CuttingRecipe) -> CuttingRecipe.craft(inventory)).orElse(itemStack);
            if(itemStack.getItem() == itemStack1.getItem()){
                return ActionResult.PASS;
            }
            dropStack(world,pos,itemStack1);
            if (!player.getAbilities().creativeMode) {
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
            case 0 -> world.playSound(null, pos, gregfood.Knife_Table_Cut_Event, SoundCategory.BLOCKS, 1f, 1f);
            case 1 -> world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1f, 1f);
            case 2 -> world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 1f);
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

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrinderBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return GrinderBlockEntity::tick;
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
            ItemStack itemStack1 = grinderBlockEntity.pickItem(pos,true);
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
                        ItemStack itemStack1 = grinderBlockEntity.pickItem(pos,false);
                        if(itemStack1 != ItemStack.EMPTY){
                            dropStack(world,pos.up(),itemStack1);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
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
        LIT = Properties.LIT;
    }

}
class SteamerBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty LIT;

    public SteamerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return SteamerBlockEntity::tick;
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
            ItemStack itemStack1 = steamerBlockEntity.pickItem(pos,true);
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
                        ItemStack itemStack1 = steamerBlockEntity.pickItem(pos,false);
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
        LIT = Properties.LIT;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SteamerBlockEntity(pos,state);
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
        dropStack(world,pos.up(),new ItemStack(gregfood.garlic,random.nextInt(age + 1) + age + 1));
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return ActionResult.PASS;
        } else {
            Random random = new Random();
            int age = state.get(AGE);
            if(itemStack.getItem() == gregfood.WEEDING_SHOVEL){
                dropStack(world,pos.up(),new ItemStack(gregfood.garlic,random.nextInt(age + 1) + age + 1));
                world.setBlockState(pos,Blocks.DIRT.getDefaultState());
                world.playSound(null,pos,SoundEvents.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES,SoundCategory.BLOCKS,1f,1f);
                if(world.getBlockState(pos.up()).isOf(gregfood.garlic)){
                    world.breakBlock(pos.up(),true);
                }
                return ActionResult.success(world.isClient);
            }else if(itemStack.getItem() == Items.BONE_MEAL && world.getBlockState(pos.up()).isAir()){
                if(!player.getAbilities().creativeMode){
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
        AGE = Properties.AGE_3;
    }

}
class DoughMakingTableBlock extends Block implements BlockEntityProvider {
    protected static final VoxelShape SHAPE;

    public DoughMakingTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DoughMakingTableEntity(pos,state);
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        if(blockEntity instanceof DoughMakingTableEntity){
            DoughMakingTableEntity doughMakingTableEntity = (DoughMakingTableEntity) blockEntity;
            ItemStack itemStack = doughMakingTableEntity.getItemStack(false);
            if(!itemStack.isEmpty()){
                dropStack(world, pos, itemStack);
            }
        }
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof DoughMakingTableEntity && hand == Hand.MAIN_HAND){
            DoughMakingTableEntity doughMakingTableEntity = (DoughMakingTableEntity) blockEntity;
            if(itemStack.isEmpty()){
                ItemStack itemStack1 = doughMakingTableEntity.getItemStack(true);
                if(!itemStack1.isEmpty()){
                    dropStack(world, pos, itemStack1);
                    return ActionResult.SUCCESS;
                }
            }else {
                if(doughMakingTableEntity.putItemStack(itemStack) && !world.isClient()){
                    if(!player.getAbilities().creativeMode){
                        itemStack.decrement(1);
                    }
                    return ActionResult.SUCCESS;
                }
                Inventory inventory = new DoubleInventory(new SimpleInventory(itemStack), new SimpleInventory(doughMakingTableEntity.getItemStack(false)));
                Optional<DoughMakingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Dough_Making_Recipe_Type, inventory, world);
                if(itemRecipe.isEmpty()){
                    return ActionResult.PASS;
                }
                ItemStack itemStack1 = itemRecipe.map((DoughMakingRecipe) -> DoughMakingRecipe.craft(inventory)).orElse(itemStack);
                boolean amount = itemRecipe.map((DoughMakingRecipe) -> DoughMakingRecipe.amount).orElse(false);
                int steps = itemRecipe.map((DoughMakingRecipe) -> DoughMakingRecipe.actionNum).orElse(1);
                if(doughMakingTableEntity.actions(itemStack1, steps, itemStack.getItem()) && !world.isClient()){
                    if(amount && !player.getAbilities().creativeMode){
                        Item returnItem = itemStack.getItem().getRecipeRemainder();
                        if(itemStack.isOf(Items.POTION)){
                            returnItem = Items.GLASS_BOTTLE;
                        }
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            player.setStackInHand(hand, new ItemStack(returnItem));
                        } else {
                            ItemStack itemStack2 = new ItemStack(returnItem);
                            if (!player.getInventory().insertStack(itemStack2)) {
                                player.dropItem(itemStack2, false);
                            }
                        }
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    static {
        SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    }
}
class EvaporatingPanBlock extends Block {
    public static final IntProperty LEVEL;
    protected static final VoxelShape SHAPE;

    public EvaporatingPanBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LEVEL, 0));
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        int level = state.get(LEVEL);
        if (!world.isClient && entity.isOnFire() && level > 0) {
            entity.extinguish();
            entity.setOnFire(false);
            world.setBlockState(pos,state.with(LEVEL,level - 1));
        }
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState blockState = world.getBlockState(pos.down());
        boolean isCooking = blockState.isOf(Blocks.FIRE) || blockState.isOf(Blocks.SOUL_FIRE) || blockState.isOf(Blocks.CAMPFIRE) || blockState.isOf(Blocks.SOUL_CAMPFIRE);
        boolean isUnderSky = world.isSkyVisible(pos) && world.isDay();
        int level = state.get(LEVEL);
        if (level > 0 && level < 3 && random.nextInt(60 - (isCooking ? 20 : 0) - (isUnderSky ? 20 : 0)) == 0) {
            world.setBlockState(pos, state.with(LEVEL, level + 1));
        }
    }

    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        int level = state.get(LEVEL);
        if (level > 0 && world.random.nextInt(5) == 1) {
            if (precipitation == Biome.Precipitation.RAIN) {
                world.setBlockState(pos, state.with(LEVEL, Math.max(level - 1, 0)));
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
        }
    }

    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        int level = state.get(LEVEL);
        if(hand == Hand.MAIN_HAND){
            if(itemStack.isOf(Items.WATER_BUCKET) && level == 0){
                if(!player.getAbilities().creativeMode){
                    player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                }
                world.setBlockState(pos, state.with(LEVEL, 1));
                return ActionResult.SUCCESS;
            }else if(itemStack.isOf(Items.BUCKET) && level > 0){
                ItemStack returnItem = ItemStack.EMPTY;
                if(level < 3 && !player.getAbilities().creativeMode){
                    returnItem = new ItemStack(Items.WATER_BUCKET);
                    itemStack.decrement(1);
                }else if(level == 3 && !player.getAbilities().creativeMode){
                    returnItem = new ItemStack(gregfood.Salt_bucket);
                    itemStack.decrement(1);
                }
                if (itemStack.isEmpty()) {
                    player.setStackInHand(hand, returnItem);
                } else {
                    if (!player.getInventory().insertStack(returnItem)) {
                        player.dropItem(returnItem, false);
                    }
                }
                world.setBlockState(pos, state.with(LEVEL, 0));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    static {
        LEVEL = Properties.AGE_3;
        SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0.0D,0.0D,0.0D,16.0D,8.0D,16.0D), Block.createCuboidShape(1.0D,1.0D,1.0D,15.0D,8.0D,15.0D),BooleanBiFunction.ONLY_FIRST);
    }
}
class CuttingBoardBlock extends Block implements BlockEntityProvider {
    protected static final VoxelShape[] SHAPE_NS;
    protected static final VoxelShape[] SHAPE_WE;
    private static final DirectionProperty FACING;

    public CuttingBoardBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING,Direction.NORTH));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardEntity(pos,state);
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        if(blockEntity instanceof CuttingBoardEntity){
            CuttingBoardEntity cuttingBoardEntity = (CuttingBoardEntity) blockEntity;
            ItemStack itemStack = cuttingBoardEntity.getItemStack(false);
            if(!itemStack.isEmpty()){
                dropStack(world, pos, itemStack);
            }
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        if(direction == Direction.NORTH || direction == Direction.SOUTH){
            return VoxelShapes.union(SHAPE_NS[0], SHAPE_NS[1], SHAPE_NS[2], SHAPE_NS[3]);
        }
        return VoxelShapes.union(SHAPE_WE[0], SHAPE_WE[1], SHAPE_WE[2], SHAPE_WE[3]);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof CuttingBoardEntity && hand == Hand.MAIN_HAND && hit.getSide() == Direction.UP){
            CuttingBoardEntity cuttingBoardEntity = (CuttingBoardEntity) blockEntity;
            if(itemStack.isEmpty()){
                ItemStack itemStack1 = cuttingBoardEntity.getItemStack(true);
                if(!itemStack1.isEmpty()){
                    dropStack(world, pos, itemStack1);
                    return ActionResult.SUCCESS;
                }
            }else if(itemStack.getItem() == gregfood.Kitchen_Knife){
                if(cuttingBoardEntity.cutAction()){
                    return ActionResult.SUCCESS;
                }
                return ActionResult.PASS;
            }else{
                ItemStack itemStack1;
                Inventory inventory = new SimpleInventory(itemStack);
                Optional<BoardCuttingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Board_Cutting_Recipe_Type,inventory,world);
                if(itemRecipe.isPresent()){
                    itemStack1 = itemRecipe.map((BoardCuttingRecipe) -> BoardCuttingRecipe.craft(inventory)).orElse(itemStack);
                    int steps = itemRecipe.get().cookTime;
                    if(cuttingBoardEntity.putItemStack(itemStack, steps, itemStack1) && !world.isClient()){
                        if(!player.getAbilities().creativeMode){
                            itemStack.decrement(1);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    static {
        SHAPE_NS = new VoxelShape[]{
                Block.createCuboidShape(1.0D,0.0D,3.0D,13.5D,1.5D,13.0D),
                Block.createCuboidShape(14.0D,0.0D,3.0D,15.0D,1.5D,13.0D),
                Block.createCuboidShape(13.5D,0.0D,3.0D,14.0D,1.5D,5.0D),
                Block.createCuboidShape(13.5D,0.0D,11.0D,14.0D,1.5D,13.0D)
        };
        SHAPE_WE = new VoxelShape[]{
                Block.createCuboidShape(3.0D,0.0D,1.0D,13.0D,1.5D,13.5D),
                Block.createCuboidShape(3.0D,0.0D,14.0D,13.0D,1.5D,15.0D),
                Block.createCuboidShape(3.0D,0.0D,13.5D,5.0D,1.5D,14.0D),
                Block.createCuboidShape(11.0D,0.0D,13.5D,13.0D,1.5D,14.0D)
        };
        FACING = HorizontalFacingBlock.FACING;
    }
}
class WoodenSteamerBlock extends Block implements BlockEntityProvider {
    public static final EnumProperty<SlabType> TYPE;
    public static BooleanProperty LIT;
    protected static final VoxelShape BOTTOM_SHAPE;
    protected static final VoxelShape TOP_SHAPE;

    public WoodenSteamerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TYPE, SlabType.BOTTOM).with(LIT, false));
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
        dropStacks(state, world, pos, blockEntity, player, stack);
        if(blockEntity instanceof WoodenSteamerEntity entity){
            for(int i = 0; i < 4; i++){
                if(!entity.downItems.get(i).isEmpty()){
                    dropStack(world, pos, entity.downItems.get(i));
                }
                if(!entity.upItems.get(i).isEmpty()){
                    dropStack(world, pos, entity.upItems.get(i));
                }
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WoodenSteamerEntity(pos,state);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(world != null){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof WoodenSteamerEntity entity){
                if(!itemStack.isEmpty() && hand == Hand.MAIN_HAND){
                    Inventory inventory = new SimpleInventory(itemStack);
                    Optional<SteamingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Steaming_Recipe_Type,inventory,world);
                    if(itemRecipe.isPresent()){
                        ItemStack itemStack1 = itemRecipe.map((BoardCuttingRecipe) -> BoardCuttingRecipe.craft(inventory)).orElse(itemStack);
                        int cookTime = itemRecipe.get().cookTime;
                        if(entity.insertItem(pos, new ItemStack(itemStack.getItem()), itemStack1, cookTime) && !world.isClient()){
                            if(!player.getAbilities().creativeMode){
                                itemStack.decrement(1);
                            }
                            return ActionResult.SUCCESS;
                        }
                    }
                }else if(hand == Hand.MAIN_HAND){
                    ItemStack pickStack = entity.pickItem(pos, true);
                    if(!pickStack.isEmpty()){
                        dropStack(world, pos.up(), pickStack);
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
    public boolean hasSidedTransparency(BlockState state) {
        return state.get(TYPE) != SlabType.DOUBLE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TYPE, LIT);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        SlabType slabType = state.get(TYPE);
        return switch (slabType) {
            case DOUBLE -> VoxelShapes.union(TOP_SHAPE, BOTTOM_SHAPE);
            case TOP -> TOP_SHAPE;
            default -> BOTTOM_SHAPE;
        };
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf(this)) {
            return blockState.with(TYPE, SlabType.DOUBLE);
        } else {
            return this.getDefaultState().with(TYPE, SlabType.BOTTOM);
        }
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabType slabType = state.get(TYPE);
        if (slabType != SlabType.DOUBLE && itemStack.isOf(this.asItem())) {
            if (context.canReplaceExisting()) {
                boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5D;
                Direction direction = context.getSide();
                if (slabType == SlabType.BOTTOM) {
                    return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
                } else {
                    return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return WoodenSteamerEntity::tick;
    }

    static {
        TYPE = Properties.SLAB_TYPE;
        LIT = Properties.LIT;
        BOTTOM_SHAPE = Block.createCuboidShape(0.0D, 0.01D, 0.0D, 16.0D, 8.0D, 16.0D);
        TOP_SHAPE = Block.createCuboidShape(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }
}
class StoveBlock extends Block implements BlockEntityProvider{
    public static BooleanProperty LIT;
    protected static VoxelShape SHAPE;

    public StoveBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StoveBlockEntity(pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(world != null){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof StoveBlockEntity entity){
                if(hand == Hand.MAIN_HAND && !itemStack.isEmpty()){
                    if(itemStack.isOf(Items.FLINT_AND_STEEL) && !entity.lit){
                        int time = entity.litTime;
                        if(time > 0){
                            entity.setLit(true);
                            entity.lit = true;
                        }
                        if(!player.getAbilities().creativeMode) {
                            itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                        }
                        world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1f,1f);
                        return ActionResult.SUCCESS;
                    }
                    boolean isSuccess = entity.addFuel(itemStack);
                    if(isSuccess){
                        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1f, 1f);
                        if(!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                        return ActionResult.SUCCESS;
                    }else {
                        return ActionResult.PASS;
                    }
                }else if(itemStack.isOf(gregfood.WEEDING_SHOVEL)){
                    entity.stopBurning();
                }
            }
        }
        return ActionResult.PASS;
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return StoveBlockEntity::tick;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    static {
        LIT = Properties.LIT;
        SHAPE = VoxelShapes.union(
                Block.createCuboidShape(0.0D,0.0D,0.0D,16.0D,2.0D,16.0D),
                Block.createCuboidShape(0.0D,2.0D,0.0D,4.0D,12.0D,4.0D),
                Block.createCuboidShape(12.0D,2.0D,0.0D,16.0D,12.0D,4.0D),
                Block.createCuboidShape(12.0D,2.0D,12.0D,16.0D,12.0D,16.0D),
                Block.createCuboidShape(0.0D,2.0D,12.0D,4.0D,12.0D,16.0D),
                VoxelShapes.combineAndSimplify(
                        Block.createCuboidShape(0.0D,12.0D,0.0D,16.0D,16.0D,16.0D),
                        Block.createCuboidShape(4.0D,12.0D,4.0D,12.0D,16.0D,12.0D),
                        BooleanBiFunction.ONLY_FIRST
                )
        );
    }
}
class PotBlock extends Block implements BlockEntityProvider{
    protected static VoxelShape SHAPE;
    public static DirectionProperty FACING;

    public PotBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    public FoodInventories toInventory(ItemStack itemStack){
        if(itemStack.isOf(Items.BOWL)) {
            return FoodInventories.BOWL;
        }else if(itemStack.isOf(Items.GLASS_BOTTLE)) {
            return FoodInventories.BOTTLE;
        }else if(itemStack.isOf(gregfood.Plate)){
            return FoodInventories.PLATE;
        }else if(itemStack.isOf(gregfood.Chopsticks)){
            return FoodInventories.HOLDABLE;
        }
        return FoodInventories.DEFAULT;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if(world != null && !world.isClient){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof PotBlockEntity entity){
                if(hand == Hand.MAIN_HAND && !itemStack.isEmpty()){
                    FoodInventories foodInventory = toInventory(itemStack);
                    if(itemStack.isOf(gregfood.Spatula)){
                        entity.fryAction();
                        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1f, 1f);
                        if(!player.getAbilities().creativeMode) {
                            itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                        }
                        return ActionResult.SUCCESS;
                    }else if(foodInventory != FoodInventories.DEFAULT){
                        ItemStack pickStack = entity.pickFood(foodInventory);
                        if(!pickStack.isEmpty()){
                            if(!player.getAbilities().creativeMode && foodInventory != FoodInventories.HOLDABLE){
                                itemStack.decrement(1);
                            }
                            if(!player.getInventory().insertStack(pickStack)) {
                                player.dropItem(itemStack, false);
                            }
                            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1f, 1f);
                            return ActionResult.SUCCESS;
                        }
                    }else{
                        boolean success = entity.putIngredient(new ItemStack(itemStack.getItem()));
                        if(!success){
                            return ActionResult.PASS;
                        }
                        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1f, 1f);
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PotBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return PotBlockEntity::tick;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    static {
        SHAPE = VoxelShapes.combineAndSimplify(
                Block.createCuboidShape(0.0D,0.0D,0.0D,16.0D,5.0D,16.0D),
                Block.createCuboidShape(1.0D,1.0D,1.0D,15.0D,5.0D,15.0D),
                BooleanBiFunction.ONLY_FIRST
        );
        FACING = HorizontalFacingBlock.FACING;
    }

}