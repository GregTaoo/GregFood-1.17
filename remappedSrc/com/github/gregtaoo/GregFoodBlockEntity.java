package com.github.gregtaoo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

import java.util.Optional;
import java.util.Random;

class GrinderBlockEntity extends BlockEntity implements Tickable {

    private final DefaultedList<ItemStack> itemStack;
    private int grindingTime;

    public GrinderBlockEntity() {
        super(gregfood.Grinder_Block_Entity);
        this.itemStack = DefaultedList.ofSize(1,ItemStack.EMPTY);
        this.grindingTime = 0;
    }

    public void setLit(boolean lit){
        if(this.world != null)
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(GrinderBlock.LIT, lit));
    }

    private static void dropExperience(World world, BlockPos vec3d, float f) {
        world.spawnEntity(new ExperienceOrbEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), (int) f));
    }

    public void tick(){
        BlockPos blockPos = this.getPos();
        if (this.itemStack.get(0)!=ItemStack.EMPTY && this.world!=null) {
            Inventory inventory = new SimpleInventory(this.itemStack.get(0));
            Optional<GrindingRecipe> itemRecipe = this.world.getRecipeManager().getFirstMatch(gregfood.Grinding_Recipe_Type,inventory,this.world);
            final int[] cookTime = {1};
            itemRecipe.ifPresent((recipe -> cookTime[0] = recipe.getCookTime()));
            if(this.grindingTime < cookTime[0] && !itemStack.get(0).isEmpty()){
                if(this.world != null){
                    if(this.world.getBlockState(blockPos.down()).isOf(Blocks.FIRE)){
                        this.grindingTime++;
                        if(!itemStack.get(0).isEmpty())
                            this.setLit(true);
                    }else{
                        this.grindingTime = 0;
                        this.setLit(false);
                    }
                }
            }else if(!itemStack.get(0).isEmpty()){
                Random random = new Random();
                if(random.nextInt(10)==0){
                    this.world.setBlockState(blockPos.down(),Blocks.AIR.getDefaultState());
                }
                ItemStack itemStack1 = itemRecipe.map((GrindingRecipe) -> GrindingRecipe.craft(inventory)).orElse(this.itemStack.get(0));
                final float[] counts = {1,1};
                itemRecipe.ifPresent((recipe -> counts[0] = recipe.getCounts()));
                itemRecipe.ifPresent((recipe -> counts[1] = recipe.getExperience()));
                blockPos.up(2);
                ItemScatterer.spawn(this.world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(itemStack1.getItem(), (int) counts[0]));
                dropExperience(this.world,this.getPos().up(),counts[1]);
                this.grindingTime = 0;
                this.itemStack.set(0,ItemStack.EMPTY);
                this.setLit(false);
            }else{
                this.grindingTime=0;
                this.setLit(false);
            }
        }
    }

    public boolean addItem(ItemStack itemStack1){
        Inventory inventory = new SimpleInventory(itemStack1);
        if(this.world!=null){
            if(!this.world.getRecipeManager().getFirstMatch(gregfood.Grinding_Recipe_Type,inventory,this.world).isPresent()){
                return false;
            }
            if(this.itemStack.get(0) == ItemStack.EMPTY){
                this.itemStack.set(0,itemStack1);
                return true;
            }
        }
        return false;
    }

    public ItemStack pickItem(boolean isOnBreak){
        if(!isOnBreak)
            this.setLit(false);
        if(this.itemStack.get(0) != ItemStack.EMPTY){
            ItemStack itemStack1 = this.itemStack.get(0);
            this.itemStack.set(0,ItemStack.EMPTY);
            this.grindingTime = 0;
            return itemStack1;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        super.readNbt(state, tag);
        this.itemStack.clear();
        Inventories.readNbt(tag,itemStack);
        this.grindingTime = tag.getInt("time");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag,itemStack,true);
        tag.putInt("time",this.grindingTime);
        return super.writeNbt(tag);
    }

}

class SteamerBlockEntity extends BlockEntity implements Tickable {

    private final DefaultedList<ItemStack> itemStack;
    private int steamingTime;

    public SteamerBlockEntity() {
        super(gregfood.Steamer_Block_Entity);
        this.itemStack = DefaultedList.ofSize(1,ItemStack.EMPTY);
        this.steamingTime = 0;
    }

    public void setLit(boolean lit){
        if(this.world != null)
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(SteamerBlock.LIT, lit));
    }

    private static void dropExperience(World world, BlockPos vec3d, float f) {
        world.spawnEntity(new ExperienceOrbEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), (int) f));
    }

    public void tick(){
        BlockPos blockPos = this.getPos();
        if (this.itemStack.get(0)!=ItemStack.EMPTY && this.world!=null) {
            Inventory inventory = new SimpleInventory(this.itemStack.get(0));
            Optional<SteamingRecipe> itemRecipe = this.world.getRecipeManager().getFirstMatch(gregfood.Steaming_Recipe_Type,inventory,this.world);
            final int[] cookTime = {1};
            itemRecipe.ifPresent((recipe -> cookTime[0] = recipe.getCookTime()));
            BlockPos waterPos = this.pos;
            BlockPos[] blockPoses = {this.pos.east(), this.pos.south(), this.pos.west(), this.pos.north()};
            for (BlockPos blockPose : blockPoses) {
                if (this.world.getBlockState(blockPose).isOf(Blocks.WATER)) {
                    waterPos = blockPose;
                    break;
                }
            }
            if(this.steamingTime < cookTime[0] && !itemStack.get(0).isEmpty()){
                if(this.world.getBlockState(blockPos.down()).isOf(Blocks.FIRE) && waterPos!=this.pos){
                    this.steamingTime++;
                    if(!itemStack.get(0).isEmpty())
                        this.setLit(true);
                }else{
                    this.steamingTime = 0;
                    this.setLit(false);
                }
            }else if(!itemStack.get(0).isEmpty()){
                Random random = new Random();
                if(random.nextInt(10)==0){
                    this.world.setBlockState(blockPos.down(),Blocks.AIR.getDefaultState());
                }
                if(random.nextInt(5)==0 && waterPos!=this.pos){
                    this.world.setBlockState(waterPos,Blocks.AIR.getDefaultState());
                }
                ItemStack itemStack1 = itemRecipe.map((SteamingRecipe) -> SteamingRecipe.craft(inventory)).orElse(this.itemStack.get(0));
                final float[] counts = {1,1};
                itemRecipe.ifPresent((recipe -> counts[0] = recipe.getCounts()));
                itemRecipe.ifPresent((recipe -> counts[1] = recipe.getExperience()));
                blockPos.up(2);
                ItemScatterer.spawn(this.world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), new ItemStack(itemStack1.getItem(), (int) counts[0]));
                dropExperience(this.world,this.getPos().up(),counts[1]);
                this.steamingTime = 0;
                this.itemStack.set(0,ItemStack.EMPTY);
                this.setLit(false);
            }else{
                this.steamingTime=0;
                this.setLit(false);
            }
        }
    }

    public boolean addItem(ItemStack itemStack1){
        Inventory inventory = new SimpleInventory(itemStack1);
        if(this.world!=null){
            if(!this.world.getRecipeManager().getFirstMatch(gregfood.Steaming_Recipe_Type,inventory,this.world).isPresent()){
                return false;
            }
            if(this.itemStack.get(0) == ItemStack.EMPTY){
                this.itemStack.set(0,itemStack1);
                return true;
            }
        }
        return false;
    }

    public ItemStack pickItem(boolean isOnBreak){
        if(!isOnBreak)
            this.setLit(false);
        if(this.itemStack.get(0) != ItemStack.EMPTY){
            ItemStack itemStack1 = this.itemStack.get(0);
            this.itemStack.set(0,ItemStack.EMPTY);
            this.steamingTime = 0;
            return itemStack1;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        super.readNbt(state, tag);
        this.itemStack.clear();
        Inventories.readNbt(tag,itemStack);
        this.steamingTime = tag.getInt("time");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag,itemStack,true);
        tag.putInt("time",this.steamingTime);
        return super.writeNbt(tag);
    }

}