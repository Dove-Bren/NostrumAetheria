package com.smanzana.nostrumaetheria.tiles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.stats.AetherTickIOEntry;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.AetherRelay.RelayMode;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.particles.NostrumParticles;
import com.smanzana.nostrummagica.client.particles.NostrumParticles.SpawnParams;
import com.smanzana.nostrummagica.utils.TileEntities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class AetherRelayEntity extends NativeAetherTickingTileEntity {

		private static final String NBT_SIDE = "relay_side";
		private static final String NBT_LINKS = "links";
		private static final String NBT_MODE = "mode";
		
		private static final int AETHER_BUFFER_AMT = 10;
		
		private static final int MAX_LINK_RANGE = 10;
		
		private Direction side;
		private RelayMode mode;
		private final List<BlockPos> links;
		
		private int idleTicks;
		
		public AetherRelayEntity() {
			this(Direction.UP, RelayMode.INOUT);
		}
		
		public AetherRelayEntity(Direction facing, RelayMode mode) {
			super(AetheriaTileEntities.Relay, 0, AETHER_BUFFER_AMT);
			
			side = facing;
			this.setMode(mode, false);
			links = new ArrayList<>();
			idleTicks = NostrumMagica.rand.nextInt(40) + 10;
		}
		
		public List<BlockPos> getLinkLocations() {
			return this.links;
		}
		
		public void addLink(@Nullable PlayerEntity player, BlockPos pos) {
			addLink(player, pos, false);
		}
		
		public void addLink(@Nullable PlayerEntity player, BlockPos pos, boolean ignoreRange) {
			if (links.contains(pos)) {
//				if (player != null) {
//					player.sendMessage(new TranslationTextComponent("info.relay.already_linked"));
//				}
			} else if (!this.canLinkTo(pos)) {
				if (player != null) {
					player.sendMessage(new TranslationTextComponent("info.relay.too_far"));
				}
			} else {
				links.add(pos);
				this.dirty();
				TileEntities.RefreshToClients(this);
				
				// And attach the other side, too
				if (world != null && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof AetherRelayEntity) {
					((AetherRelayEntity) world.getTileEntity(pos)).addLink(player, this.pos, ignoreRange);
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
			this.world.setBlockState(pos, this.getBlockState().with(AetherRelay.RELAY_MODE, mode), 3);
		}
		
		protected List<IAetherHandler> getLinkHandlers(@Nullable List<IAetherHandler> list) {
			if (list == null) {
				list = new ArrayList<>();
			}
			for (BlockPos pos : this.getLinkLocations()) {
				IAetherHandler handler = IAetherHandler.GetHandlerAt(world, pos, null);
				if (handler != null) {
					list.add(handler);
				}
			}
			return list;
		}
		
		protected boolean canLinkTo(BlockPos pos) {
			return this.pos.manhattanDistance(pos) <= MAX_LINK_RANGE;
		}
		
		@Override
		public void validate() {
			super.validate();
			
//			if (this.world != null) {
//				relayHandler.setPosition(world, pos.toImmutable());
//			}
		}
		
		@Override
		public void setWorld(World world) {
			super.setWorld(world);
			
//			if (this.pos != null && !this.pos.equals(BlockPos.ZERO)) {
//				relayHandler.setPosition(world, pos.toImmutable());
//			}
			// if this is too early for side, let's save it :(
			
//			if (!isInvalid()) {
//				if (link == null) {
//					autoLink();
//				} else {
//					// link up with the tile entity
//					repairLink();
//				}
//			}
		}
		
		@Override
		public void onLoad() {
			super.onLoad();
//			if (!world.isRemote) {
//				world.getMinecraftServer().addScheduledTask(() -> {
//					if (world != null && getPairedRelay() == null) {
//						if (link == null) {
//							autoLink();
//						} else {
//							// link up with the tile entity
//							repairLink();
//						}
//					}
//				});
//			}
		}
		
		@Override
		public void updateContainingBlockInfo() {
			super.updateContainingBlockInfo();
			
//			if (relayHandler != null && !relayHandler.hasLinks()) {
//				relayHandler.autoLink();
//			}
		}
		
//		@Override
//		public void onLinkChange() {
//			this.markDirty();
//			BlockState state = world.getBlockState(pos);
//			world.notifyBlockUpdate(pos, state, state, 2);
//		}
		
		@Override
		public void onChunkUnloaded() {
			super.onChunkUnloaded();
			
			// For any linked relays, let them know we're going away (but weren't destroyed)
//			relayHandler.unloadRelay();
		}
		
		public Direction getSide() {
			return side;
		}
		
		@Override
		public SUpdateTileEntityPacket getUpdatePacket() {
			return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
		}

		@Override
		public CompoundNBT getUpdateTag() {
			return this.write(new CompoundNBT());
		}
		
		@Override
		public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
			super.onDataPacket(net, pkt);
			handleUpdateTag(pkt.getNbtCompound());
		}
		
		@Override
		public CompoundNBT write(CompoundNBT compound) {
			super.write(compound);
			
			compound.putByte(NBT_SIDE, (byte) this.side.ordinal());
			if (!this.links.isEmpty()) {
				ListNBT list = new ListNBT();
				for (BlockPos link : links) {
					list.add(NBTUtil.writeBlockPos(link));
				}
				compound.put(NBT_LINKS, list);
			}
			compound.putString(NBT_MODE, this.mode.name());
			
			return compound;
		}
		
		@Override
		public void read(CompoundNBT compound) {
			super.read(compound);
			
			links.clear();
			this.side = Direction.values()[compound.getByte(NBT_SIDE)];
			if (compound.contains(NBT_LINKS)) {
				ListNBT list = compound.getList(NBT_LINKS, NBT.TAG_COMPOUND);
				for (int i = 0; i < list.size(); i++) {
					links.add(NBTUtil.readBlockPos(list.getCompound(i)));
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
				color = 0x80D4CF80;
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
						NostrumParticles.FILLED_ORB.spawn(world, new SpawnParams(
								count, dest.getX() + .5, dest.getY() + .5, dest.getZ() + .5, 0, 20 * 1, 10, new Vec3d(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5)
							).color(color));
					}
					
					//int count, double spawnX, double spawnY, double spawnZ, double spawnJitterRadius, int lifetime, int lifetimeJitter,
					//Vec3d targetPos
				}
			}
		}
		
		protected void refreshConnections() {
			this.handler.clearConnections();
//			for (IAetherHandler remoteHandler : this.getLinkHandlers(null)) {
//				this.handler.addAetherConnection(remoteHandler, null);
//			}
			
			for (BlockPos pos : this.getLinkLocations()) {
				IAetherHandler remoteHandler = IAetherHandler.GetHandlerAt(world, pos, null);
				if (remoteHandler != null) {
					this.handler.addAetherConnection(remoteHandler, null);
					
					// Fixup relays that get placed but are being pointed to
					TileEntity te = world.getTileEntity(pos);
					if (te != null && te instanceof AetherRelayEntity) {
						((AetherRelayEntity) te).addLink(null, getPos());
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
			handler.setShouldPropagate(true);
		}
		
		@Override
		public void tick() {
			if (this.world != null && !this.world.isRemote) {
				//if (ticksExisted > idleTicks) {
					idleTicks = ticksExisted + NostrumMagica.rand.nextInt(120) + 60;
					idleVisualTick();
				//}
			}
			
			super.tick();
		
			if (this.world != null && !this.world.isRemote()) {
				// Possibly refresh connections
				{
					if (this.world != null && !this.world.isRemote()) {
						// Refresh links every little while
						if (this.ticksExisted % 5 == 0) {
							refreshConnections();
						}
					}
				}
				
				final Direction mySide = this.getSide();
				if (this.mode == RelayMode.IN) {
					// Pull aether, and try to insert it into our attached component
					handler.fillAether(AETHER_BUFFER_AMT);
					
					// Fill attached component
					IAetherHandler otherHandler = IAetherHandler.GetHandlerAt(world, pos.offset(mySide.getOpposite()), mySide);
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
				} else if (this.mode == RelayMode.OUT) {
					// Pull aether out of attached component and make it available for drawing
					IAetherHandler otherHandler = IAetherHandler.GetHandlerAt(world, pos.offset(mySide.getOpposite()), mySide);
					if (otherHandler != null) {
						final int room = this.handler.getMaxAether(mySide.getOpposite()) - this.handler.getAether(mySide.getOpposite());
						if (room > 0) {
							final int taken = otherHandler.drawAether(mySide, room);
							this.handler.addAether(mySide.getOpposite(), taken, true);
						}
					}
				} else if (this.mode == RelayMode.INOUT) {
					;
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