package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.AetheriaIDs;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.LoreRegistry;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaBlocks {
	
	@ObjectHolder(AetheriaIDs.INFINITE_AETHER_BLOCK) public static InfiniteAetherBlock infiteAetherBlock;
	@ObjectHolder(AetheriaIDs.SMALL_BATTERY) public static AetherBatteryBlock smallBattery;
	@ObjectHolder(AetheriaIDs.MEDIUM_BATTERY) public static AetherBatteryBlock mediumBattery;
	@ObjectHolder(AetheriaIDs.LARGE_BATTERY) public static AetherBatteryBlock largeBattery;
	@ObjectHolder(AetheriaIDs.GIANT_BARRY) public static AetherBatteryBlock giantBattery;
	@ObjectHolder(AetheriaIDs.RELAY) public static AetherRelay relay;
	@ObjectHolder(AetheriaIDs.ENHANCED_RELAY) public static EnhancedAetherRelay enhancedRelay;
	@ObjectHolder(AetheriaIDs.SMALL_FURNACE) public static AetherFurnaceBlock smallFurnace;
	@ObjectHolder(AetheriaIDs.MEDIUM_FURNACE) public static AetherFurnaceBlock mediumFurnace;
	@ObjectHolder(AetheriaIDs.LARGE_FURNACE) public static AetherFurnaceBlock largeFurnace;
	@ObjectHolder(AetheriaIDs.BOILER) public static AetherBoilerBlock boiler;
	@ObjectHolder(AetheriaIDs.BATH) public static AetherBathBlock bath;
	@ObjectHolder(AetheriaIDs.CHARGER) public static AetherChargerBlock charger;
	@ObjectHolder(AetheriaIDs.REPAIRER) public static AetherRepairerBlock repairer;
	@ObjectHolder(AetheriaIDs.UNRAVELER) public static AetherUnravelerBlock unraveler;
	@ObjectHolder(AetheriaIDs.PUMP) public static AetherPumpBlock pump;
	@ObjectHolder(AetheriaIDs.WISP_BLOCK) public static Block wispBlock;
	@ObjectHolder(AetheriaIDs.AETHER_INFUSER) public static Block infuser;

	private static void registerBlockItem(Block block, ResourceLocation registryName, Item.Properties builder, IForgeRegistry<Item> registry) {
		BlockItem item = new BlockItem(block, builder);
    	item.setRegistryName(registryName);
    	registry.register(item);
	}
	
	private static void registerBlockItem(Block block, ResourceLocation registryName, IForgeRegistry<Item> registry) {
		registerBlockItem(block, registryName, AetheriaItems.PropBase(), registry);
	}
	
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	final IForgeRegistry<Item> registry = event.getRegistry();
    	
    	registerBlockItem(infiteAetherBlock, infiteAetherBlock.getRegistryName(), registry);
    	registerBlockItem(smallBattery, smallBattery.getRegistryName(), registry);
    	registerBlockItem(mediumBattery, mediumBattery.getRegistryName(), registry);
    	registerBlockItem(largeBattery, largeBattery.getRegistryName(), registry);
    	registerBlockItem(giantBattery, giantBattery.getRegistryName(), registry);
    	registerBlockItem(relay, relay.getRegistryName(), registry);
    	registerBlockItem(smallFurnace, smallFurnace.getRegistryName(), registry);
    	registerBlockItem(mediumFurnace, mediumFurnace.getRegistryName(), registry);
    	registerBlockItem(largeFurnace, largeFurnace.getRegistryName(), registry);
    	registerBlockItem(boiler, boiler.getRegistryName(), registry);
    	registerBlockItem(bath, bath.getRegistryName(), registry);
    	registerBlockItem(charger, charger.getRegistryName(), registry);
    	registerBlockItem(repairer, repairer.getRegistryName(), registry);
    	registerBlockItem(unraveler, unraveler.getRegistryName(), registry);
    	registerBlockItem(pump, pump.getRegistryName(), registry);
    	registerBlockItem(wispBlock, wispBlock.getRegistryName(), registry);
    	registerBlockItem(infuser, infuser.getRegistryName(), registry);
    	registerBlockItem(enhancedRelay, enhancedRelay.getRegistryName(), registry);
    }
    
    private static void registerBlock(Block block, String registryName, IForgeRegistry<Block> registry) {
    	block.setRegistryName(registryName);
    	registry.register(block);
    	
    	if (block instanceof ILoreTagged) {
    		LoreRegistry.instance().register((ILoreTagged)block);
    	}
    }
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();
		
    	registerBlock(new InfiniteAetherBlock(), AetheriaIDs.INFINITE_AETHER_BLOCK, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.SMALL), AetheriaIDs.SMALL_BATTERY, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.MEDIUM), AetheriaIDs.MEDIUM_BATTERY, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.LARGE), AetheriaIDs.LARGE_BATTERY, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.GIANT), AetheriaIDs.GIANT_BARRY, registry);
    	registerBlock(new AetherRelay(), AetheriaIDs.RELAY, registry);
    	registerBlock(new AetherFurnaceBlock(AetherFurnaceBlock.Type.SMALL), AetheriaIDs.SMALL_FURNACE, registry);
    	registerBlock(new AetherFurnaceBlock(AetherFurnaceBlock.Type.MEDIUM), AetheriaIDs.MEDIUM_FURNACE, registry);
    	registerBlock(new AetherFurnaceBlock(AetherFurnaceBlock.Type.LARGE), AetheriaIDs.LARGE_FURNACE, registry);
    	registerBlock(new AetherBoilerBlock(), AetheriaIDs.BOILER, registry);
    	registerBlock(new AetherBathBlock(), AetheriaIDs.BATH, registry);
    	registerBlock(new AetherChargerBlock(), AetheriaIDs.CHARGER, registry);
    	registerBlock(new AetherRepairerBlock(), AetheriaIDs.REPAIRER, registry);
    	registerBlock(new AetherUnravelerBlock(), AetheriaIDs.UNRAVELER, registry);
    	registerBlock(new AetherPumpBlock(), AetheriaIDs.PUMP, registry);
    	registerBlock(new WispBlock(), AetheriaIDs.WISP_BLOCK, registry);
    	registerBlock(new AetherInfuser(), AetheriaIDs.AETHER_INFUSER, registry);
    	registerBlock(new EnhancedAetherRelay(), AetheriaIDs.ENHANCED_RELAY, registry);
	}
}
