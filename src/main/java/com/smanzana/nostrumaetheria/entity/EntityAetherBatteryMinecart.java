package com.smanzana.nostrumaetheria.entity;

import com.smanzana.nostrumaetheria.blocks.AetherPumpBlock;
import com.smanzana.nostrumaetheria.blocks.AetheriaBlocks;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrumaetheria.tiles.AetherPumpBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class EntityAetherBatteryMinecart extends Minecart {
	
	// Most of this originally taken/sampled from Botania's mana cart
	
	public static final String ID = "aether_battery_minecart";
	
	private static final EntityDataAccessor<Integer> AETHER = SynchedEntityData.defineId(EntityAetherBatteryMinecart.class, EntityDataSerializers.INT);

	public EntityAetherBatteryMinecart(EntityType<? extends EntityAetherBatteryMinecart> type, Level worldIn) {
		super(type, worldIn);
	}
	
	public EntityAetherBatteryMinecart(EntityType<? extends EntityAetherBatteryMinecart> type, Level worldIn, double x, double y, double z) {
		this(type, worldIn);
		this.setPos(x, y, z);
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		
		entityData.define(AETHER, 0);
	}
	
	@Override
	public BlockState getDisplayBlockState() {
		return AetheriaBlocks.mediumBattery.defaultBlockState();
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
	protected void applyNaturalSlowdown() {
		this.setDeltaMovement(this.getDeltaMovement().multiply(.98, 0, .98));
	}
	
	@Override
	public ItemStack getPickedResult(HitResult target) {
		return getCartItem();
	}
	
	@Override
	public void destroy(DamageSource source) {
		//super.killMinecart(source);
		
		this.discard();
		if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			ItemStack itemstack = new ItemStack(AetheriaItems.aetherBatteryMinecart, 1);

			if (this.hasCustomName()) {
				itemstack.setHoverName(this.getName());
			}

			this.spawnAtLocation(itemstack, 0.0F);
        }
	}
	
	@Override
	public int getDefaultDisplayOffset() {
		return 4;
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public void moveMinecartOnRail(BlockPos pos) {
		super.moveMinecartOnRail(pos);
		
		if (level.isClientSide) {
			return;
		}
		
		// Look for nearby pumps
		for (Direction dir : new Direction[] {Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.UP}) {
			final BlockPos at = pos.relative(dir);
			BlockState state = level.getBlockState(at);
			if (state.getBlock() instanceof AetherPumpBlock) {
				BlockEntity te = level.getBlockEntity(at);
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
	protected void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		
		nbt.putInt("cart_aether", this.getAether());
	}
	
	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		
		this.setAether(nbt.getInt("cart_aether"));
	}
	
	public int getAether() {
		return entityData.get(AETHER);
	}
	
	public void setAether(int aether) {
		entityData.set(AETHER, aether);
	}
	
	public int getMaxAether() {
		return AetheriaBlocks.mediumBattery.getMaxAether();
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		// Have to override and use forge to use with non-living Entity types even though parent defines
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
