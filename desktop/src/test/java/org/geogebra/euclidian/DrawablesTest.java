package org.geogebra.euclidian;

import java.util.TreeSet;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.desktop.main.AppDNoGui;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DrawablesTest {
	private static AppDNoGui app;
	@BeforeClass
	public static void setuApp() {
		app = CommandsTest.createApp();
	}

	@Test
	public void checkDrawables() {
		String[] def = new String[] { "(1,1)", "Angle[x^2=y^2]", "true",
				"Button[]", "InputBox[]", "x^2+y^2/3=1",
				"Semicircle[(0,0),(1,1)]", "xx", "1<x<2", "x=y", "{(1,1)}",
				"ConvexHull[(0,0),(0,1),(1,0)]", "7",
				"Polygon[(0,0),(0,1),(1,0)]", "Polyline[(0,0),(0,1),(1,0)]",
				"Polyline[(0,0),(0,1),(1,0),true]",
				"Ray[(0,0),(2,3)]",
				"Segment[(0,0),(2,3)]", "Vector[(0,0),(2,3)]",
				"FormulaText[x^2]", "(t,t^3)", "x^4+y^4=1", "x>y",
				"Spline[(0,0),(0,1),(1,0),(2,3)]", "Turtle[]", "(1,1,0)",
				"Vector[(1,1,0)]", "Segment[(1,1,0),(1,1,1)]",
				"Line[(1,1,0),(1,1,1)]", "Ray[(1,1,0),(1,1,1)]",
				"Ellipse[(2,3,0),(1,1,0),(1,0,0)]",
				"Polygon[(0,0),(0,1),(1,0,0)]",
				"PolyLine[(0,0),(0,1),(1,0,0)]",
				"Angle[(1,1,0)]", "Net[Cube[(0,0),(1,1)],1]", "xAxis", "zAxis",
				"cub(t)=(t,t,t^3)", "x+y=z", "xx+yy+zz=1", "Cube[(0,0),(1,1)]",
				"Surface[(u,v,u+v),u,0,1,v,0,1]", "x^3=z^3",
				"Cone[(0,0,0),(0,0,1),1]", "Side[Cone[(0,0,0),(0,0,1),1]]",
				"IntersectRegion(x+y+0z=0,Cone[(0,0,0),(0,0,1),1])", "tp" };
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		ap.processAlgebraCommand("tp=ToolImage[2]", false);
		TreeSet<GeoClass> types = new TreeSet<GeoClass>();
		for (int i = 0; i < def.length; i++) {
			GeoElementND geo = ap.processAlgebraCommand(def[i], false)[0];
			DrawableND draw = app.getEuclidianView1().newDrawable(geo);
			Assert.assertEquals(geo.getDefinitionForInputBar(),
					ignore(geo.getGeoClassType()),
					draw == null);
			types.add(geo.getGeoClassType());
		}
		for (GeoClass type : GeoClass.values()) {
			Assert.assertTrue(type + "",
					types.contains(type)
							|| GeoClass.CAS_CELL == type
							|| GeoClass.SPACE == type
							|| GeoClass.DEFAULT == type
							|| GeoClass.CLIPPINGCUBE3D == type);
		}

	}

	private boolean ignore(GeoClass type) {
		switch (type) {
		case NET:
		case POLYHEDRON:
		case PLANE3D:
		case QUADRIC:
		case QUADRIC_PART:
		case QUADRIC_LIMITED:
		case SURFACECARTESIAN3D:
		case IMPLICIT_SURFACE_3D:
		case AXIS:
		case AXIS3D:
			return true;
		}
		return false;
	}
}
