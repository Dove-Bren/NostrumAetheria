package com.smanzana.nostrumaetheria.api.blocks;

import java.util.List;

import com.smanzana.nostrumaetheria.api.aether.IAetherFlowHandler.AetherFlowConnection;
import com.smanzana.nostrumaetheria.api.component.AetherHandlerComponent;
import com.smanzana.nostrumaetheria.api.component.AetherHandlerComponent.AetherComponentListener;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandlerProvider;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AetherTileEntity extends TileEntity implements IAetherHandlerProvider, AetherComponentListener {

	private static final String NBT_HANDLER = "aether_handler";
	
	protected AetherHandlerComponent handler;
	
	public AetherTileEntity(int defaultAether, int defaultMaxAether) {
		handler = createComponent(defaultAether, defaultMaxAether);
	}
	
	public AetherTileEntity() {
		this(0, 0);
	}
	
	protected AetherHandlerComponent createComponent(int defaultAether, int defaultMaxAether) {
		return new AetherHandlerComponent(this, defaultAether, defaultMaxAether);
	}
	
	@Override
	public void addConnections(List<AetherFlowConnection> connections) {
		for (EnumFacing dir : EnumFacing.values()) {
			if (!handler.getSideEnabled(dir)) {
				continue;
			}
			
			BlockPos neighbor = pos.offset(dir);
			
			// First check for a TileEntity
			TileEntity te = worldObj.getTileEntity(neighbor);
			if (te != null && te instanceof IAetherHandler) {
				connections.add(new AetherFlowConnection((IAetherHandler) te, dir.getOpposite()));
				continue;
			}
			if (te != null && te instanceof IAetherHandlerProvider) {
				connections.add(new AetherFlowConnection(((IAetherHandlerProvider) te).getHandler(), dir.getOpposite()));
				continue;
			}
			
			// See if block boasts being able to get us a handler
			IBlockState attachedState = worldObj.getBlockState(neighbor);
			Block attachedBlock = attachedState.getBlock();
			if (attachedBlock instanceof IAetherCapableBlock) {
				connections.add(new AetherFlowConnection(((IAetherCapableBlock) attachedBlock).getAetherHandler(worldObj, attachedState, neighbor, dir), dir.getOpposite()));
				continue;
			}
		}
	}
	
	@Override
	public void dirty() {
		this.markDirty();
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		;
	}
	
	@Override
	public void validate() {
		super.validate();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		
		// Clean up connections
		handler.clearConnections();
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		handler.clearConnections();
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		compound.setTag(NBT_HANDLER, handler.writeToNBT(new NBTTagCompound()));
		
		return compound;
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		handler.readFromNBT(compound.getCompoundTag(NBT_HANDLER));
	}

	@SideOnly(Side.CLIENT)
	public void syncAether(int aether) {
		this.handler.setAether(aether);
	}
	
	@Override
	public IAetherHandler getHandler() {
		return handler;
	}
}
