package com.smanzana.nostrumaetheria.client.render;

import java.util.OptionalDouble;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class AetheriaRenderTypes extends RenderType {

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
		
		final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = RenderType.TRANSLUCENT_TRANSPARENCY;//ObfuscationReflectionHelper.getPrivateValue(RenderStateShard.class, null, "TRANSLUCENT_TRANSPARENCY");
		final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
		//final RenderState.DepthTestState DEPTH_EQUAL = new RenderState.DepthTestState("==", GL11.GL_EQUAL);
		//final RenderState.DepthTestState NO_DEPTH = new RenderState.DepthTestState("none", GL11.GL_ALWAYS);
		//final RenderState.LightmapState NO_LIGHTING = new RenderState.LightmapState(false);
	    final RenderStateShard.LightmapStateShard LIGHTMAP_ENABLED = new RenderStateShard.LightmapStateShard(true);
	    final RenderStateShard.LineStateShard LINE_2 = new RenderStateShard.LineStateShard(OptionalDouble.of(2));
	    final RenderStateShard.LineStateShard LINE_3 = new RenderStateShard.LineStateShard(OptionalDouble.of(3));
	    //@SuppressWarnings("deprecation")
		//final RenderState.TextureState BLOCK_SHEET = new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS_TEXTURE, false, false);
	    final RenderStateShard.WriteMaskStateShard NO_DEPTH_WRITE = new RenderStateShard.WriteMaskStateShard(true, false);
		
		// Define render types
		RenderType.CompositeState glState;
				
		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setWriteMaskState(NO_DEPTH_WRITE)
				.setShaderState(POSITION_COLOR_LIGHTMAP_SHADER)
			.createCompositeState(false);
		AETHER_FLAT = RenderType.create(Name("AetherFlat"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 32, false, false, glState);
		AETHER_FLAT_TRIS = RenderType.create(Name("AetherFlatTris"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLES, 32, false, false, glState);
		
		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setLineState(LINE_3)
				.setShaderState(RENDERTYPE_LINES_SHADER)
			.createCompositeState(false);
		RELAY_LINES = RenderType.create(Name("AetherRelayLines"), DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 64, false, false, glState);
		
		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setWriteMaskState(NO_DEPTH_WRITE)
				.setShaderState(POSITION_COLOR_LIGHTMAP_SHADER)
			.createCompositeState(false);
		INFUSER_ORB = RenderType.create(Name("AetherInfuserOrb"), DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 64, false, false, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityAetherInfuserRenderer.SPARK_TEX_LOC, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				//.depthTest(NO_DEPTH) // actually want this?
				.setLightmapState(LIGHTMAP_ENABLED)
				.setWriteMaskState(NO_DEPTH_WRITE)
				.setShaderState(BLOCK_SHADER)
			.createCompositeState(false);
		INFUSER_SPARK = RenderType.create(Name("AetherInfuserSpark"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 64, false, false, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityWispBlockRenderer.BASE_TEX_LOC, false, false))
				.setLightmapState(LIGHTMAP_ENABLED)
				.setShaderState(BLOCK_SHADER)
			.createCompositeState(true);
		WISPBLOCK_BASE = RenderType.create(Name("WispBlockBase"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 32, false, false, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityWispBlockRenderer.PLATFORM_TEX_LOC, false, false))
				.setLightmapState(LIGHTMAP_ENABLED)
				.setShaderState(BLOCK_SHADER)
			.createCompositeState(true);
		WISPBLOCK_PLATFORM = RenderType.create(Name("WispBlockPlatform"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.TRIANGLES, 32, false, false, glState);

		glState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(TileEntityWispBlockRenderer.GEM_TEX_LOC, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setShaderState(BLOCK_SHADER)
			.createCompositeState(false);
		WISPBLOCK_GEM = RenderType.create(Name("WispBlockGem"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 64, false, false, glState);

		glState = RenderType.CompositeState.builder()
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setLineState(LINE_2)
				.setShaderState(RENDERTYPE_LINES_SHADER)
				.setCullState(NO_CULL)
			.createCompositeState(false);
		WISPBLOCK_GEM_OUTLINE = RenderType.create(Name("WispBlockGemOutline"), DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 64, false, false, glState);
	}
	
	private AetheriaRenderTypes(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
		super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
		throw new UnsupportedOperationException("Should not be instantiated");
	}
	
}
