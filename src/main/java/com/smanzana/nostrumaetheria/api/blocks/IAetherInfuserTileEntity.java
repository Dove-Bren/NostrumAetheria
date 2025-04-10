package com.smanzana.nostrumaetheria.api.blocks;

import net.minecraft.world.level.Level;

public interface IAetherInfuserTileEntity {

	// Note: can't be named getWorld() as that's the same name as a vanilla obfuscated name
	public Level getInfuserWorld();
	
}
