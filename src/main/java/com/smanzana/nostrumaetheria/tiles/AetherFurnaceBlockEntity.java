package com.smanzana.nostrumaetheria.tiles;

import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock.Type;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class AetherFurnaceBlockEntity extends AetherFurnaceGenericTileEntity {

	private static final String NBT_TYPE = "type";
	
	private Type type;
	
	public AetherFurnaceBlockEntity(BlockPos pos, BlockState state) {
		this(pos, state, Type.SMALL);
	}
	
	public AetherFurnaceBlockEntity(BlockPos pos, BlockState state, Type type) {
		super(AetheriaTileEntities.Furnace, pos, state, AetherFurnaceBlock.getFurnaceSlotsForType(type), 0, 500);
		this.type = type;
		
	}
	
	public Type getFurnceType() {
		return this.type;
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		
		nbt.putString(NBT_TYPE, type.name());
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		try {
			this.type = Type.valueOf(nbt.getString(NBT_TYPE));
		} catch (Exception e) {
			this.type = Type.SMALL;
		}
	}
	
	@Override
	protected float getAetherMultiplier() {
		return this.type.getAetherMultiplier();
	}

	@Override
	protected float getDurationMultiplier() {
		return this.type.getDurationMultiplier();
	}

	@Override
	protected void onBurningChange(boolean newBurning) {
		level.setBlockAndUpdate(worldPosition, AetherFurnaceBlock.GetForType(getFurnceType()).defaultBlockState().setValue(AetherFurnaceBlock.ON, newBurning));
	}
}