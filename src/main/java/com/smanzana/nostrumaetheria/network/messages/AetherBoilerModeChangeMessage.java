package com.smanzana.nostrumaetheria.network.messages;

import java.util.function.Supplier;

import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity.BoilerBurnMode;
import com.smanzana.nostrummagica.NostrumMagica;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

/**
 * Client has tried to change the aether boiler mode
 * @author Skyler
 *
 */
public class AetherBoilerModeChangeMessage {

	public static void handle(AetherBoilerModeChangeMessage message, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		ctx.get().enqueueWork(() -> {
			if (!NostrumMagica.isBlockLoaded(player.level, message.pos)) {
				return;
			}
			BlockEntity te = player.level.getBlockEntity(message.pos);
			if (te != null && te instanceof AetherBoilerBlockEntity) {
				((AetherBoilerBlockEntity) te).setBoilerMode(message.next_mode);
				
				// Cause an update to be sent back
				BlockState state = player.level.getBlockState(message.pos);
				player.level.sendBlockUpdated(message.pos, state, state, 2);
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
	
	public static AetherBoilerModeChangeMessage decode(FriendlyByteBuf buf) {
		return new AetherBoilerModeChangeMessage(buf.readBlockPos(), buf.readEnum(BoilerBurnMode.class));
	}

	public static void encode(AetherBoilerModeChangeMessage msg, FriendlyByteBuf buf) {
		buf.writeBlockPos(msg.pos);
		buf.writeEnum(msg.next_mode);
	}

}
