package com.smanzana.nostrumaetheria.api.aether.stats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * Collection of information about aether consumer/producer (user).
 * This is the 'rasterized' finalized version for display, etc.
 * Aether users should use an AetherStatTracker to track info and produce stat instances as needed
 * @author Skyler
 *
 */
public class AetherStatInstance {

	public static final int TOTAL_INTERVALS = 60;
	public static final int TICKS_PER_INTERVAL = (60 * 20 /* 60 seconds */) / TOTAL_INTERVALS;
	
	public final int[] aetherTotalHistory;
	public final int[] aetherInHistory;
	public final int[] aetherOutHistory;
	
	public AetherStatInstance() {
		aetherTotalHistory = new int[TOTAL_INTERVALS]; // All 0's
		aetherInHistory = new int[TOTAL_INTERVALS]; // All 0's
		aetherOutHistory = new int[TOTAL_INTERVALS]; // All 0's
	}
	
	public AetherStatInstance(int ... history) {
		this(history, null, null);
	}
	
	public AetherStatInstance(int[] totalHistory, int[] inputHistory, int[] outputHistory) {
		this();
		assert(totalHistory == null || totalHistory.length == TOTAL_INTERVALS);
		assert(inputHistory == null || inputHistory.length == TOTAL_INTERVALS);
		assert(outputHistory == null || outputHistory.length == TOTAL_INTERVALS);
		
		
		if (totalHistory != null) {
			System.arraycopy(totalHistory, 0, this.aetherTotalHistory, 0, TOTAL_INTERVALS);
		}
		
		if (inputHistory != null) {
			System.arraycopy(inputHistory, 0, this.aetherInHistory, 0, TOTAL_INTERVALS);
		}
		
		if (totalHistory != null) {
			System.arraycopy(outputHistory, 0, this.aetherOutHistory, 0, TOTAL_INTERVALS);
		}
	}
	
	private static final String NBT_INTERVALS = "count";
	private static final String NBT_TOTAL_LIST = "total_list";
	private static final String NBT_INPUT_LIST = "input_list";
	private static final String NBT_OUTPUT_LIST = "output_list";
	
	public CompoundTag toNBT() {
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(NBT_INTERVALS, TOTAL_INTERVALS);
		ListTag list = new ListTag();
		for (int i = 0; i < TOTAL_INTERVALS; i++) {
			list.add(IntTag.valueOf(aetherTotalHistory[i]));
		}
		tag.put(NBT_TOTAL_LIST, list);
		
		list = new ListTag();
		for (int i = 0; i < TOTAL_INTERVALS; i++) {
			list.add(IntTag.valueOf(aetherInHistory[i]));
		}
		tag.put(NBT_INPUT_LIST, list);
		
		list = new ListTag();
		for (int i = 0; i < TOTAL_INTERVALS; i++) {
			list.add(IntTag.valueOf(aetherOutHistory[i]));
		}
		tag.put(NBT_OUTPUT_LIST, list);
		
		return tag;
	}
	
	public void readFromNBT(CompoundTag tag) {
		final int count = tag.getInt(NBT_INTERVALS);
		final ListTag totalList = tag.getList(NBT_TOTAL_LIST, NBT.TAG_INT);
		final ListTag inputList = tag.getList(NBT_INPUT_LIST, NBT.TAG_INT);
		final ListTag outputList = tag.getList(NBT_OUTPUT_LIST, NBT.TAG_INT);
		for (int i = 0; i < TOTAL_INTERVALS; i++) {
			final int total;
			final int input;
			final int output;
			if (i < count) {
				total = totalList.getInt(i);
				input = inputList.getInt(i);
				output = outputList.getInt(i);
			} else {
				total = input = output = 0;
			}
			
			aetherTotalHistory[i] = total;
			aetherInHistory[i] = input;
			aetherOutHistory[i] = output;
		}
	}
	
	public static AetherStatInstance combine(AetherStatInstance ... instances) {
		int[] totals = new int[TOTAL_INTERVALS];
		int[] inputs = new int[TOTAL_INTERVALS];
		int[] outputs = new int[TOTAL_INTERVALS];
		
		for (AetherStatInstance inst : instances) {
			for (int i = 0; i < TOTAL_INTERVALS; i++) {
				totals[i] += inst.aetherTotalHistory[i];
				inputs[i] += inst.aetherInHistory[i];
				outputs[i] += inst.aetherOutHistory[i];
			}
		}
		
		return new AetherStatInstance(totals, inputs, outputs);
	}
	
}
