package com.smanzana.nostrumaetheria.network.messages;

import java.util.function.Supplier;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.util.DimensionUtils;
import com.smanzana.nostrummagica.util.NetUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

/**
 * Server is sending an update about the aether level of a tile entity
 * @author Skyler
 *
 */
public class AetherTileEntityMessage {

	public static void handle(AetherTileEntityMessage message, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (DimensionUtils.DimEquals(mc.player.level.dimension(), message.dimension)) {
				Level world = mc.player.level;
				if (!NostrumMagica.isBlockLoaded(world, message.pos)) {
					return;
				}
				BlockEntity te = world.getBlockEntity(message.pos);
				if (te != null && te instanceof AetherTileEntity) {
					((AetherTileEntity) te).syncAether(message.aether);
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	private final ResourceKey<Level> dimension;
	private final BlockPos pos;
	private final int aether;
	
	public AetherTileEntityMessage(Level world, BlockPos pos, int aether) {
		this(world.dimension(), pos, aether);
	}
	
	public AetherTileEntityMessage(ResourceKey<Level> dimension, BlockPos pos, int aether) {
		this.dimension = dimension;
		this.pos = pos;
		this.aether = aether;
	}
	
	public AetherTileEntityMessage(AetherTileEntity te) {
		this(te.getLevel(), te.getBlockPos(), te.getHandler().getAether(null));
	}

	public static AetherTileEntityMessage decode(FriendlyByteBuf buf) {
		ResourceKey<Level> dim = NetUtils.unpackDimension(buf);
		final BlockPos pos = buf.readBlockPos();
		final int aether = buf.readVarInt();
		
		return new AetherTileEntityMessage(dim, pos, aether);
	}

	public static void encode(AetherTileEntityMessage message, FriendlyByteBuf buf) {
		NetUtils.packDimension(buf, message.dimension);
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.aether);
	}

}
