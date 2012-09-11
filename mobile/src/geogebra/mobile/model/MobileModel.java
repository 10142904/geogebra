package geogebra.mobile.model;

import geogebra.common.euclidian.Hits;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.kernel.geos.Test;
import geogebra.mobile.utils.ToolBarCommand;

import java.util.ArrayList;

/**
 * 
 * @author Thomas Krismayer
 * 
 */
public class MobileModel
{

	private GuiModel guiModel;
	private Kernel kernel;
	private ArrayList<GeoElement> selectedElements = new ArrayList<GeoElement>();

	public MobileModel(GuiModel model, Kernel k)
	{
		this.guiModel = model;
		this.kernel = k;
	}

	public void select(GeoElement geo)
	{
		geo.setSelected(true);
		this.selectedElements.add(geo);
	}

	public boolean select(Hits hits, Test geoclass, int max)
	{
		boolean success = false;
		Hits h = new Hits();
		hits.getHits(geoclass, h);
		for (int i = 0; i < max; i++)
		{
			if (i < h.size())
			{
				select(h.get(i));
				success = true;
			}
		}
		return success;
	}

	public void resetSelection()
	{
		for (GeoElement geo : this.selectedElements)
		{
			geo.setSelected(false);
		}
		this.selectedElements.clear();
	}

	/**
	 * 
	 * @param class1
	 *            required Class
	 * @return the first element of the given Class; null in case there is no
	 *         such element
	 */
	public GeoElement getElement(Class<? extends GeoElement> class1)
	{
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				return geo;
			}
		}
		return null;
	}

	public GeoElement getElement(Class<? extends GeoElement> class1, int i)
	{
		int count = 0;
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				if (i == count)
				{
					return geo;
				}
				count++;
			}
		}
		return null;
	}

	public ArrayList<GeoElement> getAll(Class<? extends GeoElement> class1)
	{
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				geos.add(geo);
			}
		}
		return geos;
	}

	public int getNumberOf(Class<? extends GeoElement> class1)
	{
		int count = 0;
		for (GeoElement geo : this.selectedElements)
		{
			if (geo.getClass().equals(class1))
			{
				count++;
			}
		}
		return count;
	}

	public GeoElement lastSelected()
	{
		return this.selectedElements.size() > 0 ? this.selectedElements
				.get(this.selectedElements.size() - 1) : null;
	}

	public void handleEvent(Hits hits)
	{

		boolean draw = false;

		switch (this.guiModel.getCommand())
		{
		// commands that need one point or a point and an element
		case AttachDetachPoint:
			// TODO
			// attachDetach(hits);
			break;

		// commands that need two points
		case LineThroughTwoPoints:
		case SegmentBetweenTwoPoints:
		case RayThroughTwoPoints:
		case VectorBetweenTwoPoints:
		case CircleWithCenterThroughPoint:
		case Semicircle:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(GeoPoint.class) >= 2;
			break;

		// commands that need one point and one line
		case PerpendicularLine:
		case ParallelLine:
		case Parabola:
			if (!select(hits, Test.GEOPOINT, 1))
			{
				select(hits, Test.GEOLINE, 1);
			}
			draw = getNumberOf(GeoPoint.class) >= 1
					&& getNumberOf(GeoLine.class) >= 1;
			break;

		// commands that need two points or one segment
		case MidpointOrCenter:
		case PerpendicularBisector:
			if (!select(hits, Test.GEOPOINT, 1))
			{
				select(hits, Test.GEOSEGMENT, 1);
			}
			draw = getNumberOf(GeoSegment.class) >= 1
					|| getNumberOf(GeoPoint.class) >= 2;
			break;

		// commands that need any two objects
		case IntersectTwoObjects:
			// TODO
			// intersect(hits);
			break;

		// commands that need tree points
		case CircleThroughThreePoints:
		case CircularArcWithCenterBetweenTwoPoints:
		case CircularSectorWithCenterBetweenTwoPoints:
		case CircumCirculuarArcThroughThreePoints:
		case CircumCircularSectorThroughThreePoints:
		case Ellipse:
		case Hyperbola:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(GeoPoint.class) >= 3;
			break;

		// commands that need five points
		case ConicThroughFivePoints:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(GeoPoint.class) >= 5;
			break;

		// commands that need an unknown number of points
		case PolylineBetweenPoints:
		case Polygon:
			select(hits, Test.GEOPOINT, 1);
			draw = getNumberOf(GeoPoint.class) > 2
					&& getElement(GeoPoint.class).equals(lastSelected());
			break;
		default:
			break;
		}
		// draw anything other than a point
		if (draw)
		{
			switch (this.guiModel.getCommand())
			{
			case LineThroughTwoPoints:
				this.kernel.getAlgoDispatcher().Line(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1));
				break;
			case SegmentBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().Segment(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1));
				break;
			case RayThroughTwoPoints:
				this.kernel.getAlgoDispatcher().Ray(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1));
				break;
			case VectorBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().Vector(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1));
				break;
			case CircleWithCenterThroughPoint:
				this.kernel.getAlgoDispatcher().Circle(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1));
				break;
			case Semicircle:
				this.kernel.getAlgoDispatcher().Semicircle(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1));
				break;
			case PerpendicularLine:
				this.kernel.getAlgoDispatcher().OrthogonalLine(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoLine) getElement(GeoLine.class));
				break;
			case ParallelLine:
				this.kernel.getAlgoDispatcher().Line(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoLine) getElement(GeoLine.class));
				break;
			case MidpointOrCenter:
				if (getNumberOf(GeoSegment.class) > 0)
				{
					this.kernel.getAlgoDispatcher().Midpoint(null,
							(GeoSegment) getElement(GeoSegment.class));
				} else if (getNumberOf(GeoPoint.class) >= 2)
				{
					this.kernel.getAlgoDispatcher().Midpoint(null,
							(GeoPoint) getElement(GeoPoint.class),
							(GeoPoint) getElement(GeoPoint.class, 1));
				}
				break;
			case PerpendicularBisector:
				if (getNumberOf(GeoSegment.class) > 0)
				{
					this.kernel.getAlgoDispatcher().LineBisector(null,
							(GeoSegment) getElement(GeoSegment.class));
				} else if (getNumberOf(GeoPoint.class) >= 2)
				{
					this.kernel.getAlgoDispatcher().LineBisector(null,
							(GeoPoint) getElement(GeoPoint.class),
							(GeoPoint) getElement(GeoPoint.class, 1));
				}
				break;
			case Parabola:
				this.kernel.getAlgoDispatcher().Parabola(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoLine) getElement(GeoLine.class));
				break;
			case CircleThroughThreePoints:
				this.kernel.getAlgoDispatcher().Circle(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1),
						(GeoPoint) getElement(GeoPoint.class, 2));
				break;
			case CircularArcWithCenterBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().CircleArc(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1),
						(GeoPoint) getElement(GeoPoint.class, 2));
				break;
			case CircularSectorWithCenterBetweenTwoPoints:
				this.kernel.getAlgoDispatcher().CircleSector(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1),
						(GeoPoint) getElement(GeoPoint.class, 2));
				break;
			case CircumCirculuarArcThroughThreePoints:
				this.kernel.getAlgoDispatcher().CircumcircleArc(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1),
						(GeoPoint) getElement(GeoPoint.class, 2));
				break;
			case CircumCircularSectorThroughThreePoints:
				this.kernel.getAlgoDispatcher().CircumcircleSector(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1),
						(GeoPoint) getElement(GeoPoint.class, 2));
				break;
			case Ellipse:
				this.kernel.getAlgoDispatcher().Ellipse(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1),
						(GeoPoint) getElement(GeoPoint.class, 2));
				break;
			case Hyperbola:
				this.kernel.getAlgoDispatcher().Hyperbola(null,
						(GeoPoint) getElement(GeoPoint.class),
						(GeoPoint) getElement(GeoPoint.class, 1),
						(GeoPoint) getElement(GeoPoint.class, 2));
				break;
			case ConicThroughFivePoints:
				this.kernel.getAlgoDispatcher().Conic(
						null,
						new GeoPoint[] { (GeoPoint) getElement(GeoPoint.class),
								(GeoPoint) getElement(GeoPoint.class, 1),
								(GeoPoint) getElement(GeoPoint.class, 2),
								(GeoPoint) getElement(GeoPoint.class, 3),
								(GeoPoint) getElement(GeoPoint.class, 4), });
				break;
			case PolylineBetweenPoints:
				ArrayList<GeoElement> geos = getAll(GeoPoint.class);
				geos.remove(geos.size() - 1);
				this.kernel.PolyLineND(null,
						geos.toArray(new GeoPoint[geos.size()]));
				break;
			case Polygon:
				ArrayList<GeoElement> geos2 = getAll(GeoPoint.class);
				geos2.remove(geos2.size() - 1);
				this.kernel.Polygon(null,
						geos2.toArray(new GeoPoint[geos2.size() - 1]));
				break;
			default:
			}

			resetSelection();
		}
	}

	public boolean handleEvent(GeoElement geo)
	{
		if (this.guiModel.getCommand() == ToolBarCommand.DeleteObject)
		{
			geo.remove();
			return false;
		}
		Hits hits = new Hits();
		hits.add(geo);
		handleEvent(hits);
		return true;
	}

}
