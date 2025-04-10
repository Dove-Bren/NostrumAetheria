package com.smanzana.nostrumaetheria.client.gui.container;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaContainers {

	@ObjectHolder(ActivePendantGui.ActivePendantContainer.ID) public static MenuType<ActivePendantGui.ActivePendantContainer> ActivePendant;
	@ObjectHolder(AetherBoilerGui.AetherBoilerContainer.ID) public static MenuType<AetherBoilerGui.AetherBoilerContainer> Boiler;
	@ObjectHolder(AetherChargerGui.AetherChargerContainer.ID) public static MenuType<AetherChargerGui.AetherChargerContainer> Charger;
	@ObjectHolder(AetherFurnaceGui.AetherFurnaceContainer.ID) public static MenuType<AetherFurnaceGui.AetherFurnaceContainer> Furnace;
	@ObjectHolder(AetherRepairerGui.AetherRepairerContainer.ID) public static MenuType<AetherRepairerGui.AetherRepairerContainer> Repairer;
	@ObjectHolder(AetherUnravelerGui.AetherUnravelerContainer.ID) public static MenuType<AetherUnravelerGui.AetherUnravelerContainer> Unraveler;
	@ObjectHolder(WispBlockGui.WispBlockContainer.ID) public static MenuType<WispBlockGui.WispBlockContainer> WispBlock;
	
	
	@SubscribeEvent
	public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
		final IForgeRegistry<MenuType<?>> registry = event.getRegistry();
		
		registry.register(IForgeContainerType.create(ActivePendantGui.ActivePendantContainer::FromNetwork).setRegistryName(ActivePendantGui.ActivePendantContainer.ID));
		registry.register(IForgeContainerType.create(AetherBoilerGui.AetherBoilerContainer::FromNetwork).setRegistryName(AetherBoilerGui.AetherBoilerContainer.ID));
		registry.register(IForgeContainerType.create(AetherChargerGui.AetherChargerContainer::FromNetwork).setRegistryName(AetherChargerGui.AetherChargerContainer.ID));
		registry.register(IForgeContainerType.create(AetherFurnaceGui.AetherFurnaceContainer::FromNetwork).setRegistryName(AetherFurnaceGui.AetherFurnaceContainer.ID));
		registry.register(IForgeContainerType.create(AetherRepairerGui.AetherRepairerContainer::FromNetwork).setRegistryName(AetherRepairerGui.AetherRepairerContainer.ID));
		registry.register(IForgeContainerType.create(AetherUnravelerGui.AetherUnravelerContainer::FromNetwork).setRegistryName(AetherUnravelerGui.AetherUnravelerContainer.ID));
		registry.register(IForgeContainerType.create(WispBlockGui.WispBlockContainer::FromNetwork).setRegistryName(WispBlockGui.WispBlockContainer.ID));
	}
}
