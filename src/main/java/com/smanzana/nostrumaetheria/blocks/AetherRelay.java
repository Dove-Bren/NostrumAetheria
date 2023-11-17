package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.component.AetherRelayComponent;
import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class AetherRelay extends Block implements ILoreTagged {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
	
	public static final double height = 0.575 * 16;
	public static final double width = 0.125 * 16;
	private static final double lowWidth = 8 - width;
	private static final double highWidth = 8 + width;
	protected static final VoxelShape RELAY_AABBs[] = new VoxelShape[] {
			Block.makeCuboidShape(lowWidth, (16-height), lowWidth, highWidth, 16, highWidth), //down
			Block.makeCuboidShape(lowWidth, 0.0D, lowWidth, highWidth, height, highWidth), //up
			Block.makeCuboidShape(lowWidth, lowWidth, (16-height), highWidth, highWidth, 16), //north
			Block.makeCuboidShape(lowWidth, lowWidth, 0, highWidth, highWidth, height), //south
			Block.makeCuboidShape((16-height), lowWidth, lowWidth, 16, highWidth, highWidth), //east
			Block.makeCuboidShape(0, lowWidth, lowWidth, height, highWidth, highWidth), //west
	};
	
	public AetherRelay() {
		super(Block.Properties.create(Material.GLASS)
				.hardnessAndResistance(0.5f, 2.0f)
				.sound(SoundType.GLASS)
				.harvestTool(ToolType.AXE)
				.lightValue(4)
				
				);
		
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
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
	
//	@Override
//	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
//		return RELAY_AABBs[state.getValue(FACING).ordinal()];
//	}
	
//	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
//		for (Direction enumfacing : FACING.getAllowedValues()) {
//			if (this.canPlaceAt(worldIn, pos, enumfacing)) {
//				return true;
//			}
//		}
//
//		return false;
//	}
	
	public boolean canPlaceAt(IWorldReader world, BlockPos pos, Direction facing) {
		BlockPos blockpos = pos.offset(facing.getOpposite());
		BlockState wallState = world.getBlockState(blockpos);
		return wallState.func_224755_d(world, blockpos, facing);// || facing.equals(Direction.UP) && this.canPlaceOn(worldIn, blockpos);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return canPlaceAt(worldIn, pos, state.get(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getFace());
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (!worldIn.isRemote) {
			// r equest an update
			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
			
			TileEntity ent = worldIn.getTileEntity(pos);
			if (ent != null && ent instanceof AetherRelayEntity) {
				AetherRelayEntity relay = (AetherRelayEntity) ent;
				System.out.println(relay.getHandler() == null ? "Missing relay handler" : "has relay handler with " + ((AetherRelayComponent)relay.getHandler()).getLinkedPositions().size());
			}
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
		return new AetherRelayEntity(state.get(FACING));
	}
	
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(worldIn, pos, state);
			worldIn.removeTileEntity(pos);
		}
	}
	
	private void destroy(World world, BlockPos pos, BlockState state) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherRelayEntity))
			return;
		
		AetherRelayEntity relay = (AetherRelayEntity) ent;
		((AetherRelayComponent)relay.getHandler()).unlinkAll();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public boolean isTranslucent(BlockState state) {
//		return true;
//	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
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
