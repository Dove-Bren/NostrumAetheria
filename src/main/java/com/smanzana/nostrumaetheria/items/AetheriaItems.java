package com.smanzana.nostrumaetheria.items;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.LoreRegistry;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumMagica.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumMagica.MODID)
public class AetheriaItems {

	@ObjectHolder(ActivePendant.ID) public static ActivePendant activePendant = null;
	@ObjectHolder(PassivePendant.ID) public static PassivePendant passivePendant = null;
	@ObjectHolder(AetherGem.ID) public static AetherGem aetherGem = null;
	@ObjectHolder(AetherBatteryMinecartItem.ID) public static AetherBatteryMinecartItem aetherBatteryMinecart = null;
	
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
	}
	
}
