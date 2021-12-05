package com.github.gregtaoo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

class GrindingRecipe extends LongTimeRecipe {
    public GrindingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts) {
        super(gregfood.Grinding_Recipe_Type, id, group, input, output, experience, cookTime, counts);
    }
    
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Grinder.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Grinding_Recipe_Serializer;
    }
}
class SteamingRecipe extends LongTimeRecipe {
    public SteamingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts) {
        super(gregfood.Steaming_Recipe_Type, id, group, input, output, experience, cookTime, counts);
    }
    
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Steamer.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Steaming_Recipe_Serializer;
    }
}
class BoardCuttingRecipe extends LongTimeRecipe {
    public BoardCuttingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts) {
        super(gregfood.Board_Cutting_Recipe_Type, id, group, input, output, experience, cookTime, counts);
    }

    public ItemStack createIcon() {
        return new ItemStack(gregfood.Cutting_Board.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Board_Cutting_Recipe_Serializer;
    }
}
class KnifeCuttingRecipe extends ShortTimeRecipe {
    public KnifeCuttingRecipe(Identifier id, String group, Ingredient input, ItemStack output,  int counts) {
        super(gregfood.Cutting_Recipe_Type, id, group, input, output, counts);
    }
    
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Knife_Table.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Cutting_Recipe_Serializer;
    }
}
class BriningRecipe extends ShortTimeRecipe {
    public BriningRecipe(Identifier id, String group, Ingredient input, ItemStack output,  int counts) {
        super(gregfood.Brining_Recipe_Type, id, group, input, output, counts);
    }
    
    public ItemStack createIcon() {
        return new ItemStack(gregfood.Briner.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Brining_Recipe_Serializer;
    }
}
class DoughMakingRecipe extends TwoIngShortTimeRecipe {
    public DoughMakingRecipe(Identifier id, String group, Ingredient input, Ingredient input2, ItemStack output,  int counts, boolean amount, int actionNum) {
        super(gregfood.Dough_Making_Recipe_Type, id, group, input, input2, output, counts, amount, actionNum);
    }

    public ItemStack createIcon() {
        return new ItemStack(gregfood.Dough_Making_Table.asItem());
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Dough_Making_Recipe_Serializer;
    }
}
class LongTimeRecipeSerializer<T extends LongTimeRecipe> implements RecipeSerializer<T> {
    private final int cookingTime;
    private final int counts;
    private final RecipeFactory<T> recipeFactory;

    public LongTimeRecipeSerializer(RecipeFactory<T> recipeFactory, int cookingTime, int counts) {
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

    interface RecipeFactory<T extends LongTimeRecipe> {
        T create(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts);
    }
}
class ShortTimeRecipeSerializer<T extends ShortTimeRecipe> implements RecipeSerializer<T> {
    private final int counts;
    private final com.github.gregtaoo.ShortTimeRecipeSerializer.RecipeFactory<T> recipeFactory;

    public ShortTimeRecipeSerializer(RecipeFactory<T> recipeFactory, int counts) {
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
        int j = packetByteBuf.readVarInt();
        return this.recipeFactory.create(identifier, string, ingredient, itemStack, j);
    }

    public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
        packetByteBuf.writeString(abstractCookingRecipe.getGroup());
        abstractCookingRecipe.input.write(packetByteBuf);
        packetByteBuf.writeItemStack(abstractCookingRecipe.getOutput());
        packetByteBuf.writeVarInt(abstractCookingRecipe.getCounts());
    }

    interface RecipeFactory<T extends ShortTimeRecipe> {
        T create(Identifier id, String group, Ingredient input, ItemStack output, int counts);
    }
}
class TwoIngShortTimeRecipeSerializer<T extends TwoIngShortTimeRecipe> implements RecipeSerializer<T> {
    private final int counts;
    private final com.github.gregtaoo.TwoIngShortTimeRecipeSerializer.RecipeFactory<T> recipeFactory;

    public TwoIngShortTimeRecipeSerializer(RecipeFactory<T> recipeFactory, int counts) {
        this.recipeFactory = recipeFactory;
        this.counts = counts;
    }

    public T read(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        JsonElement jsonElement2 = JsonHelper.hasArray(jsonObject, "ingredient2") ? JsonHelper.getArray(jsonObject, "ingredient2") : JsonHelper.getObject(jsonObject, "ingredient2");
        Ingredient ingredient2 = Ingredient.fromJson(jsonElement2);
        String string2 = JsonHelper.getString(jsonObject, "result");
        Identifier identifier2 = new Identifier(string2);
        ItemStack itemStack = new ItemStack(Registry.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
        int j = JsonHelper.getInt(jsonObject, "counts", this.counts);
        boolean amount = JsonHelper.getBoolean(jsonObject, "amount", false);
        int actionNum = JsonHelper.getInt(jsonObject, "actionNum", 1);
        return this.recipeFactory.create(identifier, string, ingredient, ingredient2, itemStack, j, amount, actionNum);
    }

    public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String string = packetByteBuf.readString(32767);
        Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
        Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
        ItemStack itemStack = packetByteBuf.readItemStack();
        int j = packetByteBuf.readVarInt();
        boolean amount = packetByteBuf.readBoolean();
        int actionNum = packetByteBuf.readVarInt();
        return this.recipeFactory.create(identifier, string, ingredient, ingredient2, itemStack, j, amount, actionNum);
    }

    public void write(PacketByteBuf packetByteBuf, T abstractCookingRecipe) {
        packetByteBuf.writeString(abstractCookingRecipe.getGroup());
        abstractCookingRecipe.input.write(packetByteBuf);
        abstractCookingRecipe.input2.write(packetByteBuf);
        packetByteBuf.writeItemStack(abstractCookingRecipe.getOutput());
        packetByteBuf.writeVarInt(abstractCookingRecipe.getCounts());
        packetByteBuf.writeBoolean(abstractCookingRecipe.amount);
        packetByteBuf.writeVarInt(abstractCookingRecipe.actionNum);
    }

    interface RecipeFactory<T extends TwoIngShortTimeRecipe> {
        T create(Identifier id, String group, Ingredient input, Ingredient input2, ItemStack output, int counts, boolean amount, int actionNum);
    }
}
abstract class LongTimeRecipe implements Recipe<Inventory> {
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final float experience;
    protected final int cookTime;
    protected final int counts;

    public LongTimeRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime, int counts) {
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
        return new ItemStack(this.output.getItem(), this.counts);
    }
    
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
abstract class ShortTimeRecipe implements Recipe<Inventory> {
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int counts;

    public ShortTimeRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, ItemStack output, int counts) {
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
        return new ItemStack(this.output.getItem(),this.counts);
    }

    public boolean fits(int width, int height) {
        return true;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        return defaultedList;
    }

    public ItemStack getOutput() {
        return new ItemStack(this.output.getItem(),this.counts);
    }
    
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
abstract class TwoIngShortTimeRecipe implements Recipe<Inventory> {
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final Ingredient input2;
    protected final ItemStack output;
    protected final int counts;
    protected final boolean amount;
    protected final int actionNum;

    public TwoIngShortTimeRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, Ingredient input2, ItemStack output, int counts, boolean hasAmount, int actions) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.input = input;
        this.input2 = input2;
        this.output = output;
        this.counts = counts;
        this.amount = hasAmount;
        this.actionNum = actions;
    }


    public boolean matches(Inventory inv, World world) {
        return this.input.test(inv.getStack(0)) && this.input2.test(inv.getStack(1));
    }

    public ItemStack craft(Inventory inv) {
        return new ItemStack(this.output.getItem(),Math.max(this.counts,1));
    }

    public boolean fits(int width, int height) {
        return true;
    }

    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();
        defaultedList.add(this.input);
        defaultedList.add(this.input2);
        return defaultedList;
    }

    public ItemStack getOutput() {
        return new ItemStack(this.output.getItem(),this.counts);
    }

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
class FryingRecipe extends AbstractFryingRecipe{
    public FryingRecipe(Identifier id, String group, Ingredient input, int time, int steps, ItemStack output, FoodInventories foodInventory) {
        super(gregfood.Frying_Recipe_Type, id, group, input, time, steps, output, foodInventory);
    }

    public RecipeSerializer<?> getSerializer() {
        return gregfood.Frying_Recipe_Serializer;
    }
}
abstract class AbstractFryingRecipe implements Recipe<Inventory> {
    protected final RecipeType<?> type;
    protected final Identifier id;
    protected final String group;
    protected final Ingredient input;
    protected final int time;
    protected final int steps;
    protected final ItemStack output;
    protected final FoodInventories foodInventory;

    public AbstractFryingRecipe(RecipeType<?> type, Identifier id, String group, Ingredient input, int time, int steps, ItemStack output, FoodInventories foodInventory) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.input = input;
        this.time = time;
        this.steps = steps;
        this.output = output;
        this.foodInventory = foodInventory;
    }

    public boolean matches(Inventory inv, World world) {
        for(int i = 0; i < inv.size(); ++i){
            if(!this.input.test(inv.getStack(i))){
                return false;
            }
        }
        return true;
    }

    public ItemStack craft(Inventory inv) {
        return this.output;
    }

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

    public String getGroup() {
        return this.group;
    }

    public Identifier getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return this.type;
    }
}
class FryingRecipeSerializer<T extends FryingRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> recipeFactory;

    public FryingRecipeSerializer(RecipeFactory<T> recipeFactory) {
        this.recipeFactory = recipeFactory;
    }

    public T read(Identifier identifier, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "group", "");
        JsonElement jsonElement = JsonHelper.hasArray(jsonObject, "ingredient") ? JsonHelper.getArray(jsonObject, "ingredient") : JsonHelper.getObject(jsonObject, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonElement);
        int time = JsonHelper.getInt(jsonObject, "time", 1);
        int steps = JsonHelper.getInt(jsonObject,"steps",1);
        Identifier identifierResult = new Identifier(JsonHelper.getString(jsonObject, "result"));
        ItemStack itemStack = new ItemStack(Registry.ITEM.getOrEmpty(identifierResult).orElseThrow(() -> new IllegalStateException("Item: " + identifierResult.toString() + " does not exist")));
        FoodInventories foodInventory = FoodInventories.valueOf(JsonHelper.getString(jsonObject, "inventory", "HOLDABLE"));
        return this.recipeFactory.create(identifier, string, ingredient, time, steps, itemStack, foodInventory);
    }

    public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
        String string = packetByteBuf.readString(32767);
        Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
        int time = packetByteBuf.readVarInt();
        int steps = packetByteBuf.readVarInt();
        ItemStack itemStack = packetByteBuf.readItemStack();
        FoodInventories foodInventory = FoodInventories.valueOf(packetByteBuf.readString());
        return this.recipeFactory.create(identifier, string, ingredient, time, steps, itemStack, foodInventory);
    }

    public void write(PacketByteBuf packetByteBuf, T fryingRecipe) {
        packetByteBuf.writeString(fryingRecipe.getGroup());
        fryingRecipe.input.write(packetByteBuf);
        packetByteBuf.writeVarInt(fryingRecipe.time);
        packetByteBuf.writeVarInt(fryingRecipe.steps);
        packetByteBuf.writeItemStack(fryingRecipe.output);
        packetByteBuf.writeString(fryingRecipe.foodInventory.toString());
    }

    interface RecipeFactory<T extends FryingRecipe> {
        T create(Identifier id, String group, Ingredient input, int time, int steps, ItemStack output, FoodInventories foodInventory);
    }
}