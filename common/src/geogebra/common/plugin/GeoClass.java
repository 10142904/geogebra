package geogebra.common.plugin;

public enum GeoClass {
	ANGLE("Angle"), AXIS("Axis"), BOOLEAN("Boolean"), BUTTON("Button"), TEXTFIELD("TextField"), CONIC("Conic"), CONICPART("ConicPart"), FUNCTION("Function"), INTERVAL("Interval"), FUNCTIONCONDITIONAL("FunctionConditional"), IMAGE("Image"), LINE("Line"), LIST("List"), LOCUS("Locus"), NUMERIC("Numeric"), POINT("Point"), POLYGON("Polygon"), RAY("Ray"), SEGMENT("Segment"), TEXT("Text"), VECTOR("Vector"), CURVE_CARTESIAN("CurveCartesian"), CURVE_POLAR("CurvePolar"), IMPLICIT_POLY("ImplicitPoly"), FUNCTION_NVAR("FunctionNVar"), POLYLINE("PolyLine"), PENSTROKE("PenStroke"), SPLINE("CurveCartesian"),
	TURTLE("Turtle"),
	CAS_CELL("CasCell"),

	ANGLE3D("Angle3D"), POINT3D("Point3D"), VECTOR3D("Vector3D"), SEGMENT3D("Segment3D"), LINE3D("Line3D"), RAY3D("Ray3D"), CONIC3D("Conic3D"), CONICSECTION("Conic3DPart"), AXIS3D("Axis3D"), CURVE_CARTESIAN3D("CurveCartesian3D"), POLYGON3D("Polygon3D"), PLANE3D("Plane3D"), QUADRIC("Quadric"), QUADRIC_PART("QuadricPart"), QUADRIC_LIMITED("QuadricLimited"), POLYLINE3D("PolyLine3D"), POLYHEDRON("Polyhedron"), NET("Net"),

	SURFACECARTESIAN3D("SurfaceCartesian3D"),
	
	CLIPPINGCUBE3D("ClippingCube3D"),

	SPACE("Space"),

	DEFAULT("Default");
	
	public String name;

	GeoClass(String name) {
		this.name = name;
	}
}
