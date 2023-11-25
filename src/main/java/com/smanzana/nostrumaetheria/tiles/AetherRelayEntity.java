package com.smanzana.nostrumaetheria.tiles;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.aether.IAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.IWorldAetherHandler;
import com.smanzana.nostrumaetheria.api.aether.stats.AetherTickIOEntry;
import com.smanzana.nostrumaetheria.blocks.AetherRelay;
import com.smanzana.nostrumaetheria.blocks.AetherRelay.RelayMode;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.particles.NostrumParticles;
import com.smanzana.nostrummagica.client.particles.NostrumParticles.SpawnParams;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class AetherRelayEntity extends NativeAetherTickingTileEntity {

		private static final String NBT_SIDE = "relay_side";
		private static final String NBT_LINKS = "links";
		private static final String NBT_MODE = "mode";
		
		private static final int AETHER_BUFFER_AMT = 10;
		
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
			this.mode = mode;
			links = new ArrayList<>();
			idleTicks = NostrumMagica.rand.nextInt(40) + 10;
			
			
		}
		
//		@Override
//		protected AetherHandlerComponent createComponent(int defaultAether, int defaultMaxAether) {
//			// I wantd to override this and return a relay component but I can't think of a neasy way to get the side here.
//			// I could relax the component to not care about side at first, and have it attached later. instead
//			// I'll just throw the old one away?
//			relayHandler = new AetherRelayComponent(this, Direction.UP); 
//			return relayHandler;
//		}
		
		public List<BlockPos> getLinkLocations() {
			return this.links;
		}
		
		public void addLink(BlockPos pos) {
			if (!links.contains(pos)) {
				links.add(pos);
				this.dirty();
				
				// And attach the other side, too
				if (world != null && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof AetherRelayEntity) {
					((AetherRelayEntity) world.getTileEntity(pos)).addLink(this.pos);
				}
			}
		}
		
		public void clearLinks() {
			links.clear();
			this.handler.clearConnections();
			this.dirty();
		}
		
		public void setMode(RelayMode mode) {
			switch (mode) {
			case IN:
				this.handler.configureInOut(true, false);
				break;
			case INOUT:
				this.handler.configureInOut(true, true);
				break;
			case OUT:
				this.handler.configureInOut(false, true);
				break;
			}
			
			this.mode = mode;
			setBlockStateFromMode(mode);
			this.dirty();
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
			try {
				this.mode = RelayMode.valueOf(compound.getString(NBT_MODE));
			} catch (Exception e) {
				this.mode = RelayMode.INOUT;
			}
			
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
					NostrumParticles.FILLED_ORB.spawn(world, new SpawnParams(
							1, dest.getX() + .5, dest.getY() + .5, dest.getZ() + .5, 0, 20 * 1, 10, new Vec3d(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5)
						).color(color));
					
					//int count, double spawnX, double spawnY, double spawnZ, double spawnJitterRadius, int lifetime, int lifetimeJitter,
					//Vec3d targetPos
				}
			}
		}
		
		protected void refreshConnections() {
			this.handler.clearConnections();
			for (IAetherHandler remoteHandler : this.getLinkHandlers(null)) {
				this.handler.addAetherConnection(remoteHandler, null);
			}
		}
		
		/**
		 * Configure handler IO for pulling in aether. Notably, we don't want to pull from the block
		 * we're attached to unless our mode says that's right.
		 */
		protected void setupForPull() {
			handler.setInboundEnabled(true);
			handler.setOutboundEnabled(false);
			handler.enableSide(getSide().getOpposite(), false);
		}
		
		protected void setupForPush() {
			handler.setInboundEnabled(false);
			handler.setOutboundEnabled(true);
			handler.enableSide(getSide().getOpposite(), false);
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
					setupForPull();
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
					setupForPush();
					
					IAetherHandler otherHandler = IAetherHandler.GetHandlerAt(world, pos.offset(mySide.getOpposite()), mySide);
					if (otherHandler != null) {
						final int room = this.handler.getMaxAether(mySide.getOpposite()) - this.handler.getAether(mySide.getOpposite());
						if (room > 0) {
							final int taken = otherHandler.drawAether(mySide, room);
							this.handler.addAether(mySide.getOpposite(), taken, true);
						}
					}
				} else if (this.mode == RelayMode.INOUT) {
					handler.setShouldPropagate(true);
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