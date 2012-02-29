package geogebra.awt;

public class GradientPaint implements geogebra.common.awt.GradientPaint{
	private java.awt.GradientPaint impl;
	public GradientPaint(java.awt.GradientPaint copyg) {
		impl = new java.awt.GradientPaint(
			(float)copyg.getPoint1().getX(),
			(float)copyg.getPoint1().getY(),
			new java.awt.Color(
				copyg.getColor1().getRed(),
				copyg.getColor1().getGreen(),
				copyg.getColor1().getBlue(),
				copyg.getColor1().getAlpha()),
			(float)copyg.getPoint2().getX(),
			(float)copyg.getPoint2().getY(),
			new java.awt.Color(
					copyg.getColor2().getRed(),
					copyg.getColor2().getGreen(),
					copyg.getColor2().getBlue(),
					copyg.getColor2().getAlpha())
		);
	}
	public GradientPaint(float x1,float y1,geogebra.common.awt.Color color1,
			float x2,float y2,geogebra.common.awt.Color color2){
		impl = new java.awt.GradientPaint(x1, y1, Color.getAwtColor(color1),
				x2, y2, Color.getAwtColor(color2));
	}
	public java.awt.GradientPaint getPaint(){
		return impl;
	}
}
