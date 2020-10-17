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
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherRepairerBlock extends BlockContainer implements ILoreTagged {
	
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final String ID = "aether_repairer";
	
	
	private static AetherRepairerBlock instance = null;
	public static AetherRepairerBlock instance() {
		if (instance == null)
			instance = new AetherRepairerBlock();
		
		return instance;
	}
	
	public static void init() {
		GameRegistry.registerTileEntity(AetherRepairerBlockEntity.class, "aether_repairer_te");
	}
	
	public AetherRepairerBlock() {
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
		return new BlockStateContainer(this, ON);
	}
	
	private static boolean onFromMeta(int meta) {
		return (meta & 1) == 1;
	}
	
	private static int metaFromOn(boolean on) {
		return (on ? 1 : 0);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(ON, onFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return metaFromOn(state.getValue(ON));
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
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherRepairerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherRepairerBlockEntity();
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack) {
		return this.getDefaultState()
				.withProperty(ON, false);
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
		if (ent == null || !(ent instanceof AetherRepairerBlockEntity))
			return;
		
		AetherRepairerBlockEntity furnace = (AetherRepairerBlockEntity) ent;
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
		
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 1.2D;
		double d2 = (double)pos.getZ() + 0.5D;
		
		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		worldIn.spawnParticle(EnumParticleTypes.CRIT_MAGIC, d0, d1, d2, (rand.nextFloat() - .5) * .2, .75, (rand.nextFloat() - .5) * .2, new int[0]);
		
		if (rand.nextFloat() < .1f) {
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, 1.0F, 0.25F, false);
			worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_LADDER_STEP, SoundCategory.BLOCKS, 0.1F, 0.25F, false);
		}
	}
	
	@Override
	public String getLoreKey() {
		return "aether_repairer";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Repairing";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("These blocks user aether to repair items and equipment.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("These blocks user aether to repair items and equipment.", "The amount of aether it takes to repair each point of damage depends on the material, type of tool, or form of equipment.");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	public static class AetherRepairerBlockEntity extends NativeAetherTickingTileEntity implements ISidedInventory {
		
		private static int GetAetherCost(ItemStack stack) {
			float base;
			float materialMod;
			float enchantMod;
			
			if (stack.getItem() instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) stack.getItem();
				switch (armor.getEquipmentSlot()) {
				case FEET:
				case HEAD:
					base = 20f;
					break;
				case LEGS:
					base = 30f;
					break;
				case CHEST:
				case MAINHAND:
				case OFFHAND:
				default:
					base = 40f;
					break;
				}
				
				switch (armor.getArmorMaterial()) {
				case LEATHER:
					materialMod = .65f;
					break;
				case CHAIN:
				case IRON:
					materialMod = 1f;
					break;
				case GOLD:
					materialMod = .8f;
					break;
				case DIAMOND:
				default:
					materialMod = 1.5f;
					break;
				}
			} else if (stack.getItem() instanceof ItemSword) {
				ItemSword sword = (ItemSword) stack.getItem();
				
				base = 25;
				
				ToolMaterial material;
				try {
					material = ToolMaterial.valueOf(sword.getToolMaterialName().toUpperCase());
				} catch (Exception e) {
					material = ToolMaterial.DIAMOND;
				}
				
				switch (material) {
				case WOOD:
					materialMod = .25f;
					break;
				case STONE:
					materialMod = .6f;
					break;
				case IRON:
					materialMod = 1f;
					break;
				case GOLD:
					materialMod = 1.4f;
					break;
				case DIAMOND:
				default:
					materialMod = 3f;
					break;
				}
			} else if (stack.getItem() instanceof ItemTool) {
				ItemTool tool = (ItemTool) stack.getItem();
				base = 20f;
				
				switch (tool.getToolMaterial()) {
				case WOOD:
					materialMod = .25f;
					break;
				case STONE:
					materialMod = .6f;
					break;
				case IRON:
					materialMod = 1f;
					break;
				case GOLD:
					materialMod = 1.4f;
					break;
				case DIAMOND:
				default:
					materialMod = 3f;
					break;
				}
				
			} else {
				materialMod = 999f;
				base = 9999f;
			}
			
			if (stack.isItemEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTagList().tagCount());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}
		
		private boolean on;
		private boolean aetherTick;
		
		private ItemStack stack;
		
		public AetherRepairerBlockEntity(int aether, int maxAether) {
			super(aether, maxAether);
			
			this.setAutoSync(5);
			this.handler.configureInOut(true, false);
		}
		
		public AetherRepairerBlockEntity() {
			this(0, 500);
		}
		
		public ItemStack getItem() {
			return stack;
		}
		
		public void setItem(ItemStack stack) {
			this.stack = stack;
			forceUpdate();
		}
		
		private static final String NBT_ITEM = "item";
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag = stack.writeToNBT(tag);
				nbt.setTag(NBT_ITEM, tag);
			}
			
			return nbt;
		}
		
		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			
			if (nbt == null)
				return;
				
			if (!nbt.hasKey(NBT_ITEM, NBT.TAG_COMPOUND)) {
				stack = null;
			} else {
				NBTTagCompound tag = nbt.getCompoundTag(NBT_ITEM);
				stack = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		
		private void forceUpdate() {
			worldObj.notifyBlockUpdate(pos, this.worldObj.getBlockState(pos), this.worldObj.getBlockState(pos), 3);
			markDirty();
		}

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			if (index > 0)
				return null;
			return this.stack;
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			if (index > 0)
				return null;
			ItemStack ret = this.stack.splitStack(count);
			if (this.stack.stackSize == 0)
				this.stack = null;
			this.forceUpdate();
			return ret;
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			if (index > 0)
				return null;
			ItemStack ret = this.stack;
			this.stack = null;
			forceUpdate();
			return ret;
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			if (index > 0)
				return;
			this.stack = stack;
			forceUpdate();
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {
			;
		}

		@Override
		public void closeInventory(EntityPlayer player) {
			;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			if (index != 0) {
				return false;
			}
			
			if (stack == null) {
				return true;
			}
			
			if (!stack.isItemStackDamageable()) {
				return false;
			}
			
			// We specifically want weapons, tools, or armor
			return stack.getItem() instanceof ItemSword
					|| stack.getItem() instanceof ItemArmor
					|| stack.getItem() instanceof ItemTool;
			
		}

		@Override
		public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
			return !(oldState.getBlock().equals(newState.getBlock()));
		}
		
		@Override
		public int getField(int id) {
			if (id == 0) {
				return this.handler.getAether(null);
			}
			return 0;
		}

		@Override
		public void setField(int id, int value) {
			if (id == 0) {
				this.handler.setAether(value);
			}
		}

		@Override
		public int getFieldCount() {
			return 1;
		}
		
		@Override
		public void onAetherFlowTick(int diff, boolean added, boolean taken) {
			super.onAetherFlowTick(diff, added, taken);
			aetherTick = !this.heldItemFull();
		}
		
		@Override
		public void clear() {
			this.stack = null;
			forceUpdate();
		}

		@Override
		public String getName() {
			return "Aether Repairer";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public int[] getSlotsForFace(EnumFacing side) {
			return new int[] {0};
		}

		@Override
		public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
			if (index != 0 || direction == EnumFacing.DOWN || !this.isItemValidForSlot(0, itemStackIn))
				return false;
			
			return stack == null;
		}

		@Override
		public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
			return index == 0 && direction == EnumFacing.DOWN && stack != null && heldItemFull();
		}
		
		public boolean heldItemFull() {
			return stack == null || stack.getItemDamage() == 0;
		}
		
		@Override
		public void update() {
			// If we have an item, try to repair it
			if (!worldObj.isRemote && this.ticksExisted % 20 == 0) {
				if (stack != null && this.isItemValidForSlot(0, stack) && !this.heldItemFull()) {
					final int aetherPer = GetAetherCost(stack);
					if (handler.getAether(null) >= aetherPer) {
						stack.setItemDamage(stack.getItemDamage() - 1);
						handler.drawAether(null, aetherPer);
						aetherTick = true;
					}
				}
				
				if (aetherTick != on) {
					worldObj.setBlockState(pos, instance().getDefaultState().withProperty(ON, aetherTick));
				}
				
				on = aetherTick;
				aetherTick = false;
			}
				
			super.update();
		}
		
		@Override
		public void setWorldObj(World world) {
			super.setWorldObj(world);
			
			if (!world.isRemote) {
				this.handler.setAutoFill(true);
			}
		}

	}
}
