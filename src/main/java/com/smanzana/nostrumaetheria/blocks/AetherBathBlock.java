package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherBathBlock extends BaseEntityBlock implements ILoreTagged {
	
	protected static final VoxelShape ALTAR_AABB = Block.box(3.2D, 0.0D, 3.2D, 12.8D, 14.4D, 12.8D);
	
	public AetherBathBlock() {
		super(Block.Properties.of(Material.STONE)
				.strength(3.5f, 10.0f)
				.sound(SoundType.STONE)
				.lightLevel((state) -> 1)
				.noOcclusion()
				);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return ALTAR_AABB;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherBathTileEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Bath);
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te != null) {
				AetherBathTileEntity altar = (AetherBathTileEntity) te;
				if (!altar.getItem().isEmpty()) {
					ItemEntity item = new ItemEntity(world,
							pos.getX() + .5,
							pos.getY() + .5,
							pos.getZ() + .5,
							altar.getItem());
					world.addFreshEntity(item);
				}
			}
			
	        world.removeBlockEntity(pos);
		}
	}
	
	@Override
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int eventID, int eventParam) {
		super.triggerEvent(state, worldIn, pos, eventID, eventParam);
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity == null ? false : tileentity.triggerEvent(eventID, eventParam);
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
		
		AetherBathTileEntity altar = (AetherBathTileEntity) te;
		if (altar.getItem().isEmpty()) {
			// Accepting items
			if (!heldItem.isEmpty() && altar.canPlaceItem(0, heldItem)) {
				altar.setItem(heldItem.split(1));
				return InteractionResult.SUCCESS;
			} else
				return InteractionResult.FAIL;
		} else {
			// Has an item
			if (heldItem.isEmpty()) {
				if (!player.getInventory().add(altar.getItem())) {
					worldIn.addFreshEntity(
							new ItemEntity(worldIn,
									pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5,
									altar.getItem())
							);
				}
				altar.setItem(ItemStack.EMPTY);
				return InteractionResult.SUCCESS;
			} else
				return InteractionResult.FAIL;
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);
		
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te == null) {
			return;
		}
		AetherBathTileEntity altar = (AetherBathTileEntity) te;
		if (!altar.getItem().isEmpty() && !altar.heldItemFull()) {
			worldIn.addParticle(ParticleTypes.ENCHANT, pos.getX() + .5, pos.getY() + 1.5, pos.getZ() + .5,
					2 * rand.nextFloat() - .5f, 0, 2 * rand.nextFloat() - .5f);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_bath";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Bath";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Fashioned from an altar and a large stone bowl, the aether bath aesthetically displays all of your aether.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Fashioned from an altar and a large stone bowl, the aether bath aesthetically displays all of your aether.", "Aether baths fill up items with aether slowly.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
}
