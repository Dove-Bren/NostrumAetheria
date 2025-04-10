package com.smanzana.nostrumaetheria.blocks;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.client.gui.container.WispBlockGui;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrumaetheria.tiles.WispBlockTileEntity;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.item.ReagentItem;
import com.smanzana.nostrummagica.item.SpellScroll;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WispBlock extends BaseEntityBlock {
	
	protected static final VoxelShape SELECT_AABB = Block.box(3.2, 14.4, 3.2, 12.8, 20.8, 12.8);
	protected static final VoxelShape COLLIDE_AABB = Block.box(3.2, 0, 3.2, 12.8, 20.8, 12.8);
	
	public WispBlock() {
		super(Block.Properties.of(Material.GLASS)
				.strength(3.0f, 10.0f)
				.sound(SoundType.GLASS)
				.noOcclusion()
				);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		
		ItemStack heldItem = playerIn.getItemInHand(hand);
		
		// Automatically handle scrolls and reagents
		if (!playerIn.isShiftKeyDown() && !heldItem.isEmpty()) {
			WispBlockTileEntity te = (WispBlockTileEntity) worldIn.getBlockEntity(pos);
			if (heldItem.getItem() instanceof SpellScroll
					&& te.getScroll() == null
					&& SpellScroll.GetSpell(heldItem) != null) {
				// Take scroll
				te.setScroll(heldItem.copy());
				heldItem.shrink(1);
				return InteractionResult.SUCCESS;
			} else if (heldItem.getItem() instanceof ReagentItem) {
				
				if (te.getReagent().isEmpty()) {
					te.setReagent(heldItem.split(heldItem.getCount()));
					return InteractionResult.SUCCESS;
				} else if (ReagentItem.FindType(heldItem) == ReagentItem.FindType(te.getReagent())) {
					int avail = Math.max(0, Math.min(64, 64 - te.getReagent().getCount()));
					if (avail != 0) {
						int take = Math.min(avail, heldItem.getCount());
						heldItem.shrink(take);
						te.getReagent().grow(take);
						return InteractionResult.SUCCESS;
					}
				}
			}
		}
		
		WispBlockTileEntity te = (WispBlockTileEntity) worldIn.getBlockEntity(pos);
		NostrumMagica.instance.proxy.openContainer(playerIn, WispBlockGui.WispBlockContainer.Make(te));
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WispBlockTileEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.WispBlockEnt);
	}
	
	@Override
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int eventID, int eventParam) {
		super.triggerEvent(state, worldIn, pos, eventID, eventParam);
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity == null ? false : tileentity.triggerEvent(eventID, eventParam);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SELECT_AABB;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return COLLIDE_AABB;
	}
	
//	@Override
//	public boolean isOpaqueCube(BlockState state) {
//		return false;
//	}
//	
//	@Override
//	public boolean isFullCube(BlockState state) {
//		return false;
//	}
//	
//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public boolean isTranslucent(BlockState state) {
//		return true;
//	}
	
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(worldIn, pos, state);
			worldIn.removeBlockEntity(pos);
			//super.onReplaced(world, pos, state);
		}
	}
	
	private void destroy(Level world, BlockPos pos, BlockState state) {
		BlockEntity ent = world.getBlockEntity(pos);
		if (ent == null || !(ent instanceof WispBlockTileEntity))
			return;
		
		WispBlockTileEntity table = (WispBlockTileEntity) ent;
		ItemStack item = table.getScroll();
		if (!item.isEmpty()) {
			double x, y, z;
			x = pos.getX() + .5;
			y = pos.getY() + .5;
			z = pos.getZ() + .5;
			world.addFreshEntity(new ItemEntity(world, x, y, z, item.copy()));
		}
		
		item = table.getReagent();
		if (!item.isEmpty()) {
			double x, y, z;
			x = pos.getX() + .5;
			y = pos.getY() + .5;
			z = pos.getZ() + .5;
			world.addFreshEntity(new ItemEntity(world, x, y, z, item.copy()));
		}
		
		table.deactivate();
	}
	
	public static @Nonnull ItemStack getScroll(Level world, BlockPos pos) {
		BlockEntity ent = world.getBlockEntity(pos);
		if (ent == null || !(ent instanceof WispBlockTileEntity))
			return ItemStack.EMPTY;
		
		WispBlockTileEntity table = (WispBlockTileEntity) ent;
		return table.getScroll();
	}
	
	public int getWispCount(Level world, BlockPos pos) {
		BlockEntity ent = world.getBlockEntity(pos);
		if (ent == null || !(ent instanceof WispBlockTileEntity))
			return 0;
		
		WispBlockTileEntity table = (WispBlockTileEntity) ent;
		return table.getWispCount();
	}
	
	public int getMaxWisps(Level world, BlockPos pos) {
		BlockEntity ent = world.getBlockEntity(pos);
		if (ent == null || !(ent instanceof WispBlockTileEntity))
			return 0;
		
		WispBlockTileEntity table = (WispBlockTileEntity) ent;
		return table.getMaxWisps();
	}
}
