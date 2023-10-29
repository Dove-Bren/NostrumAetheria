package com.smanzana.nostrumaetheria.blocks;

import java.util.Random;

import javax.annotation.Nonnull;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.api.recipes.IAetherRepairerRecipe;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.recipes.RepairerRecipeManager;
import com.smanzana.nostrumaetheria.tiles.AetherRepairerBlockEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.items.SpellScroll;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
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

public class AetherRepairerBlock extends BlockContainer implements ILoreTagged {
	
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final String ID = "aether_repairer";
	
	
	private static AetherRepairerBlock instance = null;
	public static AetherRepairerBlock instance() {
		if (instance == null)
			instance = new AetherRepairerBlock();
		
		return instance;
	}
	
	public static void initDefaultRecipes() {
		RepairerRecipeManager.instance().addRecipe(new ArmorRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new WeaponRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new ToolRepairRecipe());
		RepairerRecipeManager.instance().addRecipe(new ScrollRepairRecipe());
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
	public BlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(ON, onFromMeta(meta));
	}
	
	@Override
	public int getMetaFromState(BlockState state) {
		return metaFromOn(state.getValue(ON));
	}
	
	public boolean getFurnaceOn(BlockState state) {
		return state.getValue(ON);
	}
	
	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherRepairerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherRepairerBlockEntity();
	}
	
	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState()
				.withProperty(ON, false);
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
		if (ent == null || !(ent instanceof AetherRepairerBlockEntity))
			return;
		
		AetherRepairerBlockEntity furnace = (AetherRepairerBlockEntity) ent;
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
	
	public static @Nonnull ItemStack RepairTool(ItemStack tool, int amt) {
		tool.setItemDamage(Math.max(0, tool.getItemDamage() - amt));
		return tool;
	}
	
	protected static class ArmorRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof ItemArmor && stack.isItemDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			final float materialMod;
			final float enchantMod;
			
			ItemArmor armor = (ItemArmor) stack.getItem();
			switch (armor.armorType) {
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
			
			if (stack.isItemEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTagList().tagCount());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class WeaponRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof ItemSword && stack.isItemDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			final float materialMod;
			final float enchantMod;
			
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
			
			if (stack.isItemEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTagList().tagCount());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class ToolRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof ItemTool && stack.isItemDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			final float base;
			float materialMod = 3f;
			final float enchantMod;
			
			ItemTool tool = (ItemTool) stack.getItem();
			base = 20f;
			
			try {
				Item.ToolMaterial material = Item.ToolMaterial.valueOf(tool.getToolMaterialName().toUpperCase());
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
			} catch (Exception e) {
				;
			}
			
			
			
			if (stack.isItemEnchanted()) {
				enchantMod = Math.min(1f, 1.2f * stack.getEnchantmentTagList().tagCount());
			} else {
				enchantMod = 1f;
			}
			
			return Math.round(base * enchantMod * materialMod);
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
	
	protected static class ScrollRepairRecipe implements IAetherRepairerRecipe {

		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SpellScroll && stack.isItemDamaged();
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return 150;
		}

		@Override
		public ItemStack repair(ItemStack stack) {
			return RepairTool(stack, 1);
		}
		
	}
}
