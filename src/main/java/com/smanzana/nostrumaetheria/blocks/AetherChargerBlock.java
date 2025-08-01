package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.client.gui.container.AetherChargerGui;
import com.smanzana.nostrumaetheria.tiles.AetherChargerBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.loretag.ELoreCategory;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherChargerBlock extends BaseEntityBlock implements ILoreTagged {
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	
	public AetherChargerBlock() {
		super(Block.Properties.of(Material.STONE)
				.strength(3.0f, 10.0f)
				.sound(SoundType.STONE)
				);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ON, FACING);
	}
	
	public boolean getFurnaceOn(BlockState state) {
		return state.getValue(ON);
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
		AetherChargerBlockEntity charger = (AetherChargerBlockEntity) worldIn.getBlockEntity(pos);
		NostrumMagica.Proxy.openContainer(player, AetherChargerGui.AetherChargerContainer.Make(charger));
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherChargerBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Charger);
	}
	
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(ON, false)
				.setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(world, pos, state);
			world.removeBlockEntity(pos);
		}
	}
	
	private void destroy(Level world, BlockPos pos, BlockState state) {
		BlockEntity ent = world.getBlockEntity(pos);
		if (ent == null || !(ent instanceof AetherChargerBlockEntity))
			return;
		
		AetherChargerBlockEntity furnace = (AetherChargerBlockEntity) ent;
		for (int i = 0; i < furnace.getContainerSize(); i++) {
			if (furnace.getItem(i) != null) {
				ItemEntity item = new ItemEntity(
						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
						furnace.removeItemNoUpdate(i));
				world.addFreshEntity(item);
			}
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		if (null == stateIn || !stateIn.getValue(ON))
			return;
		
		Direction facing = stateIn.getValue(FACING);
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 0.6D;
		double d2 = (double)pos.getZ() + 0.5D;
		
		switch (facing) {
		case EAST:
			d0 += .6;
	        break;
		case NORTH:
			d2 -= .6;
	        break;
		case SOUTH:
	        d2 += .6;
	        break;
		case WEST:
			d0 -= .6;
	        break;
		case UP:
		case DOWN:
		default:
	        break;
		}
		
		worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		worldIn.addParticle(ParticleTypes.WITCH, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		
		if (rand.nextFloat() < .1f) {
			worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.7F, 0.25F, false);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_charger";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Charger";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether chargers are an improved version of the aether bath.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether chargers allow you to add aether to items quickly and efficiently.");
	}

	@Override
	public ELoreCategory getCategory() {
		return ELoreCategory.BLOCK;
	}
}
