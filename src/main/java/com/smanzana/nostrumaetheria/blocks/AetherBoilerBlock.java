package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.gui.container.AetherBoilerGui;
import com.smanzana.nostrumaetheria.tiles.AetherBoilerBlockEntity;
import com.smanzana.nostrummagica.NostrumMagica;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class AetherBoilerBlock extends Block implements ILoreTagged {
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	public AetherBoilerBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3.0f, 10.0f)
				.sound(SoundType.STONE)
				.harvestTool(ToolType.PICKAXE));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(ON, FACING);
	}
	
	public boolean getFurnaceOn(BlockState state) {
		return state.get(ON);
	}
	
	public Direction getFacing(BlockState state) {
		return state.get(FACING);
	}
	
//	@Override
//	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
//		return true;
//	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		AetherBoilerBlockEntity boiler = (AetherBoilerBlockEntity) worldIn.getTileEntity(pos);
		NostrumMagica.instance.proxy.openContainer(player, AetherBoilerGui.AetherBoilerContainer.Make(boiler));
		return true;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AetherBoilerBlockEntity();
	}
	
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState()
				.with(ON, false)
				.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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
		if (ent == null || !(ent instanceof AetherBoilerBlockEntity))
			return;
		
		AetherBoilerBlockEntity furnace = (AetherBoilerBlockEntity) ent;
		for (int i = 0; i < furnace.getSizeInventory(); i++) {
			if (furnace.getStackInSlot(i) != null) {
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
		
		if (rand.nextBoolean()) {
			Direction facing = stateIn.get(FACING);
			double d0 = (double)pos.getX() + 0.5D;
			double d1 = (double)pos.getY() + 0.95D;
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
			worldIn.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
		
		if (rand.nextFloat() < .2f) {
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 0.25F, false);
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.05F, 0.25F, false);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_boiler";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Boiler";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("Aether boilers allow you to generate aether while also doing regular smelting.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("Aether boilers allow you to generate aether while also doing regular smelting.", "To use, put the boiler underneath a furnace. Add reagents, and then watch the furnace be powered!");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
}
