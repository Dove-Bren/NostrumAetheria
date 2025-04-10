package com.smanzana.nostrumaetheria.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.api.blocks.IAetherInfusableTileEntity;
import com.smanzana.nostrumaetheria.api.blocks.IAetherInfuserTileEntity;
import com.smanzana.nostrumaetheria.api.item.IAetherInfuserLens;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.items.AetheriaItems;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.particles.NostrumParticles;
import com.smanzana.nostrummagica.client.particles.NostrumParticles.SpawnParams;
import com.smanzana.nostrummagica.tile.AltarTileEntity;
import com.smanzana.nostrummagica.util.Inventories.ItemStackArrayWrapper;
import com.smanzana.nostrummagica.util.WorldUtil;
import com.smanzana.petcommand.api.entity.ITameableEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AetherInfuserTileEntity extends AetherTickingTileEntity implements IAetherInfuserTileEntity {

	private static final String NBT_CHARGE = "charge";
	public static final int MAX_CHARGE = 5000;
	public static final int CHARGE_PER_TICK = 100;
	
	private static final int MAX_SPARKS = 20;
	
	// Synced+saved
	private int charge;
	
	// Transient
	private boolean active; // use getter+setter to sync to client
	private @Nullable AltarTileEntity centerAltar;
	private Map<BlockPos, IAetherInfusableTileEntity> nearbyChargeables; // note: NOT center altar
	private int lastScanRadius;
	
	// Client-only + transient
	private int effectTime; // forever-growing at rate dependent on 'active'
	@OnlyIn(Dist.CLIENT)
	private List<AetherInfuserTileEntity.EffectSpark> sparks;
	
	public AetherInfuserTileEntity(BlockPos pos, BlockState state) {
		super(AetheriaTileEntities.AetherInfuserEnt, pos, state, 0, MAX_CHARGE);
		this.setAutoSync(5);
		this.compWrapper.configureInOut(true, false);
		nearbyChargeables = new HashMap<>();
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public static final void DoChargeEffect(LivingEntity entity, int count, int color) {
		NostrumParticles.GLOW_ORB.spawn(entity.getCommandSenderWorld(), new SpawnParams(
				count,
				entity.getX(), entity.getY() + entity.getBbHeight()/2f, entity.getZ(), 2.0,
				40, 0,
				entity.getId()
				).color(color));
	}
	
	public static final void DoChargeEffect(Level world, BlockPos pos, int count, int color) {
		DoChargeEffect(world, new Vec3(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5), count, color);
	}
	
	public static final void DoChargeEffect(Level world, Vec3 center, int count, int color) {
		NostrumParticles.GLOW_ORB.spawn(world, new SpawnParams(
				count,
				center.x, center.y, center.z, 2.0,
				40, 0,
				center
				).color(color));
	}
	
	public static final void DoChargeEffect(Level world, Vec3 start, Vec3 end, int count, int color) {
		NostrumParticles.GLOW_ORB.spawn(world, new SpawnParams(
				count,
				start.x, start.y, start.z, .5,
				60, 20,
				end
				).color(color));
	}
	
	protected void chargePlayer(Player player) {
		int chargeAmount = Math.min(CHARGE_PER_TICK, this.getCharge());
		final int startAmount = chargeAmount;
		// Try both regular inventory and bauble inventory
		Container inv = player.getInventory();
		chargeAmount -= APIProxy.pushToInventory(level, player, inv, chargeAmount);
		
		inv = NostrumMagica.instance.curios.getCurios(player);
		if (inv != null) {
			chargeAmount -= APIProxy.pushToInventory(level, player, inv, chargeAmount);
		}
		
		if (startAmount != chargeAmount) {
			this.getHandler().drawAether(null, startAmount - chargeAmount);
			
			final int diff = startAmount - chargeAmount;
			float countRaw = (float) diff / (float) (CHARGE_PER_TICK / 3);
			final int whole = (int) countRaw;
			if (whole > 0 || NostrumMagica.rand.nextFloat() < countRaw) {
//					NostrumParticles.GLOW_ORB.spawn(world, new SpawnParams(
//							whole > 0 ? whole : 1,
//							player.posX, player.posY + player.height/2f, player.posZ, 2.0,
//							40, 0,
//							player.getEntityId()
//							));
				DoChargeEffect(player, whole > 0 ? whole : 1, 0x4D3366FF);
			}
		}
		
		// TODO look at held item for lenses
	}
	
	protected void chargeAltar(BlockPos pos, AltarTileEntity te) {
		int chargeAmount = Math.min(CHARGE_PER_TICK, this.getCharge());
		final int startAmount = chargeAmount;
		if (te != null && !te.getItem().isEmpty()) {
			ItemStack held = te.getItem();
			Container inv = new ItemStackArrayWrapper(new ItemStack[] {held});
			chargeAmount -= APIProxy.pushToInventory(level, null, inv, chargeAmount);
		}
		
		if (startAmount != chargeAmount) {
			this.getHandler().drawAether(null, startAmount - chargeAmount);
			// Set number to spawn based on how much aether we actually put in
			final int diff = startAmount - chargeAmount;
			float countRaw = (float) diff / (float) (CHARGE_PER_TICK / 3);
			final int whole = (int) countRaw;
			if (whole > 0 || NostrumMagica.rand.nextFloat() < countRaw) {
//					NostrumParticles.GLOW_ORB.spawn(world, new SpawnParams(
//							whole > 0 ? whole : 1,
//							pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5, 2.0,
//							40, 0,
//							new Vector3d(pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5)
//							));
				DoChargeEffect(level, new Vec3(pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5), whole > 0 ? whole : 1, 0x4D3366FF);
			}
		}
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (level.isClientSide) {
			effectTime++;
			
			if (this.isActive()) {
				effectTime++; // double speed
			}
			
			this.updateSparks();
			
			if (this.getCharge() > 0) {
				// extra particles
				final float CHANCE = (float) getCharge() / ((float) MAX_CHARGE * 1f);
				final float RADIUS = 3;
				final Random rand = NostrumMagica.rand;
				if (NostrumMagica.rand.nextFloat() < CHANCE) {
					final double x = (worldPosition.getX() + .5 + (NostrumMagica.rand.nextFloat() * RADIUS)) - (RADIUS / 2f);
					final double y = (worldPosition.getY() + 1.5 + (NostrumMagica.rand.nextFloat() * RADIUS)) - 1;
					final double z = (worldPosition.getZ() + .5 + (NostrumMagica.rand.nextFloat() * RADIUS)) - (RADIUS / 2f);
					level.addParticle(ParticleTypes.MYCELIUM,
							x, y, z,
							0, 0, 0);
					
					int num = (active ? 10 : NostrumMagica.rand.nextFloat() < .05f ? 1 : 0);
					NostrumParticles.GLOW_ORB.spawn(level, new SpawnParams(
							num,
							x, y, z, 0, 100, 20,
							new Vec3(rand.nextFloat() * .05 - .025, rand.nextFloat() * .05, rand.nextFloat() * .05 - .025), null
						).color(.1f, .3f, 1f, .4f));
				}
			}
			
			return;
		}
		
		// First, use our set up altar if we have one
		if (level.getGameTime() % 5 == 0) {
			refreshAltar();
		}
		
		if (this.centerAltar != null && !this.hasAreaCharge()) {
			// Altar!
			if (this.hasLens()) {
				int maxAether = Math.min(CHARGE_PER_TICK, this.getCharge());
				final int originalMax = maxAether;
				int lastAether = maxAether;
				if (this.hasAreaInfuse()) {
					for (IAetherInfusableTileEntity te : this.nearbyChargeables.values()) {
						if (te.canAcceptAetherInfuse(this, maxAether)) {
							maxAether = te.acceptAetherInfuse(this, maxAether);
							
							if (lastAether != maxAether) {
								if (te instanceof BlockEntity) {
									final BlockEntity teRaw = (BlockEntity) te;
									final int diff = lastAether - maxAether;
									float countRaw = (float) diff / (float) (CHARGE_PER_TICK / 3);
									final int whole = (int) countRaw;
									if (whole > 0 || NostrumMagica.rand.nextFloat() < countRaw) {
										DoChargeEffect(level,
												new Vec3(worldPosition.getX() + .5, worldPosition.getY() + 1.2, worldPosition.getZ() + .5),
												new Vec3(teRaw.getBlockPos().getX() + .5, teRaw.getBlockPos().getY() + 1.2, teRaw.getBlockPos().getZ() + .5),
												whole > 0 ? whole : 1,
												0x4D3366FF);
										
										
									}
								}
								
								lastAether = maxAether;
							}
						}
					}
				} else if (this.hasMobSpawnPrevention()) {
					; // No work to do here but don't want to tick lens either
				} else {
					// Do whatever lense says to do
					ItemStack lensItem = centerAltar.getItem();
					IAetherInfuserLens lens = (IAetherInfuserLens) lensItem.getItem();
					if (lens.canAcceptAetherInfuse(lensItem, worldPosition.above(), this, maxAether)) {
						maxAether = lens.acceptAetherInfuse(lensItem, worldPosition.above(), this, maxAether);
						
						if (originalMax != maxAether) {
							final int diff = lastAether - maxAether;
							float countRaw = (float) diff / (float) (CHARGE_PER_TICK / 3);
							final int whole = (int) countRaw;
							if (whole > 0 || NostrumMagica.rand.nextFloat() < countRaw) {
								DoChargeEffect(level,
										new Vec3(centerAltar.getBlockPos().getX() + .5, centerAltar.getBlockPos().getY() + 1.2, centerAltar.getBlockPos().getZ() + .5),
										whole > 0 ? whole : 1,
										0x4D3366FF);
							}
						}
					}
				}
				
				if (maxAether != originalMax) {
					this.getHandler().drawAether(null, originalMax - maxAether);
				}
			} else {
				chargeAltar(worldPosition.above(), (AltarTileEntity) level.getBlockEntity(worldPosition.above()));
			}
		} else {
			// Check for entities in AoE
			final int radius = this.hasAreaCharge() ? this.getChargeAreaRadius() : 4; // 4 is size of bubble
			final BlockPos min = (worldPosition.offset(-radius, -radius, -radius));
			final BlockPos max = (worldPosition.offset(radius, radius, radius));
			List<Player> candidates = level.getEntitiesOfClass(Player.class, new AABB(
					min, max
					));
			Player minPlayer = null;
			double minDist = Double.MAX_VALUE;
			final double radiusSq = radius * radius;
			for (Player player : candidates) {
				final double dist = player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5 + 2, worldPosition.getZ() + .5);
				if (dist < radiusSq && dist < minDist) {
					minDist = dist;
					minPlayer = player;
				}
			}
			
			if (minPlayer != null) {
				chargePlayer(minPlayer);
			}
		}
		
	}
	
	@Override
	public CompoundTag save(CompoundTag nbt) {
		nbt = super.save(nbt);
		
		if (nbt == null)
			nbt = new CompoundTag();
		
		nbt.putInt(NBT_CHARGE, charge);
		
		return nbt;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		if (nbt == null)
			return;
		
		this.charge = nbt.getInt(NBT_CHARGE);
	}
	
	@Override
	public void setLevel(Level world) {
		super.setLevel(world);
		
		if (!world.isClientSide) {
			this.compWrapper.setAutoFill(true, 20);
		} else {
			this.sparks = new ArrayList<>();
		}
		
		NostrumMagica.playerListener.registerTimer((type, entity, data)->{
			//Event type, LivingEntity entity, Object data
			refreshNearbyBlocks();
			return true;
		}, 1, 0);
	}
	
	public int getCharge() {
		// Convenience wrapper around all the optional aether bits
		return this.getHandler().getAether(null); // We require aether to work anyways so being unsafe
	}
	
	public float getChargePerc() {
		return ((float) getCharge()) / (float) MAX_CHARGE;
	}
	
	@Override
	public boolean triggerEvent(int id, int type) {
		if (id == 0) {
			if (this.level != null && this.level.isClientSide) {
				setActive(type == 1);
			}
			return true;
		}
		
		return super.triggerEvent(id, type);
	}
	
	protected void onActiveChange() {
		
	}
	
	private void setActive(boolean active) {
		if (this.active != active && level != null) {
			
			if (!level.isClientSide) {
				level.blockEvent(getBlockPos(), getBlockState().getBlock(), 0, active ? 1 : 0);
			}
			
			this.active = active;
			onActiveChange();
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getEffectTicks() {
		return effectTime;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void spawnSpark() {
		synchronized(sparks) {
			sparks.add(new EffectSpark(
					effectTime,
					20 * (10 + NostrumMagica.rand.nextInt(5)),
					20 * (30 + NostrumMagica.rand.nextInt(20)),
					NostrumMagica.rand.nextBoolean(),
					0f, // always start at bottom
					NostrumMagica.rand.nextFloat(),
					.5f // brightness but will be adjusted right after
				));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void removeSpark() {
		synchronized(sparks) {
			if (sparks.isEmpty()) {
				return;
			}
			
			sparks.remove(NostrumMagica.rand.nextInt(sparks.size()));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<AetherInfuserTileEntity.EffectSpark> getSparks(@Nullable List<AetherInfuserTileEntity.EffectSpark> storage) {
		if (storage == null) {
			storage = new ArrayList<>();
		} else {
			storage.clear();
		}
		
		synchronized(sparks) {
			storage.addAll(sparks);
		}
		
		return storage;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void updateSparks() {
		// Spawn or despawn sparks, and adjust brightness if necessary
		float chargePerc = getChargePerc();
		int sparkCount = Math.round(chargePerc * MAX_SPARKS);
		
		// Make spawning/despawning slow and a little random
		if (sparkCount != sparks.size() && NostrumMagica.rand.nextFloat() < .05f) {
			if (sparkCount > sparks.size()) {
				spawnSpark();
			} else {
				removeSpark();
			}
		}
		
		for (AetherInfuserTileEntity.EffectSpark spark : sparks) {
			spark.brightness = chargePerc;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public net.minecraft.world.phys.AABB getRenderBoundingBox() {
		return BlockEntity.INFINITE_EXTENT_AABB;
	}
	
	protected void refreshAltar() {
		this.centerAltar = null;
		BlockPos pos = this.worldPosition.above();
		BlockEntity te = level.getBlockEntity(pos);
		if (te != null && te instanceof AltarTileEntity) {
			this.centerAltar = (AltarTileEntity) te;
			if (this.hasAreaInfuse() && getInfuseAreaRadius() != lastScanRadius) {
				lastScanRadius = getInfuseAreaRadius();
				this.onInfuseAreaChange();
			} else if (!this.hasAreaInfuse()) {
				lastScanRadius = 0;
			}
		}
	}
	
	public boolean hasLens() {
		if (this.centerAltar != null) {
			ItemStack held = centerAltar.getItem();
			if (!held.isEmpty() && held.getItem() instanceof IAetherInfuserLens) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Specifically charging players!
	 * @return
	 */
	public boolean hasAreaCharge() {
		if (hasLens()) {
			ItemStack held = centerAltar.getItem();
			return !held.isEmpty()
					&& held.getItem() == AetheriaItems.chargeAetherLens;
		}
		
		return false;
	}
	
	/**
	 * Whether this infuser should prevent nearby mob spawns
	 * @return
	 */
	public boolean hasMobSpawnPrevention() {
		if (hasLens()) {
			ItemStack held = centerAltar.getItem();
			return !held.isEmpty()
					&& held.getItem() == AetheriaItems.noSpawnAetherLens;
		}
		
		return false;
	}
	
	/**
	 * Area to remotely charge player's inventories
	 * @return
	 */
	public int getChargeAreaRadius() {
		return 50; // TODO different areas?
	}
	
	/**
	 * Specifically giving aether to altars with lenses!
	 * @return
	 */
	public boolean hasAreaInfuse() {
		if (hasLens()) {
			ItemStack held = centerAltar.getItem();
			return !held.isEmpty()
					&& held.getItem() == AetheriaItems.spreadAetherLens;
		}
		return false;
	}
	
	/**
	 * Area to look for blocks with infusables
	 * @return
	 */
	public int getInfuseAreaRadius() {
		return 20; // Make upgradeable?
	}
	
	public int getMobSpawnProtectionRadius() {
		return 50; // Make upgradeable?
	}
	
	protected void onInfuseAreaChange() {
		if (!hasAreaInfuse()) {
			this.nearbyChargeables.clear();
		} else {
			this.refreshNearbyBlocks();
		}
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onMobSpawn(LivingSpawnEvent.CheckSpawn event) {
		if (event.getResult() != Result.DEFAULT) {
			return;
		}
		
		if (!this.hasMobSpawnPrevention()
				|| event.isSpawner()
				|| event.getWorld() != this.level
				|| event.getEntityLiving() == null
				|| !event.getEntityLiving().canChangeDimensions()
				) {
			event.setResult(Result.DEFAULT);
			return;
		}
		
		// We have to have aether
		if (this.getCharge() <= 0) {
			event.setResult(Result.DEFAULT);
			return;
		}
		
		final LivingEntity entity = event.getEntityLiving();
		
		// Figure out what to not allow through
		boolean isBad = false;
		if (entity instanceof Monster) {
			isBad = true;
			
			// But make an exception for 'tameable' mobs
			if (entity instanceof ITameableEntity) {
				isBad = false;
			}
		}
		
		event.setResult(Result.DEFAULT);
		
		if (isBad) {
			final double radius = this.getMobSpawnProtectionRadius();
			if (entity.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY(), worldPosition.getZ() + .5) < radius * radius) {
				event.setResult(Result.DENY);
				
				// Mob spawning happens a lot (and in bursts, or continuously if there's a spawner)
				// so make most prevents free
				if (NostrumMagica.rand.nextInt(20) == 0) {
					this.getHandler().drawAether(null, 1);
					AetherInfuserTileEntity.DoChargeEffect(level, worldPosition.above().above(), 1, 0xFF807020);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		if (event.getWorld().isClientSide()) {
			return;
		}
		
		final BlockPos blockpos = event.getPos().immutable();
		if (blockpos.equals(this.worldPosition.above())) {
			this.refreshAltar();
			return;
		}
		
		if (!hasAreaInfuse()) {
			return;
		}
		
		final int radius = getInfuseAreaRadius();
		
		if (Math.abs(blockpos.getX() - this.worldPosition.getX()) <= radius
				&& Math.abs(blockpos.getY() - this.worldPosition.getY()) <= radius
				&& Math.abs(blockpos.getZ() - this.worldPosition.getZ()) <= radius) {
			// Use a timer since there isn't a POST break event
			NostrumMagica.playerListener.registerTimer((type, entity, data) -> {
				refreshNearbyBlock(blockpos);
				//refreshNearbyBlocks();
				return true;
			}, 1, 1);
		}
	}
	
	@SubscribeEvent
	public void onBlockPlace(EntityPlaceEvent event) {
		if (event.getWorld().isClientSide()) {
			return;
		}
		
		final BlockPos blockpos = event.getPos().immutable();
		if (blockpos.equals(this.worldPosition.above())) {
			this.refreshAltar();
			return;
		}
		
		if (!hasAreaInfuse()) {
			return;
		}
		
		final int radius = getInfuseAreaRadius();
		if (Math.abs(blockpos.getX() - this.worldPosition.getX()) <= radius
				&& Math.abs(blockpos.getY() - this.worldPosition.getY()) <= radius
				&& Math.abs(blockpos.getZ() - this.worldPosition.getZ()) <= radius
				) {
			// ""
			NostrumMagica.playerListener.registerTimer((type, entity, data) -> {
				refreshNearbyBlock(blockpos);
				//refreshNearbyBlocks();
				return true;
			}	, 1, 1);
		}
	}
	
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event) {
		if (event.getWorld() == null || event.getWorld().isClientSide()) {
			return;
		}
		
		if (!hasAreaInfuse()) {
			return;
		}
		
		final int radius = getInfuseAreaRadius() + 16; // easier than looking at both min and max
		final ChunkAccess chunk = event.getChunk();
		final BlockPos chunkMin = new BlockPos(chunk.getPos().x << 4, this.getBlockPos().getY(), chunk.getPos().z << 4);
		if (WorldUtil.getBlockDistance(chunkMin, this.getBlockPos()) < radius) {
			// ""
			NostrumMagica.playerListener.registerTimer((type, entity, data) -> {
				refreshChunk(chunk);
				//refreshNearbyBlocks();
				return true;
			}	, 1, 1);
		}
	}
	
	protected @Nullable IAetherInfusableTileEntity checkBlock(BlockPos blockpos) {
		if (!blockpos.equals(this.worldPosition.above())) { // ignore altar above platform
			BlockEntity te = level.getBlockEntity(blockpos);
			if (te != null && te instanceof IAetherInfusableTileEntity) {
				return (IAetherInfusableTileEntity) te;
			}
		}
		return null;
	}
	
	protected void refreshNearbyBlocks() {
		this.nearbyChargeables.clear();
		
		// get radius from item in altar?
		final int radius = getInfuseAreaRadius();
		WorldUtil.ScanBlocks(level,
				new BlockPos(worldPosition.getX() - radius, worldPosition.getY() - radius, worldPosition.getZ() - radius),
				new BlockPos(worldPosition.getX() + radius, worldPosition.getY() + radius, worldPosition.getZ() + radius),
				(world, blockpos) -> {
					IAetherInfusableTileEntity te = checkBlock(blockpos);
					if (te != null) {
						nearbyChargeables.put(blockpos.immutable(), (IAetherInfusableTileEntity) te);
					}
					return true;
				});
	}
	
	protected void refreshChunk(ChunkAccess chunk) {
		// Actually get min and max based on distance from us
		final int radius = getInfuseAreaRadius();
		final BlockPos min;
		final BlockPos max;
		final int minChunkX = chunk.getPos().x << 4;
		final int minChunkZ = chunk.getPos().z << 4;
		final int maxChunkX = minChunkX + 16;
		final int maxChunkZ = minChunkZ + 16;
		final int minBlockX = this.worldPosition.getX() - radius;
		final int minBlockZ = this.worldPosition.getZ() - radius;
		final int minBlockY = this.worldPosition.getY() - radius;
		final int maxBlockX = this.worldPosition.getX() - radius;
		final int maxBlockZ = this.worldPosition.getZ() - radius;
		final int maxBlockY = this.worldPosition.getY() - radius;
		
		final int minX = Math.min(minChunkX, minBlockX);
		final int minY = minBlockY;
		final int minZ = Math.min(minChunkZ, minBlockZ);
		final int maxX = Math.min(maxChunkX, maxBlockX);
		final int maxY = maxBlockY;
		final int maxZ = Math.min(maxChunkZ, maxBlockZ);
		
		min = new BlockPos(minX, minY, minZ);
		max = new BlockPos(maxX, maxY, maxZ);
		WorldUtil.ScanBlocks(level, min, max, (world, blockpos) -> {
			refreshNearbyBlock(blockpos);
			return true;
		});
	}
	
	protected void refreshNearbyBlock(BlockPos blockpos) {
		nearbyChargeables.remove(blockpos);
		
		IAetherInfusableTileEntity te = checkBlock(blockpos);
		if (te != null) {
			nearbyChargeables.put(blockpos.immutable(), (IAetherInfusableTileEntity) te);
		}
	}
	
	@Override
	public Level getInfuserWorld() { // Have to override for IAetherInfuserTileEntity
		return this.getLevel();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class EffectSpark {
		
		public static final int BLINK_PERIOD = (20 * 4);
		public static final float BLINK_FACTOR = 1f / (float) BLINK_PERIOD;
		
		public float brightness; // [0-1]
		public final float pitchStart; // [0-1]
		public final float yawStart; // [0-1]
		public final float pitchFactor; // [-1-1]
		public final float yawFactor; // [-1-1]
		
		public final int spawnTime;
		
		public EffectSpark(int spawnTime, 
				float pitchFactor, float yawFactor, float startingPitch, float startingYaw, float brightness) {
			this.spawnTime = spawnTime;
			this.pitchStart = startingPitch;
			this.yawStart = startingYaw;
			this.pitchFactor = pitchFactor;
			this.yawFactor = yawFactor;
		}
		
		public EffectSpark(int spawnTime, float pitchPeriod, float yawPeriod, boolean forwardDir,
				float startingPitch, float startingYaw, float brightness) {
			// period is ticks for a rotation, ofc
			this(spawnTime,
				(1f / pitchPeriod) * (forwardDir ? 1 : -1),
				(1f / yawPeriod) * (forwardDir ? 1 : -1),
				startingPitch, startingYaw, brightness);
		}
		
		private static final float Clamp(float in) {
			return in % 1f;
		}
		
		public float getPitch(long ticks, float partialTicks) {
			return Clamp(pitchStart + (float) (
					((double) (ticks - spawnTime) + (double) partialTicks) * pitchFactor)
				);
		}
		
		public float getYaw(long ticks, float partialTicks) {
			return Clamp(yawStart + (float) (((double) (ticks - spawnTime) + (double) partialTicks) * yawFactor)
				);
		}
		
		public float getBrightness(long ticks, float partialTicks) {
			brightness = 1f;
			// use input brightness (0-1) at 60% to allow for glowing
			// glow based on BLINK_PERIOD
			final float t = Clamp((float) (((double) (ticks - spawnTime) + partialTicks) * BLINK_FACTOR));
			final double tRad = t * Math.PI * 2;
			final float tAdj = (float) (Math.sin(tRad) + 1f) / 2f;
			return brightness * (.2f + .8f * tAdj);
		}
	}
}