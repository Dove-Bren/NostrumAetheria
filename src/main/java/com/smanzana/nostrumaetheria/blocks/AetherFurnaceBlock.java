package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.tiles.AetherFurnaceBlockEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class AetherFurnaceBlock extends Block implements ILoreTagged {
	
	public static enum Type implements IStringSerializable {
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
		public String getName() {
			return this.name().toLowerCase();
		}
		
		@Override
		public String toString() {
			return this.getName();
		}
		
		public float getAetherMultiplier() {
			return this.aetherMultiplier;
		}
		
		public float getDurationMultiplier() {
			return this.durationMultiplier;
		}
	}
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	
	protected static final String ID_PREFIX = "aether_furnace_";
	protected static final String ID_SMALL = ID_PREFIX + "small";
	protected static final String ID_MEDIUM = ID_PREFIX + "medium";
	protected static final String ID_LARGE = ID_PREFIX + "large";
	
	private final Type furnaceType;
	
	public AetherFurnaceBlock(Type type) {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3.0f, 10.0f)
				.sound(SoundType.STONE)
				.harvestTool(ToolType.PICKAXE)
				);
		this.furnaceType = type;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
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
		return state.get(ON);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			//worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherFurnaceID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AetherFurnaceBlockEntity(this.getFurnaceType());
	}
	
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(ON, false);
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			destroy(world, pos, state);
			world.removeTileEntity(pos);
		}
	}
	
	private void destroy(World world, BlockPos pos, BlockState state) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherFurnaceBlockEntity))
			return;
		
		AetherFurnaceBlockEntity furnace = (AetherFurnaceBlockEntity) ent;
		for (int i = 0; i < furnace.getSizeInventory(); i++) {
			if (!furnace.getStackInSlot(i).isEmpty()) {
				ItemEntity item = new ItemEntity(
						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
						furnace.removeStackFromSlot(i));
				world.addEntity(item);
			}
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (null == stateIn || !stateIn.get(ON))
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
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1F, false);
		}
		if (rand.nextFloat() < .1f) {
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.05F, 2F, false);
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
