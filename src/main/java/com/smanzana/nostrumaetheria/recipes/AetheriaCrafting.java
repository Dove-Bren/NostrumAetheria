package com.smanzana.nostrumaetheria.recipes;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaCrafting {

	@ObjectHolder(AetherCloakColorRecipe.Serializer.ID) public static AetherCloakColorRecipe.Serializer aetherCloakColorSerializer;
	@ObjectHolder(AetherCloakToggleRecipe.Serializer.ID) public static AetherCloakToggleRecipe.Serializer aetherCloakToggleSerializer;
	
	@SubscribeEvent
	public static void registerSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		final IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
		
		registry.register(new AetherCloakColorRecipe.Serializer().setRegistryName(AetherCloakColorRecipe.Serializer.ID));
		registry.register(new AetherCloakToggleRecipe.Serializer().setRegistryName(AetherCloakToggleRecipe.Serializer.ID));
	}
}
