package com.smanzana.nostrumaetheria.network.messages;

import java.util.function.Supplier;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;
import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Server is sending an update about the aether level of a tile entity
 * @author Skyler
 *
 */
public class AetherTileEntityMessage {

	public static void handle(AetherTileEntityMessage message, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.player.world.getDimension().getType() == message.dimension) {
				World world = mc.player.world;
				if (!NostrumMagica.isBlockLoaded(world, message.pos)) {
					return;
				}
				TileEntity te = world.getTileEntity(message.pos);
				if (te != null && te instanceof AetherTileEntity) {
					((AetherTileEntity) te).syncAether(message.aether);
				}
			}
		});
	}
	
	private final DimensionType dimension;
	private final BlockPos pos;
	private final int aether;
	
	public AetherTileEntityMessage(World world, BlockPos pos, int aether) {
		this(world.getDimension().getType(), pos, aether);
	}
	
	public AetherTileEntityMessage(DimensionType dimension, BlockPos pos, int aether) {
		this.dimension = dimension;
		this.pos = pos;
		this.aether = aether;
	}
	
	public AetherTileEntityMessage(AetherTileEntity te) {
		this(te.getWorld(), te.getPos(), te.getHandler().getAether(null));
	}

	public static AetherTileEntityMessage decode(PacketBuffer buf) {
		final int dimID = buf.readVarInt();
		final BlockPos pos = buf.readBlockPos();
		final int aether = buf.readVarInt();
		
		DimensionType dim = DimensionType.getById(dimID);
		if (dim == null) {
			dim = DimensionType.OVERWORLD;
		}
		
		return new AetherTileEntityMessage(dim, pos, aether);
	}

	public static void encode(AetherTileEntityMessage message, PacketBuffer buf) {
		buf.writeVarInt(message.dimension.getId());
		buf.writeBlockPos(message.pos);
		buf.writeVarInt(message.aether);
	}

}
