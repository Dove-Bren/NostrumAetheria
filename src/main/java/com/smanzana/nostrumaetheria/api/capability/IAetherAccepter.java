package com.smanzana.nostrumaetheria.api.capability;

import com.smanzana.nostrumaetheria.api.blocks.IAetherInfuserTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * Marks that this capability-holder might have some behavior based on receiving aether from a nearby aether infuser
 */
public interface IAetherAccepter {
	
	public static final Capability<IAetherAccepter> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	
	/**
	 * Quick check whether this tile entity can even attempt to accept any aether.
	 * No work should be done by this func besides checking if things look roughly right.
	 * @param source
	 * @param maxAether
	 * @return
	 */
	public boolean canAcceptAether(IAetherInfuserTileEntity source, BlockPos pos, int maxAether);
	
	/**
	 * Attempt to accept some aether from an Aether Infuser.
	 * Not all aether must be taken. Instead, return what couldn't be used.
	 * @param source
	 * @param maxAether Max aether the infuser can provide in this call
	 * @return
	 */
	public int acceptAether(IAetherInfuserTileEntity source, BlockPos pos, int maxAether);
	
}
