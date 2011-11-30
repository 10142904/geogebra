package geogebra.util;

import geogebra.common.awt.ColorAdapter;
import geogebra.common.util.LaTeXCache;

import java.awt.Color;

import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.cache.JLaTeXMathCache;

public class GeoLaTeXCache implements LaTeXCache{
	// used by Captions, GeoText and DrawParametricCurve to cache LaTeX formulae
	public Object keyLaTeX = null;

	public Object getCachedLaTeXKey(String latex, int fontSize, int style, ColorAdapter fgColorAdapter) {
		Object newKey;
		try {
				
		newKey = JLaTeXMathCache.getCachedTeXFormula(latex, TeXConstants.STYLE_DISPLAY, style, fontSize, 1 /* inset around the label*/, 
				(AwtColorAdapter)fgColorAdapter);
		} catch (ParseException e) {
			if (keyLaTeX != null) {
				// remove old key from cache
				try {
					JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
			throw e;
		}
		if (keyLaTeX != null && !keyLaTeX.equals(newKey)) {
			// key has changed, remove old key from cache
			try {
				JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
			//Application.debug("removing");
		}

		keyLaTeX = newKey;
		return keyLaTeX;

	}

	public void remove() {
		if (keyLaTeX != null)
			try {
				JLaTeXMathCache.removeCachedTeXFormula(keyLaTeX);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		
	}

}
