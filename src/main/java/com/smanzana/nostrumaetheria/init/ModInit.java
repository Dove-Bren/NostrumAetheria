package com.smanzana.nostrumaetheria.init;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherUnravelerBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrumaetheria.items.ItemAetherLens;
import com.smanzana.nostrumaetheria.items.ItemAetherLens.LensType;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrumaetheria.rituals.OutcomeCreateAetherInfuser;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.crafting.NostrumTags;
import com.smanzana.nostrummagica.effects.NostrumPotions;
import com.smanzana.nostrummagica.effects.NostrumPotions.PotionIngredient;
import com.smanzana.nostrummagica.entity.EntityWisp;
import com.smanzana.nostrummagica.items.NostrumItems;
import com.smanzana.nostrummagica.items.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.research.NostrumResearch;
import com.smanzana.nostrummagica.research.NostrumResearch.NostrumResearchTab;
import com.smanzana.nostrummagica.research.NostrumResearch.Size;
import com.smanzana.nostrummagica.rituals.RitualRecipe;
import com.smanzana.nostrummagica.rituals.RitualRegistry;
import com.smanzana.nostrummagica.rituals.outcomes.OutcomeSpawnItem;
import com.smanzana.nostrummagica.rituals.requirements.RRequirementResearch;
import com.smanzana.nostrummagica.spells.EMagicElement;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
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
		registerResearch();
		AetherRepairerBlock.initDefaultRecipes();
		AetherUnravelerBlock.initDefaultRecipes();
		NostrumMagica.instance.registerResearchReloadHook((i) -> {
    		registerResearch();
    		return 0;
    	});
	}
	
	private static final void postinit() {
		
	}
	
	private static final void registerResearch() {
    	APIProxy.AetherResearchTab = new NostrumResearch.NostrumResearchTab("aether", new ItemStack(AetheriaItems.aetherGem));
    	APIProxy.AetherGearResearchTab = new NostrumResearch.NostrumResearchTab("aether_gear", new ItemStack(AetheriaItems.aetherSightTool));
    	
    	NostrumResearch.startBuilding()
			.hiddenParent("rituals")
			.hiddenParent("thano_pendant")
			.lore(NostrumItems.thanoPendant)
			.reference(AetheriaItems.activePendant)
			.reference("ritual::active_pendant", "ritual.active_pendant.name")
		.build("active_pendant", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 0, -1, true, new ItemStack(AetheriaItems.activePendant));
	
		NostrumResearch.startBuilding()
			.parent("active_pendant")
			.hiddenParent("aether_bath")
			.hiddenParent("kani")
			.lore((ILoreTagged) AetheriaItems.activePendant)
			.lore(AetheriaItems.mandrakeFlower)
			.reference(AetheriaItems.passivePendant)
			.reference("ritual::passive_pendant", "ritual.passive_pendant.name")
		.build("passive_pendant", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, -1, true, new ItemStack(AetheriaItems.passivePendant));
		
		NostrumResearch.startBuilding()
			.hiddenParent("active_pendant")
			.lore((ILoreTagged) AetheriaItems.activePendant)
			.reference("ritual::aether_furnace_small", "ritual.aether_furnace_small.name")
		.build("aether_furnace", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.GIANT, 0, 0, true, new ItemStack(AetheriaBlocks.smallFurnace));
		
		NostrumResearch.startBuilding()
			.parent("aether_furnace")
			.hiddenParent("aether_boiler")
			.reference("ritual::aether_furnace_medium", "ritual.aether_furnace_medium.name")
			.reference("ritual::aether_furnace_large", "ritual.aether_furnace_large.name")
		.build("aether_furnace_adv", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, -1, 1, true, new ItemStack(AetheriaBlocks.largeFurnace));
		
		NostrumResearch.startBuilding()
			.parent("aether_furnace")
			.lore((ILoreTagged) AetheriaItems.activePendant)
			.reference("ritual::aether_bath", "ritual.aether_bath.name")
		.build("aether_bath", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 2, 1, true, new ItemStack(AetheriaBlocks.bath));
		
		NostrumResearch.startBuilding()
			.parent("aether_bath")
			.parent("aether_furnace")
			.hiddenParent("aether_gem")
			.reference("ritual::aether_charger", "ritual.aether_charger.name")
		.build("aether_charger", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, 1, true, new ItemStack(AetheriaBlocks.charger));
		
		NostrumResearch.startBuilding()
			.parent("aether_charger")
			.hiddenParent("vani")
			.reference("ritual::aether_repairer", "ritual.aether_repairer.name")
		.build("aether_repairer", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, 2, true, new ItemStack(AetheriaBlocks.repairer));
		
		NostrumResearch.startBuilding()
			.parent("aether_repairer")
			.reference("ritual::aether_unraveler", "ritual.aether_unraveler.name")
		.build("aether_unraveler", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 1, 3, true, new ItemStack(AetheriaBlocks.unraveler));
		
		//aether_unraveler
		
		NostrumResearch.startBuilding()
			.parent("aether_furnace")
			.reference("ritual::aether_boiler", "ritual.aether_boiler.name")
		.build("aether_boiler", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, -2, 1, true, new ItemStack(AetheriaBlocks.boiler));
		
		NostrumResearch.startBuilding()
			.parent("aether_bath")
			.reference("ritual::aether_battery_small", "ritual.aether_battery_small.name")
			.reference("ritual::aether_battery_medium", "ritual.aether_battery_medium.name")
		.build("aether_battery", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.LARGE, 2, 2, true, new ItemStack(AetheriaBlocks.smallBattery));
		
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.hiddenParent("kani")
			.reference("ritual::aether_battery_large", "ritual.aether_battery_large.name")
			.reference("ritual::aether_battery_giant", "ritual.aether_battery_giant.name")
		.build("aether_battery_adv", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 4, 2, true, new ItemStack(AetheriaBlocks.giantBattery));
		
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.hiddenParent("vani")
			.reference("ritual::aether_gem", "ritual.aether_gem.name")
		.build("aether_gem", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 3, 1, true, new ItemStack(AetheriaItems.aetherGem));
		
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.hiddenParent("geogems")
			.hiddenParent("kani")
			.reference("ritual::aether_relay", "ritual.aether_relay.name")
		.build("aether_relay", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.LARGE, 2, 3, true, new ItemStack(AetheriaBlocks.relay));
		
		NostrumResearch.startBuilding()
			.parent("aether_relay")
			.reference("ritual::enhanced_aether_relay", "ritual.enhanced_aether_relay.name")
			.reference("ritual::enhanced_aether_relay_direct", "ritual.enhanced_aether_relay_direct.name")
		.build("enhanced_aether_relay", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 2, 4, true, new ItemStack(AetheriaBlocks.enhancedRelay));
		
		// TODO if 'pipes' get added for short-range aether transport, consider making that a requirement for pumps & rails and stuff
		// For example, pipes -> pumps -> rails with pumps as a separate thing?
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.parent("aether_relay")
			.reference("ritual::aether_battery_cart", "ritual.aether_battery_cart.name")
			.reference("ritual::aether_pump", "ritual.aether_pump.name")
		.build("aether_carts", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.GIANT, 3, 3, true, new ItemStack(AetheriaItems.aetherBatteryMinecart));
		
		NostrumResearch.startBuilding()
			.hiddenParent("kani")
			.hiddenParent("aether_battery")
			.lore(EntityWisp.LoreKey)
			.reference("ritual::wisp_crystal", "ritual.wisp_crystal.name")
		.build("wispblock", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, -3, 2, true, new ItemStack(AetheriaBlocks.wispBlock));
		
		NostrumResearch.startBuilding()
			.hiddenParent("aether_battery")
			.parent("aether_charger")
			.reference("ritual::construct_aether_infuser", "ritual.construct_aether_infuser.name")
			.reference("ritual::make_lens_spread", "ritual.make_lens_spread.name")
			.reference("ritual::make_lens_charge", "ritual.make_lens_wide_charge.name")
			.reference("ritual::make_lens_grow", "ritual.make_lens_grow.name")
			.reference("ritual::make_lens_heal", "ritual.make_lens_heal.name")
			.reference("ritual::make_lens_bore", "ritual.make_lens_bore.name")
			.reference("ritual::make_lens_elevator", "ritual.make_lens_elevator.name")
			.reference("ritual::make_lens_swiftness", "ritual.make_lens_swiftness.name")
		.build("aether_infusers", (NostrumResearchTab) APIProxy.AetherResearchTab, Size.NORMAL, 0, 2, true, new ItemStack(AetheriaBlocks.infuser));
		
		NostrumResearch.startBuilding()
			.hiddenParent("aether_relay")
			.reference("ritual::aether_sight_item", "ritual.aether_sight_item.name")
		.build("aether_sight_item", (NostrumResearchTab) APIProxy.AetherGearResearchTab, Size.NORMAL, 0, 0, true, new ItemStack(AetheriaItems.aetherSightTool));
    }
    
    public static final void registerRituals(RitualRegistry.RitualRegisterEvent event) {
    	final RitualRegistry registry = event.registry;
    	
    	registry.register(
				RitualRecipe.createTier3("active_pendant",
						new ItemStack(AetheriaItems.activePendant),
						null,
						new ReagentType[] {ReagentType.GINSENG, ReagentType.SPIDER_SILK, ReagentType.MANI_DUST, ReagentType.MANI_DUST},
						Ingredient.fromItems(NostrumItems.thanoPendant),
						new Ingredient[] {
								Ingredient.fromTag(NostrumTags.Items.CrystalSmall),
								Ingredient.fromTag(NostrumTags.Items.InfusedGemFire), Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_LAPIS), Ingredient.fromTag(NostrumTags.Items.CrystalSmall)},
						new RRequirementResearch("active_pendant"),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.activePendant))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("passive_pendant",
						new ItemStack(AetheriaItems.passivePendant),
						null,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.SKY_ASH, ReagentType.MANI_DUST, ReagentType.SKY_ASH},
						Ingredient.fromItems(AetheriaItems.activePendant),
						new Ingredient[]{Ingredient.fromItems(AetheriaItems.ginsengFlower), Ingredient.fromTag(NostrumTags.Items.CrystalMedium), Ingredient.fromTag(NostrumTags.Items.SpriteCore), Ingredient.fromItems(AetheriaItems.mandrakeFlower)},
						new RRequirementResearch("passive_pendant"),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.passivePendant))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_furnace_small",
						new ItemStack(AetheriaBlocks.smallFurnace),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						Ingredient.fromItems(Blocks.FURNACE),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.ReagentManiDust), Ingredient.fromTag(NostrumTags.Items.SpriteCore), Ingredient.fromTag(Tags.Items.OBSIDIAN), Ingredient.fromTag(NostrumTags.Items.ReagentManiDust)},
						new RRequirementResearch("aether_furnace"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.smallFurnace))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_furnace_medium",
						new ItemStack(AetheriaBlocks.mediumFurnace),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						Ingredient.fromItems(AetheriaBlocks.smallFurnace),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromTag(NostrumTags.Items.ReagentManiDust), Ingredient.fromItems(Items.FURNACE), Ingredient.fromTag(NostrumTags.Items.CrystalSmall)},
						new RRequirementResearch("aether_furnace_adv"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.mediumFurnace))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_furnace_large",
						new ItemStack(AetheriaBlocks.largeFurnace),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						Ingredient.fromItems(AetheriaBlocks.mediumFurnace),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromTag(NostrumTags.Items.ReagentManiDust), Ingredient.fromItems(Items.FURNACE), Ingredient.fromTag(NostrumTags.Items.CrystalSmall)},
						new RRequirementResearch("aether_furnace_adv"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.largeFurnace))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_boiler",
						new ItemStack(AetheriaBlocks.boiler),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.BLACK_PEARL},
						Ingredient.fromItems(AetheriaBlocks.smallFurnace),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromTag(Tags.Items.INGOTS_IRON), Ingredient.fromItems(Items.CAULDRON), Ingredient.fromTag(NostrumTags.Items.CrystalSmall)},
						new RRequirementResearch("aether_boiler"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.boiler))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_bath",
						new ItemStack(AetheriaBlocks.bath),
						null,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.BLACK_PEARL},
						Ingredient.fromItems(NostrumItems.altarItem),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.STONE), Ingredient.fromItems(Items.BUCKET), Ingredient.fromTag(NostrumTags.Items.SpriteCore), Ingredient.fromTag(Tags.Items.STONE)},
						new RRequirementResearch("aether_bath"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.bath))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_charger",
						new ItemStack(AetheriaBlocks.charger),
						EMagicElement.ICE,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.MANDRAKE_ROOT},
						Ingredient.fromItems(AetheriaBlocks.bath),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.STONE), Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromItems(Items.CAULDRON), Ingredient.fromTag(Tags.Items.STONE)},
						new RRequirementResearch("aether_charger"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.charger))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_repairer",
						new ItemStack(AetheriaBlocks.repairer),
						EMagicElement.EARTH,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.GINSENG, ReagentType.SKY_ASH, ReagentType.GRAVE_DUST},
						Ingredient.fromItems(AetheriaBlocks.charger),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.OBSIDIAN), Ingredient.fromTag(NostrumTags.Items.CrystalLarge), Ingredient.fromTag(Tags.Items.OBSIDIAN), Ingredient.fromTag(Tags.Items.OBSIDIAN)},
						new RRequirementResearch("aether_repairer"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.repairer))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_unraveler",
						new ItemStack(AetheriaBlocks.unraveler),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.SKY_ASH, ReagentType.MANI_DUST, ReagentType.GINSENG},
						Ingredient.fromItems(AetheriaBlocks.charger),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.OBSIDIAN), Ingredient.fromTag(NostrumTags.Items.CrystalLarge), Ingredient.fromItems(Items.MAGMA_BLOCK), Ingredient.fromTag(Tags.Items.OBSIDIAN)},
						new RRequirementResearch("aether_unraveler"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.unraveler))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_small",
						new ItemStack(AetheriaBlocks.smallBattery),
						null,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.SPIDER_SILK, ReagentType.GINSENG},
						Ingredient.fromItems(Blocks.GLASS),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.ReagentManiDust), Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromTag(NostrumTags.Items.SpriteCore), Ingredient.fromTag(NostrumTags.Items.ReagentManiDust)},
						new RRequirementResearch("aether_battery"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.smallBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_medium",
						new ItemStack(AetheriaBlocks.mediumBattery),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						Ingredient.fromItems(AetheriaBlocks.smallBattery),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromTag(Tags.Items.GLASS), Ingredient.fromTag(Tags.Items.STONE), Ingredient.fromTag(NostrumTags.Items.CrystalSmall)},
						new RRequirementResearch("aether_battery"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.mediumBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_large",
						new ItemStack(AetheriaBlocks.largeBattery),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						Ingredient.fromItems(AetheriaBlocks.mediumBattery),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromItems(AetheriaBlocks.mediumBattery), Ingredient.fromTag(Tags.Items.OBSIDIAN), Ingredient.fromTag(NostrumTags.Items.CrystalSmall)},
						new RRequirementResearch("aether_battery_adv"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.largeBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_giant",
						new ItemStack(AetheriaBlocks.giantBattery),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						Ingredient.fromItems(AetheriaBlocks.largeBattery),
						new Ingredient[]{Ingredient.fromTag(NostrumTags.Items.CrystalMedium), Ingredient.fromItems(AetheriaBlocks.largeBattery), Ingredient.fromTag(Tags.Items.END_STONES), Ingredient.fromTag(NostrumTags.Items.CrystalMedium)},
						new RRequirementResearch("aether_battery_adv"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.giantBattery))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_gem",
						new ItemStack(AetheriaItems.aetherGem),
						null,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT, ReagentType.GRAVE_DUST},
						Ingredient.fromTag(NostrumTags.Items.CrystalLarge),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.ENDER_PEARLS), Ingredient.fromTag(NostrumTags.Items.SpriteCore), Ingredient.fromTag(NostrumTags.Items.ReagentManiDust), Ingredient.fromTag(Tags.Items.ENDER_PEARLS)},
						new RRequirementResearch("aether_gem"),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.aetherGem))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_relay",
						new ItemStack(AetheriaBlocks.relay),
						EMagicElement.ENDER,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.CRYSTABLOOM, ReagentType.CRYSTABLOOM, ReagentType.BLACK_PEARL},
						Ingredient.fromItems(Blocks.REDSTONE_TORCH),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.ENDER_PEARLS), Ingredient.fromTag(NostrumTags.Items.CrystalMedium), Ingredient.fromItems(NostrumItems.positionCrystal), Ingredient.fromTag(Tags.Items.DUSTS_REDSTONE)},
						new RRequirementResearch("aether_relay"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.relay, 8))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("enhanced_aether_relay",
						new ItemStack(AetheriaBlocks.enhancedRelay),
						null,
						new ReagentType[] {ReagentType.GINSENG, ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.GRAVE_DUST},
						Ingredient.fromItems(AetheriaBlocks.relay),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.DUSTS_REDSTONE), Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY},
						new RRequirementResearch("enhanced_aether_relay"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.enhancedRelay, 8))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("enhanced_aether_relay_direct", "enhanced_aether_relay",
						new ItemStack(AetheriaBlocks.enhancedRelay),
						EMagicElement.ENDER,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.CRYSTABLOOM, ReagentType.GINSENG, ReagentType.BLACK_PEARL},
						Ingredient.fromItems(Blocks.REDSTONE_TORCH),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.ENDER_PEARLS), Ingredient.fromTag(NostrumTags.Items.CrystalMedium), Ingredient.fromItems(NostrumItems.positionCrystal), Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_REDSTONE)},
						new RRequirementResearch("enhanced_aether_relay"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.enhancedRelay, 64))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_battery_cart",
						new ItemStack(AetheriaItems.aetherBatteryMinecart),
						null,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.BLACK_PEARL},
						Ingredient.fromItems(AetheriaBlocks.mediumBattery),
						new Ingredient[]{Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.fromItems(Items.MINECART), Ingredient.EMPTY},
						new RRequirementResearch("aether_carts"),
						new OutcomeSpawnItem(new ItemStack(AetheriaItems.aetherBatteryMinecart, 1))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_pump",
						new ItemStack(AetheriaBlocks.pump),
						null,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.BLACK_PEARL},
						Ingredient.fromItems(Blocks.HOPPER),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.INGOTS_IRON), Ingredient.fromTag(Tags.Items.INGOTS_GOLD), Ingredient.fromItems(Items.CAULDRON), Ingredient.fromTag(Tags.Items.INGOTS_IRON)},
						new RRequirementResearch("aether_carts"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.pump, 1))
						)
				);
		
		for (LensType type : LensType.values()) {
			Ingredient ingredient = type.getIngredient();
			if (ingredient == Ingredient.EMPTY) {
				continue;
			}
			
			registry.register(
				RitualRecipe.createTier3("make_lens_" + type.getUnlocSuffix(),
						new ItemStack(ItemAetherLens.GetLens(type)),
						null,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.MANI_DUST, ReagentType.SPIDER_SILK},
						Ingredient.fromTag(Tags.Items.GLASS_PANES),
						new Ingredient[] {Ingredient.EMPTY, ingredient, Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.EMPTY},
						new RRequirementResearch("aether_infusers"),
						new OutcomeSpawnItem(new ItemStack(ItemAetherLens.GetLens(type)))
				)
			);
		}
		
		registry.register(
				RitualRecipe.createTier3("wisp_crystal",
						new ItemStack(AetheriaBlocks.wispBlock),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.MANI_DUST, ReagentType.MANI_DUST, ReagentType.MANI_DUST},
						Ingredient.fromItems(NostrumItems.altarItem),
						new Ingredient[] {
								Ingredient.fromTag(NostrumTags.Items.CrystalMedium),
								Ingredient.fromItems(AetheriaBlocks.smallBattery),
								Ingredient.fromTag(Tags.Items.OBSIDIAN),
								Ingredient.fromTag(NostrumTags.Items.CrystalMedium),
								},
						new RRequirementResearch("wispblock"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.wispBlock))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("construct_aether_infuser",
						new ItemStack(AetheriaBlocks.infuser),
						null,
						new ReagentType[] {ReagentType.GINSENG, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.SPIDER_SILK},
						Ingredient.fromTag(Tags.Items.OBSIDIAN),
						new Ingredient[] {
								Ingredient.fromStacks(new ItemStack(AetheriaBlocks.smallBattery)),
								Ingredient.fromTag(NostrumTags.Items.CrystalMedium),
								Ingredient.fromStacks(new ItemStack(AetheriaBlocks.smallBattery)),
								Ingredient.fromStacks(new ItemStack(AetheriaBlocks.smallBattery))},
						new RRequirementResearch("aether_infusers"),
						new OutcomeCreateAetherInfuser()
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_sight_item",
						new ItemStack(AetheriaItems.aetherSightTool),
						EMagicElement.LIGHTNING,
						new ReagentType[] {ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG, ReagentType.SPIDER_SILK},
						Ingredient.fromTag(Tags.Items.GLASS_PANES),
						new Ingredient[]{Ingredient.fromTag(Tags.Items.NUGGETS_GOLD), Ingredient.fromTag(NostrumTags.Items.CrystalSmall), Ingredient.fromTag(Tags.Items.NUGGETS_GOLD), Ingredient.fromTag(Tags.Items.NUGGETS_GOLD)},
						new RRequirementResearch("aether_sight_item"),
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
       			Ingredient.fromItems(AetheriaItems.mandrakeFlower),
       			NostrumPotions.MakePotion(NostrumPotions.MANAREGEN_REALLY_STRONG.getType()));
   		
   		BrewingRecipeRegistry.addRecipe(new PotionIngredient(NostrumPotions.MANAREGEN_STRONG.getType()),
       			Ingredient.fromItems(AetheriaItems.ginsengFlower),
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
