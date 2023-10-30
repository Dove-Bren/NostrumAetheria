package com.smanzana.nostrumaetheria.items;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
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

	@ObjectHolder(ActivePendant.ID) public static ActivePendant activePendant = null;
	@ObjectHolder(PassivePendant.ID) public static PassivePendant passivePendant = null;
	@ObjectHolder(AetherGem.ID) public static AetherGem aetherGem = null;
	@ObjectHolder(AetherBatteryMinecartItem.ID) public static AetherBatteryMinecartItem aetherBatteryMinecart = null;
	@ObjectHolder(NostrumAetherResourceItem.ID_GINSENG_FLOWER) public static NostrumAetherResourceItem ginsengFlower;
	@ObjectHolder(NostrumAetherResourceItem.ID_MANDRAKE_FLOWER) public static NostrumAetherResourceItem mandrakeFlower;
	@ObjectHolder(ItemAetherLens.ID_SPREAD) public static ItemAetherLens spreadAetherLens;
	@ObjectHolder(ItemAetherLens.ID_CHARGE) public static ItemAetherLens chargeAetherLens;
	@ObjectHolder(ItemAetherLens.ID_GROW) public static ItemAetherLens growAetherLens;
	@ObjectHolder(ItemAetherLens.ID_SWIFTNESS) public static ItemAetherLens swiftnessAetherLens;
	@ObjectHolder(ItemAetherLens.ID_ELEVATOR) public static ItemAetherLens elevatorAetherLens;
	@ObjectHolder(ItemAetherLens.ID_HEAL) public static ItemAetherLens healAetherLens;
	@ObjectHolder(ItemAetherLens.ID_BORE) public static ItemAetherLens boreAetherLens;
	@ObjectHolder(ItemAetherLens.ID_BORE_REVERSED) public static ItemAetherLens reversedBoreAetherLens;
	@ObjectHolder(ItemAetherLens.ID_MANA_REGEN) public static ItemAetherLens manaRegenAetherLens;
	@ObjectHolder(ItemAetherLens.ID_NO_SPAWN) public static ItemAetherLens noSpawnAetherLens;
	
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
		
		register(registry, new ActivePendant().setRegistryName(ActivePendant.ID));
		register(registry, new PassivePendant().setRegistryName(PassivePendant.ID));
		register(registry, new AetherGem().setRegistryName(AetherGem.ID));
		register(registry, new AetherBatteryMinecartItem().setRegistryName(AetherBatteryMinecartItem.ID));
		registry.register(new NostrumAetherResourceItem(300, 450, PropBase()).setRegistryName(NostrumAetherResourceItem.ID_GINSENG_FLOWER));
		registry.register(new NostrumAetherResourceItem(300, 350, PropBase()).setRegistryName(NostrumAetherResourceItem.ID_MANDRAKE_FLOWER));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.SPREAD, PropBase()).setRegistryName(ItemAetherLens.ID_SPREAD));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.CHARGE, PropBase()).setRegistryName(ItemAetherLens.ID_CHARGE));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.GROW, PropBase()).setRegistryName(ItemAetherLens.ID_GROW));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.SWIFTNESS, PropBase()).setRegistryName(ItemAetherLens.ID_SWIFTNESS));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.ELEVATOR, PropBase()).setRegistryName(ItemAetherLens.ID_ELEVATOR));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.HEAL, PropBase()).setRegistryName(ItemAetherLens.ID_HEAL));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.BORE, PropBase()).setRegistryName(ItemAetherLens.ID_BORE));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.BORE_REVERSED, PropBase()).setRegistryName(ItemAetherLens.ID_BORE_REVERSED));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.MANA_REGEN, PropBase()).setRegistryName(ItemAetherLens.ID_MANA_REGEN));
    	registry.register(new ItemAetherLens(ItemAetherLens.LensType.NO_SPAWN, PropBase()).setRegistryName(ItemAetherLens.ID_NO_SPAWN));
	}
	
}
