package com.smanzana.nostrumaetheria.tiles;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock.Size;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class AetherBatteryEntity extends NativeAetherTickingTileEntity {

	private static final String NBT_SIZE = "battery_size";
	
	private Size size;
	
	public AetherBatteryEntity(Size size) {
		super(0, size.capacity);
		this.size = size;
		this.setAutoSync(5);
	}
	
	public AetherBatteryEntity() {
		this(Size.SMALL);
	}
	
	public Size getSize() {
		return size;
	}

	@Override
	public void update() {
		if (this.ticksExisted % 5 == 0) {
			this.flowIntoNearby();
		}
		
		super.update();
	}
	
	protected void flowIntoNearby() {
		// Look for adjacent batteries to flow into or fill from.
		// Get total sum'ed aether to figure out how much it looks like each should have.
		AetherBatteryEntity[] batteries = new AetherBatteryEntity[EnumFacing.values().length];
		int myAether = this.handler.getAether(null);
		
		// First, try to flow down.
		if (handler.getSideEnabled(EnumFacing.DOWN)) {
			TileEntity te = world.getTileEntity(pos.down());
			if (te != null && te instanceof AetherBatteryEntity) {
				AetherBatteryEntity other = (AetherBatteryEntity) te;
				final int startAether = myAether;
				myAether = other.handler.addAether(null, myAether, true);
				this.handler.drawAether(null, startAether - myAether);
			}
		}
		
		if (myAether > 0) {
			int totalAether = myAether;
			int neighborCount = 0;
			int max = totalAether;
			int min = totalAether;
			for (EnumFacing dir : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST}) {
				if (!handler.getSideEnabled(dir)) {
					continue;
				}
				
				BlockPos neighbor = pos.offset(dir);
				
				// First check for a TileEntity
				TileEntity te = world.getTileEntity(neighbor);
				if (te != null && te instanceof AetherBatteryEntity) {
					AetherBatteryEntity other = (AetherBatteryEntity) te;
					batteries[dir.ordinal()] = other;
					int aether = other.getHandler().getAether(dir.getOpposite());
					totalAether += aether;
					if (aether > max) {
						max = aether;
					}
					if (aether < min) {
						min = aether;
					}
					neighborCount++;
				}
			}
			
			List<AetherBatteryEntity> entities = Lists.newArrayList(batteries);
			entities.add(this);
			neighborCount++;
			while (neighborCount > 0 && totalAether > 0 && (max - min) > 2) {
				// Found neighbors. How much should be in each?
				int each = totalAether / (neighborCount);
				int spillover = (totalAether % (neighborCount));
				int i = 0;
				totalAether = 0;
				
				// Repeat for each found battery
				// Note: Using a shuffled list to prevent spillover from causing bad surface tension
				Collections.shuffle(entities);
				Iterator<AetherBatteryEntity> iter = entities.iterator();
				while (iter.hasNext()) {
					AetherBatteryEntity other = iter.next();
					if (other != null) {
						int amt = (other.handler.getAether(null) - each + (spillover > i ? 1 : 0));
						if (amt > 0) {
							other.handler.drawAether(null, amt);
							amt = 0;
						} else if (amt < 0) {
							amt = other.handler.addAether(null, -amt, true);
						}
						
						// if any 'amt' is leftover, block couldn't fit it all
						if (amt > 0) {
							totalAether += amt;
							neighborCount--;
							iter.remove(); // remove because we know this one can't fit any more
						}
						
						i++;
					} else {
						iter.remove();
					}
				}
				
				if (totalAether > 0) {
					// at least one block couldn't fit all aether. Repeat loop.
					// Add (count * each + leftover) to arrive at total for new subset
					totalAether += (each * (neighborCount)) + spillover;
				}
			}
		}
	}
	
	@Override
	public void onAetherFlowTick(int diff, boolean added, boolean taken) {
		super.onAetherFlowTick(diff, added, taken);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		
		nbt.setString(NBT_SIZE, this.size.name());
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		try {
			this.size = Size.valueOf(nbt.getString(NBT_SIZE));
		} catch (Exception e) {
			this.size = Size.SMALL;
		}
	}
}