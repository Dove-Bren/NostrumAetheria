package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.tiles.AetherPumpBlockEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class AetherPumpBlock extends Block implements ILoreTagged {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
	
	private static final double BBWIDTH = 4.0;
	protected static final VoxelShape PUMP_AABBS[] = new VoxelShape[] {
			Block.makeCuboidShape(8 - BBWIDTH, 0, 8 - BBWIDTH, 8 + BBWIDTH, 16, 8 + BBWIDTH), //down
			Block.makeCuboidShape(8 - BBWIDTH, 0, 8 - BBWIDTH, 8 + BBWIDTH, 16, 8 + BBWIDTH), //up
			Block.makeCuboidShape(8 - BBWIDTH, 8 - BBWIDTH, 0, 8 + BBWIDTH, 8 + BBWIDTH, 16), //north
			Block.makeCuboidShape(8 - BBWIDTH, 8 - BBWIDTH, 0, 8 + BBWIDTH, 8 + BBWIDTH, 16), //south
			Block.makeCuboidShape(0, 8 - BBWIDTH, 8 - BBWIDTH, 16, 8 + BBWIDTH, 8 + BBWIDTH), //east
			Block.makeCuboidShape(0, 8 - BBWIDTH, 8 - BBWIDTH, 16, 8 + BBWIDTH, 8 + BBWIDTH), //west
	};
	
	public AetherPumpBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(1.0f, 10.0f)
				.sound(SoundType.STONE)
				.harvestTool(ToolType.PICKAXE)
				);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	public Direction getFacing(BlockState state) {
		return state.get(FACING);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
//		if (!worldIn.isRemote) {
//			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherChargerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
//			return true;
//		}
		
		return false;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AetherPumpBlockEntity();
	}
	
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
	}
	
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
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public boolean isTranslucent(BlockState state) {
//		return true;
//	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return PUMP_AABBS[state.get(FACING).ordinal()];
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);
	}
	
	@Override
	public String getLoreKey() {
		return "aether_pump";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Pump";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether Pumps pull aether out of one block and put it in another.");
	}

	@Override
	public Lore getDeepLore() {	
		return new Lore().add("Aether Pumps pull aether out of one block and put it in another.", "The larger end of the pump is where it pulls from.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
}
