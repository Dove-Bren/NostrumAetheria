package com.smanzana.nostrumaetheria.entity;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.blocks.WispBlock;
import com.smanzana.nostrummagica.entity.EntityWisp;
import com.smanzana.nostrummagica.items.SpellScroll;
import com.smanzana.nostrummagica.spells.EMagicElement;
import com.smanzana.nostrummagica.spells.Spell;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SentinelWispEntity extends EntityWisp {

	public SentinelWispEntity(EntityType<? extends SentinelWispEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	public SentinelWispEntity(EntityType<? extends SentinelWispEntity> type, World worldIn, BlockPos homePos) {
		this(type, worldIn);
		this.setHomePosAndDistance(homePos, (int) MAX_WISP_DISTANCE_SQ);
		this.setHome(homePos);
	}
	
	@Override
	protected Spell getSpellToUse() {
		@Nullable Spell spell = null;
		ItemStack scroll = ItemStack.EMPTY;
		BlockPos homePos = this.getHome();
		if (homePos != null) {
			scroll = WispBlock.getScroll(world, homePos); // ENCAPSULATION LEAK
		}
		
		
		if (!scroll.isEmpty()) {
			spell = SpellScroll.getSpell(scroll);
		}
		
		if (spell == null) {
			spell = super.getSpellToUse();
		} else {
			this.setElement(spell.getPrimaryElement());
		}
		
		return spell;
	}
	
	@Override
	public boolean canEnchant(Entity entity, EMagicElement element, int power) {
		// Only allow buffing with same element
		return element == this.getElement();
	}
}
