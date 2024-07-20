package com.smanzana.nostrumaetheria.capability;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.capability.IAetherBurnable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants.NBT;

public class AetherBurnableCapability implements IAetherBurnable {
	
	private int burnTicks;
	private float aetherYield;
	
	public AetherBurnableCapability(int burnTicks, float aetherYield) {
		this.burnTicks = burnTicks;
		this.aetherYield = aetherYield;
	}
	
	public AetherBurnableCapability() {
		this(20, 100);
	}

	@Override
	public int getBurnTicks() {
		return burnTicks;
	}

	@Override
	public float getAetherYield() {
		return aetherYield;
	}
	
	public static class Serializer implements IStorage<IAetherBurnable> {
		
		public static final Serializer INSTANCE = new Serializer();
		
		private static final String NBT_BURN_TICKS = "burn_ticks";
		private static final String NBT_AETHER = "aether";
		
		protected Serializer() {
			
		}
	
		@Override
		public INBT writeNBT(Capability<IAetherBurnable> capability, IAetherBurnable instanceIn, Direction side) {
			AetherBurnableCapability instance = (AetherBurnableCapability) instanceIn;
			CompoundNBT nbt = new CompoundNBT();
			
			nbt.putInt(NBT_BURN_TICKS, instance.burnTicks);
			nbt.putFloat(NBT_AETHER, instance.aetherYield);
			
			return nbt;
		}

		@Override
		public void readNBT(Capability<IAetherBurnable> capability, IAetherBurnable instanceIn, Direction side, INBT nbtIn) {
			AetherBurnableCapability instance = (AetherBurnableCapability) instanceIn;
			
			if (nbtIn.getId() == NBT.TAG_COMPOUND) {
				CompoundNBT nbt = (CompoundNBT) nbtIn;
				
				NostrumAetheria.logger.fatal("Writing burn capability?");
				instance.burnTicks = nbt.getInt(NBT_BURN_TICKS);
				instance.aetherYield = nbt.getFloat(NBT_AETHER);
			}
		}
	}

}
