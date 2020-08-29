package com.smanzana.nostrumaetheria.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrummagica.items.ReagentItem;
import com.smanzana.nostrummagica.utils.Inventories;

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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

public class AetherBoilerBlock extends BlockContainer {
	
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
//		GameRegistry.addShapedRecipe(new ItemStack(instance()),
//				"WPW", "WCW", "WWW",
//				'W', new ItemStack(Blocks.PLANKS, 1, OreDictionary.WILDCARD_VALUE),
//				'P', new ItemStack(Items.PAPER, 1, OreDictionary.WILDCARD_VALUE),
//				'C', NostrumResourceItem.getItem(ResourceType.CRYSTAL_LARGE, 1));
	}
	
	public AetherBoilerBlock() {
		super(Material.ROCK, MapColor.OBSIDIAN);
		this.setUnlocalizedName(ID);
		this.setHardness(3.0f);
		this.setResistance(10.0f);
		this.setCreativeTab(NostrumAetheria.creativeTab);
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
		}
		
		return false;
	}
	
	public static class AetherBoilerBlockEntity extends AetherTickingTileEntity implements IInventory {
		
		private static final String NBT_INVENTORY = "inventory";
		private static final String NBT_PROGRESS = "progress";
		private static final String NBT_AETHER_BUILDUP = "aether_buildup";
		
		private final static float REAGENT_PER_TICK = 1f / (30f * 20f); // 1 reagent per 20 seconds
		private final static float AETHER_PER_TICK = 100 * REAGENT_PER_TICK; // 100 aether per reagent
		
		private ItemStack[] slots = new ItemStack[1];
		private float progress;
		private float aetherBuildup;
		private boolean burning;
		
		public AetherBoilerBlockEntity() {
			super(0, 500);
			this.handler.configureInOut(false, true);
		}
		
		public float getBurnProgress() {
			return progress;
		}
		
		protected boolean consumeReagentStack() {
			if (this.getStackInSlot(0) != null) {
				this.decrStackSize(0, 1);
				return true;
			}
			return false;
		}
		
		/**
		 * Attempts to consume reagents from the inventory (or eat up progress). Returns true if
		 * there is fuel that was consumed and the furnace is still powered.
		 * @return
		 */
		protected boolean consumeTick() {
			if (progress > 0) {
				progress = Math.max(0f, progress - REAGENT_PER_TICK);
				this.markDirty();
				return true;
			} else if (consumeReagentStack()) {
				progress = 1f;
				this.markDirty();
				return true;
			}
			
			return false;
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
				ReflectionHelper.setPrivateValue(TileEntityFurnace.class, furnace, 20, "furnaceBurnTime");
				BlockFurnace.setState(true, worldObj, pos.up());
			}
		}

		@Override
		public void update() {
			if (!worldObj.isRemote) {
				this.handler.pushAether(500);
				if (handler.getAether(null) < handler.getMaxAether(null) && consumeTick()) {
					this.aetherBuildup += AETHER_PER_TICK;
					if (this.aetherBuildup >= 1) {
						int amt = (int) Math.floor(aetherBuildup);
						this.aetherBuildup -= amt;
						this.handler.addAether(null, amt, true); // 'force' to disable having aether added by others but force ourselves.
					}
					fuelNearbyFurnace();
					
					if (!burning) {
						IBlockState state = worldObj.getBlockState(pos);
						worldObj.setBlockState(pos, instance().getDefaultState().withProperty(ON, true).withProperty(FACING, state.getValue(FACING)));
						burning = true;
					}
				} else {
					if (burning) {
						IBlockState state = worldObj.getBlockState(pos);
						worldObj.setBlockState(pos, instance().getDefaultState().withProperty(ON, false).withProperty(FACING, state.getValue(FACING)));
						burning = false;
					}
				}
			}
			super.update();
		}
		
		@Override
		public int getSizeInventory() {
			return 1;
		}
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			nbt.setTag(NBT_INVENTORY, Inventories.serializeInventory(this));
			nbt.setFloat(NBT_PROGRESS, progress);
			nbt.setFloat(NBT_AETHER_BUILDUP, aetherBuildup);
			
			return nbt;
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			
			Inventories.deserializeInventory(this, nbt.getTag(NBT_INVENTORY));
			this.progress = nbt.getFloat(NBT_PROGRESS);
			this.aetherBuildup = nbt.getFloat(NBT_AETHER_BUILDUP);
		}
		
		@Override
		public ItemStack getStackInSlot(int index) {
			if (index < 0 || index >= getSizeInventory())
				return null;
			
			return slots[index];
		}
		
		@Override
		public ItemStack decrStackSize(int index, int count) {
			if (index < 0 || index >= getSizeInventory() || slots[index] == null)
				return null;
			
			ItemStack stack;
			if (slots[index].stackSize <= count) {
				stack = slots[index];
				slots[index] = null;
			} else {
				stack = slots[index].copy();
				stack.stackSize = count;
				slots[index].stackSize -= count;
			}
			
			this.markDirty();
			
			return stack;
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			if (index < 0 || index >= getSizeInventory())
				return null;
			
			ItemStack stack = slots[index];
			slots[index] = null;
			
			this.markDirty();
			return stack;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			if (!isItemValidForSlot(index, stack))
				return;
			
			slots[index] = stack;
			this.markDirty();
		}
		
		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {
		}

		@Override
		public void closeInventory(EntityPlayer player) {
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			if (index < 0 || index >= getSizeInventory())
				return false;
			
			if (stack != null && !(stack.getItem() instanceof ReagentItem)) {
				return false;
			}
			
			return true;
		}
		
		private static final int progressToInt(float progress) {
			return Math.round(progress * 10000);
		}
		
		private static final float intToProgress(int value) {
			return (float) value / 10000f;
		}

		@Override
		public int getField(int id) {
			if (id == 0) {
				return progressToInt(this.progress);
			}
			return 0;
		}

		@Override
		public void setField(int id, int value) {
			if (id == 0) {
				this.progress = intToProgress(value);
			}
		}

		@Override
		public int getFieldCount() {
			return 1;
		}

		@Override
		public void clear() {
			for (int i = 0; i < getSizeInventory(); i++) {
				removeStackFromSlot(i);
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

//		@Override
//		public int[] getSlotsForFace(EnumFacing side) {
//			if (side == EnumFacing.DOWN) {
//				// proxy up to furnace, if it's there
//				TileEntityFurnace furnace = getNearbyFurnace();
//				if (furnace != null) {
//					return furnace.getSlotsForFace(side);
//				} else {
//					return SLOTS_NONE;
//				}
//			}
//			return SLOTS_FUEL;
//		}
//
//		@Override
//		public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
//			return this.isItemValidForSlot(index, itemStackIn);
//		}
//
//		@Override
//		public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
//			if (direction == EnumFacing.DOWN) {
//				TileEntityFurnace furnace = getNearbyFurnace();
//				if (furnace != null) {
//					return furnace.canExtractItem(index, stack, direction);
//				}
//			}
//			return false;
//		}
		
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
}
