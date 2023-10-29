package com.smanzana.nostrumaetheria.tiles;

import java.util.Collection;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.api.component.OptionalAetherHandlerComponent;
import com.smanzana.nostrumaetheria.component.AetherRelayComponent;
import com.smanzana.nostrumaetheria.component.AetherRelayComponent.AetherRelayListener;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.particles.NostrumParticles;
import com.smanzana.nostrummagica.client.particles.NostrumParticles.SpawnParams;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AetherRelayEntity extends NativeAetherTickingTileEntity implements AetherRelayListener {

		private static final String NBT_SIDE = "relay_side";
		
		protected @Nullable AetherRelayComponent relayHandler;
		private EnumFacing side;
		
		private int idleTicks;
		
		public AetherRelayEntity() {
			this(EnumFacing.UP);
		}
		
		public AetherRelayEntity(EnumFacing facing) {
			super(0, 0);
			
			side = facing;
			this.relayHandler = new AetherRelayComponent(this, facing);
			this.handler = relayHandler;
			this.compWrapper = new OptionalAetherHandlerComponent(relayHandler);
			idleTicks = NostrumMagica.rand.nextInt(40) + 10;
		}
		
//		@Override
//		protected AetherHandlerComponent createComponent(int defaultAether, int defaultMaxAether) {
//			// I wantd to override this and return a relay component but I can't think of a neasy way to get the side here.
//			// I could relax the component to not care about side at first, and have it attached later. instead
//			// I'll just throw the old one away?
//			relayHandler = new AetherRelayComponent(this, EnumFacing.UP); 
//			return relayHandler;
//		}
		
		@Override
		public void validate() {
			super.validate();
			
			if (this.world != null) {
				relayHandler.setPosition(world, pos.toImmutable());
			}
		}
		
		@Override
		public void setWorld(World world) {
			super.setWorld(world);
			
			if (this.pos != null && !this.pos.equals(BlockPos.ORIGIN)) {
				relayHandler.setPosition(world, pos.toImmutable());
			}
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
			
			if (relayHandler != null && !relayHandler.hasLinks()) {
				relayHandler.autoLink();
			}
		}
		
		@Override
		public void onLinkChange() {
			this.markDirty();
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 2);
		}
		
		@Override
		public void onChunkUnload() {
			super.onChunkUnload();
			
			// For any linked relays, let them know we're going away (but weren't destroyed)
			relayHandler.unloadRelay();
		}
		
		public EnumFacing getSide() {
			return side;
		}
		
		@Override
		public SPacketUpdateTileEntity getUpdatePacket() {
			return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
		}

		@Override
		public NBTTagCompound getUpdateTag() {
			return this.writeToNBT(new NBTTagCompound());
		}
		
		@Override
		public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
			super.onDataPacket(net, pkt);
			handleUpdateTag(pkt.getNbtCompound());
		}
		
		public NBTTagCompound writeToNBT(NBTTagCompound compound) {
			super.writeToNBT(compound);
			
			compound.setByte(NBT_SIDE, (byte) this.side.ordinal());
			
			return compound;
		}
		
		public void readFromNBT(NBTTagCompound compound) {
			super.readFromNBT(compound);
			
			this.side = EnumFacing.values()[compound.getByte(NBT_SIDE)];
			relayHandler.setSide(this.side);
		}
		
		protected void idleVisualTick() {
			AetherRelayComponent relay = (AetherRelayComponent) this.getHandler();
			final Collection<BlockPos> links = relay.getLinkedPositions();
			
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
				if (NostrumMagica.rand.nextBoolean() && NostrumMagica.rand.nextBoolean()) {
					NostrumParticles.FILLED_ORB.spawn(world, new SpawnParams(
							1, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 0, 20 * 1, 10, new Vec3d(dest.getX() + .5, dest.getY() + .5, dest.getZ() + .5)
						).color(color));
					
					//int count, double spawnX, double spawnY, double spawnZ, double spawnJitterRadius, int lifetime, int lifetimeJitter,
					//Vec3d targetPos
				}
			}
		}
		
		@Override
		public void update() {
			super.update();
			
			if (this.world != null && this.world.isRemote) {
				if (ticksExisted > idleTicks) {
					idleTicks = ticksExisted + NostrumMagica.rand.nextInt(120) + 60;
					idleVisualTick();
				}
			}
		}
	}