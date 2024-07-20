package com.smanzana.nostrumaetheria.capability;

import com.smanzana.nostrumaetheria.api.capability.IAetherBurnable;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class AetherBurnableCapabilityProvider implements ICapabilitySerializable<INBT> {

	@CapabilityInject(IAetherBurnable.class)
	public static Capability<IAetherBurnable> CAPABILITY = null;
	
	private IAetherBurnable instance = CAPABILITY.getDefaultInstance();
	
	public AetherBurnableCapabilityProvider() {
		;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CAPABILITY) {
			return CAPABILITY.orEmpty(capability, LazyOptional.of(() -> instance));
		}
		
		return LazyOptional.empty();
	}

	@Override
	public INBT serializeNBT() {
		return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance, null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		CAPABILITY.getStorage().readNBT(CAPABILITY, instance, null, nbt);
	}

}
