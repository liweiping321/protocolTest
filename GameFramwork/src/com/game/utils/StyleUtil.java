package com.game.utils;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * @author jianpeng.zhang
 * @since 2017/1/10.
 */
public class StyleUtil {
	public static SimpleAttributeSet getFontStyle(int fontSize) {
		return getFontStyle(null, fontSize, false, false);
	}

	public static SimpleAttributeSet getFontStyle(Color color) {
		return getFontStyle(color, 0, false, false);
	}

	public static SimpleAttributeSet getFontStyle(Color color, int fontSize, boolean isBold, boolean isItalic) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		if (color != null) {
			StyleConstants.setForeground(attrSet, color);
		}
		if (fontSize > 0) {
			StyleConstants.setFontSize(attrSet, fontSize);
		}
		StyleConstants.setBold(attrSet, isBold);
		StyleConstants.setItalic(attrSet, isItalic);
		return attrSet;
	}
}
