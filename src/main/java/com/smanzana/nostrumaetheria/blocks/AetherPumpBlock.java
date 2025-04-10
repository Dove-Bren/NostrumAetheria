package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.tiles.AetherPumpBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherPumpBlock extends BaseEntityBlock implements ILoreTagged {
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
	
	private static final double BBWIDTH = 4.0;
	protected static final VoxelShape PUMP_AABBS[] = new VoxelShape[] {
			Block.box(8 - BBWIDTH, 0, 8 - BBWIDTH, 8 + BBWIDTH, 16, 8 + BBWIDTH), //down
			Block.box(8 - BBWIDTH, 0, 8 - BBWIDTH, 8 + BBWIDTH, 16, 8 + BBWIDTH), //up
			Block.box(8 - BBWIDTH, 8 - BBWIDTH, 0, 8 + BBWIDTH, 8 + BBWIDTH, 16), //north
			Block.box(8 - BBWIDTH, 8 - BBWIDTH, 0, 8 + BBWIDTH, 8 + BBWIDTH, 16), //south
			Block.box(0, 8 - BBWIDTH, 8 - BBWIDTH, 16, 8 + BBWIDTH, 8 + BBWIDTH), //east
			Block.box(0, 8 - BBWIDTH, 8 - BBWIDTH, 16, 8 + BBWIDTH, 8 + BBWIDTH), //west
	};
	
	public AetherPumpBlock() {
		super(Block.Properties.of(Material.STONE)
				.strength(1.0f, 10.0f)
				.sound(SoundType.STONE)
				.noOcclusion()
				);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	public Direction getFacing(BlockState state) {
		return state.getValue(FACING);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
//		if (!worldIn.isRemote) {
//			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherChargerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
//			return true;
//		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherPumpBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Pump);
	}
	
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return PUMP_AABBS[state.getValue(FACING).ordinal()];
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
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
