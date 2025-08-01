package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.tiles.AetherRelayEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.item.PositionCrystal;
import com.smanzana.nostrummagica.loretag.ELoreCategory;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.sound.NostrumMagicaSounds;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;
import com.smanzana.nostrummagica.util.DimensionUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherRelay extends BaseEntityBlock implements ILoreTagged {
	
	public static enum RelayMode implements StringRepresentable {
		INOUT,
		IN,
		OUT,
		;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase();
		}
		
		public RelayMode next() {
			switch (this) {
			case IN:
				return RelayMode.OUT;
			case INOUT:
				return RelayMode.IN;
			case OUT:
			default:
				return RelayMode.INOUT;
			}
		}
	}
	
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
	public static final EnumProperty<RelayMode> RELAY_MODE = EnumProperty.<RelayMode>create("mode", RelayMode.class);
	
	public static final double height = 0.575 * 16;
	public static final double width = 0.125 * 16;
	private static final double lowWidth = 8 - width;
	private static final double highWidth = 8 + width;
	protected static final VoxelShape RELAY_AABBs[] = new VoxelShape[] {
			Block.box(lowWidth, (16-height), lowWidth, highWidth, 16, highWidth), //down
			Block.box(lowWidth, 0.0D, lowWidth, highWidth, height, highWidth), //up
			Block.box(lowWidth, lowWidth, (16-height), highWidth, highWidth, 16), //north
			Block.box(lowWidth, lowWidth, 0, highWidth, highWidth, height), //south
			Block.box((16-height), lowWidth, lowWidth, 16, highWidth, highWidth), //east
			Block.box(0, lowWidth, lowWidth, height, highWidth, highWidth), //west
	};
	
	public AetherRelay() {
		super(Block.Properties.of(Material.GLASS)
				.strength(0.5f, 2.0f)
				.sound(SoundType.GLASS)
				.lightLevel((state) -> 4)
				);
		
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(RELAY_MODE, RelayMode.INOUT));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, RELAY_MODE);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return RELAY_AABBs[state.getValue(FACING).ordinal()];
	}
	
	public boolean canPlaceAt(LevelReader world, BlockPos pos, Direction facing) {
		BlockPos blockpos = pos.relative(facing.getOpposite());
		BlockState wallState = world.getBlockState(blockpos);
		return wallState.isFaceSturdy(world, blockpos, facing);// || facing.equals(Direction.UP) && this.canPlaceOn(worldIn, blockpos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return canPlaceAt(worldIn, pos, state.getValue(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		
		if (!worldIn.isClientSide) {
//			// request an update
//			worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
//			
//			TileEntity ent = worldIn.getTileEntity(pos);
//			if (ent != null && ent instanceof AetherRelayEntity) {
//				AetherRelayEntity relay = (AetherRelayEntity) ent;
//				System.out.println(relay.getHandler() == null ? "Missing relay handler" : "has relay handler with " + ((AetherRelayComponent)relay.getHandler()).getLinkedPositions().size());
//			}
//			return true;
			
			// If using a geogem, link to its position.
			// If no hand, switch modes
			ItemStack heldItem = player.getItemInHand(handIn);
			if (heldItem.isEmpty()) {
				BlockEntity te = worldIn.getBlockEntity(pos);
				if (te != null) {
					AetherRelayEntity ent = (AetherRelayEntity) te;
					ent.setMode(state.getValue(RELAY_MODE).next());
					NostrumMagicaSounds.STATUS_BUFF1.play(worldIn, pos.getX(), pos.getY(), pos.getZ());
				}
				return InteractionResult.SUCCESS;
			} else if (heldItem.getItem() instanceof PositionCrystal) {
				BlockPos heldPos = PositionCrystal.getBlockPosition(heldItem);
				if (heldPos != null && DimensionUtils.DimEquals(PositionCrystal.getDimension(heldItem), worldIn.dimension())) {
					BlockEntity te = worldIn.getBlockEntity(pos);
					if (te != null) {
						AetherRelayEntity ent = (AetherRelayEntity) te;
						ent.addLink(player, heldPos, player.isCreative());
						NostrumMagicaSounds.STATUS_BUFF1.play(worldIn, pos.getX(), pos.getY(), pos.getZ());
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherRelayEntity(pos, state, state.getValue(FACING), state.getValue(RELAY_MODE));
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Relay);
	}
	
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}
	
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(worldIn, pos, state);
			worldIn.removeBlockEntity(pos);
		}
	}
	
	private void destroy(Level world, BlockPos pos, BlockState state) {
		BlockEntity ent = world.getBlockEntity(pos);
		if (ent == null || !(ent instanceof AetherRelayEntity))
			return;
		
		//AetherRelayEntity relay = (AetherRelayEntity) ent;
		//((AetherRelayComponent)relay.getHandler()).unlinkAll();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		
	}
	
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : stateIn;
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
	public ELoreCategory getCategory() {
		return ELoreCategory.BLOCK;
	}
}
