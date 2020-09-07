package com.smanzana.nostrumaetheria.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherFurnaceBlock extends BlockContainer {
	
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
	
	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final String ID = "aether_furnace_block";
	
	public static final String UnlocalizedForType(Type type) {
		return ID + "_" + type.name().toLowerCase();
	}
	
	private static AetherFurnaceBlock instance = null;
	public static AetherFurnaceBlock instance() {
		if (instance == null)
			instance = new AetherFurnaceBlock();
		
		return instance;
	}
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherFurnaceBlockEntity.class, "aether_furnace_block_te");
//		GameRegistry.addShapedRecipe(new ItemStack(instance()),
//				"WPW", "WCW", "WWW",
//				'W', new ItemStack(Blocks.PLANKS, 1, OreDictionary.WILDCARD_VALUE),
//				'P', new ItemStack(Items.PAPER, 1, OreDictionary.WILDCARD_VALUE),
//				'C', NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1));
	}
	
	public AetherFurnaceBlock() {
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
		return new BlockStateContainer(this, TYPE, ON);
	}
	
	private Type typeFromMeta(int meta) {
		return Type.values()[meta % 3];
	}
	
	private int metaFromType(Type type) {
		return type.ordinal();
	}
	
	private boolean onFromMeta(int meta) {
		return ((meta >> 2) & 1) == 1;
	}
	
	private int metaFromOn(boolean on) {
		return (on ? 1 : 0) << 2;
	}
	
	private int metaFromState(Type type, boolean on) {
		return metaFromType(type) | metaFromOn(on);
	}
	
	protected static int getFurnaceSlotsForType(Type type) {
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
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(TYPE, typeFromMeta(meta))
				.withProperty(ON, onFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return metaFromState(state.getValue(TYPE), state.getValue(ON));
	}
	
	public Type getType(IBlockState state) {
		return state.getValue(TYPE);
	}
	
	public boolean getFurnaceOn(IBlockState state) {
		return state.getValue(ON);
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			//worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherFurnaceID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		
		return true;
	}
	
	public static class AetherFurnaceBlockEntity extends AetherFurnaceGenericTileEntity {

		private static final String NBT_TYPE = "type";
		
		private Type type;
		
		public AetherFurnaceBlockEntity() {
			this(Type.SMALL);
		}
		
		public AetherFurnaceBlockEntity(Type type) {
			super(getFurnaceSlotsForType(type), 0, 500);
			this.type = type;
			
		}
		
		public Type getType() {
			return this.type;
		}
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			nbt.setString(NBT_TYPE, type.name());
			
			return nbt;
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			
			try {
				this.type = Type.valueOf(nbt.getString(NBT_TYPE));
			} catch (Exception e) {
				this.type = Type.SMALL;
			}
		}
		
		@Override
		public String getName() {
			return "Aether Furnace Inventory";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}
		
		@Override
		public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
			return !(oldState.getBlock().equals(newState.getBlock()) && instance().getType(oldState) == instance().getType(newState));
		}

		@Override
		protected float getAetherMultiplier() {
			return this.type.getAetherMultiplier();
		}

		@Override
		protected float getDurationMultiplier() {
			return this.type.getDurationMultiplier();
		}

		@Override
		protected void onBurningChange(boolean newBurning) {
			worldObj.setBlockState(pos, instance().getDefaultState().withProperty(TYPE, this.type).withProperty(ON, newBurning));
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherFurnaceBlockEntity(typeFromMeta(meta));
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return this.getDefaultState()
				.withProperty(TYPE, typeFromMeta(stack.getMetadata()))
				.withProperty(ON, false);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (Type type : Type.values()) {
			list.add(new ItemStack(this, 1, metaFromType(type)));
		}
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		destroy(world, pos, state);
		super.breakBlock(world, pos, state);
	}
	
	private void destroy(World world, BlockPos pos, IBlockState state) {
		TileEntity ent = world.getTileEntity(pos);
		if (ent == null || !(ent instanceof AetherFurnaceBlockEntity))
			return;
		
		AetherFurnaceBlockEntity furnace = (AetherFurnaceBlockEntity) ent;
		for (int i = 0; i < furnace.getSizeInventory(); i++) {
			if (furnace.getStackInSlot(i) != null) {
				EntityItem item = new EntityItem(
						world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
						furnace.removeStackFromSlot(i));
				world.spawnEntityInWorld(item);
			}
		}
		
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (null == stateIn || !stateIn.getValue(ON))
			return;
		
		final double d0 = (double)pos.getX() + 0.5D;
		final double d1 = (double)pos.getY() + 0.2D;
		final double d2 = (double)pos.getZ() + 0.5D;
		
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
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
			
			worldIn.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
			worldIn.spawnParticle(EnumParticleTypes.CRIT_MAGIC, x, y + .5, z, 0.0D, 0.0D, 0.0D, new int[0]);
		}
		
		if (rand.nextFloat() < .1f) {
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1F, false);
		}
		if (rand.nextFloat() < .1f) {
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.05F, 2F, false);
		}
	}
	
	public static final IItemPropertyGetter SIZE_GETTER = new IItemPropertyGetter() {
		@SideOnly(Side.CLIENT)
		public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
			Type type = AetherFurnaceBlock.instance.typeFromMeta(stack.getMetadata());
			return (float) type.ordinal() / (float) Type.values().length;
		}
	};
	
	public static final IItemPropertyGetter ON_GETTER = new IItemPropertyGetter() {
		@SideOnly(Side.CLIENT)
		public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
			return AetherFurnaceBlock.instance.onFromMeta(stack.getMetadata()) ? 1.0F : 0.0F;
		}
	};
}
