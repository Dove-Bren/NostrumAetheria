package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.NostrumAetheria;
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
	
	@ObjectHolder(InfiniteAetherBlock.ID) public static InfiniteAetherBlock infiteAetherBlock;
	@ObjectHolder(AetherBatteryBlock.ID_SMALL) public static AetherBatteryBlock smallBattery;
	@ObjectHolder(AetherBatteryBlock.ID_MEDIUM) public static AetherBatteryBlock mediumBattery;
	@ObjectHolder(AetherBatteryBlock.ID_LARGE) public static AetherBatteryBlock largeBattery;
	@ObjectHolder(AetherBatteryBlock.ID_GIANT) public static AetherBatteryBlock giantBattery;
	@ObjectHolder(AetherRelay.ID) public static AetherRelay relay;
	@ObjectHolder(AetherFurnaceBlock.ID_SMALL) public static AetherFurnaceBlock smallFurnace;
	@ObjectHolder(AetherFurnaceBlock.ID_MEDIUM) public static AetherFurnaceBlock mediumFurnace;
	@ObjectHolder(AetherFurnaceBlock.ID_LARGE) public static AetherFurnaceBlock largeFurnace;
	@ObjectHolder(AetherBoilerBlock.ID) public static AetherBoilerBlock boiler;
	@ObjectHolder(AetherBathBlock.ID) public static AetherBathBlock bath;
	@ObjectHolder(AetherChargerBlock.ID) public static AetherChargerBlock charger;
	@ObjectHolder(AetherRepairerBlock.ID) public static AetherRepairerBlock repairer;
	@ObjectHolder(AetherUnravelerBlock.ID) public static AetherUnravelerBlock unraveler;
	@ObjectHolder(AetherPumpBlock.ID) public static AetherPumpBlock pump;
	@ObjectHolder(WispBlock.ID) public static Block wispBlock;
	@ObjectHolder(AetherInfuser.ID) public static Block infuser;

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
		
    	registerBlock(new InfiniteAetherBlock(), InfiniteAetherBlock.ID, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.SMALL), AetherBatteryBlock.ID_SMALL, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.MEDIUM), AetherBatteryBlock.ID_MEDIUM, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.LARGE), AetherBatteryBlock.ID_LARGE, registry);
    	registerBlock(new AetherBatteryBlock(AetherBatteryBlock.Size.GIANT), AetherBatteryBlock.ID_GIANT, registry);
    	registerBlock(new AetherRelay(), AetherRelay.ID, registry);
    	registerBlock(new AetherFurnaceBlock(AetherFurnaceBlock.Type.SMALL), AetherFurnaceBlock.ID_SMALL, registry);
    	registerBlock(new AetherFurnaceBlock(AetherFurnaceBlock.Type.MEDIUM), AetherFurnaceBlock.ID_MEDIUM, registry);
    	registerBlock(new AetherFurnaceBlock(AetherFurnaceBlock.Type.LARGE), AetherFurnaceBlock.ID_LARGE, registry);
    	registerBlock(new AetherBoilerBlock(), AetherBoilerBlock.ID, registry);
    	registerBlock(new AetherBathBlock(), AetherBathBlock.ID, registry);
    	registerBlock(new AetherChargerBlock(), AetherChargerBlock.ID, registry);
    	registerBlock(new AetherRepairerBlock(), AetherRepairerBlock.ID, registry);
    	registerBlock(new AetherUnravelerBlock(), AetherUnravelerBlock.ID, registry);
    	registerBlock(new AetherPumpBlock(), AetherPumpBlock.ID, registry);
    	registerBlock(new WispBlock(), WispBlock.ID, registry);
    	registerBlock(new AetherInfuser(), AetherInfuser.ID, registry);
	}
}
