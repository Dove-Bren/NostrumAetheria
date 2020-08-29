package com.smanzana.nostrumaetheria.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.blocks.AetherTickingTileEntity;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrummagica.items.ReagentItem;
import com.smanzana.nostrummagica.items.ReagentItem.ReagentType;
import com.smanzana.nostrummagica.utils.Inventories;

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
import net.minecraft.inventory.IInventory;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AetherFurnaceBlock extends BlockContainer {
	
	public static enum Type implements IStringSerializable {
		SMALL(1),
		MEDIUM(2),
		LARGE(3);
		
		private final int multiplier;
		
		private Type(int multiplier) {
			this.multiplier = multiplier;
		}
		
		@Override
		public String getName() {
			return this.name().toLowerCase();
		}
		
		@Override
		public String toString() {
			return this.getName();
		}
		
		public int getMultiplier() {
			return this.multiplier;
		}
	}
	
	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final String ID = "aether_furnace_block";
	
	
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
		this.setCreativeTab(NostrumAetheria.creativeTab);
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
		
		return false;
	}
	
	public static class AetherFurnaceBlockEntity extends AetherTickingTileEntity implements IInventory {

		private static final String NBT_INVENTORY = "inventory";
		private static final String NBT_TYPE = "type";
		private static final String NBT_PROGRESS = "progress";
		
		private final static float REAGENT_PER_TICK_BASE = 1f / (20f * 20f); // 1 reagent per 20 seconds
		private final static int AETHER_PER_TICK_BASE = 1;
		
		private Type type;
		private ItemStack[] slots;
		private float progress;
		private boolean burning;
		
		public AetherFurnaceBlockEntity() {
			this(Type.SMALL);
		}
		
		public AetherFurnaceBlockEntity(Type type) {
			super(0, 500);
			this.handler.configureInOut(false, true);
			this.type = type;
			
			this.initInventory();
		}
		
		public float getBurnProgress() {
			return progress;
		}
		
		public Type getType() {
			return this.type;
		}
		
		protected static float ReagentPerTick(Type type) {
			return REAGENT_PER_TICK_BASE / type.multiplier; // get smaller with bigger furnaces, meaning reagents burn longer
		}
		
		protected static int AetherPerTick(Type type) {
			return AETHER_PER_TICK_BASE; // We burn longer instead of releasing aether faster
		}
		
		/**
		 * Checks whether this furnace's slots are filled with unique reagents that can be consumed.
		 */
		public boolean allReagentsValid(ReagentType type, boolean allowEmpty) {
			boolean[] seen = new boolean[ReagentType.values().length];
			if (type != null) {
				seen[type.ordinal()] = true;
			}
			for (int i = 0; i < getSizeInventory(); i++) {
				@Nullable ItemStack stack = slots[i];
				if (stack == null) {
					if (!allowEmpty) {
						return false;
					}
					
					continue;
				}
				
				ReagentType reagentType = ReagentItem.findType(stack);
				if (reagentType == null || seen[reagentType.ordinal()]) {
					return false;
				}
				
				seen[reagentType.ordinal()] = true;
			}
			
			return true;
		}
		
		protected void consumeReagentStack() {
			for (int i = 0; i < getSizeInventory(); i++) {
				this.decrStackSize(i, 1);
			}
		}
		
		/**
		 * Attempts to consume reagents from the inventory (or eat up progress). Returns true if
		 * there is fuel that was consumed and the furnace is still powered.
		 * @return
		 */
		protected boolean consumeTick() {
			if (progress > 0) {
				progress = Math.max(0f, progress - ReagentPerTick(type));
				this.markDirty();
				return true;
			} else if (allReagentsValid(null, false)) {
				consumeReagentStack();
				progress = 1f;
				this.markDirty();
				return true;
			}
			
			return false;
		}

		@Override
		public void update() {
			if (!worldObj.isRemote) {
				this.handler.pushAether(500);
				if (handler.getAether(null) < handler.getMaxAether(null) && consumeTick()) {
					this.handler.addAether(null, AetherPerTick(this.type), true); // 'force' to disable having aether added by others but force ourselves.
					
					if (!burning) {
						worldObj.setBlockState(pos, instance().getDefaultState().withProperty(TYPE, this.type).withProperty(ON, true));
						burning = true;
					}
				} else {
					if (burning) {
						worldObj.setBlockState(pos, instance().getDefaultState().withProperty(TYPE, this.type).withProperty(ON, false));
						burning = false;
					}
				}
			}
			super.update();
		}
		
		@Override
		public int getSizeInventory() {
			return AetherFurnaceBlock.getFurnaceSlotsForType(this.type);
		}
		
		private void initInventory() {
			slots = new ItemStack[this.getSizeInventory()];
		}
		
		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			nbt = super.writeToNBT(nbt);
			
			nbt.setString(NBT_TYPE, type.name());
			nbt.setTag(NBT_INVENTORY, Inventories.serializeInventory(this));
			nbt.setFloat(NBT_PROGRESS, progress);
			
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
			initInventory();
			
			Inventories.deserializeInventory(this, nbt.getTag(NBT_INVENTORY));
			this.progress = nbt.getFloat(NBT_PROGRESS);
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
			
			ItemStack inSlot = this.getStackInSlot(index);
			if (inSlot == null) {
				if (!allReagentsValid(ReagentItem.findType(stack), true)) {
					return false;
				}
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
				return progressToInt(progress);
			}
			return 0;
		}

		@Override
		public void setField(int id, int value) {
			if (id == 0) {
				progress = intToProgress(value);
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
