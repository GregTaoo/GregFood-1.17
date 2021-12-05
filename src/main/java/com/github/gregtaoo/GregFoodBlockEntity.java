package com.github.gregtaoo;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

class GrinderBlockEntity extends BlockEntity{

    private final DefaultedList<ItemStack> itemStack;
    private int grindingTime;

    public GrinderBlockEntity(BlockPos pos, BlockState state) {
        super(gregfood.Grinder_Block_Entity, pos, state);
        this.itemStack = DefaultedList.ofSize(1,ItemStack.EMPTY);
        this.grindingTime = 0;
    }

    public static void setLit(World world,BlockPos pos,boolean lit){
        if(world != null)
            world.setBlockState(pos, world.getBlockState(pos).with(GrinderBlock.LIT, lit));
    }

    private static void dropExperience(World world, BlockPos vec3d, float f) {
        world.spawnEntity(new ExperienceOrbEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), (int) f));
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T t) {
        if(t instanceof GrinderBlockEntity) {
            GrinderBlockEntity g = (GrinderBlockEntity) t;
            if (g.itemStack.get(0) != ItemStack.EMPTY) {
                Inventory inventory = new SimpleInventory(g.itemStack.get(0));
                Optional<GrindingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Grinding_Recipe_Type, inventory, world);
                final int[] cookTime = {1};
                itemRecipe.ifPresent((recipe -> cookTime[0] = recipe.getCookTime()));
                if (g.grindingTime < cookTime[0] && !g.itemStack.get(0).isEmpty()) {
                    BlockState stove = world.getBlockState(blockPos.down());
                    if (stove.isOf(gregfood.Stove) && stove.get(Properties.LIT)) {
                        g.grindingTime++;
                        if (!g.itemStack.get(0).isEmpty())
                            setLit(world, blockPos, true);
                    } else {
                        g.grindingTime = 0;
                        setLit(world, blockPos, false);
                    }
                } else if (!g.itemStack.get(0).isEmpty()) {
                    ItemStack itemStack1 = itemRecipe.map((GrindingRecipe) -> GrindingRecipe.craft(inventory)).orElse(g.itemStack.get(0));
                    final float[] counts = {1, 1};
                    itemRecipe.ifPresent((recipe -> counts[0] = recipe.getCounts()));
                    itemRecipe.ifPresent((recipe -> counts[1] = recipe.getExperience()));
                    blockPos.up(2);
                    ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(itemStack1.getItem(), (int) counts[0]));
                    dropExperience(world, blockPos.up(), counts[1]);
                    g.grindingTime = 0;
                    g.itemStack.set(0, ItemStack.EMPTY);
                    setLit(world, blockPos, false);
                } else {
                    g.grindingTime = 0;
                    setLit(world, blockPos, false);
                }
            }
        }
    }

    public boolean addItem(ItemStack itemStack1){
        Inventory inventory = new SimpleInventory(itemStack1);
        if(world!=null){
            if(world.getRecipeManager().getFirstMatch(gregfood.Grinding_Recipe_Type,inventory,world).isEmpty()){
                return false;
            }
            if(this.itemStack.get(0) == ItemStack.EMPTY){
                this.itemStack.set(0,itemStack1);
                return true;
            }
        }
        return false;
    }

    public ItemStack pickItem(BlockPos blockPos,boolean isOnBreak){
        if(!isOnBreak)
            setLit(world,blockPos,false);
        if(this.itemStack.get(0) != ItemStack.EMPTY){
            ItemStack itemStack1 = this.itemStack.get(0);
            this.itemStack.set(0,ItemStack.EMPTY);
            this.grindingTime = 0;
            return itemStack1;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.itemStack.clear();
        Inventories.readNbt(tag,this.itemStack);
        this.grindingTime = tag.getInt("time");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag,this.itemStack,true);
        tag.putInt("time",this.grindingTime);
        return super.writeNbt(tag);
    }

}

class SteamerBlockEntity extends BlockEntity{

    private final DefaultedList<ItemStack> itemStack;
    private int steamingTime;

    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(gregfood.Steamer_Block_Entity, pos, state);
        this.itemStack = DefaultedList.ofSize(1,ItemStack.EMPTY);
        this.steamingTime = 0;
    }

    public static void setLit(World world,BlockPos pos, boolean lit){
        if(world != null)
            world.setBlockState(pos, world.getBlockState(pos).with(SteamerBlock.LIT, lit));
    }

    private static void dropExperience(World world, BlockPos vec3d, float f) {
        world.spawnEntity(new ExperienceOrbEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), (int) f));
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T t) {
        if(t instanceof SteamerBlockEntity) {
            SteamerBlockEntity s = (SteamerBlockEntity) t;
            if (s.itemStack.get(0) != ItemStack.EMPTY) {
                Inventory inventory = new SimpleInventory(s.itemStack.get(0));
                Optional<SteamingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Steaming_Recipe_Type, inventory, world);
                final int[] cookTime = {1};
                itemRecipe.ifPresent((recipe -> cookTime[0] = recipe.getCookTime()));
                if (s.steamingTime < cookTime[0] && !s.itemStack.get(0).isEmpty()) {
                    if (world.getBlockState(blockPos.down()).isOf(Blocks.FIRE)) {
                        s.steamingTime++;
                        if (!s.itemStack.get(0).isEmpty())
                            setLit(world, blockPos, true);
                    } else {
                        s.steamingTime = 0;
                        setLit(world, blockPos, false);
                    }
                } else if (!s.itemStack.get(0).isEmpty()) {
                    Random random = new Random();
                    if (random.nextInt(10) == 0) {
                        world.setBlockState(blockPos.down(), Blocks.AIR.getDefaultState());
                    }
                    ItemStack itemStack1 = itemRecipe.map((steamingRecipe) -> steamingRecipe.craft(inventory)).orElse(s.itemStack.get(0));
                    final float[] counts = {1, 1};
                    itemRecipe.ifPresent((recipe -> counts[0] = recipe.getCounts()));
                    itemRecipe.ifPresent((recipe -> counts[1] = recipe.getExperience()));
                    blockPos.up(2);
                    ItemScatterer.spawn(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(itemStack1.getItem(), (int) counts[0]));
                    dropExperience(world, blockPos.up(), counts[1]);
                    s.steamingTime = 0;
                    s.itemStack.set(0, ItemStack.EMPTY);
                    setLit(world, blockPos, false);
                } else {
                    s.steamingTime = 0;
                    setLit(world, blockPos, false);
                }
            }
        }
    }

    public boolean addItem(ItemStack itemStack1){
        Inventory inventory = new SimpleInventory(itemStack1);
        if(world!=null){
            if(world.getRecipeManager().getFirstMatch(gregfood.Steaming_Recipe_Type,inventory,world).isEmpty()){
                return false;
            }
            if(this.itemStack.get(0) == ItemStack.EMPTY){
                this.itemStack.set(0,itemStack1);
                return true;
            }
        }
        return false;
    }

    public ItemStack pickItem(BlockPos blockPos,boolean isOnBreak){
        if(!isOnBreak)
            setLit(world,blockPos,false);
        if(this.itemStack.get(0) != ItemStack.EMPTY){
            ItemStack itemStack1 = this.itemStack.get(0);
            this.itemStack.set(0,ItemStack.EMPTY);
            this.steamingTime = 0;
            return itemStack1;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.itemStack.clear();
        Inventories.readNbt(tag,this.itemStack);
        this.steamingTime = tag.getInt("time");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag,this.itemStack,true);
        tag.putInt("time",this.steamingTime);
        return super.writeNbt(tag);
    }

}

class DoughMakingTableEntity extends BlockEntity implements BlockEntityClientSerializable {
    private final DefaultedList<ItemStack> itemStacksList;
    private int steps;
    private ItemStack itemBeingMade;
    private Item requiredTool;

    public DoughMakingTableEntity(BlockPos pos, BlockState state) {
        super(gregfood.Dough_Making_Table_Entity, pos, state);
        this.itemStacksList = DefaultedList.ofSize(3, ItemStack.EMPTY);
        this.steps = -1;
        this.itemBeingMade = ItemStack.EMPTY;
        this.requiredTool = Items.AIR;
    }
    
    public boolean allowActing(ItemStack itemStack){
        return itemStack.isOf(gregfood.Dough) || itemStack.isOf(gregfood.Noodle) || itemStack.isOf(gregfood.Dough_Piece) || itemStack.isOf(gregfood.Uncooked_Mantou) || itemStack.isOf(gregfood.Wheat_Flour);
    }

    public boolean hasItem(){
        return !this.itemStacksList.get(0).isEmpty();
    }
    
    public boolean actions(ItemStack changeStack, int addStep, Item handItem){
        if(this.hasItem() && this.allowActing(this.getItemStack(false))){
            if(this.steps < 0 || this.requiredTool != handItem){
                this.steps = addStep;
                this.itemBeingMade = changeStack;
                this.requiredTool = handItem;
                return true;
            }
            if(this.steps > 0){
                this.steps--;
                return true;
            }
            this.itemStacksList.set(0, new ItemStack(this.itemBeingMade.getItem(), this.itemBeingMade.getCount() * this.getItemStack(false).getCount()));
            this.steps = -1;
            this.markDirty();
            return true;
        }
        return false;
    }

    public boolean putItemStack(ItemStack handStack){
        if(!this.hasItem() && this.allowActing(handStack)){
            this.itemStacksList.set(0, new ItemStack(handStack.getItem(), 1));
            this.markDirty();
            return true;
        }
        return false;
    }

    public ItemStack getItemStack(boolean isPick){
        if(this.hasItem()) {
            if (isPick) {
                ItemStack itemStack1 = this.itemStacksList.get(0);
                this.itemStacksList.clear();
                this.markDirty();
                return itemStack1;
            }
            return this.itemStacksList.get(0);
        }else{
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.itemStacksList.clear();
        Inventories.readNbt(tag, this.itemStacksList);
        this.itemBeingMade = this.itemStacksList.get(1);
        this.requiredTool = this.itemStacksList.get(2).getItem();
        this.steps = tag.getInt("steps");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        this.itemStacksList.set(1, this.itemBeingMade);
        this.itemStacksList.set(2, new ItemStack(this.requiredTool));
        Inventories.writeNbt(tag, this.itemStacksList, true);
        tag.putInt("steps", this.steps);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        super.readNbt(tag);
        this.itemStacksList.clear();
        Inventories.readNbt(tag, this.itemStacksList);
        this.itemBeingMade = this.itemStacksList.get(1);
        this.requiredTool = this.itemStacksList.get(2).getItem();
        this.steps = tag.getInt("steps");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        super.writeNbt(tag);
        this.itemStacksList.set(1, this.itemBeingMade);
        this.itemStacksList.set(2, new ItemStack(this.requiredTool));
        Inventories.writeNbt(tag, this.itemStacksList, true);
        tag.putInt("steps", this.steps);
        return tag;
    }
}
class CuttingBoardEntity extends BlockEntity implements BlockEntityClientSerializable{
    private final DefaultedList<ItemStack> itemStacksList;
    private ItemStack itemBeingCut;
    private int step;

    public CuttingBoardEntity(BlockPos pos, BlockState state) {
        super(gregfood.Cutting_Board_Entity, pos, state);
        this.itemStacksList = DefaultedList.ofSize(2, ItemStack.EMPTY);
        this.itemBeingCut = ItemStack.EMPTY;
        this.step = -1;
    }

    public boolean cutAction(){
        if(!this.getItemStack(false).isEmpty() && !this.itemBeingCut.isEmpty()) {
            if (this.step > 0) {
                this.step--;
                return true;
            } else if (this.step == 0){
                this.itemStacksList.set(0, this.itemBeingCut.copy());
                this.markDirty();
                return true;
            }
        }
        return false;
    }

    public boolean putItemStack(ItemStack handStack, int addStep, ItemStack itemStack){
        if(!this.itemStacksList.get(0).isEmpty()){
            return false;
        }
        this.itemStacksList.set(0, new ItemStack(handStack.getItem(),1));
        this.itemBeingCut = itemStack.copy();
        this.step = addStep;
        this.markDirty();
        return true;
    }

    public ItemStack getItemStack(boolean isPick){
        if(isPick){
            ItemStack itemStack = this.itemStacksList.get(0).copy();
            this.itemStacksList.clear();
            this.markDirty();
            return itemStack;
        }
        return this.itemStacksList.get(0).copy();
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, this.itemStacksList);
        this.itemBeingCut = this.itemStacksList.get(1).copy();
        this.itemStacksList.set(1, ItemStack.EMPTY);
        this.step = tag.getInt("step");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        this.itemStacksList.set(1, this.itemBeingCut.copy());
        Inventories.writeNbt(tag, this.itemStacksList);
        tag.putInt("step", this.step);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, this.itemStacksList);
        this.itemBeingCut = this.itemStacksList.get(1).copy();
        this.itemStacksList.set(1, ItemStack.EMPTY);
        this.step = tag.getInt("step");
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        super.writeNbt(tag);
        this.itemStacksList.set(1, this.itemBeingCut.copy());
        Inventories.writeNbt(tag, this.itemStacksList);
        tag.putInt("step", this.step);
        return tag;
    }
}
class WoodenSteamerEntity extends BlockEntity implements BlockEntityClientSerializable{
    public boolean enableUp;
    public boolean canLit;
    public boolean lit;
    public int floor;
    public final DefaultedList<ItemStack> upItems;
    public int[] upTimes;
    public final DefaultedList<ItemStack> downItems;
    public int[] downTimes;

    public WoodenSteamerEntity(BlockPos pos, BlockState state) {
        super(gregfood.Wooden_Steamer_Entity, pos, state);
        this.enableUp = this.getEnableUp();
        this.canLit = false;
        this.lit = false;
        this.floor = this.getFloors(pos);
        this.upItems = DefaultedList.ofSize(8, ItemStack.EMPTY);
        this.upTimes = new int[]{0, 0, 0, 0, 0, 0};
        this.downItems = DefaultedList.ofSize(8, ItemStack.EMPTY);
        this.downTimes = new int[]{0, 0, 0, 0, 0, 0};
    }

    public void updateBlock(){
        this.enableUp = this.getEnableUp();
        this.floor = this.getFloors(this.pos);
        this.canLit = this.getLitCond(this.pos);
        this.setLit();
    }

    public void setLit(){
        if(this.world != null){
            this.world.setBlockState(this.pos, this.getCachedState().with(Properties.LIT, this.lit));
        }
    }

    public boolean getEnableUp(){
        return this.getCachedState().get(Properties.SLAB_TYPE) == SlabType.DOUBLE;
    }

    public boolean hasFire(BlockState blockState){
        return blockState.isOf(Blocks.FIRE) || blockState.isOf(Blocks.SOUL_FIRE);
    }

    public int getFloors(BlockPos pos){
        int floors = 1;
        BlockPos locatePos = pos.down();
        if(this.world != null){
            BlockState blockState = this.world.getBlockState(locatePos);
            while (blockState.isOf(gregfood.Wooden_Steamer)){
                BlockEntity entity = this.world.getBlockEntity(locatePos);
                if(entity instanceof WoodenSteamerEntity && ((WoodenSteamerEntity) entity).enableUp){
                    floors++;
                    locatePos = locatePos.down();
                }else{
                    return floors;
                }
            }
        }
        return floors;
    }

    public boolean getLitCond(BlockPos pos){
        BlockPos locatePos = pos.down(this.floor);
        if(this.world != null){
            BlockState cauldronState = this.world.getBlockState(locatePos);
            return cauldronState.isOf(Blocks.WATER_CAULDRON) && cauldronState.get(Properties.LEVEL_3) > 0 && this.hasFire(this.world.getBlockState(locatePos.down()));
        }
        return false;
    }

    public boolean insertItem(BlockPos pos, ItemStack itemStack, ItemStack itemBeingCooked, int cookingTime){
        this.updateBlock();
        int floors = this.floor;
        while(this.world != null && floors >= 0){
            BlockEntity blockEntity = this.world.getBlockEntity(pos.down(floors - 1));
            WoodenSteamerEntity entity = (blockEntity instanceof WoodenSteamerEntity) ? (WoodenSteamerEntity)blockEntity : null;
            if(entity != null){
                this.updateBlock();
                for(int i = 0; i < 4; ++i){
                    if(entity.downItems.get(i).isEmpty()){
                        entity.downItems.set(i, itemStack);
                        entity.downItems.set(i + 4, itemBeingCooked);
                        entity.downTimes[i] = cookingTime;
                        this.updateBlock();
                        return true;
                    }
                }
                if(entity.enableUp){
                    for(int i = 0; i < 4; ++i){
                        if(entity.upItems.get(i).isEmpty()){
                            entity.upItems.set(i, itemStack);
                            entity.upItems.set(i + 4, itemBeingCooked);
                            entity.upTimes[i] = cookingTime;
                            this.updateBlock();
                            return true;
                        }
                    }
                }
            }
            floors--;
        }
        return false;
    }

    public ItemStack pickItem(BlockPos pos, boolean isPick){
        this.updateBlock();
        int floors = this.floor;
        while(this.world != null && floors >= 0){
            BlockEntity blockEntity = this.world.getBlockEntity(pos.down(this.floor - floors));
            WoodenSteamerEntity entity = (blockEntity instanceof WoodenSteamerEntity) ? (WoodenSteamerEntity)blockEntity : null;
            if(entity != null){
                if(entity.enableUp){
                    for(int i = 3; i >= 0; --i){
                        if(!entity.upItems.get(i).isEmpty()){
                            ItemStack itemStack = entity.upItems.get(i);
                            if(isPick) {
                                entity.upItems.set(i, ItemStack.EMPTY);
                                entity.upItems.set(i + 4, ItemStack.EMPTY);
                                entity.upTimes[i] = 0;
                            }
                            this.updateBlock();
                            return itemStack;
                        }
                    }
                }
                for(int i = 3; i >= 0; --i){
                    if(!entity.downItems.get(i).isEmpty()){
                        ItemStack itemStack = entity.downItems.get(i);
                        if(isPick) {
                            entity.downItems.set(i, ItemStack.EMPTY);
                            entity.downItems.set(i + 4, ItemStack.EMPTY);
                            entity.downTimes[i] = 0;
                        }
                        this.updateBlock();
                        return itemStack;
                    }
                }
            }
            floors--;
        }
        return ItemStack.EMPTY;
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T t) {
        if(world != null){
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if(blockEntity instanceof WoodenSteamerEntity){
                WoodenSteamerEntity entity = (WoodenSteamerEntity) blockEntity;
                entity.updateBlock();
                if(entity.floor > 5){
                    return;
                }
                if(entity.canLit){
                    boolean isLit = false;
                    for(int i = 0; i < 4 && i < entity.upTimes.length && i < entity.downTimes.length; ++i){
                        if(entity.upTimes[i] <= 0){
                            entity.upItems.set(i, entity.upItems.get(i + 4));
                        }else{
                            entity.upTimes[i]--;
                            isLit = true;
                        }
                        if(entity.downTimes[i] <= 0){
                            entity.downItems.set(i, entity.downItems.get(i + 4));
                        }else{
                            entity.downTimes[i]--;
                            isLit = true;
                        }
                        entity.lit = isLit;
                    }
                    Random random = new Random();
                    if(entity.lit && random.nextInt(2400) == 1){
                        BlockState cauldronState = world.getBlockState(blockPos.down(entity.floor));
                        int level = cauldronState.get(Properties.LEVEL_3);
                        world.setBlockState(blockPos.down(entity.floor), cauldronState.with(Properties.LEVEL_3, Math.max(1, level - 1)));
                        if(level - 1 <= 0){
                            world.setBlockState(blockPos.down(entity.floor), Blocks.CAULDRON.getDefaultState());
                        }
                    }
                    if(world.isClient() && random.nextInt(100) == 1){
                        DefaultParticleType defaultParticleType = ParticleTypes.CAMPFIRE_SIGNAL_SMOKE;
                        world.addImportantParticle(defaultParticleType, true, (double)entity.pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), (double)entity.pos.getY() + random.nextDouble() + random.nextDouble(), (double)entity.pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double)(random.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
                    }
                }else{
                    entity.lit = false;
                }
            }
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.enableUp = tag.getBoolean("up");
        this.canLit = tag.getBoolean("can");
        this.lit = tag.getBoolean("lit");
        this.floor = tag.getInt("floor");
        Inventories.readNbt(tag, this.downItems);
        this.upTimes = tag.getIntArray("uptime");
        Inventories.readNbt(tag, this.upItems);
        this.downTimes = tag.getIntArray("downtime");
        this.updateBlock();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putBoolean("up", this.enableUp);
        tag.putBoolean("can", this.canLit);
        tag.putBoolean("lit", this.lit);
        tag.putInt("floor", this.floor);
        Inventories.writeNbt(tag, this.downItems);
        tag.putIntArray("uptime", this.upTimes);
        Inventories.writeNbt(tag, this.upItems);
        tag.putIntArray("downtime", this.downTimes);
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, this.downItems);
        Inventories.readNbt(tag, this.upItems);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, this.downItems);
        Inventories.writeNbt(tag, this.upItems);
        return tag;
    }
}
class StoveBlockEntity extends BlockEntity{
    public Map<Item, Integer> fuels;
    public boolean lit;
    public int litTime;

    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(gregfood.Stove_Block_Entity, pos, state);
        this.fuels = AbstractFurnaceBlockEntity.createFuelTimeMap();
        this.lit = false;
        this.litTime = 0;
    }

    public void setLit(boolean isLit){
        if(this.world != null){
            this.world.setBlockState(this.pos, this.getCachedState().with(Properties.LIT, isLit));
        }
    }

    public void stopBurning(){
        this.setLit(false);
        this.lit = false;
    }

    public boolean addFuel(ItemStack handStack){
        if(handStack.isEmpty()){
            return false;
        }
        Item item = handStack.getItem();
        if(this.fuels.containsKey(item)){
            int time = this.fuels.get(item);
            if(time > 0){
                this.litTime += time;
                return true;
            }
        }
        return false;
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T t) {
        if(t instanceof StoveBlockEntity entity){
            boolean lit = entity.lit;
            int litTime = entity.litTime;
            if(litTime > 0 && lit){
                entity.litTime--;
            }else if(lit){
                entity.lit = false;
                entity.setLit(false);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.lit = tag.getBoolean("lit");
        this.setLit(this.lit);
        this.litTime = tag.getInt("time");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putBoolean("lit", this.lit);
        tag.putInt("time", this.litTime);
        return tag;
    }
}
enum FoodInventories{
    BOWL,
    BOTTLE,
    PLATE,
    HOLDABLE,
    DEFAULT
}
class PotBlockEntity extends BlockEntity implements BlockEntityClientSerializable{
    public int steps;
    public int requireSteps;
    public int time;
    public int requireTime;
    public int preheat;
    public boolean isCooking;
    public int amount;
    public DefaultedList<ItemStack> ingredients;
    public ItemStack itemBeingCooked;
    public ItemStack itemStack;
    public FoodInventories foodInventory;

    public PotBlockEntity(BlockPos pos, BlockState state) {
        super(gregfood.pot_Block_Entity, pos, state);
        this.steps = 0;
        this.requireSteps = 0;
        this.time = 0;
        this.requireTime = 0;
        this.preheat = 200;
        this.isCooking = false;
        this.amount = 0;
        this.ingredients = DefaultedList.ofSize(20, ItemStack.EMPTY);
        this.itemBeingCooked = ItemStack.EMPTY;
        this.itemStack = ItemStack.EMPTY;
        this.foodInventory = FoodInventories.DEFAULT;
    }

    public void fryAction(){
        this.steps++;
    }

    public boolean isEmpty(){
        return this.ingredients.isEmpty();
    }

    public boolean putIngredient(ItemStack itemStack){
        if(this.amount >= 19 || this.preheat <= 0){
            return false;
        }
        this.ingredients.set(this.amount, itemStack);
        this.amount++;
        return true;
    }

    public ItemStack pickFood(FoodInventories type){
        if(type == this.foodInventory){
            this.preheat = 200;
            this.steps = 0;
            this.time = 0;
            this.ingredients.clear();
            this.amount = 0;
            this.foodInventory = FoodInventories.DEFAULT;
            return this.itemStack;
        }
        return ItemStack.EMPTY;
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T t) {
        if(t instanceof PotBlockEntity entity && !world.isClient){
            BlockState stove = world.getBlockState(blockPos.down());
            if(entity.isEmpty() || !stove.isOf(gregfood.Stove) || !stove.get(StoveBlock.LIT)){
                entity.preheat = 200;
                entity.steps = 0;
                entity.time = 0;
                entity.isCooking = false;
            }else{
                if(entity.preheat > 0){
                    entity.isCooking = true;
                    entity.preheat--;
                }else if(entity.preheat == 0){
                    ItemStack[] itemStacks = new ItemStack[0];
                    entity.ingredients.toArray(itemStacks);
                    Inventory inventory = new SimpleInventory(itemStacks);
                    Optional<FryingRecipe> itemRecipe = world.getRecipeManager().getFirstMatch(gregfood.Frying_Recipe_Type, inventory, world);
                    if(itemRecipe.isPresent()){
                        entity.itemBeingCooked = itemRecipe.map((FryingRecipe) -> FryingRecipe.craft(inventory)).orElse(ItemStack.EMPTY);
                        entity.requireTime = itemRecipe.get().time;
                        entity.requireSteps = itemRecipe.get().steps;
                        entity.foodInventory = itemRecipe.get().foodInventory;
                    }
                }else if(!entity.itemBeingCooked.isEmpty()){
                    entity.time++;
                    if(entity.time >= entity.requireTime && entity.steps >= entity.requireSteps){
                        entity.itemStack = entity.itemBeingCooked;
                        entity.ingredients.clear();
                    }else{
                        entity.itemStack = new ItemStack(Items.PORKCHOP);
                    }
                }
            }
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.steps = tag.getInt("steps");
        this.requireSteps = tag.getInt("rst");
        this.time = tag.getInt("time");
        this.requireTime = tag.getInt("rti");
        this.preheat = tag.getInt("preheat");
        this.isCooking = tag.getBoolean("cooking");
        this.amount = tag.getInt("amount");
        Inventories.readNbt(tag, this.ingredients);
        DefaultedList<ItemStack> tempItemStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);
        Inventories.readNbt(tag, tempItemStacks);
        this.itemBeingCooked = tempItemStacks.get(0);
        this.itemStack = tempItemStacks.get(1);
        this.foodInventory = FoodInventories.valueOf(tag.getString("inventory"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("steps", this.steps);
        tag.putInt("rst", this.requireSteps);
        tag.putInt("time", this.time);
        tag.putInt("rti", this.requireTime);
        tag.putInt("preheat", this.preheat);
        tag.putBoolean("cooking", this.isCooking);
        tag.putInt("amount", this.amount);
        Inventories.writeNbt(tag, this.ingredients);
        DefaultedList<ItemStack> tempItemStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);
        tempItemStacks.set(0, this.itemBeingCooked);
        tempItemStacks.set(1, this.itemStack);
        Inventories.writeNbt(tag, tempItemStacks);
        tag.putString("inventory", this.foodInventory.toString());
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        super.readNbt(tag);
        tag.putInt("amount", this.amount);
        Inventories.readNbt(tag, this.ingredients);
        DefaultedList<ItemStack> tempItemStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);
        Inventories.readNbt(tag, tempItemStacks);
        this.itemBeingCooked = tempItemStacks.get(0);
        this.itemStack = tempItemStacks.get(1);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("amount", this.amount);
        Inventories.writeNbt(tag, this.ingredients);
        DefaultedList<ItemStack> tempItemStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);
        tempItemStacks.set(0, this.itemBeingCooked);
        tempItemStacks.set(1, this.itemStack);
        Inventories.writeNbt(tag, tempItemStacks);
        return tag;
    }
}