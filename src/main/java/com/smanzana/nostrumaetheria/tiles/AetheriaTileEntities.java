package com.smanzana.nostrumaetheria.tiles;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.blocks.AetherInfuser;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.blocks.WispBlock;

import net.minecraft.tileentity.TileEntityType;
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
	private static final String ID_WISPBLOCK = WispBlock.ID + "_entity";
	private static final String ID_INFUSER = AetherInfuser.ID + "_entity";

	@ObjectHolder(ID_BATH) public static TileEntityType<AetherBathTileEntity> Bath;
	@ObjectHolder(ID_BATTERY) public static TileEntityType<AetherBatteryEntity> Battery;
	@ObjectHolder(ID_BOILER) public static TileEntityType<AetherBoilerBlockEntity> Boiler;
	@ObjectHolder(ID_CHARGER) public static TileEntityType<AetherChargerBlockEntity> Charger;
	@ObjectHolder(ID_FURNACE) public static TileEntityType<AetherFurnaceBlockEntity> Furnace;
	@ObjectHolder(ID_PUMP) public static TileEntityType<AetherPumpBlockEntity> Pump;
	@ObjectHolder(ID_RELAY) public static TileEntityType<AetherRelayEntity> Relay;
	@ObjectHolder(ID_REPAIRER) public static TileEntityType<AetherRepairerBlockEntity> Repairer;
	@ObjectHolder(ID_UNRAVELER) public static TileEntityType<AetherUnravelerBlockEntity> Unraveler;
	@ObjectHolder(ID_INFINITE) public static TileEntityType<InfiniteAetherBlockEntity> InfiniteBlock;
	@ObjectHolder(ID_WISPBLOCK) public static TileEntityType<WispBlockTileEntity> WispBlockEnt;
	@ObjectHolder(ID_INFUSER) public static TileEntityType<AetherInfuserTileEntity> AetherInfuserEnt;
	
	private static void register(IForgeRegistry<TileEntityType<?>> registry, TileEntityType<?> type, String ID) {
		registry.register(type.setRegistryName(ID));
	}
	
	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
		final IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

    	register(registry, TileEntityType.Builder.create(AetherBathTileEntity::new, AetheriaBlocks.bath).build(null), ID_BATH);
    	register(registry, TileEntityType.Builder.create(AetherBatteryEntity::new, AetheriaBlocks.smallBattery, AetheriaBlocks.mediumBattery, AetheriaBlocks.largeBattery, AetheriaBlocks.giantBattery).build(null), ID_BATTERY);
    	register(registry, TileEntityType.Builder.create(AetherBoilerBlockEntity::new, AetheriaBlocks.boiler).build(null), ID_BOILER);
    	register(registry, TileEntityType.Builder.create(AetherChargerBlockEntity::new, AetheriaBlocks.charger).build(null), ID_CHARGER);
    	register(registry, TileEntityType.Builder.create(AetherFurnaceBlockEntity::new, AetheriaBlocks.smallFurnace, AetheriaBlocks.mediumFurnace, AetheriaBlocks.largeFurnace).build(null), ID_FURNACE);
    	register(registry, TileEntityType.Builder.create(AetherPumpBlockEntity::new, AetheriaBlocks.pump).build(null), ID_PUMP);
		register(registry, TileEntityType.Builder.create(AetherRelayEntity::new, AetheriaBlocks.relay).build(null), ID_RELAY);
    	register(registry, TileEntityType.Builder.create(AetherRepairerBlockEntity::new, AetheriaBlocks.repairer).build(null), ID_REPAIRER);
    	register(registry, TileEntityType.Builder.create(AetherUnravelerBlockEntity::new, AetheriaBlocks.unraveler).build(null), ID_UNRAVELER);
    	register(registry, TileEntityType.Builder.create(InfiniteAetherBlockEntity::new, AetheriaBlocks.infiteAetherBlock).build(null), ID_INFINITE);
    	register(registry, TileEntityType.Builder.create(WispBlockTileEntity::new, AetheriaBlocks.wispBlock).build(null), ID_WISPBLOCK);
    	register(registry, TileEntityType.Builder.create(AetherInfuserTileEntity::new, AetheriaBlocks.infuser).build(null), ID_INFUSER);
	}
}
