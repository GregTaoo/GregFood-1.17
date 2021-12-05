package com.github.gregtaoo;

import com.github.gregtaoo.GrindingRecipeSerializer.RecipeFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

class GrindingRecipe extends AbstractGrindingRecipe {
    public GrindingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts) {
        super(gregfood.Grinding_Recipe_Type, id, group, input, output, experience, cookTime, counts);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Grinder.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Grinding_Recipe_Serializer;
    }
}
class SteamingRecipe extends AbstractGrindingRecipe {
    public SteamingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts) {
        super(gregfood.Steaming_Recipe_Type, id, group, input, output, experience, cookTime, counts);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Steamer.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Grinding_Recipe_Serializer;
    }
}
class CuttingRecipe extends AbstractAtOnceRecipe {
    public CuttingRecipe(Identifier id, String group, Ingredient input, ItemStack output,  int counts) {
        super(gregfood.Cutting_Recipe_Type, id, group, input, output, counts);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Knife_Table.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Cutting_Recipe_Serializer;
    }
}
class BriningRecipe extends AbstractAtOnceRecipe {
    public BriningRecipe(Identifier id, String group, Ingredient input, ItemStack output,  int counts) {
        super(gregfood.Brining_Recipe_Type, id, group, input, output, counts);
    }

    @Environment(EnvType.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Briner.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Brining_Recipe_Serializer;
    }
}
class GrindingRecipeSerializer<T extends AbstractGrindingRecipe> implements RecipeSerializer<T> {
    private final int cookingTime;
    private final int counts;
    private final RecipeFactory<T> recipeFactory;

    public GrindingRecipeSerializer(RecipeFactory<T> recipeFactory, int cookingTime, int counts) {
        this.cookingTime = cookingTime;
        this.recipeFactory = recipeFactory;
        this.counts = counts;
    }

    public T read(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        String string2 = JsonHelper.getString(jsonObject, "result");
        Identifier identifier2 = new Identifier(string2);
        ItemStack itemStack = new ItemStack(Registry.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
        float f = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
        int i = JsonHelper.getInt(jsonObject, "cookingtime", this.cookingTime);
        int j = JsonHelper.getInt(jsonObject, "counts", this.counts);
        return this.recipeFactory.create(identifier, string, ingredient, itemStack, f, i, j);
    }

    public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String string = packetByteBuf.readString(32767);
        Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
        ItemStack itemStack = packetByteBuf.readItemStack();
        float f = packetByteBuf.readFloat();
        int i = packetByteBuf.readVarInt();
        int j = packetByteBuf.readVarInt();
        return this.recipeFactory.create(identifier, string, ingredient, itemStack, f, i, j);
    }

    public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
        packetByteBuf.writeString(abstractCookingRecipe.getGroup());
        abstractCookingRecipe.input.write(packetByteBuf);
        packetByteBuf.writeItemStack(abstractCookingRecipe.getOutput());
        packetByteBuf.writeFloat(abstractCookingRecipe.getExperience());
        packetByteBuf.writeVarInt(abstractCookingRecipe.getCookTime());
        packetByteBuf.writeVarInt(abstractCookingRecipe.getCounts());
    }

    interface RecipeFactory<T extends AbstractGrindingRecipe> {
        T create(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts);
    }
}
class AtOnceRecipeSerializer<T extends AbstractAtOnceRecipe> implements RecipeSerializer<T> {
    private final int counts;
    private final com.github.gregtaoo.AtOnceRecipeSerializer.RecipeFactory<T> recipeFactory;

    public AtOnceRecipeSerializer(com.github.gregtaoo.AtOnceRecipeSerializer.RecipeFactory<T> recipeFactory, int counts) {
        this.recipeFactory = recipeFactory;
        this.counts = counts;
    }

    public T read(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        String string2 = JsonHelper.getString(jsonObject, "result");
        Identifier identifier2 = new Identifier(string2);
        ItemStack itemStack = new ItemStack(Registry.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
        int j = JsonHelper.getInt(jsonObject, "counts", this.counts);
        return this.recipeFactory.create(identifier, string, ingredient, itemStack, j);
    }

    public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String string = packetByteBuf.readString(32767);
        Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
        ItemStack itemStack = packetByteBuf.readItemStack();
        float f = packetByteBuf.readFloat();
        int i = packetByteBuf.readVarInt();
        int j = packetByteBuf.readVarInt();
        return this.recipeFactory.create(identifier, string, ingredient, itemStack, j);
    }

    public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
        packetByteBuf.writeString(abstractCookingRecipe.getGroup());
        abstractCookingRecipe.input.write(packetByteBuf);
        packetByteBuf.writeItemStack(abstractCookingRecipe.getOutput());
        packetByteBuf.writeVarInt(abstractCookingRecipe.getCounts());
    }

    interface RecipeFactory<T extends AbstractAtOnceRecipe> {
        T create(Identifier id, String group, Ingredient input, ItemStack output,int counts);
    }
}
abstract class AbstractGrindingRecipe implements Recipe<Inventory> {
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final float experience;
    protected final int cookTime;
    protected final int counts;

    public AbstractGrindingRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.experience = experience;
        this.cookTime = cookTime;
        this.counts = counts;
    }

    public boolean matches(Inventory inv, World world) {
        return this.input.test(inv.getStack(0));
    }

    public ItemStack craft(Inventory inv) {
        return this.output.copy();
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return true;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    public float getExperience() {
        return this.experience;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    public int getCounts() { return this.counts; }

    public Identifier getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return this.type;
    }
}
abstract class AbstractAtOnceRecipe implements Recipe<Inventory> {
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int counts;

    public AbstractAtOnceRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, ItemStack output, int counts) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
        this.counts = counts;
    }


    public boolean matches(Inventory inv, World world) {
        return this.input.test(inv.getStack(0));
    }

    public ItemStack craft(Inventory inv) {
        return this.output.copy();
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return true;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    public int getCounts() { return this.counts; }

    public Identifier getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return this.type;
    }
}
