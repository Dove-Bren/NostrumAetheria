package com.smanzana.nostrumaetheria.api.aether.stats;

/**
 * Meant to represent in and out flow of aether in one unit of time.
 * For example, how much was given to or taken from a component in one tick.
 * @author Skyler
 *
 */
public final class AetherTickIOEntry {

	// Not final, as we're using a mempool and reusing objects.
	private int input;
	private int output;
	
	protected AetherTickIOEntry() {
		
	}
	
	protected void set(int input, int output) {
		this.input = input;
		this.output = output;
	}
	
	public final int getInput() {
		return this.input;
	}
	
	public final int getOutput() {
		return this.output;
	}
	
	public void release() {
		AetherTickIOEntry.release(this);
	}
	
	// Bring back (maybe pulling out of NostrumMAgica?) if this gets used!
	//protected static final MemoryPool<AetherTickIOEntry> Pool = new MemoryPool<>(AetherTickIOEntry::new);
	
	public static AetherTickIOEntry reserve(int input, int output) {
		AetherTickIOEntry inst = new AetherTickIOEntry();//Pool.claim();
		inst.set(input, output);
		return inst;
	}
	
	public static void release(AetherTickIOEntry entry) {
		//Pool.release(entry);
	}
}
