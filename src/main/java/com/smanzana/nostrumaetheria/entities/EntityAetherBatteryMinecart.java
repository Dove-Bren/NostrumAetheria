package com.smanzana.nostrumaetheria.entities;

import com.smanzana.nostrumaetheria.blocks.AetherPumpBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrumaetheria.tiles.AetherPumpBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityAetherBatteryMinecart extends MinecartEntity {
	
	// Most of this originally taken/sampled from Botania's mana cart
	
	public static final String ID = "aether_battery_minecart";
	
	private static final DataParameter<Integer> AETHER = EntityDataManager.createKey(EntityAetherBatteryMinecart.class, DataSerializers.VARINT);

	public EntityAetherBatteryMinecart(EntityType<?> type, World worldIn) {
		super(type, worldIn);
	}
	
	public EntityAetherBatteryMinecart(EntityType<?> type, World worldIn, double x, double y, double z) {
		this(type, worldIn);
		this.setPosition(x, y, z);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		
		dataManager.register(AETHER, 0);
	}
	
	@Override
	public BlockState getDisplayTile() {
		return AetheriaBlocks.mediumBattery.getDefaultState();
	}
	
	@Override
	public ItemStack getCartItem() {
		return new ItemStack(AetheriaItems.aetherBatteryMinecart);
	}

	@Override
	public Type getMinecartType() {
		return Type.RIDEABLE;
	}
	
	@Override
	public boolean canBeRidden() {
		return false;
	}
	
	@Override
	protected void applyDrag() {
		this.setMotion(this.getMotion().mul(.98, 0, .98));
	}
	
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return getCartItem();
	}
	
	@Override
	public void killMinecart(DamageSource source) {
		//super.killMinecart(source);
		
		this.remove();
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			ItemStack itemstack = new ItemStack(AetheriaItems.aetherBatteryMinecart, 1);

			if (this.hasCustomName()) {
				itemstack.setDisplayName(this.getName());
			}

			this.entityDropItem(itemstack, 0.0F);
        }
	}
	
	@Override
	public int getDefaultDisplayTileOffset() {
		return 4;
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public void moveMinecartOnRail(BlockPos pos) {
		super.moveMinecartOnRail(pos);
		
		if (world.isRemote) {
			return;
		}
		
		// Look for nearby pumps
		for (Direction dir : new Direction[] {Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.UP}) {
			final BlockPos at = pos.offset(dir);
			BlockState state = world.getBlockState(at);
			if (state.getBlock() instanceof AetherPumpBlock) {
				TileEntity te = world.getTileEntity(at);
				if (te != null && te instanceof AetherPumpBlockEntity) {
					AetherPumpBlockEntity ent = (AetherPumpBlockEntity) te;
					final Direction pumpDir = ((AetherPumpBlock) state.getBlock()).getFacing(state);
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
	protected void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		
		nbt.putInt("cart_aether", this.getAether());
	}
	
	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		
		this.setAether(nbt.getInt("cart_aether"));
	}
	
	public int getAether() {
		return dataManager.get(AETHER);
	}
	
	public void setAether(int aether) {
		dataManager.set(AETHER, aether);
	}
	
	public int getMaxAether() {
		return AetheriaBlocks.mediumBattery.getMaxAether();
	}
}
