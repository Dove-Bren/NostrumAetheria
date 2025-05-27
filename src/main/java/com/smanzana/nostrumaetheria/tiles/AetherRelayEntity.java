package com.smanzana.nostrumaetheria.tiles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.stats.AetherTickIOEntry;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.AetherRelay.RelayMode;
import com.smanzana.nostrummagica.client.particles.NostrumParticles;
import com.smanzana.nostrummagica.client.particles.NostrumParticles.SpawnParams;
import com.smanzana.nostrummagica.util.TargetLocation;
import com.smanzana.nostrummagica.util.TileEntities;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AetherRelayEntity extends NativeAetherTickingTileEntity {

		private static final String NBT_SIDE = "relay_side";
		private static final String NBT_LINKS = "links";
		private static final String NBT_MODE = "mode";
		
		private static final int AETHER_BUFFER_AMT = 10;
		
		private static final int MAX_LINK_RANGE = 10;
		
		private Direction side;
		protected RelayMode mode;
		private final List<BlockPos> links;
		
//		private int idleTicks;
		
		public AetherRelayEntity(BlockPos pos, BlockState state) {
			this(pos, state, Direction.UP, RelayMode.INOUT);
		}
		
		public AetherRelayEntity(BlockPos pos, BlockState state, Direction facing, RelayMode mode) {
			this(AetheriaTileEntities.Relay, pos, state, facing, mode);
		}
		
		protected AetherRelayEntity(BlockEntityType<? extends AetherRelayEntity> type, BlockPos pos, BlockState state, Direction facing, RelayMode mode) {
			super(type, pos, state, 0, AETHER_BUFFER_AMT);
			
			side = facing;
			this.setMode(mode, false);
			links = new ArrayList<>();
			//idleTicks = NostrumMagica.rand.nextInt(40) + 10;
		}
		
		public List<BlockPos> getLinkLocations() {
			return this.links;
		}
		
		public void addLink(@Nullable Player player, BlockPos pos) {
			addLink(player, pos, false);
		}
		
		public void addLink(@Nullable Player player, BlockPos pos, boolean ignoreRange) {
			if (links.contains(pos)) {
//				if (player != null) {
//					player.sendMessage(new TranslationTextComponent("info.relay.already_linked"));
//				}
			} else if (!this.canLinkTo(pos)) {
				if (player != null) {
					player.sendMessage(new TranslatableComponent("info.relay.too_far"), Util.NIL_UUID);
				}
			} else {
				links.add(pos);
				this.dirty();
				TileEntities.RefreshToClients(this);
				
				// And attach the other side, too
				if (level != null && level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof AetherRelayEntity) {
					((AetherRelayEntity) level.getBlockEntity(pos)).addLink(player, this.worldPosition, ignoreRange);
				}
			}
		}
		
		public void clearLinks() {
			links.clear();
			this.handler.clearConnections();
			this.dirty();
		}
		
		public void setMode(RelayMode mode) {
			setMode(mode, true);
		}
		
		public void setMode(RelayMode mode, boolean flush) {
			switch (mode) {
			case IN:
				this.handler.configureInOut(true, false);
				this.setupForIn();
				break;
			case INOUT:
				//this.handler.configureInOut(true, true);
				this.setupForInOut();
				break;
			case OUT:
				//this.handler.configureInOut(false, true);
				this.setupForOut();
				break;
			}
			
			this.mode = mode;
			if (flush) {
				setBlockStateFromMode(mode);
				this.dirty();
			}
		}
		
		protected void setBlockStateFromMode(RelayMode mode) {
			this.level.setBlock(worldPosition, this.getBlockState().setValue(AetherRelay.RELAY_MODE, mode), 3);
		}
		
		protected List<IAetherHandler> getLinkHandlers(@Nullable List<IAetherHandler> list) {
			if (list == null) {
				list = new ArrayList<>();
			}
			for (BlockPos pos : this.getLinkLocations()) {
				IAetherHandler handler = IAetherHandler.GetHandlerAt(level, pos, null);
				if (handler != null) {
					list.add(handler);
				}
			}
			return list;
		}
		
		protected boolean canLinkTo(BlockPos pos) {
			return this.worldPosition.distManhattan(pos) <= MAX_LINK_RANGE;
		}
		
		@Override
		public void clearRemoved() {
			super.clearRemoved();
			
//			if (this.world != null) {
//				relayHandler.setPosition(world, pos.toImmutable());
//			}
		}
		
		@Override
		public void setLevel(Level world) {
			super.setLevel(world);
		}
		
		@Override
		public void onLoad() {
			super.onLoad();
		}
		
		@Override
		public void onChunkUnloaded() {
			super.onChunkUnloaded();
		}
		
		public Direction getSide() {
			return side;
		}
		
		@Override
		public ClientboundBlockEntityDataPacket getUpdatePacket() {
			return ClientboundBlockEntityDataPacket.create(this);
		}

		@Override
		public CompoundTag getUpdateTag() {
			return this.saveWithId(); // Always send ID so that empty lists also get sent and parsed instead of nulled and no-called
		}
		
		@Override
		public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
			super.onDataPacket(net, pkt);
			//handleUpdateTag(pkt.getTag());
		}
		
		@Override
		public void saveAdditional(CompoundTag compound) {
			super.saveAdditional(compound);
			
			compound.putByte(NBT_SIDE, (byte) this.side.ordinal());
			if (!this.links.isEmpty()) {
				ListTag list = new ListTag();
				for (BlockPos link : links) {
					list.add(NbtUtils.writeBlockPos(link));
				}
				compound.put(NBT_LINKS, list);
			}
			compound.putString(NBT_MODE, this.mode.name());
		}
		
		@Override
		public void load(CompoundTag compound) {
			super.load(compound);
			
			links.clear();
			this.side = Direction.values()[compound.getByte(NBT_SIDE)];
			if (compound.contains(NBT_LINKS)) {
				ListTag list = compound.getList(NBT_LINKS, Tag.TAG_COMPOUND);
				for (int i = 0; i < list.size(); i++) {
					links.add(NbtUtils.readBlockPos(list.getCompound(i)));
				}
			}
			RelayMode modeToSet;
			try {
				modeToSet = RelayMode.valueOf(compound.getString(NBT_MODE));
			} catch (Exception e) {
				modeToSet = RelayMode.INOUT;
			}
			
			this.setMode(modeToSet, false);
//			relayHandler.setSide(this.side);
		}
		
		protected void idleVisualTick() {
			if (links == null || links.isEmpty()) {
				return;
			}
			
			final int color;
			//if (relay.isAetherActive()) {
				color = 0xA0D4CF80;
			//} else {
			//	color = 0x80D3D3CD;
			//}
			
			for (BlockPos dest : links) {
				AetherTickIOEntry entry = this.handler.getIOStatsFor(dest);
				if (entry != null && entry.getInput() > 0) {
					final int tickCount = entry.getInput();
					final int count;
					if (tickCount > 10) {
						count = tickCount / 10;
					} else {
						count = NostrumAetheria.random.nextFloat() < ((float) tickCount / 10f) ? 1 : 0;
					}
					
					if (count > 0) {
						NostrumParticles.FILLED_ORB.spawn(level, new SpawnParams(
								count, dest.getX() + .5, dest.getY() + .5, dest.getZ() + .5, 0, 30 * 1, 10,
								new TargetLocation(new Vec3(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5))
							).color(color));
					}
					
					//int count, double spawnX, double spawnY, double spawnZ, double spawnJitterRadius, int lifetime, int lifetimeJitter,
					//Vector3d targetPos
				}
			}
		}
		
		protected void refreshConnections() {
			this.handler.clearConnections();
//			for (IAetherHandler remoteHandler : this.getLinkHandlers(null)) {
//				this.handler.addAetherConnection(remoteHandler, null);
//			}
			
			for (BlockPos pos : this.getLinkLocations()) {
				IAetherHandler remoteHandler = IAetherHandler.GetHandlerAt(level, pos, null);
				if (remoteHandler != null) {
					this.handler.addAetherConnection(remoteHandler, null);
					
					// Fixup relays that get placed but are being pointed to
					BlockEntity te = level.getBlockEntity(pos);
					if (te != null && te instanceof AetherRelayEntity) {
						((AetherRelayEntity) te).addLink(null, getBlockPos());
					}
				}
			}
		}
		
		protected void setupForIn() {
			handler.setInboundEnabled(true);
			handler.setOutboundEnabled(false);
			handler.enableSide(getSide().getOpposite(), false);
			handler.setShouldPropagate(false);
		}
		
		protected void setupForOut() {
			handler.setInboundEnabled(false);
			handler.setOutboundEnabled(true);
			handler.enableSide(getSide().getOpposite(), false);
			handler.setShouldPropagate(false);
		}
		
		protected void setupForInOut() {
			handler.setShouldPropagate(false);
			handler.configureInOut(true, true); // Both allowed
			handler.enableSide(getSide().getOpposite(), false);
		}
		
		protected void inTick() {
			final Direction mySide = this.getSide();
			
			// Pull aether, and try to insert it into our attached component
			handler.fillAether(AETHER_BUFFER_AMT);
			
			// Fill attached component
			IAetherHandler otherHandler = IAetherHandler.GetHandlerAt(level, worldPosition.relative(mySide.getOpposite()), mySide);
			if (otherHandler != null) {
				// Avoid constantly pulling and putting back to a full component by finding out how much
				// it needs first.
				final int room = otherHandler.getMaxAether(mySide) - otherHandler.getAether(mySide);
				if (room > 0) {
					int amt = this.handler.drawAether(mySide.getOpposite(), AETHER_BUFFER_AMT);
					amt = otherHandler.addAether(mySide, amt);
					this.handler.addAether(null, amt, true); // Put what doesn't fit back
				}
			}
		}
		
		protected void outTick() {
			final Direction mySide = this.getSide();
			
			// Pull aether out of attached component and make it available for drawing
			IAetherHandler otherHandler = IAetherHandler.GetHandlerAt(level, worldPosition.relative(mySide.getOpposite()), mySide);
			if (otherHandler != null) {
				final int room = this.handler.getMaxAether(mySide.getOpposite()) - this.handler.getAether(mySide.getOpposite());
				if (room > 0) {
					final int taken = otherHandler.drawAether(mySide, room);
					this.handler.addAether(mySide.getOpposite(), taken, true);
				}
			}
		}
		
		protected void inOutTick() {
			//final Direction mySide = this.getSide();
			
			// Pull aether into ourselves so that other things can take it, but don't
			// try to push into the att ached thing.
			handler.fillAether(AETHER_BUFFER_AMT);
		}
		
		@Override
		public void tick() {
			if (this.level != null && !this.level.isClientSide) {
				//if (ticksExisted > idleTicks) {
					//idleTicks = ticksExisted + NostrumMagica.rand.nextInt(120) + 60;
					idleVisualTick();
				//}
			}
			
			super.tick();
		
			if (this.level != null && !this.level.isClientSide()) {
				// Possibly refresh connections
				{
					if (this.level != null && !this.level.isClientSide()) {
						// Refresh links every little while
						if (this.ticksExisted % 5 == 0) {
							refreshConnections();
						}
					}
				}
				
				if (this.mode == RelayMode.IN) {
					inTick();
				} else if (this.mode == RelayMode.OUT) {
					outTick();
				} else if (this.mode == RelayMode.INOUT) {
					inOutTick();
				}
				
//				// Note: expect to still be configured for pulling from end of last tick
//				
//				// Pull
//				handler.fillAether(AETHER_BUFFER_AMT);
//				
//				// Configure sides for pushing
//				setupForPush();
//				
//				// Try to push aether into connections
//				{
//					this.handler.pushAether(AETHER_BUFFER_AMT);
//
//				}
//				
//				// Set back up for pulling, so that other handlers can push into us if needed
//				// Configure side IO for pulling
//				setupForPull();
			}
		}
	}