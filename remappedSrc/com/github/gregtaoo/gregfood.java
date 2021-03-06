package com.github.gregtaoo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class gregfood implements ModInitializer {

	public static boolean hasNewVer = false;
	public static boolean forceUpdate = false;
	public static String verNum = "1.7.1";
	public static String newVerNum = verNum;
	public static String Announcement = " ";
	public static String forceUpdateAnn = " ";

	//ItemGroup
	public static final ItemGroup GregFood_Group = FabricItemGroupBuilder.build(
			new Identifier("gregfood", "gregfoodgroup"),
			() -> new ItemStack(gregfood.ginger));
	//crop
	public static Item green_onion_leaf = new Item(new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT));
	public static Block green_onion = new OnionCrop(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static Block soybean = new SoybeanCrop(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static Block tea = new TeaBlock(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static Block paddy = new PaddyBlock(FabricBlockSettings.of(Material.UNDERWATER_PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static Block turnip = new TurnipCrop(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static Block ginger = new GingerBlock(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
	public static Item garlic_leaf = new Item(new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT));
	public static Block garlic = new GarlicBlock(FabricBlockSettings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));

	//tools
	public static ToolItem WEEDING_SHOVEL = new ShovelItem(CustomToolMaterial.Weeding1, 1.5F, -3.0F, new Item.Settings().group(GregFood_Group));
	public static ToolItem Pot = new ShovelItem(PotToolMaterial.Pot1,0,1.0F,new Item.Settings().group(GregFood_Group));
	public static ToolItem Kitchen_Knife = new SwordItem(PotToolMaterial.Pot1,0,1.0F,new Item.Settings().group(GregFood_Group));

	//feature
	public static final Block Salt_Ore = new Block(Block.Settings.of(Material.STONE).strength(6.0F,4.0F));
	public static ConfiguredFeature<?, ?> Salt_Ores = Feature.ORE
			.configure(new OreFeatureConfig(
					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
					gregfood.Salt_Ore.getDefaultState(),
					8)) // vein size
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(
					0, // bottom offset
					64, // min y level
					128))) // max y level
			.spreadHorizontally()
			.repeat(16); // number of veins per chunk


	//Items
	public static final Item Glowing_Apple = new Item(new Item.Settings()
			.food((new FoodComponent.Builder())
					.hunger(6)
					.alwaysEdible()
					.saturationModifier(7.2F)
					.statusEffect(new StatusEffectInstance(StatusEffects.GLOWING,1200,0),1.0F)
					.build()));

	public static final Item Levitation_Apple = new Item(new Item.Settings()
			.food((new FoodComponent.Builder())
					.hunger(6)
					.alwaysEdible()
					.saturationModifier(7.2F)
					.statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION,400,0),1.0F)
					.build()));

	public static final Item Bad_Omen_Apple = new Item(new Item.Settings()
			.food((new FoodComponent.Builder())
					.hunger(6)
					.alwaysEdible()
					.saturationModifier(7.2F)
					.statusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN,400,0),1.0F)
					.build()));

	public static final Item Biscuit_Embryo = new Item(new Item.Settings()
			.group(GregFood_Group));

	public static final Item Dough = new Item(new Item.Settings()
			.group(GregFood_Group));

	public static final Item Dumpling = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Fried_Dumpling = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(3)
					.snack()
					.saturationModifier(0.5F)
					.build()));

	public static final Item Biscuit = new Item(new Item.Settings()
			.food((new FoodComponent.Builder())
					.hunger(4)
					.snack()
					.saturationModifier(4.0F)
					.build())
			.group(GregFood_Group));

	public static final Item Salt = new Item(new Item.Settings()
			.group(GregFood_Group));

	public static final Item Salt_bucket = new SaltBucketItem(new Item.Settings()
			.group(GregFood_Group)
			.recipeRemainder(Items.BUCKET)
			.food((new FoodComponent.Builder())
					.hunger(8)
					.saturationModifier(5.0F)
					.statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,12000,2),0.25F)
					.build())
			.maxCount(1));

	public static final Item Golden_Bean = new GoldenBeanItem(new Item.Settings()
			.group(GregFood_Group).
					food((new FoodComponent.Builder())
							.hunger(6)
							.saturationModifier(3.0F)
							.snack()
							.build())
			.maxCount(1));

	public static final Item Bottle_of_Soybeans = new Item(new Item.Settings()
			.group(GregFood_Group)
			.recipeRemainder(Items.GLASS_BOTTLE));

	public static final Item Bottle_of_Meat = new Item(new Item.Settings()
			.group(GregFood_Group)
			.recipeRemainder(Items.GLASS_BOTTLE));

	public static final Item Oil_Bottle = new Item(new Item.Settings()
			.group(GregFood_Group)
			.recipeRemainder(Items.GLASS_BOTTLE));

	public static final Item uncooked_RouJiaMo = new Item(new Item.Settings()
			.group(GregFood_Group)
			.maxCount(16));

	public static final Item RouJiaMo = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
							.hunger(6)
							.saturationModifier(3.0F)
							.meat()
							.build())
			.maxCount(16));

	public static final Item Cumin = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item uncooked_Kebab_Mutton = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Kebab_Mutton = new KebabMuttonItem(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(3)
					.saturationModifier(1.0F)
					.meat()
					.build()));

    public static final Item uncooked_Fried_Egg = new Item(new Item.Settings().group(GregFood_Group).maxCount(1));

    public static final Item Cooked_Fried_Egg = new CookedFriedEggItem(new Item.Settings()
			.group(GregFood_Group)
			.maxCount(1)
			.recipeRemainder(gregfood.Pot)
			.food((new FoodComponent.Builder())
					.hunger(9)
					.saturationModifier(4.0F)
					.build()));

    public static final Item Fried_Egg = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(3)
					.saturationModifier(1.0F)
					.meat()
					.build()));

	public static final Item Tea_cup = new TeaCupItem(new Item.Settings()
			.group(GregFood_Group)
			.maxCount(16)
			.food((new FoodComponent.Builder())
					.hunger(4)
					.saturationModifier(3.0F)
					.build()));

	public static final Item Milk_tea = new MilkTeaItem(new Item.Settings()
			.maxCount(16)
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(6)
					.saturationModifier(3.0F)
					.build()));

	public static final Item Ginger_tea = new GingerTeaItem(new Item.Settings()
			.maxCount(16)
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(6)
					.saturationModifier(3.0F)
					.build()));

	public static final Item Noodle = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Wheat_Flour = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item rice = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item rice_husk = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Uncooked_rice = new Item(new Item.Settings().group(GregFood_Group).recipeRemainder(Items.BOWL));

	public static final Item Steamed_rice = new SteamedRiceItem(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(6)
					.saturationModifier(3.0F)
					.build()));

	public static final Item Plate = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Uncooked_fried_noodles = new Item(new Item.Settings().group(GregFood_Group).maxCount(1));

	public static final Item Fried_Noodles = new FriedNoodlesItem(new Item.Settings()
			.group(GregFood_Group)
			.maxCount(1)
			.food((new FoodComponent.Builder())
					.hunger(8)
					.saturationModifier(4.0F)
					.build()));

	public static final Item Uncooked_fried_rice = new Item(new Item.Settings().maxCount(1).group(GregFood_Group));

	public static final Item Fried_Rice = new FriedRiceItem(new Item.Settings()
			.group(GregFood_Group)
			.maxCount(1)
			.food((new FoodComponent.Builder())
					.hunger(8)
					.saturationModifier(4.0F)
					.build()));

	public static final Item Uncooked_Tea_Egg = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Tea_Egg = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(4)
					.snack()
					.saturationModifier(1.0F)
					.build()));

	public static final Item Tea_Seed = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Bottle_of_Tea_Seeds = new Item(new Item.Settings()
			.group(GregFood_Group)
			.recipeRemainder(Items.GLASS_BOTTLE));

	public static final Item Tea_Root = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Brining_Ingredient = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Brined_brining_Ingredient = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(6).snack().alwaysEdible()
					.saturationModifier(4.0F)
					.statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,100,3),1.0F)
					.build()));

	public static final Item Brined_turnip = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(4)
					.snack()
					.saturationModifier(1.0F)
					.build()));

	public static final Item Brined_pork = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(5)
					.saturationModifier(3.0F)
					.build()));

	public static final Item CutGinger = new Item(new Item.Settings().group(GregFood_Group));

    public static final Item CutGarlic = new Item(new Item.Settings().group(GregFood_Group));

	public static final Item Fried_shell = new Item(new Item.Settings()
			.group(GregFood_Group)
			.food((new FoodComponent.Builder())
					.hunger(5)
					.saturationModifier(3.0F)
					.build()));

    //blocks
	public static final Block Biscuit_block = new BiscuitBlock(FabricBlockSettings.of(Material.STONE).strength(1.0F,1.0F).sounds(BlockSoundGroup.SNOW));
	public static final Block Salt_block = new SaltBlock(FabricBlockSettings.of(Material.SNOW_BLOCK).strength(1.0F,1.0F).sounds(BlockSoundGroup.SNOW));
	public static final Block Dough_block = new HoneyBlock(FabricBlockSettings.of(Material.SNOW_BLOCK).strength(0.5F,0.5F).sounds(BlockSoundGroup.HONEY).jumpVelocityMultiplier(0.1F).velocityMultiplier(0.3F));
	public static final Block Elastic_Dough_block = new SlimeBlock(FabricBlockSettings.of(Material.SNOW_BLOCK).strength(0.5F,0.5F).sounds(BlockSoundGroup.SLIME).jumpVelocityMultiplier(4.0F).velocityMultiplier(0.7F));
	public static final Block TeaRooted_dirt = new TeaRootedDirtBlock(FabricBlockSettings.of(Material.SOIL).strength(1.0F,1.0F).sounds(BlockSoundGroup.ROOTS));
	public static final Block GingerRooted_dirt = new GingerRootedDirtBlock(FabricBlockSettings.of(Material.SOIL).strength(1.0F,1.0F).sounds(BlockSoundGroup.ROOTS));
	public static final Block GarlicRooted_dirt = new GarlicRootedDirtBlock(FabricBlockSettings.of(Material.SOIL).strength(1.0F,1.0F).sounds(BlockSoundGroup.ROOTS));
	public static final Block ModerHead = new NoteBlock(FabricBlockSettings.of(Material.TNT).strength(5.0F,30.0F).sounds(BlockSoundGroup.BAMBOO).jumpVelocityMultiplier(40.0F));
	public static final Block Briner = new BrinerBlock(FabricBlockSettings.of(Material.STONE).strength(1.0F,1.0F).sounds(BlockSoundGroup.STONE));
	public static final Block Knife_Table = new KnifeTableBlock(FabricBlockSettings.of(Material.WOOD).strength(1.0F,1.0F).sounds(BlockSoundGroup.WOOD));
	public static final Block Grinder = new GrinderBlock(FabricBlockSettings.of(Material.STONE).strength(1.0F,1.0F).sounds(BlockSoundGroup.STONE));
	public static final Block Steamer = new SteamerBlock(FabricBlockSettings.of(Material.STONE).strength(1.0F,1.0F).sounds(BlockSoundGroup.STONE));

	//blockEntity
	public static BlockEntityType<GrinderBlockEntity> Grinder_Block_Entity;
	public static BlockEntityType<SteamerBlockEntity> Steamer_Block_Entity;

	//recipes
	public static RecipeType<GrindingRecipe> Grinding_Recipe_Type;
	public static GrindingRecipeSerializer<GrindingRecipe> Grinding_Recipe_Serializer;
	public static RecipeType<SteamingRecipe> Steaming_Recipe_Type;
	public static GrindingRecipeSerializer<SteamingRecipe> Steaming_Recipe_Serializer;
	public static RecipeType<CuttingRecipe> Cutting_Recipe_Type;
	public static AtOnceRecipeSerializer<CuttingRecipe> Cutting_Recipe_Serializer;
	public static RecipeType<BriningRecipe> Brining_Recipe_Type;
	public static AtOnceRecipeSerializer<BriningRecipe> Brining_Recipe_Serializer;

	//sounds
	public static final Identifier Knife_Table_Cut_Id = new Identifier("gregfood","knife_table_cut");
	public static final SoundEvent Knife_Table_Cut_Event = new SoundEvent(Knife_Table_Cut_Id);

	public static final Block Orange_tree_sapling = new GregFoodSaplingBlock(new OrangeSaplingGenerator(), Block.Settings.of(Material.PLANT).noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.GRASS));
	public static final Block Orange_tree_leaves = new OrangeLeavesBlock(FabricBlockSettings.of(Material.LEAVES).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque());
	public static final ConfiguredFeature<TreeFeatureConfig, ?> Orange_tree_feature = Feature.TREE.configure((new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.OAK_LOG.getDefaultState()), new WeightedBlockStateProvider().addState(gregfood.Orange_tree_leaves.getDefaultState(),1).addState(gregfood.Orange_tree_leaves.getDefaultState().with(OrangeLeavesBlock.FRUIT,true),1), new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3), new StraightTrunkPlacer(5, 2, 0), new TwoLayersFeatureSize(1, 0, 1))).ignoreVines().build());
	public static final ConfiguredFeature<?,?> Orange_tree_spawn_feature = Orange_tree_feature.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 2)));
	public static final Item Orange = new Item(new Item.Settings()
            .food((new FoodComponent.Builder())
                    .hunger(2)
                    .snack()
                    .saturationModifier(2.0F)
                    .build())
            .group(GregFood_Group));


	public static final EntityType<SeashellEntity> Seashell = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("gregfood", "seashell"),
			FabricEntityTypeBuilder.create(SpawnGroup.WATER_AMBIENT, SeashellEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.25f)).build()
	);
	public static final Item Seashell_spawn_egg = new SpawnEggItem(gregfood.Seashell, 4996656, 986895, (new Item.Settings()).group(GregFood_Group));
	public static final Item Shell = new Item(new Item.Settings().group(GregFood_Group));

	private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.asItem(), levelIncreaseChance);
	}
	private static void registerCompostFood() {
		registerCompostableItem(0.2F,gregfood.Dough);
		registerCompostableItem(0.3F,gregfood.Dumpling);
		registerCompostableItem(0.1F,gregfood.Biscuit_Embryo);
		registerCompostableItem(0.3F,gregfood.uncooked_RouJiaMo);
		registerCompostableItem(0.1F,gregfood.Cumin);
		registerCompostableItem(0.2F,gregfood.uncooked_Kebab_Mutton);
		registerCompostableItem(0.2F,gregfood.Noodle);
		registerCompostableItem(0.1F,gregfood.Wheat_Flour);
		registerCompostableItem(0.2F,gregfood.rice);
		registerCompostableItem(0.1F,gregfood.rice_husk);
		registerCompostableItem(0.2F,gregfood.Uncooked_Tea_Egg);
		registerCompostableItem(0.2F,gregfood.Tea_Seed);
		registerCompostableItem(0.1F,gregfood.Tea_Root);
		registerCompostableItem(0.7F,gregfood.Brining_Ingredient);
		registerCompostableItem(1.0F,gregfood.Brined_brining_Ingredient);
		registerCompostableItem(0.4F,gregfood.CutGinger);
		registerCompostableItem(0.3F,gregfood.TeaRooted_dirt);
		registerCompostableItem(0.2F,gregfood.green_onion_leaf);
		registerCompostableItem(0.1F,gregfood.green_onion);
		registerCompostableItem(0.2F,gregfood.soybean);
		registerCompostableItem(0.1F,gregfood.tea);
		registerCompostableItem(0.2F,gregfood.paddy);
		registerCompostableItem(0.2F,gregfood.turnip);
		registerCompostableItem(0.3F,gregfood.ginger);
		registerCompostableItem(0.3F,gregfood.garlic);
		registerCompostableItem(0.2F,gregfood.garlic_leaf);
		registerCompostableItem(0.2F,gregfood.Orange);
		registerCompostableItem(0.2F,gregfood.Orange_tree_leaves);
		registerCompostableItem(0.2F,gregfood.Orange_tree_sapling);
	}

	@Override
	public void onInitialize() {

		try
		{
			URL url = new URL("http://81.71.133.124/update.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream(), StandardCharsets.UTF_8));
			String data;
			while ((data = reader.readLine()) != null)
			{
				System.out.print("\n[GregFood]Now Version:"+verNum+"\n\n");
				if(!data.equals(verNum)){
					System.out.println("\n[GregFood]Found new version:"+data+"\n");
					hasNewVer = true;
					newVerNum = data;
				}
			}
			reader.close();
			url = new URL("http://81.71.133.124/announce.txt");
			reader = new BufferedReader(new InputStreamReader(
					url.openStream(), StandardCharsets.UTF_8));
			while ((data = reader.readLine()) != null)
			{
				Announcement = Announcement.concat(data+"\n");
			}
			reader.close();
			url = new URL("http://81.71.133.124/forceupdateann.txt");
			reader = new BufferedReader(new InputStreamReader(
					url.openStream(), StandardCharsets.UTF_8));
			while ((data = reader.readLine()) != null)
			{
				forceUpdateAnn = forceUpdateAnn.concat(data+"\n");
			}
			reader.close();
			url = new URL("http://81.71.133.124/forceupdate.txt");
			reader = new BufferedReader(new InputStreamReader(
					url.openStream(), StandardCharsets.UTF_8));
			while ((data = reader.readLine()) != null)
			{
				if(data.equals(verNum)){
					forceUpdate = true;
					System.out.println("[GregFood]This version you have now has serious bug so that it will force you to update it.");
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("[GregFood]Fail to load announcement and updating information resource!");
			e.printStackTrace();
		}

		System.out.println("\n????????????GregFood?????????Welcome to play GregFood!\nMCBBS??????:https://www.mcbbs.net/thread-1120123-1-1.html\n?????????GregTao???\nBiliBili???GregStone");

		FabricDefaultAttributeRegistry.register(Seashell, LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).add(EntityAttributes.GENERIC_MAX_HEALTH,6.0D));
		Registry.register(Registry.ITEM, new Identifier("gregfood","seashell_spawn_egg"),Seashell_spawn_egg);
		Registry.register(Registry.ITEM, new Identifier("gregfood","shell"),Shell);

		//sounds
		Registry.register(Registry.SOUND_EVENT,Knife_Table_Cut_Id,Knife_Table_Cut_Event);

		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,new Identifier("gregfood","orange_tree_feature"),Orange_tree_feature);
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,new Identifier("gregfood","orange_tree_spawn_feature"),Orange_tree_spawn_feature);
		Registry.register(Registry.BLOCK, new Identifier("gregfood", "orange_tree_sapling"), Orange_tree_sapling);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "orange_tree_sapling"), new BlockItem(Orange_tree_sapling,new Item.Settings().group(GregFood_Group)));
		Registry.register(Registry.BLOCK, new Identifier("gregfood", "orange_tree_leaves"), Orange_tree_leaves);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "orange_tree_leaves"), new BlockItem(Orange_tree_leaves,new Item.Settings().group(GregFood_Group)));
        Registry.register(Registry.ITEM, new Identifier("gregfood","orange"),Orange);

		//tools
		Registry.register(Registry.ITEM,new Identifier("gregfood","weeding_shovel"), WEEDING_SHOVEL);
		Registry.register(Registry.ITEM,new Identifier("gregfood","pot"),Pot);
		Registry.register(Registry.ITEM,new Identifier("gregfood","kitchen_knife"),Kitchen_Knife);

		//ore
		Registry.register(Registry.BLOCK, new Identifier("gregfood", "salt_ore"), Salt_Ore);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "salt_ore"), new BlockItem(Salt_Ore, new Item.Settings().group(GregFood_Group)));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier("gregfood", "salt_ores"), Salt_Ores);

		//register item
		Registry.register(Registry.ITEM, new Identifier("gregfood", "glowing_apple"), Glowing_Apple);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "levitation_apple"), Levitation_Apple);
		Registry.register(Registry.ITEM, new Identifier("gregfood","bad_omen_apple"),Bad_Omen_Apple);
		Registry.register(Registry.ITEM, new Identifier("gregfood","biscuit"),Biscuit);
		Registry.register(Registry.ITEM, new Identifier("gregfood","dough"),Dough);
		Registry.register(Registry.ITEM, new Identifier("gregfood","dumpling"),Dumpling);
		Registry.register(Registry.ITEM, new Identifier("gregfood","fried_dumpling"),Fried_Dumpling);
		Registry.register(Registry.ITEM, new Identifier("gregfood","biscuit_embryo"),Biscuit_Embryo);
		Registry.register(Registry.ITEM, new Identifier("gregfood","salt"),Salt);
		Registry.register(Registry.ITEM, new Identifier("gregfood","salt_bucket"),Salt_bucket);
		Registry.register(Registry.ITEM, new Identifier("gregfood","golden_bean"),Golden_Bean);
		Registry.register(Registry.ITEM, new Identifier("gregfood","bottle_of_soybeans"),Bottle_of_Soybeans);
		Registry.register(Registry.ITEM, new Identifier("gregfood","bottle_of_meat"),Bottle_of_Meat);
		Registry.register(Registry.ITEM, new Identifier("gregfood","oil_bottle"),Oil_Bottle);
		Registry.register(Registry.ITEM, new Identifier("gregfood","roujiamo"),RouJiaMo);
		Registry.register(Registry.ITEM, new Identifier("gregfood","uncooked_roujiamo"),uncooked_RouJiaMo);
		Registry.register(Registry.ITEM, new Identifier("gregfood","cumin"),Cumin);
		Registry.register(Registry.ITEM, new Identifier("gregfood","uncooked_kebab_mutton"),uncooked_Kebab_Mutton);
		Registry.register(Registry.ITEM, new Identifier("gregfood","kebab_mutton"),Kebab_Mutton);
		Registry.register(Registry.ITEM, new Identifier("gregfood","uncooked_fried_egg"),uncooked_Fried_Egg);
		Registry.register(Registry.ITEM, new Identifier("gregfood","cooked_fried_egg"),Cooked_Fried_Egg);
		Registry.register(Registry.ITEM, new Identifier("gregfood","fried_egg"),Fried_Egg);
		Registry.register(Registry.ITEM, new Identifier("gregfood","noodle"),Noodle);
		Registry.register(Registry.ITEM, new Identifier("gregfood","wheat_flour"),Wheat_Flour);
		Registry.register(Registry.ITEM, new Identifier("gregfood","tea_cup"),Tea_cup);
		Registry.register(Registry.ITEM, new Identifier("gregfood","milk_tea"),Milk_tea);
		Registry.register(Registry.ITEM, new Identifier("gregfood","ginger_tea"),Ginger_tea);
		Registry.register(Registry.ITEM, new Identifier("gregfood","rice"),rice);
		Registry.register(Registry.ITEM, new Identifier("gregfood","rice_husk"),rice_husk);
		Registry.register(Registry.ITEM, new Identifier("gregfood","uncooked_rice"),Uncooked_rice);
		Registry.register(Registry.ITEM, new Identifier("gregfood","steamed_rice"),Steamed_rice);
		Registry.register(Registry.ITEM, new Identifier("gregfood","uncooked_fried_rice"),Uncooked_fried_rice);
		Registry.register(Registry.ITEM, new Identifier("gregfood","fried_rice"),Fried_Rice);
		Registry.register(Registry.ITEM, new Identifier("gregfood","plate"),Plate);
		Registry.register(Registry.ITEM, new Identifier("gregfood","uncooked_fried_noodles"),Uncooked_fried_noodles);
		Registry.register(Registry.ITEM, new Identifier("gregfood","fried_noodles"),Fried_Noodles);
		Registry.register(Registry.ITEM, new Identifier("gregfood","uncooked_tea_egg"),Uncooked_Tea_Egg);
		Registry.register(Registry.ITEM, new Identifier("gregfood","tea_egg"),Tea_Egg);
		Registry.register(Registry.ITEM, new Identifier("gregfood","tea_seed"),Tea_Seed);
		Registry.register(Registry.ITEM, new Identifier("gregfood","bottle_of_tea_seeds"),Bottle_of_Tea_Seeds);
		Registry.register(Registry.ITEM, new Identifier("gregfood","tea_root"),Tea_Root);
		Registry.register(Registry.ITEM, new Identifier("gregfood","brining_ingredient"),Brining_Ingredient);
		Registry.register(Registry.ITEM, new Identifier("gregfood","brined_brining_ingredient"),Brined_brining_Ingredient);
		Registry.register(Registry.ITEM, new Identifier("gregfood","brined_turnip"),Brined_turnip);
		Registry.register(Registry.ITEM, new Identifier("gregfood","brined_pork"),Brined_pork);
		Registry.register(Registry.ITEM, new Identifier("gregfood","cut_ginger"),CutGinger);
		Registry.register(Registry.ITEM, new Identifier("gregfood","cut_garlic"),CutGarlic);
		Registry.register(Registry.ITEM, new Identifier("gregfood","fried_shell"),Fried_shell);

		//blocks
		Registry.register(Registry.BLOCK, new Identifier("gregfood", "biscuit_block"), Biscuit_block);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "biscuit_block"), new BlockItem(Biscuit_block, new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "salt_block"), Salt_block);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "salt_block"), new BlockItem(Salt_block, new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "dough_block"), Dough_block);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "dough_block"), new BlockItem(Dough_block, new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "elastic_dough_block"), Elastic_Dough_block);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "elastic_dough_block"), new BlockItem(Elastic_Dough_block, new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "moder_head"), ModerHead);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "moder_head"), new BlockItem(ModerHead,new Item.Settings().maxCount(128)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "tea_rooted_dirt"), TeaRooted_dirt);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "tea_rooted_dirt"), new BlockItem(TeaRooted_dirt,new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "ginger_rooted_dirt"), GingerRooted_dirt);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "ginger_rooted_dirt"), new BlockItem(GingerRooted_dirt,new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "garlic_rooted_dirt"), GarlicRooted_dirt);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "garlic_rooted_dirt"), new BlockItem(GarlicRooted_dirt,new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "briner"), Briner);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "briner"), new BlockItem(Briner,new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "knife_table"), Knife_Table);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "knife_table"), new BlockItem(Knife_Table,new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "grinder"), Grinder);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "grinder"), new BlockItem(Grinder,new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "steamer"), Steamer);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "steamer"), new BlockItem(Steamer,new Item.Settings().group(GregFood_Group)));

		//blockEntity
		Grinder_Block_Entity = Registry.register(Registry.BLOCK_ENTITY_TYPE, "gregfood:grinder_block_entity", BlockEntityType.Builder.create(GrinderBlockEntity::new, Grinder).build(null));
		Steamer_Block_Entity = Registry.register(Registry.BLOCK_ENTITY_TYPE, "gregfood:steamer_block_entity", BlockEntityType.Builder.create(SteamerBlockEntity::new, Steamer).build(null));

		Grinding_Recipe_Type = Registry.register(Registry.RECIPE_TYPE, "gregfood:grinding", new RecipeType<GrindingRecipe>() {
			public String toString() {
				return "gregfood:grinding";
			}
		});
		Steaming_Recipe_Type = Registry.register(Registry.RECIPE_TYPE, "gregfood:steaming", new RecipeType<SteamingRecipe>() {
			public String toString() {
				return "gregfood:steaming";
			}
		});
		Cutting_Recipe_Type = Registry.register(Registry.RECIPE_TYPE, "gregfood:cutting", new RecipeType<CuttingRecipe>() {
			public String toString() {
				return "gregfood:cutting";
			}
		});
		Brining_Recipe_Type = Registry.register(Registry.RECIPE_TYPE, "gregfood:brining", new RecipeType<BriningRecipe>() {
			public String toString() {
				return "gregfood:brining";
			}
		});

		Grinding_Recipe_Serializer = Registry.register(Registry.RECIPE_SERIALIZER,"gregfood:grinding",new GrindingRecipeSerializer<>(GrindingRecipe::new,100,1));
		Steaming_Recipe_Serializer = Registry.register(Registry.RECIPE_SERIALIZER,"gregfood:steaming",new GrindingRecipeSerializer<>(SteamingRecipe::new,100,1));
		Cutting_Recipe_Serializer = Registry.register(Registry.RECIPE_SERIALIZER,"gregfood:cutting",new AtOnceRecipeSerializer<>(CuttingRecipe::new,1));
		Brining_Recipe_Serializer = Registry.register(Registry.RECIPE_SERIALIZER,"gregfood:brining",new AtOnceRecipeSerializer<>(BriningRecipe::new,1));

		//crop
		Registry.register(Registry.BLOCK, new Identifier("gregfood", "green_onion"), green_onion);
		Registry.register(Registry.ITEM, new Identifier("gregfood","green_onion_leaf"),green_onion_leaf);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "green_onion"), new BlockItem(green_onion, new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "soybean"), soybean);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "soybean"), new BlockItem(soybean, new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "tea"),tea);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "tea"), new BlockItem(tea, new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "paddy"),paddy);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "paddy"), new BlockItem(paddy, new Item.Settings().group(GregFood_Group)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "turnip"), turnip);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "turnip"), new BlockItem(turnip, new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "ginger"),ginger);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "ginger"), new BlockItem(ginger, new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT)));

		Registry.register(Registry.BLOCK, new Identifier("gregfood", "garlic"),garlic);
		Registry.register(Registry.ITEM, new Identifier("gregfood", "garlic"), new BlockItem(garlic, new Item.Settings().food((new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).build()).group(GregFood_Group).food(FoodComponents.CARROT)));
		Registry.register(Registry.ITEM, new Identifier("gregfood","garlic_leaf"),garlic_leaf);

		registerCompostFood();
	}

}
