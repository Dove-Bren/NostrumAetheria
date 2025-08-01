package com.smanzana.nostrumaetheria.blocks;

import com.smanzana.nostrumaetheria.tiles.AetherBatteryEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.loretag.ELoreCategory;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

public class AetherBatteryBlock extends BaseEntityBlock implements ILoreTagged {
	
	public static enum Size {
		SMALL(1000),
		MEDIUM(3000),
		LARGE(10000),
		GIANT(50000);
		
		public final int capacity;
		
		private Size(int capacity) {
			this.capacity = capacity;
		}
	}
	
	private final Size size;
	
	public AetherBatteryBlock(Size size) {
		super(Block.Properties.of(Material.STONE)
				.strength(3.0f, 10.0f)
				.sound(SoundType.GLASS)
				.noOcclusion()
				);
		
		this.size = size;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		
		if (!worldIn.isClientSide && handIn == InteractionHand.MAIN_HAND) {
			// request an update
			worldIn.sendBlockUpdated(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
			
//			int unused;
//			{
//				AetherBatteryEntity tileentity = (AetherBatteryEntity) worldIn.getTileEntity(pos);
//				NostrumAetheria.logger.debug(tileentity.getHandler().getAether(null));
//			}
			
			//return true;
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherBatteryEntity(pos, state, size);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Battery);
	}
	
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(world, pos, state);
			world.removeBlockEntity(pos);
		}
	}
	
	private void destroy(Level world, BlockPos pos, BlockState state) {
		
	}

	@Override
	public String getLoreKey() {
		return "AetherBattery";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Batteries";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether batteries store aether, allowing you to build up a bunch for big costs.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether batteries store aether, allowing you to build up a bunch for big costs.", "There are four levels of battery. Batteries automatically flow into eachother, prefering to flow down when possible.");
	}

	@Override
	public ELoreCategory getCategory() {
		return ELoreCategory.BLOCK;
	}
	
	public int getMaxAether() {
		return size.capacity;
	}
}
