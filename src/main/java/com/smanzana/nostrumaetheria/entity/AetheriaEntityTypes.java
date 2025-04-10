package com.smanzana.nostrumaetheria.entity;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
		
		registry.register(EntityType.Builder.<EntityAetherBatteryMinecart>of(EntityAetherBatteryMinecart::new, MobCategory.MISC)
				.sized(0.98F, 0.7F)
				.setTrackingRange(128).setUpdateInterval(1).setShouldReceiveVelocityUpdates(false)
			.build("").setRegistryName(EntityAetherBatteryMinecart.ID));
		
		registry.register(EntityType.Builder.<SentinelWispEntity>of(SentinelWispEntity::new, MobCategory.CREATURE)
				.setTrackingRange(64).setUpdateInterval(1).setShouldReceiveVelocityUpdates(false)
				.sized(.75F, .75F)
			.build("").setRegistryName(SentinelWispEntity.ID));
	}
	
	@SubscribeEvent
	public static void registerEntityPlacement(FMLCommonSetupEvent event) {
		; // No naturally spawning entities
	}
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(sentinelWisp, SentinelWispEntity.BuildSentinelAttributes().build());
		// No attributes event.put(batteryCart, EntityAetherBatteryMinecart.BuildAttributes().create());
	}
	
}
