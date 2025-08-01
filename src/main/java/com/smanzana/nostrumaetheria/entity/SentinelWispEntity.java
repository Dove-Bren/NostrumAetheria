package com.smanzana.nostrumaetheria.entity;

import javax.annotation.Nullable;

import com.smanzana.nostrumaetheria.blocks.WispBlock;
import com.smanzana.nostrummagica.entity.WispEntity;
import com.smanzana.nostrummagica.item.equipment.SpellScroll;
import com.smanzana.nostrummagica.spell.EMagicElement;
import com.smanzana.nostrummagica.spell.Spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SentinelWispEntity extends WispEntity {

	public SentinelWispEntity(EntityType<? extends SentinelWispEntity> type, Level worldIn) {
		super(type, worldIn);
	}
	
	public SentinelWispEntity(EntityType<? extends SentinelWispEntity> type, Level worldIn, BlockPos homePos) {
		this(type, worldIn);
		this.restrictTo(homePos, (int) MAX_WISP_DISTANCE_SQ);
		this.setHome(homePos);
	}
	
	public static final AttributeSupplier.Builder BuildSentinelAttributes(){
		return WispEntity.BuildAttributes();
	}
	
	@Override
	protected Spell getSpellToUse() {
		@Nullable Spell spell = null;
		ItemStack scroll = ItemStack.EMPTY;
		BlockPos homePos = this.getHome();
		if (homePos != null) {
			scroll = WispBlock.getScroll(level, homePos); // ENCAPSULATION LEAK
		}
		
		
		if (!scroll.isEmpty()) {
			spell = SpellScroll.GetSpell(scroll);
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
