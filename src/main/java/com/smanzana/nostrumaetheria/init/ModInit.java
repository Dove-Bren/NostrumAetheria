package com.smanzana.nostrumaetheria.init;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.capability.IAetherBurnable;
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
import com.smanzana.nostrummagica.effect.NostrumPotions;
import com.smanzana.nostrummagica.effect.NostrumPotions.PotionIngredient;
import com.smanzana.nostrummagica.entity.WispEntity;
import com.smanzana.nostrummagica.item.NostrumItems;
import com.smanzana.nostrummagica.item.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.progression.requirement.ResearchRequirement;
import com.smanzana.nostrummagica.progression.research.NostrumResearch;
import com.smanzana.nostrummagica.progression.research.NostrumResearch.NostrumResearchTab;
import com.smanzana.nostrummagica.progression.research.NostrumResearch.Size;
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
		APIProxy.AetherResearchTab = new NostrumResearch.NostrumResearchTab("aether", new ItemStack(AetheriaItems.aetherGem));
    	APIProxy.AetherGearResearchTab = new NostrumResearch.NostrumResearchTab("aether_gear", new ItemStack(AetheriaItems.aetherSightTool));
		registerResearch();
		AetherRepairerBlock.initDefaultRecipes();
		AetherUnravelerBlock.initDefaultRecipes();
		NostrumMagica.instance.registerResearchReloadHook(() -> {
    		registerResearch();
    	});
	}
	
	@SubscribeEvent
	public static final void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IAetherBurnable.class);
	}
	
	private static final void postinit() {
		
	}
	
	private static final void registerResearch() {
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
			.lore(WispEntity.LoreKey)
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
						Ingredient.of(NostrumItems.thanoPendant),
						new Ingredient[] {
								Ingredient.of(NostrumTags.Items.CrystalSmall),
								Ingredient.of(NostrumTags.Items.InfusedGemFire), Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS), Ingredient.of(NostrumTags.Items.CrystalSmall)},
						new ResearchRequirement("active_pendant"),
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
						new ResearchRequirement("passive_pendant"),
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
						new ResearchRequirement("aether_furnace"),
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
						new ResearchRequirement("aether_furnace_adv"),
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
						new ResearchRequirement("aether_furnace_adv"),
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
						new ResearchRequirement("aether_boiler"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.boiler))
						)
				);
		
		registry.register(
				RitualRecipe.createTier3("aether_bath",
						new ItemStack(AetheriaBlocks.bath),
						null,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.BLACK_PEARL},
						Ingredient.of(NostrumItems.altarItem),
						new Ingredient[]{Ingredient.of(Tags.Items.STONE), Ingredient.of(Items.BUCKET), Ingredient.of(NostrumTags.Items.SpriteCore), Ingredient.of(Tags.Items.STONE)},
						new ResearchRequirement("aether_bath"),
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
						new ResearchRequirement("aether_charger"),
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
						new ResearchRequirement("aether_repairer"),
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
						new ResearchRequirement("aether_unraveler"),
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
						new ResearchRequirement("aether_battery"),
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
						new ResearchRequirement("aether_battery"),
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
						new ResearchRequirement("aether_battery_adv"),
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
						new ResearchRequirement("aether_battery_adv"),
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
						new ResearchRequirement("aether_gem"),
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
						new ResearchRequirement("aether_relay"),
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
						new ResearchRequirement("enhanced_aether_relay"),
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
						new ResearchRequirement("enhanced_aether_relay"),
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
						new ResearchRequirement("aether_carts"),
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
						new ResearchRequirement("aether_carts"),
						new OutcomeSpawnItem(new ItemStack(AetheriaBlocks.pump, 1))
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
						new ResearchRequirement("aether_infusers"),
						new OutcomeSpawnItem(new ItemStack(ItemAetherLens.GetLens(type)))
				)
			);
		}
		
		registry.register(
				RitualRecipe.createTier3("wisp_crystal",
						new ItemStack(AetheriaBlocks.wispBlock),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.MANI_DUST, ReagentType.MANI_DUST, ReagentType.MANI_DUST},
						Ingredient.of(NostrumItems.altarItem),
						new Ingredient[] {
								Ingredient.of(NostrumTags.Items.CrystalMedium),
								Ingredient.of(AetheriaBlocks.smallBattery),
								Ingredient.of(Tags.Items.OBSIDIAN),
								Ingredient.of(NostrumTags.Items.CrystalMedium),
								},
						new ResearchRequirement("wispblock"),
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
						new ResearchRequirement("aether_infusers"),
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
						new ResearchRequirement("aether_sight_item"),
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
