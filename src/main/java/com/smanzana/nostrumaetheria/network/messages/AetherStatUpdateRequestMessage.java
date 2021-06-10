package com.smanzana.nostrumaetheria.network.messages;

import com.smanzana.nostrumaetheria.api.blocks.AetherTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Server is sending an update about the aether level of a tile entity
 * @author Skyler
 *
 */
public class AetherStatUpdateRequestMessage implements IMessage {

	public static class Handler implements IMessageHandler<AetherStatUpdateRequestMessage, IMessage> {

		@Override
		public IMessage onMessage(AetherStatUpdateRequestMessage message, MessageContext ctx) {
			
			int dim = message.tag.getInteger(NBT_DIM);
			BlockPos pos = BlockPos.fromLong(message.tag.getLong(NBT_POS));
			int aether = message.tag.getInteger(NBT_AETHER);
			
			Minecraft.getMinecraft().addScheduledTask(() -> {
				if (Minecraft.getMinecraft().thePlayer.worldObj.provider.getDimension() == dim) {
					World world = Minecraft.getMinecraft().thePlayer.worldObj;
					if (!world.isBlockLoaded(pos)) {
						return;
					}
					TileEntity te = world.getTileEntity(pos);
					if (te != null && te instanceof AetherTileEntity) {
						((AetherTileEntity) te).syncAether(aether);
					}
				}
			});
			
			return null;
		}
		
	}

	private static final String NBT_DIM = "dim";
	private static final String NBT_POS = "pos";
	private static final String NBT_AETHER = "aether";
	
	protected NBTTagCompound tag;
	
	public AetherStatUpdateRequestMessage() {
		tag = new NBTTagCompound();
	}
	
	public AetherStatUpdateRequestMessage(World world, BlockPos pos, int aether) {
		tag = new NBTTagCompound();
		
		tag.setInteger(NBT_DIM, world.provider.getDimension());
		tag.setLong(NBT_POS, pos.toLong());
		tag.setInteger(NBT_AETHER, aether);
	}
	
	public AetherStatUpdateRequestMessage(AetherTileEntity te) {
		this(te.getWorld(), te.getPos(), te.getHandler().getAether(null));
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
