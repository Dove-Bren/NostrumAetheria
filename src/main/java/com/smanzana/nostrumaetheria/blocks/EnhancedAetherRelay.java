package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.tiles.EnhancedAetherRelayEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * A relay except it can only do relaying to other relays. No direct IN/OUT
 * @author Skyler
 *
 */
public class EnhancedAetherRelay extends AetherRelay {
	
//	public static final double height = 0.575 * 16;
//	public static final double width = 0.125 * 16;
//	private static final double lowWidth = 8 - width;
//	private static final double highWidth = 8 + width;
//	protected static final VoxelShape RELAY_AABBs[] = new VoxelShape[] {
//			Block.makeCuboidShape(lowWidth, (16-height), lowWidth, highWidth, 16, highWidth), //down
//			Block.makeCuboidShape(lowWidth, 0.0D, lowWidth, highWidth, height, highWidth), //up
//			Block.makeCuboidShape(lowWidth, lowWidth, (16-height), highWidth, highWidth, 16), //north
//			Block.makeCuboidShape(lowWidth, lowWidth, 0, highWidth, highWidth, height), //south
//			Block.makeCuboidShape((16-height), lowWidth, lowWidth, 16, highWidth, highWidth), //east
//			Block.makeCuboidShape(0, lowWidth, lowWidth, height, highWidth, highWidth), //west
//	};
	
	public EnhancedAetherRelay() {
		super();
		
		//this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		//builder.add(FACING);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return state.getValue(FACING) == side;
//	}
	
//	@Override
//	public boolean isVisuallyOpaque() {
//		return false;
//	}
//	
//	@Override
//	public boolean isFullyOpaque(BlockState state) {
//		return false;
//	}
	
//	@Override
//	public boolean isFullBlock(BlockState state) {
//		return false;
//	}
//	
//	@Override
//	public boolean isOpaqueCube(BlockState state) {
//		return false;
//	}
//	
//	@Override
//	public boolean isFullCube(BlockState state) {
//		return false;
//	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return RELAY_AABBs[state.get(FACING).ordinal()];
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		// Only thing we have to do it STOP mode changes
		
		if (!worldIn.isRemote) {
			ItemStack heldItem = player.getHeldItem(handIn);
			if (heldItem.isEmpty()) {
				return false;
			}
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new EnhancedAetherRelayEntity(state.get(FACING));
	}
	
	@Override
	public String getLoreKey() {
		return "aether_relay";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Relays";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether relays allow aether to be pulled from distant blocks.", "Relays automatically link to one another if placed close enough.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether relays allow aether to be pulled from distant blocks.", "Relays automatically link to one another if placed close enough.", "Relays can link to other relays up to 8 blocks away.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
}
