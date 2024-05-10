package com.smanzana.nostrumaetheria.tiles;

import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock.Type;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class AetherFurnaceBlockEntity extends AetherFurnaceGenericTileEntity {

	private static final String NBT_TYPE = "type";
	
	private Type type;
	
	public AetherFurnaceBlockEntity() {
		this(Type.SMALL);
	}
	
	public AetherFurnaceBlockEntity(Type type) {
		super(AetheriaTileEntities.Furnace, AetherFurnaceBlock.getFurnaceSlotsForType(type), 0, 500);
		this.type = type;
		
	}
	
	public Type getFurnceType() {
		return this.type;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		
		nbt.putString(NBT_TYPE, type.name());
		
		return nbt;
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		
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
		world.setBlockState(pos, AetherFurnaceBlock.GetForType(getFurnceType()).getDefaultState().with(AetherFurnaceBlock.ON, newBurning));
	}
}