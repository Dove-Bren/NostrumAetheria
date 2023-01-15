package com.smanzana.nostrumaetheria.network.messages;

import com.smanzana.nostrumaetheria.blocks.tiles.AetherBoilerBlockEntity;
import com.smanzana.nostrumaetheria.blocks.tiles.AetherBoilerBlockEntity.BoilerBurnMode;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Client has tried to change the aether boiler mode
 * @author Skyler
 *
 */
public class AetherBoilerModeChangeMessage implements IMessage {

	public static class Handler implements IMessageHandler<AetherBoilerModeChangeMessage, IMessage> {

		@Override
		public IMessage onMessage(AetherBoilerModeChangeMessage message, MessageContext ctx) {
			
			BlockPos pos = BlockPos.fromLong(message.tag.getLong(NBT_POS));
			BoilerBurnMode mode = BoilerBurnMode.valueOf(message.tag.getString(NBT_NEXT_MODE));
			
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				if (!player.world.isBlockLoaded(pos)) {
					return;
				}
				TileEntity te = player.world.getTileEntity(pos);
				if (te != null && te instanceof AetherBoilerBlockEntity) {
					((AetherBoilerBlockEntity) te).setBoilerMode(mode);
					
					// Cause an update to be sent back
					IBlockState state = player.world.getBlockState(pos);
					player.world.notifyBlockUpdate(pos, state, state, 2);
				}
			});
			
			return null;
		}
		
	}

	private static final String NBT_POS = "pos";
	private static final String NBT_NEXT_MODE = "next_mode";
	
	protected NBTTagCompound tag;
	
	public AetherBoilerModeChangeMessage() {
		tag = new NBTTagCompound();
	}
	
	public AetherBoilerModeChangeMessage(BlockPos pos, BoilerBurnMode mode) {
		tag = new NBTTagCompound();
		
		tag.setLong(NBT_POS, pos.toLong());
		tag.setString(NBT_NEXT_MODE, mode.name());
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag);
	}

}
