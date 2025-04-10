package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.tiles.AetherInfuserTileEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class AetherInfuser extends BaseEntityBlock {
	
	private static final BooleanProperty MASTER = BooleanProperty.create("master");
	
	public AetherInfuser() {
		super(Block.Properties.of(Material.STONE)
				.strength(5.0f, 8.0f)
				.sound(SoundType.STONE)
				.noDrops()
				.noOcclusion() // so that light f or TE is non-zero
				);
		
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		if (state.getValue(MASTER)) {
			return new AetherInfuserTileEntity(pos, state);
		}
		
		return null;
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.AetherInfuserEnt);
	}
	
	// TODO need the event stuff from ContainerBlock?
	
//	@Override
//	public TileEntity createNewTileEntity(IBlockReader world) {
//		return new AetherInfuserTileEntity();
//	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(MASTER);
	}
	
	public static boolean IsMaster(BlockState state) {
		return state != null && state.getBlock() instanceof AetherInfuser && state.getValue(MASTER);
	}
	
	public static void SetBlock(Level world, BlockPos pos, boolean master) {
		world.setBlockAndUpdate(pos, AetheriaBlocks.infuser.defaultBlockState().setValue(MASTER, master));
	}
	
	public static boolean CreateAetherInfuser(Level world, BlockPos pos) {
		AetherInfuser.SetBlock(world, pos, true);
		for (BlockPos offset : new BlockPos[] {
			pos.north(), pos.south(), pos.east(), pos.west(), pos.north().east(), pos.north().west(), pos.south().east(), pos.south().west()
		}) {
			AetherInfuser.SetBlock(world, offset, false);
		}
		return true;
	}
}
