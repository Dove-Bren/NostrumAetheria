package com.smanzana.nostrumaetheria.capability;

import com.smanzana.nostrumaetheria.NostrumAetheria;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityHandler {

	public static final ResourceLocation CAPABILITY_AETHER_BURNABLE_LOC = new ResourceLocation(NostrumAetheria.MODID, "aether_burnable");
	
	public CapabilityHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		//if player. Or not.
//		if (event.getObject() instanceof PlayerEntity) {
//			//event.addCapability(CAPABILITY_MAGIC_LOC, new NostrumMagicAttributeProvider(event.getObject()));
//		}
	}
	
	@SubscribeEvent
	public void onClone(PlayerEvent.Clone event) {
		//if (event.isWasDeath()) {
//			INostrumMagic cap = NostrumMagica.getMagicWrapper(event.getOriginal());
//			event.getPlayer().getCapability(NostrumMagicAttributeProvider.CAPABILITY, null).orElse(null)
//				.copy(cap);
		//}
		//if (!event.getEntityPlayer().world.isRemote)
		//	NostrumMagica.instance.proxy.syncPlayer((ServerPlayerEntity) event.getEntityPlayer());
	}
}
