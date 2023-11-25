package com.smanzana.nostrumaetheria.api.aether;

import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

/**
 * Optional version of AetherHandler that has world position information, which can be
 * used for display. For example, relays use this info for particles about flow.
 * @author Skyler
 *
 */
public interface IWorldAetherHandler extends IAetherHandler {

	public @Nullable DimensionType getDimension();
	
	public @Nullable BlockPos getPosition();
	
}
