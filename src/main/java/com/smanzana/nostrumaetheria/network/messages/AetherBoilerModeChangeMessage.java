package com.smanzana.nostrumaetheria.network.messages;

import java.util.function.Supplier;

import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity.BoilerBurnMode;
import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Client has tried to change the aether boiler mode
 * @author Skyler
 *
 */
public class AetherBoilerModeChangeMessage {

	public static void handle(AetherBoilerModeChangeMessage message, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ctx.get().enqueueWork(() -> {
			if (!NostrumMagica.isBlockLoaded(player.world, message.pos)) {
				return;
			}
			TileEntity te = player.world.getTileEntity(message.pos);
			if (te != null && te instanceof AetherBoilerBlockEntity) {
				((AetherBoilerBlockEntity) te).setBoilerMode(message.next_mode);
				
				// Cause an update to be sent back
				BlockState state = player.world.getBlockState(message.pos);
				player.world.notifyBlockUpdate(message.pos, state, state, 2);
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	private final BlockPos pos;
	private final BoilerBurnMode next_mode;
	
	public AetherBoilerModeChangeMessage(BlockPos pos, BoilerBurnMode mode) {
		this.pos = pos;
		this.next_mode = mode;
	}
	
	public static AetherBoilerModeChangeMessage decode(PacketBuffer buf) {
		return new AetherBoilerModeChangeMessage(buf.readBlockPos(), buf.readEnumValue(BoilerBurnMode.class));
	}

	public static void encode(AetherBoilerModeChangeMessage msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.pos);
		buf.writeEnumValue(msg.next_mode);
	}

}
