package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrumaetheria.tiles.EnhancedAetherRelayEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A relay except it can only do relaying to other relays. No direct IN/OUT
 * @author Skyler
 *
 */
public class EnhancedAetherRelay extends AetherRelay {
	
	public EnhancedAetherRelay() {
		super();
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return RELAY_AABBs[state.getValue(FACING).ordinal()];
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		
		// Only thing we have to do it STOP mode changes
		
		if (!worldIn.isClientSide) {
			ItemStack heldItem = player.getItemInHand(handIn);
			if (heldItem.isEmpty()) {
				return InteractionResult.FAIL;
			}
		}
		
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EnhancedAetherRelayEntity(pos, state, state.getValue(FACING));
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.EnhancedRelay);
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
