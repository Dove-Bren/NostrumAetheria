package com.smanzana.nostrumaetheria.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class AetherBoilerBlock extends BlockContainer implements ILoreTagged {
	
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final String ID = "aether_boiler_block";
	
	
	private static AetherBoilerBlock instance = null;
	public static AetherBoilerBlock instance() {
		if (instance == null)
			instance = new AetherBoilerBlock();
		
		return instance;
	}
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherBoilerBlockEntity.class, "aether_boiler_block_te");
	}
	
	public AetherBoilerBlock() {
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
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(ON, onFromMeta(meta))
				.withProperty(FACING, facingFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return metaFromOn(state.getValue(ON)) | metaFromFacing(state.getValue(FACING));
	}
	
	public boolean getFurnaceOn(IBlockState state) {
		return state.getValue(ON);
	}
	
	public EnumFacing getFacing(IBlockState state) {
		return state.getValue(FACING);
	}
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherBoilerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		
		return false;
	}
	
	public static class AetherBoilerBlockEntity extends AetherFurnaceGenericTileEntity {
		
		public AetherBoilerBlockEntity() {
			super(1, 0, 500);
		}
		
		protected @Nullable TileEntityFurnace getNearbyFurnace() {
			TileEntity te = worldObj.getTileEntity(pos.up());
			if (te != null && te instanceof TileEntityFurnace) {
				return (TileEntityFurnace) te;
			}
			return null;
		}
		
		protected void fuelNearbyFurnace() {
			TileEntityFurnace furnace = getNearbyFurnace();
			if (furnace != null) {
				ReflectionHelper.setPrivateValue(TileEntityFurnace.class, furnace, 20, "furnaceBurnTime", "field_145956_a");
				BlockFurnace.setState(true, worldObj, pos.up());
			}
		}

		@Override
		public String getName() {
			return "Aether Boiler Inventory";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}
		
		@Override
		public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
			return !(oldState.getBlock().equals(newState.getBlock()));
		}

		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return (facing == EnumFacing.DOWN && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		}
		
		private IItemHandler handlerProxy = null;

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				if (facing == EnumFacing.DOWN) {
					
					// Proxy up to a furnace that's above us, if there is one
					if (handlerProxy == null) {
						handlerProxy = new IItemHandler() {

							@Override
							public int getSlots() {
								return 1;
							}

							@Override
							public ItemStack getStackInSlot(int slot) {
								TileEntityFurnace furnace = getNearbyFurnace();
								if (furnace != null) {
									return furnace.getStackInSlot(2);
								}
								return null;
							}

							@Override
							public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
								return stack;
							}

							@Override
							public ItemStack extractItem(int slot, int amount, boolean simulate) {
								TileEntityFurnace furnace = getNearbyFurnace();
								if (furnace != null) {
									if (simulate) {
										return furnace.getStackInSlot(2);
									} else {
										return furnace.removeStackFromSlot(2);
									}
								}
								return null;
							}
							
						};
					}
					return (T) handlerProxy;
				}
			}
			return super.getCapability(capability, facing);
		}

		@Override
		protected float getAetherMultiplier() {
			// Inverting duration multiplier because we don't want it to actually increase yield.
			// So this is 75% efficiency overall, BUT we power a furnace above us.
			return .75f * (1 / getDurationMultiplier());
		}

		@Override
		protected float getDurationMultiplier() {
			return 20f/5f; // default 1 reagent is 5 seconds, but we want 20 seconds of burn
		}

		@Override
		protected void onBurningChange(boolean newBurning) {
			IBlockState state = worldObj.getBlockState(pos);
			worldObj.setBlockState(pos, instance().getDefaultState().withProperty(ON, newBurning).withProperty(FACING, state.getValue(FACING)));
		}
		
		@Override
		protected void generateAether() {
			super.generateAether();
			this.fuelNearbyFurnace();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherBoilerBlockEntity();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return this.getDefaultState()
				.withProperty(ON, false)
				.withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		super.getSubBlocks(itemIn, tab, list);
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
		if (ent == null || !(ent instanceof AetherBoilerBlockEntity))
			return;
		
		AetherBoilerBlockEntity furnace = (AetherBoilerBlockEntity) ent;
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
		
		if (rand.nextBoolean()) {
			EnumFacing facing = stateIn.getValue(FACING);
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
			
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
			worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
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
