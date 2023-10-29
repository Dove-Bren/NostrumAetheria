package com.smanzana.nostrumaetheria.entities;

import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumMagica.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumMagica.MODID)
public class AetheriaEntityTypes {

	@ObjectHolder(EntityAetherBatteryMinecart.ID) public static EntityType<EntityAetherBatteryMinecart> batteryCart;
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		final IForgeRegistry<EntityType<?>> registry = event.getRegistry();
		
		registry.register(EntityType.Builder.create(EntityAetherBatteryMinecart::new, EntityClassification.MISC)
				.size(0.98F, 0.7F)
				.setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(false)
			.build("").setRegistryName(EntityAetherBatteryMinecart.ID));
	}
	
}
