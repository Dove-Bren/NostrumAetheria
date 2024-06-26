package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.tiles.AetherInfuserTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AetherInfuser extends Block {
	
	private static final BooleanProperty MASTER = BooleanProperty.create("master");
	
	public AetherInfuser() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(5.0f, 8.0f)
				.sound(SoundType.STONE)
				.noDrops()
				.notSolid() // so that light f or TE is non-zero
				);
		
	}
	
	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockReader worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(MASTER);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if (state.get(MASTER)) {
			return new AetherInfuserTileEntity();
		}
		
		return null;
	}
	
	// TODO need the event stuff from ContainerBlock?
	
//	@Override
//	public TileEntity createNewTileEntity(IBlockReader world) {
//		return new AetherInfuserTileEntity();
//	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(MASTER);
	}
	
	public static boolean IsMaster(BlockState state) {
		return state != null && state.getBlock() instanceof AetherInfuser && state.get(MASTER);
	}
	
	public static void SetBlock(World world, BlockPos pos, boolean master) {
		world.setBlockState(pos, AetheriaBlocks.infuser.getDefaultState().with(MASTER, master));
	}
	
	public static boolean CreateAetherInfuser(World world, BlockPos pos) {
		AetherInfuser.SetBlock(world, pos, true);
		for (BlockPos offset : new BlockPos[] {
			pos.north(), pos.south(), pos.east(), pos.west(), pos.north().east(), pos.north().west(), pos.south().east(), pos.south().west()
		}) {
			AetherInfuser.SetBlock(world, offset, false);
		}
		return true;
	}
}
