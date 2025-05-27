package com.smanzana.nostrumaetheria.blocks;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.tiles.LensHolderBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LensHolderBlock extends BaseEntityBlock {

	protected static final VoxelShape ALTAR_AABB = Block.box(2, 2, 2, 14, 14, 14);
	
	public LensHolderBlock() {
		super(Block.Properties.of(Material.STONE)
				.strength(3.5f, 10.0f)
				.sound(SoundType.STONE)
				.lightLevel((state) -> 1)
				.noOcclusion()
				);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		final VoxelShape BASE_AABB = Block.box(2, 0, 2, 14, 14, 14);
		final VoxelShape HOLLOW_1 = Block.box(2, 2, 3, 14, 12, 13);
		final VoxelShape HOLLOW_2 = Block.box(3, 2, 2, 13, 12, 14);
		
		final VoxelShape HOLLOW = Shapes.or(HOLLOW_1, HOLLOW_2);
		
		return Shapes.join(BASE_AABB, HOLLOW, BooleanOp.ONLY_FIRST);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new LensHolderBlockEntity(pos, state);
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te != null) {
				LensHolderBlockEntity holder = (LensHolderBlockEntity) te;
				if (!holder.getItem().isEmpty()) {
					ItemEntity item = new ItemEntity(world,
							pos.getX() + .5,
							pos.getY() + .5,
							pos.getZ() + .5,
							holder.getItem());
					world.addFreshEntity(item);
				}
			}
			
	        world.removeBlockEntity(pos);
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (worldIn.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te == null)
			return InteractionResult.FAIL;
		
		final @Nonnull ItemStack heldItem = player.getItemInHand(hand);
		
		LensHolderBlockEntity holder = (LensHolderBlockEntity) te;
		if (holder.getItem().isEmpty()) {
			// Accepting items
			if (!heldItem.isEmpty() && holder.canPlaceItem(0, heldItem)) {
				holder.setItem(heldItem.split(1));
				return InteractionResult.SUCCESS;
			} else
				return InteractionResult.FAIL;
		} else {
			// Has an item
			if (heldItem.isEmpty()) {
				if (!player.getInventory().add(holder.getItem())) {
					worldIn.addFreshEntity(
							new ItemEntity(worldIn,
									pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5,
									holder.getItem())
							);
				}
				holder.setItem(ItemStack.EMPTY);
				return InteractionResult.SUCCESS;
			} else
				return InteractionResult.FAIL;
		}
		
	}

}
