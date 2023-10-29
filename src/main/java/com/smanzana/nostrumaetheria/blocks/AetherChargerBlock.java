package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.tiles.AetherChargerBlockEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherChargerBlock extends BlockContainer implements ILoreTagged {
	
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final String ID = "aether_charger";
	
	
	private static AetherChargerBlock instance = null;
	public static AetherChargerBlock instance() {
		if (instance == null)
			instance = new AetherChargerBlock();
		
		return instance;
	}
	
	public AetherChargerBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		this.setCreativeTab(APIProxy.creativeTab);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", 0);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ON, FACING);
	}
	
	private static boolean onFromMeta(int meta) {
		return (meta & 1) == 1;
	}
	
	private static int metaFromOn(boolean on) {
		return (on ? 1 : 0);
	}
	
	private static EnumFacing facingFromMeta(int meta) {
		return EnumFacing.getHorizontal((meta >> 1) & 3);
	}
	
	private static int metaFromFacing(EnumFacing facing) {
		return facing.getHorizontalIndex() << 1;
	}
	
	@Override
	public BlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(ON, onFromMeta(meta))
				.withProperty(FACING, facingFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(BlockState state) {
		return metaFromOn(state.getValue(ON)) | metaFromFacing(state.getValue(FACING));
	}
	
	public boolean getFurnaceOn(BlockState state) {
		return state.getValue(ON);
	}
	
	public EnumFacing getFacing(BlockState state) {
		return state.getValue(FACING);
	}
	
	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherChargerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherChargerBlockEntity();
	}
	
	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState()
				.withProperty(ON, false)
				.withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public int damageDropped(BlockState state) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		super.getSubBlocks(tab, list);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(BlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		destroy(world, pos, state);
		super.breakBlock(world, pos, state);
	}
	
	private void destroy(World world, BlockPos pos, BlockState state) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherChargerBlockEntity))
			return;
		
		AetherChargerBlockEntity furnace = (AetherChargerBlockEntity) ent;
		for (int i = 0; i < furnace.getSizeInventory(); i++) {
			if (furnace.getStackInSlot(i) != null) {
				EntityItem item = new EntityItem(
						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
						furnace.removeStackFromSlot(i));
				world.spawnEntity(item);
			}
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (null == stateIn || !stateIn.getValue(ON))
			return;
		
		EnumFacing facing = stateIn.getValue(FACING);
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
		
		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		worldIn.spawnParticle(EnumParticleTypes.CRIT_MAGIC, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		
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
