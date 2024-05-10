package com.smanzana.nostrumaetheria.client.render;

import java.util.OptionalDouble;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class AetheriaRenderTypes {

	public static final RenderType AETHER_FLAT;
	public static final RenderType AETHER_FLAT_TRIS;
	public static final RenderType RELAY_LINES;
	public static final RenderType INFUSER_ORB;
	public static final RenderType INFUSER_SPARK;
	public static final RenderType WISPBLOCK_BASE;
	public static final RenderType WISPBLOCK_PLATFORM;
	public static final RenderType WISPBLOCK_GEM;
	public static final RenderType WISPBLOCK_GEM_OUTLINE;
	
	private static final String Name(String suffix) {
		return "aetheriarender_" + suffix;
	}
	
	static {
		
		final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "field_228515_g_");
		final RenderState.CullState NO_CULL = new RenderState.CullState(false);
		final RenderState.DepthTestState DEPTH_EQUAL = new RenderState.DepthTestState("==", GL11.GL_EQUAL);
		final RenderState.DepthTestState NO_DEPTH = new RenderState.DepthTestState("none", GL11.GL_ALWAYS);
		final RenderState.LightmapState NO_LIGHTING = new RenderState.LightmapState(false);
	    final RenderState.LightmapState LIGHTMAP_ENABLED = new RenderState.LightmapState(true);
	    final RenderState.LineState LINE_2 = new RenderState.LineState(OptionalDouble.of(2));
	    final RenderState.LineState LINE_3 = new RenderState.LineState(OptionalDouble.of(3));
	    @SuppressWarnings("deprecation")
		final RenderState.TextureState BLOCK_SHEET = new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, false);
	    final RenderState.AlphaState HALF_ALPHA = new RenderState.AlphaState(.5f);
	    final RenderState.ShadeModelState SHADE_ENABLED = new RenderState.ShadeModelState(true);
		
		// Define render types
		RenderType.State glState;
				
		glState = RenderType.State.getBuilder()
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(NO_CULL)
				.depthTest(DEPTH_EQUAL)
				.lightmap(LIGHTMAP_ENABLED)
			.build(false);
		AETHER_FLAT = RenderType.makeType(Name("AetherFlat"), DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_QUADS, 32, glState);
		AETHER_FLAT_TRIS = RenderType.makeType(Name("AetherFlatTris"), DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_TRIANGLES, 32, glState);
		
		glState = RenderType.State.getBuilder()
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(NO_CULL)
				.depthTest(DEPTH_EQUAL)
				.lightmap(LIGHTMAP_ENABLED)
				.line(LINE_3)
			.build(false);
		RELAY_LINES = RenderType.makeType(Name("AetherRelayLines"), DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_LINES, 64, glState);
		
		glState = RenderType.State.getBuilder()
				.texture(BLOCK_SHEET)
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(NO_CULL)
				.depthTest(DEPTH_EQUAL) // used to turn off
				.lightmap(LIGHTMAP_ENABLED)
			.build(false);
		INFUSER_ORB = RenderType.makeType(Name("AetherInfuserOrb"), DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 64, glState);

		glState = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(TileEntityAetherInfuserRenderer.SPARK_TEX_LOC, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(NO_CULL)
				.depthTest(NO_DEPTH) // actually want this?
				.lightmap(LIGHTMAP_ENABLED)
			.build(false);
		INFUSER_SPARK = RenderType.makeType(Name("AetherInfuserSpark"), DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 64, glState);

		glState = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(TileEntityWispBlockRenderer.BASE_TEX_LOC, false, false))
				.lightmap(LIGHTMAP_ENABLED)
				.alpha(HALF_ALPHA)
				.shadeModel(SHADE_ENABLED)
			.build(true);
		WISPBLOCK_BASE = RenderType.makeType(Name("WispBlockBase"), DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 32, glState);

		glState = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(TileEntityWispBlockRenderer.PLATFORM_TEX_LOC, false, false))
				.lightmap(LIGHTMAP_ENABLED)
				.alpha(HALF_ALPHA)
				.shadeModel(SHADE_ENABLED)
			.build(true);
		WISPBLOCK_PLATFORM = RenderType.makeType(Name("WispBlockPlatform"), DefaultVertexFormats.BLOCK, GL11.GL_TRIANGLES, 32, glState);

		glState = RenderType.State.getBuilder()
				.texture(new RenderState.TextureState(TileEntityWispBlockRenderer.GEM_TEX_LOC, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(LIGHTMAP_ENABLED)
			.build(false);
		WISPBLOCK_GEM = RenderType.makeType(Name("WispBlockGem"), DefaultVertexFormats.BLOCK, GL11.GL_QUADS, 64, glState);

		glState = RenderType.State.getBuilder()
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.lightmap(LIGHTMAP_ENABLED)
				.line(LINE_2)
			.build(false);
		WISPBLOCK_GEM_OUTLINE = RenderType.makeType(Name("WispBlockGemOutline"), DefaultVertexFormats.POSITION_COLOR_LIGHTMAP, GL11.GL_LINES, 64, glState);
	}
	
}
