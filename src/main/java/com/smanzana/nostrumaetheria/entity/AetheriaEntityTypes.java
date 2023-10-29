package com.smanzana.nostrumaetheria.entity;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = NostrumAetheria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(NostrumAetheria.MODID)
public class AetheriaEntityTypes {

	@ObjectHolder(EntityAetherBatteryMinecart.ID) public static EntityType<EntityAetherBatteryMinecart> batteryCart;
	@ObjectHolder(SentinelWispEntity.ID) public static EntityType<SentinelWispEntity> sentinelWisp;
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		final IForgeRegistry<EntityType<?>> registry = event.getRegistry();
		
		registry.register(EntityType.Builder.<EntityAetherBatteryMinecart>create(EntityAetherBatteryMinecart::new, EntityClassification.MISC)
				.size(0.98F, 0.7F)
				.setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(false)
			.build("").setRegistryName(EntityAetherBatteryMinecart.ID));
		
		registry.register(EntityType.Builder.<SentinelWispEntity>create(SentinelWispEntity::new, EntityClassification.CREATURE)
				.setTrackingRange(64).setUpdateInterval(1).setShouldReceiveVelocityUpdates(false)
				.size(.75F, .75F)
			.build("").setRegistryName(SentinelWispEntity.ID));
	}
	
}
