package com.smanzana.nostrumaetheria.tiles;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.AetheriaIDs;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaTileEntities {

	private static final String ID_BATH = "nostrum_aether_altar_te";
	private static final String ID_BATTERY = "aether_battery_te";
	private static final String ID_BOILER = "aether_boiler_block_te";
	private static final String ID_CHARGER = "aether_charger_te";
	private static final String ID_FURNACE = "aether_furnace_block_te";
	private static final String ID_PUMP = "aether_pump_te";
	private static final String ID_RELAY = "aether_relay_te";
	private static final String ID_REPAIRER = "aether_repairer_te";
	private static final String ID_UNRAVELER = "aether_unraveler_te";	
	private static final String ID_INFINITE = "infinite_aether_block_te";
	private static final String ID_WISPBLOCK = AetheriaIDs.WISP_BLOCK + "_entity";
	private static final String ID_INFUSER = AetheriaIDs.AETHER_INFUSER + "_entity";
	private static final String ID_ENHANCED_RELAY = "enhanced_aether_relay_te";
	private static final String ID_LENS_HOLDER = "lens_holder";

	@ObjectHolder(ID_BATH) public static BlockEntityType<AetherBathTileEntity> Bath;
	@ObjectHolder(ID_BATTERY) public static BlockEntityType<AetherBatteryEntity> Battery;
	@ObjectHolder(ID_BOILER) public static BlockEntityType<AetherBoilerBlockEntity> Boiler;
	@ObjectHolder(ID_CHARGER) public static BlockEntityType<AetherChargerBlockEntity> Charger;
	@ObjectHolder(ID_FURNACE) public static BlockEntityType<AetherFurnaceBlockEntity> Furnace;
	@ObjectHolder(ID_PUMP) public static BlockEntityType<AetherPumpBlockEntity> Pump;
	@ObjectHolder(ID_RELAY) public static BlockEntityType<AetherRelayEntity> Relay;
	@ObjectHolder(ID_REPAIRER) public static BlockEntityType<AetherRepairerBlockEntity> Repairer;
	@ObjectHolder(ID_UNRAVELER) public static BlockEntityType<AetherUnravelerBlockEntity> Unraveler;
	@ObjectHolder(ID_INFINITE) public static BlockEntityType<InfiniteAetherBlockEntity> InfiniteBlock;
	@ObjectHolder(ID_WISPBLOCK) public static BlockEntityType<WispBlockTileEntity> WispBlockEnt;
	@ObjectHolder(ID_INFUSER) public static BlockEntityType<AetherInfuserTileEntity> AetherInfuserEnt;
	@ObjectHolder(ID_ENHANCED_RELAY) public static BlockEntityType<EnhancedAetherRelayEntity> EnhancedRelay;
	@ObjectHolder(ID_LENS_HOLDER) public static BlockEntityType<LensHolderBlockEntity> LensHolder;
	
	private static void register(IForgeRegistry<BlockEntityType<?>> registry, BlockEntityType<?> type, String ID) {
		registry.register(type.setRegistryName(ID));
	}
	
	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
		final IForgeRegistry<BlockEntityType<?>> registry = event.getRegistry();

    	register(registry, BlockEntityType.Builder.of(AetherBathTileEntity::new, AetheriaBlocks.bath).build(null), ID_BATH);
    	register(registry, BlockEntityType.Builder.of(AetherBatteryEntity::new, AetheriaBlocks.smallBattery, AetheriaBlocks.mediumBattery, AetheriaBlocks.largeBattery, AetheriaBlocks.giantBattery).build(null), ID_BATTERY);
    	register(registry, BlockEntityType.Builder.of(AetherBoilerBlockEntity::new, AetheriaBlocks.boiler).build(null), ID_BOILER);
    	register(registry, BlockEntityType.Builder.of(AetherChargerBlockEntity::new, AetheriaBlocks.charger).build(null), ID_CHARGER);
    	register(registry, BlockEntityType.Builder.of(AetherFurnaceBlockEntity::new, AetheriaBlocks.smallFurnace, AetheriaBlocks.mediumFurnace, AetheriaBlocks.largeFurnace).build(null), ID_FURNACE);
    	register(registry, BlockEntityType.Builder.of(AetherPumpBlockEntity::new, AetheriaBlocks.pump).build(null), ID_PUMP);
		register(registry, BlockEntityType.Builder.of(AetherRelayEntity::new, AetheriaBlocks.relay).build(null), ID_RELAY);
    	register(registry, BlockEntityType.Builder.of(AetherRepairerBlockEntity::new, AetheriaBlocks.repairer).build(null), ID_REPAIRER);
    	register(registry, BlockEntityType.Builder.of(AetherUnravelerBlockEntity::new, AetheriaBlocks.unraveler).build(null), ID_UNRAVELER);
    	register(registry, BlockEntityType.Builder.of(InfiniteAetherBlockEntity::new, AetheriaBlocks.infiteAetherBlock).build(null), ID_INFINITE);
    	register(registry, BlockEntityType.Builder.of(WispBlockTileEntity::new, AetheriaBlocks.wispBlock).build(null), ID_WISPBLOCK);
    	register(registry, BlockEntityType.Builder.of(AetherInfuserTileEntity::new, AetheriaBlocks.infuser).build(null), ID_INFUSER);
    	register(registry, BlockEntityType.Builder.of(EnhancedAetherRelayEntity::new, AetheriaBlocks.enhancedRelay).build(null), ID_ENHANCED_RELAY);
    	register(registry, BlockEntityType.Builder.of(LensHolderBlockEntity::new, AetheriaBlocks.lensHolder).build(null), ID_LENS_HOLDER);
	}
}
