package com.smanzana.nostrumaetheria.integration.curios.items;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.LoreRegistry;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaCurios {

	@ObjectHolder(ShieldRingItem.ID_SMALL) public static @Nullable ShieldRingItem ringShieldSmall; // Requires Aether
	@ObjectHolder(ShieldRingItem.ID_LARGE) public static @Nullable ShieldRingItem ringShieldLarge; // Requires Aether
	@ObjectHolder(EludeCloakItem.ID) public static @Nullable EludeCloakItem eludeCape; // Requires Aether
	@ObjectHolder(AetherCloakItem.ID) public static @Nullable AetherCloakItem aetherCloak; // Requires Aether
	@ObjectHolder(AetherSightPendant.ID) public static @Nullable AetherSightPendant sightPendant;
	
	public static Item.Properties PropBase() {
		return new Item.Properties()
				.tab(APIProxy.creativeTab)
				;
	}
	
	public static Item.Properties PropCurio() {
		return PropBase()
				.stacksTo(1)
				;
	}
	
	public AetheriaCurios() {
		
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
		
		register(registry, new ShieldRingItem(2, ShieldRingItem.ID_SMALL).setRegistryName(ShieldRingItem.ID_SMALL));
		register(registry, new ShieldRingItem(4, ShieldRingItem.ID_LARGE).setRegistryName(ShieldRingItem.ID_LARGE));
		register(registry, new EludeCloakItem().setRegistryName(EludeCloakItem.ID));
		register(registry, new AetherCloakItem().setRegistryName(AetherCloakItem.ID));
		register(registry, new AetherSightPendant().setRegistryName(AetherSightPendant.ID));
	}
}
