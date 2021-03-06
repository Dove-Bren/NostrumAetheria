package com.smanzana.nostrumaetheria.blocks.tiles;

import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock;
import com.smanzana.nostrumaetheria.blocks.AetherFurnaceBlock.Type;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AetherFurnaceBlockEntity extends AetherFurnaceGenericTileEntity {

	private static final String NBT_TYPE = "type";
	
	private Type type;
	
	public AetherFurnaceBlockEntity() {
		this(Type.SMALL);
	}
	
	public AetherFurnaceBlockEntity(Type type) {
		super(AetherFurnaceBlock.getFurnaceSlotsForType(type), 0, 500);
		this.type = type;
		
	}
	
	public Type getType() {
		return this.type;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		
		nbt.setString(NBT_TYPE, type.name());
		
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		try {
			this.type = Type.valueOf(nbt.getString(NBT_TYPE));
		} catch (Exception e) {
			this.type = Type.SMALL;
		}
	}
	
	@Override
	public String getName() {
		return "Aether Furnace Inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return !(oldState.getBlock().equals(newState.getBlock()) && AetherFurnaceBlock.instance().getType(oldState) == AetherFurnaceBlock.instance().getType(newState));
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
		world.setBlockState(pos, AetherFurnaceBlock.instance().getDefaultState().withProperty(AetherFurnaceBlock.TYPE, this.type).withProperty(AetherFurnaceBlock.ON, newBurning));
	}
}