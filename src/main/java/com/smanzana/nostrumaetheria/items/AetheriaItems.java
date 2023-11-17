package com.smanzana.nostrumaetheria.items;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.api.proxy.AetheriaIDs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.LoreRegistry;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaItems {

	@ObjectHolder(AetheriaIDs.ACTIVE_PENDANT) public static ActivePendant activePendant = null;
	@ObjectHolder(AetheriaIDs.PASSIVE_PENDANT) public static PassivePendant passivePendant = null;
	@ObjectHolder(AetheriaIDs.AETHER_GEM) public static AetherGem aetherGem = null;
	@ObjectHolder(AetheriaIDs.AETHER_BATTERY_MINECART) public static AetherBatteryMinecartItem aetherBatteryMinecart = null;
	@ObjectHolder(AetheriaIDs.GINSENG_FLOWER) public static NostrumAetherResourceItem ginsengFlower;
	@ObjectHolder(AetheriaIDs.MANDRAKE_FLOWER) public static NostrumAetherResourceItem mandrakeFlower;
	@ObjectHolder(AetheriaIDs.LENS_SPREAD) public static ItemAetherLens spreadAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_CHARGE) public static ItemAetherLens chargeAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_GROW) public static ItemAetherLens growAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_SWIFTNESS) public static ItemAetherLens swiftnessAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_ELEVATOR) public static ItemAetherLens elevatorAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_HEAL) public static ItemAetherLens healAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_BORE) public static ItemAetherLens boreAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_BORE_REVERSED) public static ItemAetherLens reversedBoreAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_MANA_REGEN) public static ItemAetherLens manaRegenAetherLens;
	@ObjectHolder(AetheriaIDs.LENS_NO_SPAWN) public static ItemAetherLens noSpawnAetherLens;
	
	public static Item.Properties PropBase() {
		return new Item.Properties()
				.group(APIProxy.creativeTab)
				;
	}
	
	public static Item.Properties PropUnstackable() {
		return PropBase()
				.maxStackSize(1);
	}
	
	public static Item.Properties PropEquipment() {
		return PropUnstackable()
				;
	}
	
	private static final void register(IForgeRegistry<Item> registry, Item item) {
		registry.register(item);
		
		if (item instanceof ILoreTagged) {
			LoreRegistry.instance().register((ILoreTagged) item);
		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();
		
		register(registry, new ActivePendant().setRegistryName(AetheriaIDs.ACTIVE_PENDANT));
		register(registry, new PassivePendant().setRegistryName(AetheriaIDs.PASSIVE_PENDANT));
		register(registry, new AetherGem().setRegistryName(AetheriaIDs.AETHER_GEM));
		register(registry, new AetherBatteryMinecartItem().setRegistryName(AetheriaIDs.AETHER_BATTERY_MINECART));
		register(registry, new NostrumAetherResourceItem(300, 450, PropBase()).setRegistryName(AetheriaIDs.GINSENG_FLOWER));
		register(registry, new NostrumAetherResourceItem(300, 350, PropBase()).setRegistryName(AetheriaIDs.MANDRAKE_FLOWER));
		register(registry, new ItemAetherLens(ItemAetherLens.LensType.SPREAD, PropBase()).setRegistryName(AetheriaIDs.LENS_SPREAD));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.CHARGE, PropBase()).setRegistryName(AetheriaIDs.LENS_CHARGE));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.GROW, PropBase()).setRegistryName(AetheriaIDs.LENS_GROW));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.SWIFTNESS, PropBase()).setRegistryName(AetheriaIDs.LENS_SWIFTNESS));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.ELEVATOR, PropBase()).setRegistryName(AetheriaIDs.LENS_ELEVATOR));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.HEAL, PropBase()).setRegistryName(AetheriaIDs.LENS_HEAL));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.BORE, PropBase()).setRegistryName(AetheriaIDs.LENS_BORE));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.BORE_REVERSED, PropBase()).setRegistryName(AetheriaIDs.LENS_BORE_REVERSED));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.MANA_REGEN, PropBase()).setRegistryName(AetheriaIDs.LENS_MANA_REGEN));
    	register(registry, new ItemAetherLens(ItemAetherLens.LensType.NO_SPAWN, PropBase()).setRegistryName(AetheriaIDs.LENS_NO_SPAWN));
	}
	
}
