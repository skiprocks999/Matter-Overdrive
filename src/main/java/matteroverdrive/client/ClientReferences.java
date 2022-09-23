package matteroverdrive.client;

import matteroverdrive.References;
import matteroverdrive.core.screen.component.utils.ITexture;
import matteroverdrive.core.utils.UtilsRendering;
import net.minecraft.resources.ResourceLocation;

public class ClientReferences {

	public static final String PATH_GFX = References.ID + ":textures/";
	public static final String PATH_GUI = PATH_GFX + "gui/";
	public static final String PATH_ELEMENTS = PATH_GUI + "elements/";
	
	public static final String PATH_TEXT_CUSTOM_ATLAS = "atlas/";

	public static enum Colors {

		HOLO(169, 226, 251, 255), 
		HOLO_GREEN(24, 207, 0, 255), 
		HOLO_RED(230, 80, 20, 255),
		HOLO_YELLOW(252, 223, 116, 255),
		HOLO_PURPLE(116, 23, 230, 255),
		MATTER(191, 228, 230, 255), 
		GUI_STANDARD(62, 81, 84, 255), 
		GUI_LIGHT(100, 113, 136, 255),
		GUI_DARK(44, 54, 52, 255), 
		GUI_DARKER(34, 40, 17, 255),
		GUI_ENERGY(224, 0, 0, 255),
		RED(255, 0, 0, 255),
		GREEN(0, 255, 0, 255), 
		YELLOW(255, 255, 0, 255),
		WHITE(255, 255, 255, 255),
		YELLOW_STRIPES(254, 203, 4, 255),
		ORANGE_STRIPES(255, 132, 0, 255),
		PATTERN_DRIVE_NONE(35, 45, 48, 255),
		PILL_RED(208, 0, 0, 255),
		PILL_YELLOW(255, 228, 0, 255),
		PILL_BLUE(1, 159, 234, 255);

		private final int rInt;
		private final int gInt;
		private final int bInt;
		private final int aInt;

		private final int color;

		private final float rFloat;
		private final float gFloat;
		private final float bFloat;
		private final float aFloat;

		private final float[] floatArr;

		public static final int MAX_COLOR_VAL_INT = 255;
		public static final float MAX_COLOR_VAL_FLOAT = 255.0F;

		private Colors(int r, int g, int b, int a) {
			rInt = r;
			gInt = g;
			bInt = b;
			aInt = a;
			color = UtilsRendering.getRGBA(a, r, g, b);
			rFloat = (float) r / MAX_COLOR_VAL_FLOAT;
			gFloat = (float) g / MAX_COLOR_VAL_FLOAT;
			bFloat = (float) b / MAX_COLOR_VAL_FLOAT;
			aFloat = (float) a / MAX_COLOR_VAL_FLOAT;
			floatArr = new float[] { rFloat, gFloat, bFloat, aFloat };
		}

		public int getRInt() {
			return rInt;
		}

		public int getGRInt() {
			return gInt;
		}

		public int getBInt() {
			return bInt;
		}

		public int getAInt() {
			return aInt;
		}

		public int getColor() {
			return color;
		}

		public float getRFloat() {
			return rFloat;
		}

		public float getGFloat() {
			return gFloat;
		}

		public float getBFloat() {
			return bFloat;
		}

		public float getAFloat() {
			return aFloat;
		}

		public float[] getFloatArr() {
			return floatArr;
		}

		public float[] getFloatArrModAlpha(float newAlpha) {
			return new float[] { rFloat, gFloat, bFloat, newAlpha };
		}

	}
	
	public static enum AtlasTextures implements ITexture {
		
		HOLO_GRID("holo_grid", 17, 17),
		// rotating matricies is a pain in the ass fight me
		HOLO_PATTERN_MONITOR("pattern_monitor_holo", 17, 17),
		HOLO_PATTERN_MONITOR_90("pattern_monitor_holo_90", 17, 17),
		HOLO_PATTERN_MONITOR_180("pattern_monitor_holo_180", 17, 17),
		HOLO_PATTERN_MONITOR_270("pattern_monitor_holo_270", 17, 17),
		SPINNER("spinner", 64, 64),
		HOLO_GLOW("holo_monitor_glow", 16, 16),
		CONNECTION_ICON("connection_icon", 16, 16);
		
		private final ResourceLocation loc;
		private final int textWidth;
		private final int textHeight;
		
		private AtlasTextures(String name, int width, int height) {
			this.loc = new ResourceLocation(References.ID, PATH_TEXT_CUSTOM_ATLAS + name);
			textWidth = width;
			textHeight = height;
		}
		
		public ResourceLocation getTexture() {
			return loc;
		}

		@Override
		public int getTextureWidth() {
			return textWidth;
		}

		@Override
		public int getTextureHeight() {
			return textHeight;
		}
		
	}

}
