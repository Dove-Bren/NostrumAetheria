package com.smanzana.nostrumaetheria.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetherBathBlock;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock;
import com.smanzana.nostrumaetheria.blocks.AetherBoilerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherChargerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock;
import com.smanzana.nostrumaetheria.blocks.AetherPumpBlock;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.AetherRepairerBlock;
import com.smanzana.nostrumaetheria.blocks.AetherUnravelerBlock;
import com.smanzana.nostrumaetheria.blocks.InfineAetherBlock;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBathTileEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBatteryEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBoilerBlockEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherChargerBlockEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherFurnaceBlockEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherPumpBlockEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherRelayEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherRepairerBlockEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherUnravelerBlockEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.InfiniteAetherBlockEntity;
import com.smanzana.nostrumaetheria.entities.EntityAetherBatteryMinecart;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.items.ActivePendant;
import com.smanzana.nostrumaetheria.items.AetherBatteryMinecartItem;
import com.smanzana.nostrumaetheria.items.AetherGem;
import com.smanzana.nostrumaetheria.items.PassivePendant;
import com.smanzana.nostrumaetheria.network.NetworkHandler;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.aetheria.items.AetherResourceType;
import com.smanzana.nostrummagica.aetheria.items.NostrumAetherResourceItem;
import com.smanzana.nostrummagica.items.AltarItem;
import com.smanzana.nostrummagica.items.InfusedGemItem;
import com.smanzana.nostrummagica.items.NostrumResourceItem;
import com.smanzana.nostrummagica.items.NostrumResourceItem.ResourceType;
import com.smanzana.nostrummagica.items.PositionCrystal;
import com.smanzana.nostrummagica.items.ReagentItem;
import com.smanzana.nostrummagica.items.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.items.ThanoPendant;
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

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class CommonProxy {
	
	public void preinit() {
		NetworkHandler.getInstance();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(NostrumAetheria.instance, new NostrumAetheriaGui());

    	this.registerRituals();
    	
    	NostrumMagica.instance.registerResearchReloadHook((i) -> {
    		registerResearch();
    		return 0;
    	});
    	
		this.registerResearch();
		this.registerLore();
	}
	
	public void postinit() {
		AetherRepairerBlock.initDefaultRecipes();
		AetherUnravelerBlock.initDefaultRecipes();
	}
	
	private List<Item> blockItems = new ArrayList<>(16);
    
	@SubscribeEvent
    private void registerItems(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		registry.register(ActivePendant.instance());
    	APIProxy.ActivePendantItem = ActivePendant.instance();
    	
    	registry.register(PassivePendant.instance());
    	APIProxy.PassivePendantItem = PassivePendant.instance();
    	
    	registry.register(AetherGem.instance());
    	APIProxy.AetherGemItem = AetherGem.instance();
    	
    	registry.register(AetherBatteryMinecartItem.instance());
    	APIProxy.AetherBatteryMinecartItem = AetherBatteryMinecartItem.instance();
    	
    	for (Item blockItem : blockItems) {
    		registry.register(blockItem);
    	}
    }
	
	 private void registerBlock(Block block, String registryName, IForgeRegistry<Block> registry) {
    	block.setRegistryName(registryName);
    	registry.register(block);
    }
    
    private void registerBlockAndItemBlock(Block block, String registryName, @Nullable CreativeTabs tab, IForgeRegistry<Block> registry) {
    	registerBlock(block, registryName, registry);
    	
    	ItemBlock item = new ItemBlock(block);
    	item.setRegistryName(registryName);
    	item.setUnlocalizedName(registryName);
    	item.setCreativeTab(tab == null ? APIProxy.creativeTab : tab);
    	blockItems.add(item);
    }
    
    private void registerBlockAndItemBlock(Block block, String registryName, IForgeRegistry<Block> registry) {
    	registerBlockAndItemBlock(block, registryName, null, registry);
    }
    
    @SubscribeEvent
    private void registerBlocks(RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();
		
    	registerBlockAndItemBlock(InfineAetherBlock.instance(), InfineAetherBlock.ID, registry);
    	APIProxy.InfiniteAetherBlock = InfineAetherBlock.instance();
    	
    	for (AetherBatteryBlock block : new AetherBatteryBlock[]{
    			AetherBatteryBlock.small(),
    			AetherBatteryBlock.medium(),
    			AetherBatteryBlock.large(),
    			AetherBatteryBlock.giant()
    	}) {
    		registerBlockAndItemBlock(block, block.getID(), registry);
    	}
    	APIProxy.AetherBatterySmallBlock = AetherBatteryBlock.small();
    	APIProxy.AetherBatteryMediumBlock = AetherBatteryBlock.medium();
    	APIProxy.AetherBatteryLargeBlock = AetherBatteryBlock.large();
    	APIProxy.AetherBatteryGiantBlock = AetherBatteryBlock.giant();
    	
    	registerBlockAndItemBlock(AetherRelay.instance(), AetherRelay.ID, registry);
    	APIProxy.AetherRelay = AetherRelay.instance();
    	
    	registerBlock(AetherFurnaceBlock.instance(), AetherFurnaceBlock.ID, registry);
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
    	blockItems.add(furnaceItem);
    	
    	APIProxy.AetherFurnaceBlock = AetherFurnaceBlock.instance();
    	
    	registerBlockAndItemBlock(AetherBoilerBlock.instance(), AetherBoilerBlock.ID, registry);
    	APIProxy.AetherBoilerBlock = AetherBoilerBlock.instance();
    	
    	registerBlockAndItemBlock(AetherBathBlock.instance(), AetherBathBlock.ID, registry);
    	APIProxy.AetherBathBlock = AetherBathBlock.instance();
    	
    	
    	registerBlockAndItemBlock(AetherChargerBlock.instance(), AetherChargerBlock.ID, registry);
    	APIProxy.AetherChargerBlock = AetherChargerBlock.instance();
    	
    	
    	registerBlockAndItemBlock(AetherRepairerBlock.instance(), AetherRepairerBlock.ID, registry);
    	APIProxy.AetherRepairerBlock = AetherRepairerBlock.instance();
    	
    	registerBlockAndItemBlock(AetherUnravelerBlock.instance(), AetherUnravelerBlock.ID, registry);
    	APIProxy.AetherUnravelerBlock = AetherUnravelerBlock.instance();
    	
    	registerBlockAndItemBlock(AetherPumpBlock.instance(), AetherPumpBlock.ID, registry);
    	APIProxy.AetherPumpBlock = AetherPumpBlock.instance();
    	
    	registerTileEntities();
    }
    
    private void registerTileEntities() {
    	GameRegistry.registerTileEntity(AetherRelayEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_relay_te"));
    	GameRegistry.registerTileEntity(InfiniteAetherBlockEntity.class, new ResourceLocation(NostrumAetheria.MODID, "infinite_aether_block_te"));
    	GameRegistry.registerTileEntity(AetherBatteryEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_battery_te"));
    	GameRegistry.registerTileEntity(AetherFurnaceBlockEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_furnace_block_te"));
    	GameRegistry.registerTileEntity(AetherBoilerBlockEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_boiler_block_te"));
    	GameRegistry.registerTileEntity(AetherBathTileEntity.class, new ResourceLocation(NostrumAetheria.MODID, "nostrum_aether_altar_te"));
    	GameRegistry.registerTileEntity(AetherChargerBlockEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_charger_te"));
    	GameRegistry.registerTileEntity(AetherRepairerBlockEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_repairer_te"));
    	GameRegistry.registerTileEntity(AetherUnravelerBlockEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_unraveler_te"));
    	GameRegistry.registerTileEntity(AetherPumpBlockEntity.class, new ResourceLocation(NostrumAetheria.MODID, "aether_pump_te"));
    }
    
    @SubscribeEvent
    private void registerEntities(RegistryEvent.Register<EntityEntry> event) {
    	final IForgeRegistry<EntityEntry> registry = event.getRegistry();
    	
    	int entityID = 0;
    	registry.register(EntityEntryBuilder.create()
    			.entity(EntityAetherBatteryMinecart.class)
    			.id("aether_battery_minecart", entityID++)
    			.name("aether_battery_minecart")
    			.tracker(128, 1, false)
    			.build());
    	APIProxy.AetherBatteryMinecart = EntityAetherBatteryMinecart.class;
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
			.parent("aether_repairer")
			.reference("ritual::aether_unraveler", "ritual.aether_unraveler.name")
		.build("aether_unraveler", (NostrumResearchTab) APIProxy.ResearchTab, Size.NORMAL, 1, 3, true, new ItemStack(APIProxy.AetherUnravelerBlock));
		
		//aether_unraveler
		
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
		
		// TODO if 'pipes' get added for short-range aether transport, consider making that a requirement for pumps & rails and stuff
		// For example, pipes -> pumps -> rails with pumps as a separate thing?
		NostrumResearch.startBuilding()
			.parent("aether_battery")
			.parent("aether_relay")
			.reference("ritual::aether_battery_cart", "ritual.aether_battery_cart.name")
			.reference("ritual::aether_pump", "ritual.aether_pump.name")
		.build("aether_carts", (NostrumResearchTab) APIProxy.ResearchTab, Size.GIANT, 3, 3, true, new ItemStack(APIProxy.AetherBatteryMinecartItem));
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
				RitualRecipe.createTier3("aether_unraveler",
						new ItemStack(APIProxy.AetherUnravelerBlock),
						EMagicElement.FIRE,
						new ReagentType[] {ReagentType.BLACK_PEARL, ReagentType.SKY_ASH, ReagentType.MANI_DUST, ReagentType.GINSENG},
						new ItemStack(APIProxy.AetherChargerBlock),
						new ItemStack[] {new ItemStack(Blocks.OBSIDIAN), NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1), new ItemStack(Blocks.MAGMA), new ItemStack(Blocks.OBSIDIAN)},
						new RRequirementResearch("aether_unraveler"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherUnravelerBlock))
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
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherRelay, 8))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_battery_cart",
						new ItemStack(APIProxy.AetherBatteryMinecartItem),
						null,
						new ReagentType[] {ReagentType.SPIDER_SILK, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.BLACK_PEARL},
						new ItemStack(APIProxy.AetherBatteryMediumBlock),
						new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, new ItemStack(Items.MINECART), ItemStack.EMPTY},
						new RRequirementResearch("aether_carts"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherBatteryMinecartItem, 1))
						)
				);
		
		RitualRegistry.instance().addRitual(
				RitualRecipe.createTier3("aether_pump",
						new ItemStack(APIProxy.AetherPumpBlock),
						null,
						new ReagentType[] {ReagentType.GRAVE_DUST, ReagentType.GRAVE_DUST, ReagentType.MANI_DUST, ReagentType.BLACK_PEARL},
						new ItemStack(Blocks.HOPPER),
						new ItemStack[] {new ItemStack(Items.IRON_INGOT), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.CAULDRON), new ItemStack(Items.IRON_INGOT)},
						new RRequirementResearch("aether_carts"),
						new OutcomeSpawnItem(new ItemStack(APIProxy.AetherPumpBlock, 1))
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
    	LoreRegistry.instance().register(AetherUnravelerBlock.instance());
    	LoreRegistry.instance().register(AetherPumpBlock.instance());
    }

	public EntityPlayer getPlayer() {
		return null; // Doesn't mean anything on the server
	}
	
	public boolean isServer() {
		return true;
	}
}
