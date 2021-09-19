package com.smanzana.nostrumaetheria.api.event;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * Called before or after aether is drawn from an inventory
 * @author Skyler
 *
 */
public class LivingAetherDrawEvent extends LivingEvent {
	
	public static enum Phase {
		BEFORE_EARLY,
		BEFORE_LATE,
		AFTER,
	}
	
	/**
	 * Entity involved with the aether draw
	 */
	public final EntityLivingBase entity;
	
	/**
	 * Item that's doing the drawing
	 */
	public final @Nonnull ItemStack originalItem;
	
	/**
	 * Original amount requested to be drawn at event creation
	 */
	public final int desiredDrawAmt;
	
	/**
	 * Total amount of aether in the draw attempt. This includes any amount covered
	 * from previous events.
	 */
	public final int totalDrawAmt;
	
	/**
	 * Amount left to draw. Draw attempt passes if this ends up at <=0.
	 * Changing in the AFTER phase doesn't do anything.
	 */
	public int amtRemaining;
	
	public final Phase phase;
	
	public LivingAetherDrawEvent(Phase phase, EntityLivingBase entity, @Nonnull ItemStack originalItem, int origAmt, int drawAmt) {
		super(entity);
		this.phase = phase;
		this.entity = entity;
		this.originalItem = originalItem;
		this.totalDrawAmt = origAmt;
		this.desiredDrawAmt = this.amtRemaining = drawAmt;
		
		Validate.notNull(originalItem);
	}
	
	public EntityLivingBase getEntity() {
		return entity;
	}

	public ItemStack getOriginalItem() {
		return originalItem;
	}

	public int getDesiredDrawAmt() {
		return desiredDrawAmt;
	}

	public int getAmtRemaining() {
		return amtRemaining;
	}
	
	/**
	 * Contribute up to the amount provided.
	 * If less than the amount provided is needed, will only 'contribute' the amount remaining.
	 * Returns amount not consumed.
	 * @param maxCount
	 * @return
	 */
	public int contributeAmt(int maxCount) {
		if (amtRemaining >= maxCount) {
			amtRemaining -= maxCount;
			return 0;
		}
		
		maxCount -= amtRemaining;
		amtRemaining = 0;
		return maxCount;
	}
	
	public boolean isFinished() {
		return amtRemaining <= 0;
	}
	
	public Phase getDrawPhase() {
		return phase;
	}
	
}
