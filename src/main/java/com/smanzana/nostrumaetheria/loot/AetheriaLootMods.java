package com.smanzana.nostrumaetheria.loot;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaLootMods {

	@ObjectHolder(AddItemMod.Serializer.ID) public static AddItemMod.Serializer addItem;
	
	@SubscribeEvent
	public static void registerSerializers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
		final IForgeRegistry<GlobalLootModifierSerializer<?>> registry = event.getRegistry();
		
		registry.register(new AddItemMod.Serializer().setRegistryName(AddItemMod.Serializer.ID));
	}
	
}
