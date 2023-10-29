package com.smanzana.nostrumaetheria.gui.container;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaContainers {

	
	
	@ObjectHolder(WispBlockGui.WispBlockContainer.ID) public static ContainerType<WispBlockGui.WispBlockContainer> WispBlock;
	
	
	@SubscribeEvent
	public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
		final IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
		
		registry.register(IForgeContainerType.create(WispBlockGui.WispBlockContainer::FromNetwork).setRegistryName(WispBlockGui.WispBlockContainer.ID));
	}
	
	@SubscribeEvent
	public static void registerContainerScreens(FMLClientSetupEvent event) {
		
		ScreenManager.registerFactory(WispBlock, WispBlockGui.WispBlockGuiContainer::new);
	}
}
