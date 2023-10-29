package com.smanzana.nostrumaetheria.blocks;

import java.util.List;
import java.util.Random;

import com.smanzana.nostrumaetheria.NostrumAetheria;
import com.smanzana.nostrumaetheria.api.proxy.APIProxy;
import com.smanzana.nostrumaetheria.api.recipes.IAetherUnravelerRecipe;
import com.smanzana.nostrumaetheria.gui.NostrumAetheriaGui;
import com.smanzana.nostrumaetheria.tiles.AetherUnravelerBlockEntity;
import com.smanzana.nostrummagica.client.gui.infoscreen.InfoScreenTabs;
import com.smanzana.nostrummagica.items.SpellRune;
import com.smanzana.nostrummagica.items.SpellScroll;
import com.smanzana.nostrummagica.loretag.ILoreTagged;
import com.smanzana.nostrummagica.loretag.Lore;
import com.smanzana.nostrummagica.spells.Spell;
import com.smanzana.nostrummagica.spells.Spell.SpellPart;

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

public class AetherUnravelerBlock extends BlockContainer implements ILoreTagged {
	
	public static final PropertyBool ON = PropertyBool.create("on");
	public static final String ID = "aether_unraveler";
	
	
	private static AetherUnravelerBlock instance = null;
	public static AetherUnravelerBlock instance() {
		if (instance == null)
			instance = new AetherUnravelerBlock();
		
		return instance;
	}
	
	public static void initDefaultRecipes() {
		APIProxy.addUnravelerRecipe(new ScrollUnravelerRecipe());
	}
	
	public AetherUnravelerBlock() {
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
			playerIn.openGui(NostrumAetheria.instance, NostrumAetheriaGui.aetherUnravelerID, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new AetherUnravelerBlockEntity();
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
		if (ent == null || !(ent instanceof AetherUnravelerBlockEntity))
			return;
		
		AetherUnravelerBlockEntity furnace = (AetherUnravelerBlockEntity) ent;
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
		return "aether_unraveler";
	}

	@Override
	public String getLoreDisplayName() {
		return "Aether Unraveling";
	}

	@Override
	public Lore getBasicLore() {
		return new Lore().add("This block uses aether to break down magical items into their base components.");
	}

	@Override
	public Lore getDeepLore() {
		return new Lore().add("This block uses aether to break down magical items into their base components.", "This allows it to take spell scrolls and return some of the runes that were used to craft it!");
	}

	@Override
	public InfoScreenTabs getTab() {
		return InfoScreenTabs.INFO_BLOCKS;
	}
	
	protected static class ScrollUnravelerRecipe implements IAetherUnravelerRecipe {

		private static final int AETHER_COST = 2000;
		private static final int DURATION = 20 * 100;
		
		@Override
		public boolean matches(ItemStack stack) {
			return stack.getItem() instanceof SpellScroll;
		}

		@Override
		public int getAetherCost(ItemStack stack) {
			return AETHER_COST;
		}
		
		@Override
		public int getDuration(ItemStack stack) {
			return DURATION;
		}

		@Override
		public NonNullList<ItemStack> unravel(ItemStack stack) {
			NonNullList<ItemStack> ret = NonNullList.create();
			
			Spell spell = SpellScroll.getSpell(stack);
			if (spell == null) {
				NostrumAetheria.logger.error("Tried to process spell scroll in unraveler but found no spell");
				return ret;
			}
			
			List<SpellPart> parts = spell.getSpellParts();
			NonNullList<ItemStack> runes = NonNullList.create();
			for (SpellPart part : parts) {
				runes.clear();
				runes = SpellRune.decomposeRune(SpellRune.getRune(part, 1), runes);
				
//				if (part.isTrigger()) {
//					SpellTrigger trigger = part.getTrigger();
//					runes = new ItemStack[] {SpellRune.getRune(trigger)};
//				} else {
//					SpellShape shape = part.getShape();
//					EMagicElement elem = part.getElement();
//					int elemCount = part.getElementCount();
//					EAlteration alt = part.getAlteration();
//					
//					// TIER 3 element is 4 runes
//					elemCount = (int) Math.pow(2, elemCount-1);
//					
//					runes = new ItemStack[1 + elemCount + (alt == null ? 0 : 1)];
//					runes[0] = SpellRune.getRune(shape);
//					if (alt != null) {
//						runes[1] = SpellRune.getRune(alt);
//					}
//					
//					
//					for (; elemCount > 0; elemCount--) {
//						runes[1 + (alt == null ? 0 : 1) + (elemCount - 1)]
//								= SpellRune.getRune(elem, 1);
//					}
//				}
				
				for (ItemStack rune : runes) {
					ret.add(rune);
				}
			}
			
			return ret;
		}
		
	}
}
