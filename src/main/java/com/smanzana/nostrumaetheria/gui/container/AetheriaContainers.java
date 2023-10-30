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

	@ObjectHolder(ActivePendantGui.ActivePendantContainer.ID) public static ContainerType<ActivePendantGui.ActivePendantContainer> ActivePendant;
	@ObjectHolder(AetherBoilerGui.AetherBoilerContainer.ID) public static ContainerType<AetherBoilerGui.AetherBoilerContainer> Boiler;
	@ObjectHolder(AetherChargerGui.AetherChargerContainer.ID) public static ContainerType<AetherChargerGui.AetherChargerContainer> Charger;
	@ObjectHolder(AetherFurnaceGui.AetherFurnaceContainer.ID) public static ContainerType<AetherFurnaceGui.AetherFurnaceContainer> Furnace;
	@ObjectHolder(AetherRepairerGui.AetherRepairerContainer.ID) public static ContainerType<AetherRepairerGui.AetherRepairerContainer> Repairer;
	@ObjectHolder(AetherUnravelerGui.AetherUnravelerContainer.ID) public static ContainerType<AetherUnravelerGui.AetherUnravelerContainer> Unraveler;
	@ObjectHolder(WispBlockGui.WispBlockContainer.ID) public static ContainerType<WispBlockGui.WispBlockContainer> WispBlock;
	
	
	@SubscribeEvent
	public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
		final IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
		
		registry.register(IForgeContainerType.create(ActivePendantGui.ActivePendantContainer::FromNetwork).setRegistryName(ActivePendantGui.ActivePendantContainer.ID));
		registry.register(IForgeContainerType.create(AetherBoilerGui.AetherBoilerContainer::FromNetwork).setRegistryName(AetherBoilerGui.AetherBoilerContainer.ID));
		registry.register(IForgeContainerType.create(AetherChargerGui.AetherChargerContainer::FromNetwork).setRegistryName(AetherChargerGui.AetherChargerContainer.ID));
		registry.register(IForgeContainerType.create(AetherFurnaceGui.AetherFurnaceContainer::FromNetwork).setRegistryName(AetherFurnaceGui.AetherFurnaceContainer.ID));
		registry.register(IForgeContainerType.create(AetherRepairerGui.AetherRepairerContainer::FromNetwork).setRegistryName(AetherRepairerGui.AetherRepairerContainer.ID));
		registry.register(IForgeContainerType.create(AetherUnravelerGui.AetherUnravelerContainer::FromNetwork).setRegistryName(AetherUnravelerGui.AetherUnravelerContainer.ID));
		registry.register(IForgeContainerType.create(WispBlockGui.WispBlockContainer::FromNetwork).setRegistryName(WispBlockGui.WispBlockContainer.ID));
	}
	
	@SubscribeEvent
	public static void registerContainerScreens(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(ActivePendant, ActivePendantGui.ActivePendantGuiContainer::new);
		ScreenManager.registerFactory(Boiler, AetherBoilerGui.AetherBoilerGuiContainer::new);
		ScreenManager.registerFactory(Charger, AetherChargerGui.AetherChargerGuiContainer::new);
		ScreenManager.registerFactory(Furnace, AetherFurnaceGui.AetherFurnaceGuiContainer::new);
		ScreenManager.registerFactory(Repairer, AetherRepairerGui.AetherRepairerGuiContainer::new);
		ScreenManager.registerFactory(Unraveler, AetherUnravelerGui.AetherUnravelerGuiContainer::new);
		ScreenManager.registerFactory(WispBlock, WispBlockGui.WispBlockGuiContainer::new);
	}
}
