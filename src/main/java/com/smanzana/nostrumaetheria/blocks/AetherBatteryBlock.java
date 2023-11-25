package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherBatteryBlock extends Block implements ILoreTagged {
	
	public static enum Size {
		SMALL(1000),
		MEDIUM(3000),
		LARGE(10000),
		GIANT(50000);
		
		public final int capacity;
		
		private Size(int capacity) {
			this.capacity = capacity;
		}
	}
	
	private final Size size;
	
	public AetherBatteryBlock(Size size) {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3.0f, 10.0f)
				.sound(SoundType.GLASS)
				);
		
		this.size = size;
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (!worldIn.isRemote && handIn == Hand.MAIN_HAND) {
			// request an update
			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
			
//			int unused;
//			{
//				AetherBatteryEntity tileentity = (AetherBatteryEntity) worldIn.getTileEntity(pos);
//				NostrumAetheria.logger.debug(tileentity.getHandler().getAether(null));
//			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AetherBatteryEntity(size);
	}
	
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(world, pos, state);
			world.removeTileEntity(pos);
		}
	}
	
	private void destroy(World world, BlockPos pos, BlockState state) {
		
	}

	@Override
	public String getLoreKey() {
		return "AetherBattery";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Batteries";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether batteries store aether, allowing you to build up a bunch for big costs.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether batteries store aether, allowing you to build up a bunch for big costs.", "There are four levels of battery. Batteries automatically flow into eachother, prefering to flow down when possible.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public int getMaxAether() {
		return size.capacity;
	}
}
