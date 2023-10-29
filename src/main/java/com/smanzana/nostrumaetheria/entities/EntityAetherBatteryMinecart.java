package com.smanzana.nostrumaetheria.entities;

import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.blocks.AetherBatteryBlock;
import com.smanzana.nostrumaetheria.blocks.AetherPumpBlock;
import com.smanzana.nostrumaetheria.items.AetherBatteryMinecartItem;
import com.smanzana.nostrumaetheria.tiles.AetherPumpBlockEntity;

import net.minecraft.block.state.BlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityAetherBatteryMinecart extends EntityMinecart {
	
	// Most of this taken/sampled from Botania's mana cart
	
	private static final DataParameter<Integer> AETHER = EntityDataManager.createKey(EntityAetherBatteryMinecart.class, DataSerializers.VARINT);

	public EntityAetherBatteryMinecart(World worldIn) {
		super(worldIn);
	}
	
	public EntityAetherBatteryMinecart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		
		dataManager.register(AETHER, 0);
	}
	
	@Override
	public BlockState getDisplayTile() {
		return AetherBatteryBlock.medium().getDefaultState();
	}
	
	@Override
	public ItemStack getCartItem() {
		return new ItemStack(AetherBatteryMinecartItem.instance());
	}

	@Override
	public Type getType() {
		return Type.RIDEABLE;
	}
	
	@Override
	public boolean canBeRidden() {
		return false;
	}
	
	@Override
	protected void applyDrag() {
		this.motionX *= .98;
		this.motionY *= 0;
		this.motionZ *= .98;
	}
	
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return getCartItem();
	}
	
	@Override
	public void killMinecart(DamageSource source) {
		//super.killMinecart(source);
		
		this.setDead();
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemstack = new ItemStack(APIProxy.AetherBatteryMinecartItem, 1);

			if (this.hasCustomName()) {
				itemstack.setStackDisplayName(this.getName());
			}

			this.entityDropItem(itemstack, 0.0F);
        }
	}
	
	@Override
	public int getDefaultDisplayTileOffset() {
		return 4;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
	}
	
	@Override
	public void moveMinecartOnRail(BlockPos pos) {
		super.moveMinecartOnRail(pos);
		
		if (world.isRemote) {
			return;
		}
		
		// Look for nearby pumps
		for (EnumFacing dir : new EnumFacing[] {EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.UP}) {
			final BlockPos at = pos.offset(dir);
			BlockState state = world.getBlockState(at);
			if (state.getBlock() instanceof AetherPumpBlock) {
				TileEntity te = world.getTileEntity(at);
				if (te != null && te instanceof AetherPumpBlockEntity) {
					AetherPumpBlockEntity ent = (AetherPumpBlockEntity) te;
					final EnumFacing pumpDir = AetherPumpBlock.instance().getFacing(state);
					if (pumpDir == dir.getOpposite()) {
						//Taking from
						final int leftover = ent.getHandler().addAether(dir.getOpposite(), this.getAether());
						this.setAether(leftover);
					} else if (pumpDir == dir) {
						// Pushing INTO
						final int room = Math.min(this.getMaxAether() - this.getAether(), 20);
						final int drawn = ent.getHandler().drawAether(dir.getOpposite(), room);
						if (drawn > 0) {
							this.setAether(this.getAether() + drawn);
						}
					}
			}
			}
		}
	}
	
	@Override
	protected void writeEntityToNBT(CompoundNBT nbt) {
		super.writeEntityToNBT(nbt);
		
		nbt.putInt("cart_aether", this.getAether());
	}
	
	@Override
	public void readEntityFromNBT(CompoundNBT nbt) {
		super.readEntityFromNBT(nbt);
		
		this.setAether(nbt.getInt("cart_aether"));
	}
	
	public int getAether() {
		return dataManager.get(AETHER);
	}
	
	public void setAether(int aether) {
		dataManager.set(AETHER, aether);
	}
	
	public int getMaxAether() {
		return AetherBatteryBlock.medium().getMaxAether();
	}
}
