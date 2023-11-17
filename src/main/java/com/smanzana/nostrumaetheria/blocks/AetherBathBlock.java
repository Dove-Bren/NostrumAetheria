package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.tiles.AetherBathTileEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherBathBlock extends Block implements ILoreTagged {
	
	protected static final VoxelShape ALTAR_AABB = Block.makeCuboidShape(3.2D, 0.0D, 3.2D, 12.8D, 14.4D, 12.8D);
	
	public AetherBathBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3.5f, 10.0f)
				.sound(SoundType.STONE)
				.lightValue(1)
				);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return ALTAR_AABB;
	}
	
//	@Override
//	public boolean isVisuallyOpaque() {
//		return false;
//	}
	
//	@Override
//	public boolean isOpaqueCube(BlockState state) {
//		return false;
//	}
//	
//	@Override
//	public boolean isFullCube(BlockState state) {
//        return false;
//    }
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
//	
//	@Override
//	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
//		return false;
//	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		AetherBathTileEntity ent = new AetherBathTileEntity();
		
		return ent;
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getTileEntity(pos);
			if (te != null) {
				AetherBathTileEntity altar = (AetherBathTileEntity) te;
				if (!altar.getItem().isEmpty()) {
					ItemEntity item = new ItemEntity(world,
							pos.getX() + .5,
							pos.getY() + .5,
							pos.getZ() + .5,
							altar.getItem());
					world.addEntity(item);
				}
			}
			
	        world.removeTileEntity(pos);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
		super.eventReceived(state, worldIn, pos, eventID, eventParam);
		TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			return true;
		}
		
		TileEntity te = worldIn.getTileEntity(pos);
		if (te == null)
			return false;
		
		final @Nonnull ItemStack heldItem = player.getHeldItem(hand);
		
		AetherBathTileEntity altar = (AetherBathTileEntity) te;
		if (altar.getItem().isEmpty()) {
			// Accepting items
			if (!heldItem.isEmpty() && altar.isItemValidForSlot(0, heldItem)) {
				altar.setItem(heldItem.split(1));
				return true;
			} else
				return false;
		} else {
			// Has an item
			if (heldItem.isEmpty()) {
				if (!player.inventory.addItemStackToInventory(altar.getItem())) {
					worldIn.addEntity(
							new ItemEntity(worldIn,
									pos.getX() + .5, pos.getY() + 1.2, pos.getZ() + .5,
									altar.getItem())
							);
				}
				altar.setItem(ItemStack.EMPTY);
				return true;
			} else
				return false;
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);
		
		TileEntity te = worldIn.getTileEntity(pos);
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
