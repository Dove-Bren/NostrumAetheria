package com.smanzana.nostrumaetheria.proxy;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetherBathBlock;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherChargerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock;
import com.smanzana.nostrumaetheria.blocks.InfineAetherBlock;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.items.ActivePendant;
import com.smanzana.nostrumaetheria.items.AetherGem;
import com.smanzana.nostrumaetheria.items.PassivePendant;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.aetheria.items.AetherResourceType;
import com.smanzana.nostrummagica.aetheria.items.NostrumAetherResourceItem;
import com.smanzana.nostrummagica.items.AltarItem;
import com.smanzana.nostrummagica.items.InfusedGemItem;
import com.smanzana.nostrummagica.items.NostrumResourceItem;
import com.smanzana.nostrummagica.items.PositionCrystal;
import com.smanzana.nostrummagica.items.ReagentItem;
import com.smanzana.nostrummagica.items.ThanoPendant;
import com.smanzana.nostrummagica.items.NostrumResourceItem.ResourceType;
import com.smanzana.nostrummagica.items.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.LoreRegistry;
import com.smanzana.nostrummagica.research.NostrumResearch;
import com.smanzana.nostrummagica.research.NostrumResearch.NostrumResearchTab;
import com.smanzana.nostrummagica.research.NostrumResearch.Size;
import com.smanzana.nostrummagica.rituals.RitualRecipe;
import com.smanzana.nostrummagica.rituals.RitualRegistry;
import com.smanzana.nostrummagica.rituals.outcomes.OutcomeSpawnItem;
import com.smanzana.nostrummagica.rituals.requirements.RRequirementResearch;
import com.smanzana.nostrummagica.spells.EMagicElement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class CommonProxy {
	
	public void preinit() {
		NetworkHandler.getInstance();
    	
//    	int entityID = 0;
//    	EntityRegistry.registerModEntity(EntityTestFairy.class, "test_fairy",
//    			entityID++,
//    			NostrumFairies.instance,
//    			128,
//    			1,
//    			false
//    			);

    	registerItems();
    	registerBlocks();
    	this.registerRituals();
    	
    	NostrumMagica.instance.registerResearchReloadHook((i) -> {
    		registerResearch();
    		return 0;
    	});
	}
	
	public void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(NostrumAetheria.instance, new NostrumAetheriaGui());
		
		this.registerResearch();
		this.registerLore();
	}
	
	public void postinit() {
		;
	}
    
    private void registerItems() {
    	GameRegistry.register(
    			ActivePendant.instance().setRegistryName(ActivePendant.ID));
    	APIProxy.ActivePendantItem = ActivePendant.instance();
    	
    	GameRegistry.register(
    			PassivePendant.instance().setRegistryName(PassivePendant.ID));
    	APIProxy.PassivePendantItem = PassivePendant.instance();
    	
    	GameRegistry.register(
    			AetherGem.instance().setRegistryName(AetherGem.ID));
    	APIProxy.AetherGemItem = AetherGem.instance();
    }
    
    private void registerBlocks() {
    	GameRegistry.register(InfineAetherBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, InfineAetherBlock.ID));
    	GameRegistry.register(
    			(InfineAetherBlock.instance().itemBlock.setRegistryName(InfineAetherBlock.ID)
    					.setCreativeTab(APIProxy.creativeTab).setUnlocalizedName(InfineAetherBlock.ID))
    			);
    	InfineAetherBlock.init();
    	APIProxy.InfiniteAetherBlock = InfineAetherBlock.instance();
    	
    	for (AetherBatteryBlock block : new AetherBatteryBlock[]{
    			AetherBatteryBlock.small(),
    			AetherBatteryBlock.medium(),
    			AetherBatteryBlock.large(),
    			AetherBatteryBlock.giant()
    	}) {
    		GameRegistry.register(block,
        			new ResourceLocation(NostrumAetheria.MODID, block.getID()));
        	GameRegistry.register(
        			(new ItemBlock(block).setRegistryName(block.getID())
        					.setCreativeTab(APIProxy.creativeTab).setUnlocalizedName(block.getID()))
        			);
        	
    	}
    	AetherBatteryBlock.init();
    	APIProxy.AetherBatterySmallBlock = AetherBatteryBlock.small();
    	APIProxy.AetherBatteryMediumBlock = AetherBatteryBlock.medium();
    	APIProxy.AetherBatteryLargeBlock = AetherBatteryBlock.large();
    	APIProxy.AetherBatteryGiantBlock = AetherBatteryBlock.giant();
    	
    	GameRegistry.register(AetherRelay.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherRelay.ID));
    	GameRegistry.register(
    			(new ItemBlock(AetherRelay.instance()).setRegistryName(AetherRelay.ID)
    					.setCreativeTab(APIProxy.creativeTab).setUnlocalizedName(AetherRelay.ID))
    			);
    	AetherRelay.init();
    	APIProxy.AetherRelay = AetherRelay.instance();
    	
    	GameRegistry.register(AetherFurnaceBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherFurnaceBlock.ID));
    	ItemBlock furnaceItem = new ItemBlock(AetherFurnaceBlock.instance()){
			@Override
			public String getUnlocalizedName(ItemStack stack) {
				AetherFurnaceBlock.Type type;
				try {
					type = AetherFurnaceBlock.Type.values()[stack.getMetadata()];
				} catch (Exception e) {
					type = AetherFurnaceBlock.Type.SMALL;
				}
				return "tile." + AetherFurnaceBlock.UnlocalizedForType(type);
			}
		};
    	furnaceItem.setRegistryName(AetherFurnaceBlock.ID)
				.setCreativeTab(APIProxy.creativeTab)
				.setUnlocalizedName(AetherFurnaceBlock.ID)
				.setHasSubtypes(true);
    	furnaceItem.addPropertyOverride(new ResourceLocation("on"), AetherFurnaceBlock.ON_GETTER);
    	furnaceItem.addPropertyOverride(new ResourceLocation("size"), AetherFurnaceBlock.SIZE_GETTER);
    	GameRegistry.register(furnaceItem);
    	
    	AetherFurnaceBlock.init();
    	APIProxy.AetherFurnaceBlock = AetherFurnaceBlock.instance();
    	
    	GameRegistry.register(AetherBoilerBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherBoilerBlock.ID));
    	GameRegistry.register(
    			(new ItemBlock(AetherBoilerBlock.instance()).setRegistryName(AetherBoilerBlock.ID)
    					.setCreativeTab(APIProxy.creativeTab).setUnlocalizedName(AetherBoilerBlock.ID))
    			);
    	AetherBoilerBlock.init();
    	APIProxy.AetherBoilerBlock = AetherBoilerBlock.instance();
    	
    	GameRegistry.register(AetherBathBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherBathBlock.ID));
    	GameRegistry.register(
    			(new ItemBlock(AetherBathBlock.instance()).setRegistryName(AetherBathBlock.ID)
    					.setCreativeTab(APIProxy.creativeTab).setUnlocalizedName(AetherBathBlock.ID))
    			);
    	AetherBathBlock.init();
    	APIProxy.AetherBathBlock = AetherBathBlock.instance();
    	
    	
    	GameRegistry.register(AetherChargerBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherChargerBlock.ID));
    	GameRegistry.register(
    			(new ItemBlock(AetherChargerBlock.instance()).setRegistryName(AetherChargerBlock.ID)
    					.setCreativeTab(APIProxy.creativeTab).setUnlocalizedName(AetherChargerBlock.ID))
    			);
    	AetherChargerBlock.init();
    	APIProxy.AetherChargerBlock = AetherChargerBlock.instance();
    	
    	
    	GameRegistry.register(AetherRepairerBlock.instance(),
    			new ResourceLocation(NostrumAetheria.MODID, AetherRepairerBlock.ID));
    	GameRegistry.register(
    			(new ItemBlock(AetherRepairerBlock.instance()).setRegistryName(AetherRepairerBlock.ID)
    					.setCreativeTab(APIProxy.creativeTab).setUnlocalizedName(AetherRepairerBlock.ID))
    			);
    	AetherRepairerBlock.init();
    	APIProxy.AetherRepairerBlock = AetherRepairerBlock.instance();
    }
    
    private void registerResearch() {
    	NostrumResearch.startBuilding()
			.hiddenParent("rituals")
			.hiddenParent("thano_pendant")
			.lore(ThanoPendant.instance())
			.reference(APIProxy.ActivePendantItem)
			.reference("ritual::active_pendant", "ritual.active_pendant.name")
		.build("active_pendant", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 0, -1, true, new ItemStack(APIProxy.ActivePendantItem));
	
		NostrumResearch.startBuilding()
			.parent("active_pendant")
			.hiddenParent("aether_bath")
			.hiddenParent("kani")
			.lore((ILoreTagged) APIProxy.ActivePendantItem)
			.lore(NostrumAetherResourceItem.instance())
			.reference(APIProxy.PassivePendantItem)
			.reference("ritual::passive_pendant", "ritual.passive_pendant.name")
		.build("passive_pendant", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 1, -1, true, new ItemStack(APIProxy.PassivePendantItem));
		
		NostrumResearch.startBuilding()
			.hiddenParent("active_pendant")
			.lore((ILoreTagged) APIProxy.ActivePendantItem)
			.reference("ritual::aether_furnace_small", "ritual.aether_furnace_small.name")
		.build("aether_furnace", (NostrumResearchTab) APIProxy.ResearchTab, Size.GIANT, 0, 0, true, new ItemStack(APIProxy.AetherFurnaceBlock));
		
		NostrumResearch.startBuilding()
			.parent("aether_furnace")
			.hiddenParent("aether_boiler")
			.reference("ritual::aether_furnace_medium", "ritual.aether_furnace_medium.name")
			.reference("ritual::aether_furnace_large", "ritual.aether_furnace_large.name")
		.build("aether_furnace_adv", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, -1, 1, true, new ItemStack(APIProxy.AetherFurnaceBlock, 1, 2));
		
		NostrumResearch.startBuilding()
			.parent("aether_furnace")
			.lore((ILoreTagged) APIProxy.ActivePendantItem)
			.reference("ritual::aether_bath", "ritual.aether_bath.name")
		.build("aether_bath", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 2, 1, true, new ItemStack(APIProxy.AetherBathBlock));
		
		NostrumResearch.startBuilding()
			.parent("aether_bath")
			.parent("aether_furnace")
			.hiddenParent("aether_gem")
			.reference("ritual::aether_charger", "ritual.aether_charger.name")
		.build("aether_charger", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 1, 1, true, new ItemStack(APIProxy.AetherChargerBlock));
		
		NostrumResearch.startBuilding()
			.parent("aether_charger")
			.hiddenParent("vani")
			.reference("ritual::aether_repairer", "ritual.aether_repairer.name")
		.build("aether_repairer", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 1, 2, true, new ItemStack(APIProxy.AetherRepairerBlock));
		
		NostrumResearch.startBuilding()
			.parent("aether_furnace")
			.reference("ritual::aether_boiler", "ritual.aether_boiler.name")
		.build("aether_boiler", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, -2, 1, true, new ItemStack(APIProxy.AetherBoilerBlock));
		
		NostrumResearch.startBuilding()
			.parent("aether_bath")
			.reference("ritual::aether_battery_small", "ritual.aether_battery_small.name")
			.reference("ritual::aether_battery_medium", "ritual.aether_battery_medium.name")
		.build("aether_battery", (NostrumResearchTab) APIProxy.ResearchTab, Size.LARGE, 2, 2, true, new ItemStack(APIProxy.AetherBatterySmallBlock));
		
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.hiddenParent("kani")
			.reference("ritual::aether_battery_large", "ritual.aether_battery_large.name")
			.reference("ritual::aether_battery_giant", "ritual.aether_battery_giant.name")
		.build("aether_battery_adv", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 4, 2, true, new ItemStack(APIProxy.AetherBatteryGiantBlock));
		
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.hiddenParent("vani")
			.reference("ritual::aether_gem", "ritual.aether_gem.name")
		.build("aether_gem", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 3, 1, true, new ItemStack(APIProxy.AetherGemItem));
		
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.hiddenParent("geogems")
			.hiddenParent("kani")
			.reference("ritual::aether_relay", "ritual.aether_relay.name")
		.build("aether_relay", (NostrumResearchTab) APIProxy.ResearchTab, Size.LARGE, 2, 3, true, new ItemStack(APIProxy.AetherRelay));
    }
    
    private void registerRituals() {
    	RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("active_pendant",
						new ItemStack(APIProxy.ActivePendantItem),
						null,
						new ReagentType[] {ReagentType.GINSENG, ReagentType.SPIDER_SILK, ReagentType.MANI_DUST, ReagentType.MANI_DUST},
						new ItemStack(ThanoPendant.instance(), 1, OreDictionary.WILDCARD_VALUE),
						new ItemStack[] {NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), InfusedGemItem.instance().getGem(EMagicElement.FIRE, 1), new ItemStack(Blocks.LAPIS_BLOCK, 1, OreDictionary.WILDCARD_VALUE), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1)},
						new RRequirementResearch("active_pendant"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.ActivePendantItem))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("passive_pendant",
						new ItemStack(APIProxy.PassivePendantItem),
						null,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.SKY_ASH, ReagentType.MANI_DUST, ReagentType.SKY_ASH},
						new ItemStack(APIProxy.ActivePendantItem, 1, OreDictionary.WILDCARD_VALUE),
						new ItemStack[] {NostrumMagica.aetheria.getResourceItem(AetherResourceType.FLOWER_GINSENG, 1), NostrumResourceItem.getItem(ResourceType.CRYSTAL_MEDIUM, 1), NostrumResourceItem.getItem(ResourceType.SPRITE_CORE, 1), NostrumMagica.aetheria.getResourceItem(AetherResourceType.FLOWER_MANDRAKE, 1)},
						new RRequirementResearch("passive_pendant"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.PassivePendantItem))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_furnace_small",
						new ItemStack(APIProxy.AetherFurnaceBlock),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						new ItemStack(Blocks.FURNACE),
						new ItemStack[] {ReagentItem.instance().getReagent(ReagentType.MANI_DUST, 1), NostrumResourceItem.getItem(ResourceType.SPRITE_CORE, 1), new ItemStack(Blocks.OBSIDIAN, 1, OreDictionary.WILDCARD_VALUE), ReagentItem.instance().getReagent(ReagentType.MANI_DUST, 1)},
						new RRequirementResearch("aether_furnace"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherFurnaceBlock, 1, 0))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_furnace_medium",
						new ItemStack(APIProxy.AetherFurnaceBlock, 1, 1),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						new ItemStack(APIProxy.AetherFurnaceBlock, 1, 0),
						new ItemStack[] {NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), ReagentItem.instance().getReagent(ReagentType.MANI_DUST, 1), new ItemStack(Blocks.FURNACE), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1)},
						new RRequirementResearch("aether_furnace_adv"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherFurnaceBlock, 1, 1))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_furnace_large",
						new ItemStack(APIProxy.AetherFurnaceBlock, 1, 2),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.GRAVE_DUST, ReagentType.SKY_ASH},
						new ItemStack(APIProxy.AetherFurnaceBlock, 1, 1),
						new ItemStack[] {NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), ReagentItem.instance().getReagent(ReagentType.MANI_DUST, 1), new ItemStack(Blocks.FURNACE), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1)},
						new RRequirementResearch("aether_furnace_adv"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherFurnaceBlock, 1, 2))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_boiler",
						new ItemStack(APIProxy.AetherBoilerBlock),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.BLACK_PEARL},
						new ItemStack(APIProxy.AetherFurnaceBlock, 1, 0),
						new ItemStack[] {NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.CAULDRON), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1)},
						new RRequirementResearch("aether_boiler"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherBoilerBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_bath",
						new ItemStack(APIProxy.AetherBathBlock),
						null,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.BLACK_PEARL},
						new ItemStack(AltarItem.instance()),
						new ItemStack[] {new ItemStack(Blocks.STONE, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.BUCKET), NostrumResourceItem.getItem(ResourceType.SPRITE_CORE, 1), new ItemStack(Blocks.STONE, 1, OreDictionary.WILDCARD_VALUE)},
						new RRequirementResearch("aether_bath"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherBathBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_charger",
						new ItemStack(APIProxy.AetherChargerBlock),
						EMagicElement.ICE,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.MANDRAKE_ROOT},
						new ItemStack(APIProxy.AetherBathBlock),
						new ItemStack[] {new ItemStack(Blocks.STONE, 1, OreDictionary.WILDCARD_VALUE), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), new ItemStack(Items.CAULDRON), new ItemStack(Blocks.STONE, 1, OreDictionary.WILDCARD_VALUE)},
						new RRequirementResearch("aether_charger"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherChargerBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_repairer",
						new ItemStack(APIProxy.AetherRepairerBlock),
						EMagicElement.EARTH,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.GINSENG, ReagentType.SKY_ASH, ReagentType.GRAVE_DUST},
						new ItemStack(APIProxy.AetherChargerBlock),
						new ItemStack[] {new ItemStack(Blocks.OBSIDIAN), NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1), new ItemStack(Blocks.OBSIDIAN), new ItemStack(Blocks.OBSIDIAN)},
						new RRequirementResearch("aether_repairer"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherRepairerBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_battery_small",
						new ItemStack(APIProxy.AetherBatterySmallBlock),
						null,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.BLACK_PEARL, ReagentType.SPIDER_SILK, ReagentType.GINSENG},
						new ItemStack(Blocks.GLASS),
						new ItemStack[] {ReagentItem.instance().getReagent(ReagentType.MANI_DUST, 1), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), NostrumResourceItem.getItem(ResourceType.SPRITE_CORE, 1), ReagentItem.instance().getReagent(ReagentType.MANI_DUST, 1)},
						new RRequirementResearch("aether_battery"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherBatterySmallBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_battery_medium",
						new ItemStack(APIProxy.AetherBatteryMediumBlock),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						new ItemStack(APIProxy.AetherBatterySmallBlock),
						new ItemStack[] {NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), new ItemStack(Blocks.GLASS), new ItemStack(Blocks.STONE), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1)},
						new RRequirementResearch("aether_battery"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherBatteryMediumBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_battery_large",
						new ItemStack(APIProxy.AetherBatteryLargeBlock),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						new ItemStack(APIProxy.AetherBatteryMediumBlock),
						new ItemStack[] {NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1), new ItemStack(APIProxy.AetherBatteryMediumBlock), new ItemStack(Blocks.OBSIDIAN), NostrumResourceItem.getItem(ResourceType.CRYSTAL_SMALL, 1)},
						new RRequirementResearch("aether_battery_adv"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherBatteryLargeBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_battery_giant",
						new ItemStack(APIProxy.AetherBatteryGiantBlock),
						null,
						new ReagentType[] {ReagentType.MANI_DUST, ReagentType.CRYSTABLOOM, ReagentType.SKY_ASH, ReagentType.GINSENG},
						new ItemStack(APIProxy.AetherBatteryLargeBlock),
						new ItemStack[] {NostrumResourceItem.getItem(ResourceType.CRYSTAL_MEDIUM, 1), new ItemStack(APIProxy.AetherBatteryLargeBlock), new ItemStack(Blocks.END_STONE), NostrumResourceItem.getItem(ResourceType.CRYSTAL_MEDIUM, 1)},
						new RRequirementResearch("aether_battery_adv"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherBatteryGiantBlock))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_gem",
						new ItemStack(APIProxy.AetherGemItem),
						null,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.MANI_DUST, ReagentType.MANDRAKE_ROOT, ReagentType.GRAVE_DUST},
						 NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1),
						new ItemStack[] {new ItemStack(Items.ENDER_PEARL), NostrumResourceItem.getItem(ResourceType.SPRITE_CORE, 1), ReagentItem.instance().getReagent(ReagentType.MANI_DUST, 1), new ItemStack(Items.ENDER_PEARL)},
						new RRequirementResearch("aether_gem"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherGemItem))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_relay",
						new ItemStack(APIProxy.AetherRelay),
						EMagicElement.ENDER,
						new ReagentType[] {ReagentType.SKY_ASH, ReagentType.CRYSTABLOOM, ReagentType.CRYSTABLOOM, ReagentType.BLACK_PEARL},
						new ItemStack(Blocks.REDSTONE_TORCH),
						new ItemStack[] {new ItemStack(Items.ENDER_PEARL), NostrumResourceItem.getItem(ResourceType.CRYSTAL_MEDIUM, 1), new ItemStack(PositionCrystal.instance()), new ItemStack(Items.REDSTONE)},
						new RRequirementResearch("aether_relay"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherRelay, 3))
						)
				);
    }
    
    private void registerLore() {
    	LoreRegistry.instance().register(AetherBathBlock.instance());
    	LoreRegistry.instance().register(AetherBatteryBlock.small());
    	LoreRegistry.instance().register(AetherBoilerBlock.instance());
    	LoreRegistry.instance().register(AetherChargerBlock.instance());
    	LoreRegistry.instance().register(AetherFurnaceBlock.instance());
    	LoreRegistry.instance().register(AetherRelay.instance());
    	LoreRegistry.instance().register(AetherRepairerBlock.instance());
    	LoreRegistry.instance().register(ActivePendant.instance());
    	LoreRegistry.instance().register(PassivePendant.instance());
    	LoreRegistry.instance().register(AetherGem.instance());
    }

	public EntityPlayer getPlayer() {
		return null; // Doesn't mean anything on the server
	}
	
	public boolean isServer() {
		return true;
	}
}
