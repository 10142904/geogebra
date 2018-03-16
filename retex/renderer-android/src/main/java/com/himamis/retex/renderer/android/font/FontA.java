package com.himamis.retex.renderer.android.font;

import android.graphics.Typeface;

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.TextAttribute;

import java.util.Map;

public class FontA implements Font {

    private Typeface mTypeface;
    private int mSize;
    private String mName;

    public FontA(String name, Typeface typeface, int size) {
        mName = name;
        mTypeface = typeface;
        mSize = size;
    }

    public FontA(String name, int style, int size) {
        mTypeface = Typeface.create(name, getTypefaceStyle(style));
        mName = name;
        mSize = size;
    }

    private static int getTypefaceStyle(int fontStyle) {
        int typefaceStyle = Typeface.NORMAL;

        if (fontStyle == BOLD) {
            typefaceStyle = Typeface.BOLD;
        } else if (fontStyle == ITALIC) {
            typefaceStyle = Typeface.ITALIC;
        } else if (fontStyle == (BOLD | ITALIC)) {
            typefaceStyle = Typeface.BOLD_ITALIC;
        }

        return typefaceStyle;
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public int getSize() {
        return mSize;
    }

    public Font deriveFont(Map<TextAttribute, Object> map) {
        // FIXME cannot infer Font from map
        return this;
    }

    public Font deriveFont(int type) {
        int typefaceStyle = getTypefaceStyle(type);
        return new FontA(mName, Typeface.create(mTypeface, typefaceStyle), mSize);
    }
    
	@Override
	public boolean isEqual(Font f) {
        FontA font = (FontA) f;
        return mName.equals(font.mName) && mTypeface.equals(font.mTypeface) && mSize == font.mSize;
    }
	
	public int getScale() {
		return 1;
	}


}
