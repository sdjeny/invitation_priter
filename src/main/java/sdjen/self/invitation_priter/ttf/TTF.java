package sdjen.self.invitation_priter.ttf;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public class TTF {
	/**
	 * 获取系统中可用的字体的名字
	 */
	private static void viewFontInSystem() {
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fontName = e.getAvailableFontFamilyNames();
		for (int i = 0; i < fontName.length; i++) {
			System.out.println(fontName[i]);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getFont(Font.PLAIN, 30).getFontName());
	}

	public static Font getFont(int style, float size) throws Exception {
		Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT,
				new File("font.ttf"));
		dynamicFont = dynamicFont.deriveFont(style, size);
		return dynamicFont;
	}
}
