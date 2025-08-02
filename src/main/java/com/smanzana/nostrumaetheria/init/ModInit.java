package com.smanzana.nostrumaetheria.init;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.capability.IAetherAccepter;
import com.smanzana.nostrumaetheria.api.capability.IAetherBurnable;
import com.smanzana.nostrumaetheria.api.lib.AetheriaResearches;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherUnravelerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrumaetheria.items.ItemAetherLens;
import com.smanzana.nostrumaetheria.items.ItemAetherLens.LensType;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.research.AetheriaResearchesImpl;
import com.smanzana.nostrumaetheria.rituals.OutcomeCreateAetherInfuser;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.block.NostrumBlocks;
import com.smanzana.nostrummagica.crafting.NostrumTags;
import com.smanzana.nostrummagica.effect.NostrumPotions;
import com.smanzana.nostrummagica.effect.NostrumPotions.PotionIngredient;
import com.smanzana.nostrummagica.item.NostrumItems;
import com.smanzana.nostrummagica.item.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.progression.requirement.ResearchRequirement;
import com.smanzana.nostrummagica.progression.research.NostrumResearchTab;
import com.smanzana.nostrummagica.ritual.RitualRecipe;
import com.smanzana.nostrummagica.ritual.RitualRegistry;
import com.smanzana.nostrummagica.ritual.outcome.OutcomeSpawnItem;
import com.smanzana.nostrummagica.spell.EMagicElement;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Common (client and server) handler for MOD bus events.
 * MOD bus is not game event bus.
 * @author Skyler
 *
 */
@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModInit {

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		// EARLY phase:
		////////////////////////////////////////////
				
    	// NOTE: These registering methods are on the regular gameplay BUS,
    	// because they depend on data and re-fire when data is reloaded?
		MinecraftForge.EVENT_BUS.addListener(ModInit::registerRituals);
		
		preinit();
		NostrumAetheria.curios.preInit();
		
		
		// MID phase:
		////////////////////////////////////////////
		// Register rituals, quests, etc. after item and block init
		//registerQuests();
		//registerTrials();
		init();
		NostrumAetheria.curios.init();
	
		// LATE phase:
		//////////////////////////////////////////
		// Used to be two different mod init steps!
		
		postinit();
		//NostrumAetheria.instance.curios.postInit();
	}
	
	private static final void preinit() {
		NetworkHandler.getInstance();
	}
	
	private static final void init() {
		APIProxy.AetherResearchTab = new NostrumResearchTab("aether", () -> new ItemStack(AetheriaItems.aetherGem));
    	APIProxy.AetherGearResearchTab = new NostrumResearchTab("aether_gear", () -> new ItemStack(AetheriaItems.aetherSightTool));
		AetheriaResearchesImpl.init();
		AetherRepairerBlock.initDefaultRecipes();
		AetherUnravelerBlock.initDefaultRecipes();
		NostrumMagica.instance.registerResearchReloadHook(AetheriaResearchesImpl::init);
	}
	
	@SubscribeEvent
	public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IAetherBurnable.class);
		event.register(IAetherAccepter.class);
	}
	
	private static final void postinit() {
		
	}
	
    public static final void registerRituals(RitualRegistry.RitualRegisterEvent event) {
    	final RitualRegistry registry = event.registry;
    	
    	registry.register(
				RitualRecipe.createTier3("active_pendant",
						new ItemStack(AetheriaItems.activePendant),
						null,
						new ReagentType[] {ReagentType.GINSENG, ReagentType.SPIDER_SILK, ReagentType.MANI_DUST, ReagentType.MANI_DUST},
						Ingredient.of(NostrumItems.thanoPendant),
						new Ingredient[] {
								Ingredient.of(NostrumTags.Items.CrystalSmall),
								Ingredient.of(NostrumTags.Items.InfusedGemFire), Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS), Ingredient.of(NostrumTags.Items.CrystalSmall)},
						new ResearchRequirement(AetheriaResearches.ID_Active_Pendant),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.activePendant))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("passive_pendant",
						new ItemStack(AetheriaItems.passivePendant),
						null,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.SKY_ASH, ReagentType.MANI_DUST, ReagentType.SKY_ASH},
						Ingredient.of(AetheriaItems.activePendant),
						new Ingredient[]{Ingredient.of(AetheriaItems.ginsengFlower), Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.of(NostrumTags.Items.SpriteCore), Ingredient.of(AetheriaItems.mandrakeFlower)},
						new ResearchRequirement(AetheriaResearches.ID_Passive_Pendant),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.passivePendant))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_furnace_small",
						new ItemStack(AetheriaBlocks.smallFurnace),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						Ingredient.of(Blocks.FURNACE),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.ReagentManiDust), Ingredient.of(NostrumTags.Items.SpriteCore), Ingredient.of(Tags.Items.OBSIDIAN), Ingredient.of(NostrumTags.Items.ReagentManiDust)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Furnace),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.smallFurnace))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_furnace_medium",
						new ItemStack(AetheriaBlocks.mediumFurnace),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						Ingredient.of(AetheriaBlocks.smallFurnace),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(NostrumTags.Items.ReagentManiDust), Ingredient.of(Items.FURNACE), Ingredient.of(NostrumTags.Items.CrystalSmall)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Furnace_Adv),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.mediumFurnace))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_furnace_large",
						new ItemStack(AetheriaBlocks.largeFurnace),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						Ingredient.of(AetheriaBlocks.mediumFurnace),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(NostrumTags.Items.ReagentManiDust), Ingredient.of(Items.FURNACE), Ingredient.of(NostrumTags.Items.CrystalSmall)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Furnace_Adv),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.largeFurnace))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_boiler",
						new ItemStack(AetheriaBlocks.boiler),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.BLACK_PEARL},
						Ingredient.of(AetheriaBlocks.smallFurnace),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(Tags.Items.INGOTS_IRON), Ingredient.of(Items.CAULDRON), Ingredient.of(NostrumTags.Items.CrystalSmall)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Boiler),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.boiler))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_bath",
						new ItemStack(AetheriaBlocks.bath),
						null,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.BLACK_PEARL},
						Ingredient.of(NostrumBlocks.pedestal),
						new Ingredient[]{Ingredient.of(Tags.Items.STONE), Ingredient.of(Items.BUCKET), Ingredient.of(NostrumTags.Items.SpriteCore), Ingredient.of(Tags.Items.STONE)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Bath),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.bath))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_charger",
						new ItemStack(AetheriaBlocks.charger),
						EMagicElement.ICE,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.MANDRAKE_ROOT},
						Ingredient.of(AetheriaBlocks.bath),
						new Ingredient[]{Ingredient.of(Tags.Items.STONE), Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(Items.CAULDRON), Ingredient.of(Tags.Items.STONE)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Charger),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.charger))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_repairer",
						new ItemStack(AetheriaBlocks.repairer),
						EMagicElement.EARTH,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.GINSENG, ReagentType.SKY_ASH, ReagentType.GRAVE_DUST},
						Ingredient.of(AetheriaBlocks.charger),
						new Ingredient[]{Ingredient.of(Tags.Items.OBSIDIAN), Ingredient.of(NostrumTags.Items.CrystalLarge), Ingredient.of(Tags.Items.OBSIDIAN), Ingredient.of(Tags.Items.OBSIDIAN)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Repairer),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.repairer))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_unraveler",
						new ItemStack(AetheriaBlocks.unraveler),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.SKY_ASH, ReagentType.MANI_DUST, ReagentType.GINSENG},
						Ingredient.of(AetheriaBlocks.charger),
						new Ingredient[]{Ingredient.of(Tags.Items.OBSIDIAN), Ingredient.of(NostrumTags.Items.CrystalLarge), Ingredient.of(Items.MAGMA_BLOCK), Ingredient.of(Tags.Items.OBSIDIAN)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Unraveler),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.unraveler))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_small",
						new ItemStack(AetheriaBlocks.smallBattery),
						null,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.SPIDER_SILK, ReagentType.GINSENG},
						Ingredient.of(Blocks.GLASS),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.ReagentManiDust), Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(NostrumTags.Items.SpriteCore), Ingredient.of(NostrumTags.Items.ReagentManiDust)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Battery),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.smallBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_medium",
						new ItemStack(AetheriaBlocks.mediumBattery),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						Ingredient.of(AetheriaBlocks.smallBattery),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(Tags.Items.GLASS), Ingredient.of(Tags.Items.STONE), Ingredient.of(NostrumTags.Items.CrystalSmall)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Battery),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.mediumBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_large",
						new ItemStack(AetheriaBlocks.largeBattery),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						Ingredient.of(AetheriaBlocks.mediumBattery),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(AetheriaBlocks.mediumBattery), Ingredient.of(Tags.Items.OBSIDIAN), Ingredient.of(NostrumTags.Items.CrystalSmall)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Battery_Adv),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.largeBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_giant",
						new ItemStack(AetheriaBlocks.giantBattery),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						Ingredient.of(AetheriaBlocks.largeBattery),
						new Ingredient[]{Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.of(AetheriaBlocks.largeBattery), Ingredient.of(Tags.Items.END_STONES), Ingredient.of(NostrumTags.Items.CrystalMedium)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Battery_Adv),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.giantBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_gem",
						new ItemStack(AetheriaItems.aetherGem),
						null,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT, ReagentType.GRAVE_DUST},
						Ingredient.of(NostrumTags.Items.CrystalLarge),
						new Ingredient[]{Ingredient.of(Tags.Items.ENDER_PEARLS), Ingredient.of(NostrumTags.Items.SpriteCore), Ingredient.of(NostrumTags.Items.ReagentManiDust), Ingredient.of(Tags.Items.ENDER_PEARLS)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Gem),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.aetherGem))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_relay",
						new ItemStack(AetheriaBlocks.relay),
						EMagicElement.ENDER,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.CRYSTABLOOM, ReagentType.CRYSTABLOOM, ReagentType.BLACK_PEARL},
						Ingredient.of(Blocks.REDSTONE_TORCH),
						new Ingredient[]{Ingredient.of(Tags.Items.ENDER_PEARLS), Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.of(NostrumItems.positionCrystal), Ingredient.of(Tags.Items.DUSTS_REDSTONE)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Relay),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.relay, 8))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("enhanced_aether_relay",
						new ItemStack(AetheriaBlocks.enhancedRelay),
						null,
						new ReagentType[] {ReagentType.GINSENG, ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.GRAVE_DUST},
						Ingredient.of(AetheriaBlocks.relay),
						new Ingredient[]{Ingredient.of(Tags.Items.DUSTS_REDSTONE), Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Relay_Enhanced),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.enhancedRelay, 8))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("enhanced_aether_relay_direct", "enhanced_aether_relay",
						new ItemStack(AetheriaBlocks.enhancedRelay),
						EMagicElement.ENDER,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.CRYSTABLOOM, ReagentType.GINSENG, ReagentType.BLACK_PEARL},
						Ingredient.of(Blocks.REDSTONE_TORCH),
						new Ingredient[]{Ingredient.of(Tags.Items.ENDER_PEARLS), Ingredient.of(NostrumTags.Items.CrystalMedium), Ingredient.of(NostrumItems.positionCrystal), Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Relay_Enhanced),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.enhancedRelay, 64))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_cart",
						new ItemStack(AetheriaItems.aetherBatteryMinecart),
						null,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.BLACK_PEARL},
						Ingredient.of(AetheriaBlocks.mediumBattery),
						new Ingredient[]{Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.of(Items.MINECART), Ingredient.EMPTY},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Carts),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.aetherBatteryMinecart, 1))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_pump",
						new ItemStack(AetheriaBlocks.pump),
						null,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.BLACK_PEARL},
						Ingredient.of(Blocks.HOPPER),
						new Ingredient[]{Ingredient.of(Tags.Items.INGOTS_IRON), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Items.CAULDRON), Ingredient.of(Tags.Items.INGOTS_IRON)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Carts),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.pump, 1))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("make_lens_holder",
						new ItemStack(AetheriaBlocks.lensHolder),
						null,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANDRAKE_ROOT, ReagentType.MANI_DUST, ReagentType.SPIDER_SILK},
						Ingredient.of(NostrumBlocks.pedestal),
						new Ingredient[] {Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.EMPTY},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Infusers),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.lensHolder, 8))
				)
			);
		
		for (LensType type : LensType.values()) {
			Ingredient ingredient = type.getIngredient();
			if (ingredient == Ingredient.EMPTY) {
				continue;
			}
			
			registry.register(
				RitualRecipe.createTier3("make_" + type.getUnlocSuffix(),
						new ItemStack(ItemAetherLens.GetLens(type)),
						null,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.MANI_DUST, ReagentType.SPIDER_SILK},
						Ingredient.of(Tags.Items.GLASS_PANES),
						new Ingredient[] {Ingredient.EMPTY, ingredient, Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.EMPTY},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Infusers),
						new OutcomeSpawnItem(new ItemStack(ItemAetherLens.GetLens(type)))
				)
			);
		}
		
		registry.register(
				RitualRecipe.createTier3("wisp_crystal",
						new ItemStack(AetheriaBlocks.wispBlock),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.MANI_DUST, ReagentType.MANI_DUST, ReagentType.MANI_DUST},
						Ingredient.of(NostrumBlocks.pedestal),
						new Ingredient[] {
								Ingredient.of(NostrumTags.Items.CrystalMedium),
								Ingredient.of(AetheriaBlocks.smallBattery),
								Ingredient.of(Tags.Items.OBSIDIAN),
								Ingredient.of(NostrumTags.Items.CrystalMedium),
								},
						new ResearchRequirement(AetheriaResearches.ID_WispBlock),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.wispBlock))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("construct_aether_infuser",
						new ItemStack(AetheriaBlocks.infuser),
						null,
						new ReagentType[] {ReagentType.GINSENG, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.SPIDER_SILK},
						Ingredient.of(Tags.Items.OBSIDIAN),
						new Ingredient[] {
								Ingredient.of(new ItemStack(AetheriaBlocks.smallBattery)),
								Ingredient.of(NostrumTags.Items.CrystalMedium),
								Ingredient.of(new ItemStack(AetheriaBlocks.smallBattery)),
								Ingredient.of(new ItemStack(AetheriaBlocks.smallBattery))},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Infusers),
						new OutcomeCreateAetherInfuser()
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_sight_item",
						new ItemStack(AetheriaItems.aetherSightTool),
						EMagicElement.LIGHTNING,
						new ReagentType[] {ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG, ReagentType.SPIDER_SILK},
						Ingredient.of(Tags.Items.GLASS_PANES),
						new Ingredient[]{Ingredient.of(Tags.Items.NUGGETS_GOLD), Ingredient.of(NostrumTags.Items.CrystalSmall), Ingredient.of(Tags.Items.NUGGETS_GOLD), Ingredient.of(Tags.Items.NUGGETS_GOLD)},
						new ResearchRequirement(AetheriaResearches.ID_Aether_Sight_Item),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.aetherSightTool))
						)
				);
    }
    
    @SubscribeEvent
   	public static final void register(RegistryEvent.Register<Potion> event) {
   		registerPotionMixes();
   	}
   	
   	protected static final void registerPotionMixes() {
   		// Mana regen potion
   		BrewingRecipeRegistry.addRecipe(new PotionIngredient(NostrumPotions.MANAREGEN_STRONG.getType()),
       			Ingredient.of(AetheriaItems.mandrakeFlower),
       			NostrumPotions.MakePotion(NostrumPotions.MANAREGEN_REALLY_STRONG.getType()));
   		
   		BrewingRecipeRegistry.addRecipe(new PotionIngredient(NostrumPotions.MANAREGEN_STRONG.getType()),
       			Ingredient.of(AetheriaItems.ginsengFlower),
       			NostrumPotions.MakePotion(NostrumPotions.MANAREGEN_STRONG_AND_LONG.getType()));
   	}
       
//       private void registerLore() {
//       	LoreRegistry.instance().register(AetherBathBlock.instance());
//       	LoreRegistry.instance().register(AetherBatteryBlock.small());
//       	LoreRegistry.instance().register(AetherBoilerBlock.instance());
//       	LoreRegistry.instance().register(AetherChargerBlock.instance());
//       	LoreRegistry.instance().register(AetherFurnaceBlock.instance());
//       	LoreRegistry.instance().register(AetherRelay.instance());
//       	LoreRegistry.instance().register(AetherRepairerBlock.instance());
//       	LoreRegistry.instance().register(ActivePendant.instance());
//       	LoreRegistry.instance().register(PassivePendant.instance());
//       	LoreRegistry.instance().register(AetherGem.instance());
//       	LoreRegistry.instance().register(AetherUnravelerBlock.instance());
//       	LoreRegistry.instance().register(AetherPumpBlock.instance());
//       }
	
}
