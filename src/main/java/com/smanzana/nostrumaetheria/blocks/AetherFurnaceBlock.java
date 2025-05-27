package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.client.gui.container.AetherFurnaceGui;
import com.smanzana.nostrumaetheria.tiles.AetherFurnaceBlockEntity;
import com.smanzana.nostrumaetheria.tiles.AetheriaTileEntities;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.tile.TickableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AetherFurnaceBlock extends BaseEntityBlock implements ILoreTagged {
	
	public static enum Type implements StringRepresentable {
		SMALL(1, 1),
		MEDIUM(1, 1.2f),
		LARGE(1, 1.5f);
		
		private float aetherMultiplier;
		private float durationMultiplier;
		
		private Type(float aetherMultiplier, float durationMultiplier) {
			this.aetherMultiplier = aetherMultiplier;
			this.durationMultiplier = durationMultiplier;
		}
		
		@Override
		public String getSerializedName() {
			return this.name().toLowerCase();
		}
		
		@Override
		public String toString() {
			return this.getSerializedName();
		}
		
		public float getAetherMultiplier() {
			return this.aetherMultiplier;
		}
		
		public float getDurationMultiplier() {
			return this.durationMultiplier;
		}
	}
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	
	private final Type furnaceType;
	
	public AetherFurnaceBlock(Type type) {
		super(Block.Properties.of(Material.STONE)
				.strength(3.0f, 10.0f)
				.sound(SoundType.STONE)
				);
		this.furnaceType = type;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ON);
	}
	
	public static int getFurnaceSlotsForType(Type type) {
		switch (type) {
		case LARGE:
			return 7;
		case MEDIUM:
			return 5;
		case SMALL:
			return 3;
		}
		
		return 3;
	}
	
	public Type getFurnaceType() {
		return this.furnaceType;
	}
	
	public Type getType(BlockState state) {
		if (state.getBlock() instanceof AetherFurnaceBlock) {
			return ((AetherFurnaceBlock) state.getBlock()).getFurnaceType();
		} else {
			return null;
		}
	}
	
	public boolean getFurnaceOn(BlockState state) {
		return state.getValue(ON);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		AetherFurnaceBlockEntity furnace = (AetherFurnaceBlockEntity) worldIn.getBlockEntity(pos);
		NostrumMagica.Proxy.openContainer(player, AetherFurnaceGui.AetherFurnaceContainer.Make(furnace));
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AetherFurnaceBlockEntity(pos, state, this.getFurnaceType());
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return TickableBlockEntity.createTickerHelper(type, AetheriaTileEntities.Furnace);
	}
	
	public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		return tileentity == null ? false : tileentity.triggerEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(ON, false);
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
		if (ent == null || !(ent instanceof AetherFurnaceBlockEntity))
			return;
		
		AetherFurnaceBlockEntity furnace = (AetherFurnaceBlockEntity) ent;
		for (int i = 0; i < furnace.getContainerSize(); i++) {
			if (!furnace.getItem(i).isEmpty()) {
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
		
		final double d0 = (double)pos.getX() + 0.5D;
		final double d1 = (double)pos.getY() + 0.2D;
		final double d2 = (double)pos.getZ() + 0.5D;
		
		for (Direction facing : Direction.Plane.HORIZONTAL) {
			if (!rand.nextBoolean()) {
				continue;
			}
			
			double x = d0;
			double y = d1;
			double z = d2;
			
			switch (facing) {
			case EAST:
				x += .6;
		        break;
			case NORTH:
				z -= .6;
		        break;
			case SOUTH:
		        z += .6;
		        break;
			case WEST:
				x -= .6;
		        break;
			case UP:
			case DOWN:
			default:
		        break;
			}
			
			worldIn.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
			worldIn.addParticle(ParticleTypes.WITCH, x, y + .5, z, 0.0D, 0.0D, 0.0D);
		}
		
		if (rand.nextFloat() < .1f) {
			worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1F, false);
		}
		if (rand.nextFloat() < .1f) {
			worldIn.playLocalSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.05F, 2F, false);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_furnace";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Furnace";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether furnaces burn reagents and produce aether.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether furnaces burn reagents and produce aether.", "Each different tier of furnace takes more reagents, but burn longer.", "All reagents placed in the furnace must be different.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public static AetherFurnaceBlock GetForType(Type type) {
		switch (type) {
		case SMALL:
			return AetheriaBlocks.smallFurnace;
		case MEDIUM:
			return AetheriaBlocks.mediumFurnace;
		case LARGE:
			return AetheriaBlocks.largeFurnace;
		}
		
		NostrumAetheria.logger.error("Failed to find furnace for type " + (type == null ? "NULL" : type));
		return null;
	}
}
