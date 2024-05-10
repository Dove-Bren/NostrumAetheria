package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.client.gui.container.AetherChargerGui;
import com.smanzana.nostrumaetheria.tiles.AetherChargerBlockEntity;
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
import net.minecraft.util.ActionResultType;
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

public class AetherChargerBlock extends Block implements ILoreTagged {
	
	public static final BooleanProperty ON = BooleanProperty.create("on");
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	public AetherChargerBlock() {
		super(Block.Properties.create(Material.ROCK)
				.hardnessAndResistance(3.0f, 10.0f)
				.sound(SoundType.STONE)
				.harvestTool(ToolType.PICKAXE)
				);
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
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		AetherChargerBlockEntity charger = (AetherChargerBlockEntity) worldIn.getTileEntity(pos);
		NostrumMagica.instance.proxy.openContainer(player, AetherChargerGui.AetherChargerContainer.Make(charger));
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new AetherChargerBlockEntity();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
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
		if (ent == null || !(ent instanceof AetherChargerBlockEntity))
			return;
		
		AetherChargerBlockEntity furnace = (AetherChargerBlockEntity) ent;
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
		
		Direction facing = stateIn.get(FACING);
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
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 0.7F, 0.25F, false);
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
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
}
