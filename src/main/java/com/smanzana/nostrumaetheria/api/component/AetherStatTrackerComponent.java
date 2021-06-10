package com.smanzana.nostrumaetheria.api.component;

import com.smanzana.nostrumaetheria.api.aether.stats.AetherStatInstance;
import com.smanzana.nostrumaetheria.api.aether.stats.IAetherStatTracker;

/**
 * Simple stat-tracking implementation of IAetherStatTracker. Used automatically by default in AetherTileEntities.
 * This class is intended to be hooked up to the onAetherFlowTick callback on an IAetherComponentListener.
 * 
 * 
 * @author Skyler
 *
 */
public class AetherStatTrackerComponent implements IAetherStatTracker {
	
	private final int[] totalBuffer;
	private final int[] inputBuffer;
	private final int[] outputBuffer;
	private int index; // indexes the buffers. Points to the most-recent entry.
	private long indexTick; // The tick that `index` represents
	
	public AetherStatTrackerComponent() {
		// + 2 for a leading and trailing interval for smoothing
		final int length = (int) Math.ceil((AetherStatInstance.TOTAL_INTERVALS + 2) * AetherStatInstance.TICKS_PER_INTERVAL);
		totalBuffer = new int[length];
		inputBuffer = new int[length];
		outputBuffer = new int[length];
		index = 0;
		indexTick = -1;
	}
	
	/**
	 * Increments current index, wrapping around if it goes past the edge of the buffer
	 */
	protected void incr() {
		index++;
		if (index >= totalBuffer.length) {
			index = 0;
		}
	}
	
	/**
	 * Calculates the index into the arrays that corresponds to the provided tick.
	 * @param tick
	 * @return -1 if the tick isn't represented anymore. Otherwise, the real array index.
	 */
	protected int indexFor(long tick) {
		if (tick > indexTick || indexTick - tick >= totalBuffer.length) {
			return -1;
		}
		
		// [50] [51] [52] [53] [49]
		//                 ^^
		//
		//  51: 2 => 3 - 2 = 1
		//  49: 4 => 3 - 4 = -1
		
		final int relative = (int) (indexTick - tick); // positive but represents how backwards to go
		final int adjusted = (index - relative);
		
		// If negative, wrap
		return (adjusted < 0 ? adjusted + totalBuffer.length : adjusted);
	}
	
	/**
	 * Update our indexes to point to the provided tick, filling in 0's for any ticks that weren't reported on (and the new current tick)
	 * @param tick
	 */
	protected void forwardTo(long tick) {
		int diff = (int) (tick - this.indexTick);
		if (diff <= 0) {
			return;
		}
		
		for (; diff > 0; diff++) {
			incr();
			totalBuffer[index] = 0;
			inputBuffer[index] = 0;
			outputBuffer[index] = 0;
		}
	}

	@Override
	public void reportTotal(long tick, int totalAether) {
		forwardTo(tick);
		final int idx = indexFor(tick);
		if (idx != -1) {
			totalBuffer[idx] = totalAether;
		}
	}

	@Override
	public void reportInput(long tick, int incomingAether) {
		forwardTo(tick);
		final int idx = indexFor(tick);
		if (idx != -1) {
			inputBuffer[idx] = incomingAether;
		}
	}

	@Override
	public void reportOutput(long tick, int outgoingAether) {
		forwardTo(tick);
		final int idx = indexFor(tick);
		if (idx != -1) {
			outputBuffer[idx] = outgoingAether;
		}
	}

	@Override
	public AetherStatInstance getInstance(long tick) {
		forwardTo(tick); // Fill any new 0's that are needed
		
		int[] totals = new int[AetherStatInstance.TOTAL_INTERVALS];
		int[] inputs = new int[AetherStatInstance.TOTAL_INTERVALS];
		int[] outputs = new int[AetherStatInstance.TOTAL_INTERVALS];
		
		// Smoothing and consistency: Group into intervals based off of 0, not current index. In other words,
		// if we're on tick 73, make tick [0-19], [20-49], etc. intervals instead of ..., [34-53], [54-73] intervals. 
		// Smoothing: Don't report the current interval until it's completed
		// Smoothing: don't report the interval that's falling off the edge of the buffer
		
		final long startTick = (indexTick - (totalBuffer.length - 1)); // tick that is at [index+1]
		// Jump forward to the start of the first whole interval. Figure how many ticks to ignore by figuring how many ticks OVER
		// a full interval we are and subtract that from the interval size.
		final int ignoredTicks = (int) (AetherStatInstance.TICKS_PER_INTERVAL - (startTick % AetherStatInstance.TICKS_PER_INTERVAL));
		
		for (int interval = 0; interval < AetherStatInstance.TOTAL_INTERVALS; interval++) {
			int total = 0;
			int input = 0;
			int output = 0;
			for (int t = 0; t < AetherStatInstance.TICKS_PER_INTERVAL; t++) {
				final int idx = (index + 1 + ignoredTicks + t + (interval * AetherStatInstance.TICKS_PER_INTERVAL)) % totalBuffer.length;
				total += this.totalBuffer[idx];
				input += this.inputBuffer[idx];
				output += this.outputBuffer[idx];
			}
			totals[interval] = total;
			inputs[interval] = input;
			outputs[interval] = output;
		}
		
		return new AetherStatInstance(totals, inputs, outputs);
	}
	
}
