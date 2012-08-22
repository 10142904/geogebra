package geogebra.mobile.gui.view.algebra;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.web.main.DrawEquationWeb;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Taken from the web-project 
 *
 */
public class RadioButtonTreeItem extends HorizontalPanel implements DoubleClickHandler, ClickHandler, MouseMoveHandler, MouseDownHandler
{

	GeoElement geo;
	Kernel kernel;
	App app;
	AlgebraView av;
	boolean previouslyChecked;
	boolean LaTeX = false;
	boolean thisIsEdited = false;
	boolean mout = false;

	SpanElement seMayLatex;
	SpanElement seNoLatex;

	RadioButtonHandy radio;
	InlineHTML ihtml;
	TextBox tb;

	private class RadioButtonHandy extends RadioButton
	{
		public RadioButtonHandy()
		{
			super(DOM.createUniqueId());
		}

		@Override
		public void onBrowserEvent(Event event)
		{

			if (RadioButtonTreeItem.this.av.isEditing())
				return;

			if (event.getTypeInt() == Event.ONCLICK)
			{
				// Part of AlgebraController.mouseClicked in Desktop
				if (Element.is(event.getEventTarget()))
				{
					if (Element.as(event.getEventTarget()) == getElement().getFirstChild())
					{
						setChecked(RadioButtonTreeItem.this.previouslyChecked = !RadioButtonTreeItem.this.previouslyChecked);
						RadioButtonTreeItem.this.geo.setEuclidianVisible(!RadioButtonTreeItem.this.geo.isSetEuclidianVisible());
						RadioButtonTreeItem.this.geo.update();
						RadioButtonTreeItem.this.geo.getKernel().getApplication().storeUndoInfo();
						RadioButtonTreeItem.this.geo.getKernel().notifyRepaint();
						return;
					}
				}
			}
		}
	}

	public RadioButtonTreeItem(GeoElement ge, AlgebraView algebraView)
	{
		super();
		this.geo = ge;
		this.kernel = this.geo.getKernel();
		this.app = this.kernel.getApplication();
		this.av = algebraView;

		setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);

		this.radio = new RadioButtonHandy();
		this.radio.setEnabled(ge.isEuclidianShowable());
		this.radio.setChecked(RadioButtonTreeItem.this.previouslyChecked = ge.isEuclidianVisible());
		add(this.radio);

		SpanElement se = DOM.createSpan().cast();
		se.getStyle().setProperty("display", "-moz-inline-box");
		se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		se.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
		this.ihtml = new InlineHTML();
		this.ihtml.addDoubleClickHandler(this);
		this.ihtml.addClickHandler(this);
		this.ihtml.addMouseMoveHandler(this);
		this.ihtml.addMouseDownHandler(this);
		add(this.ihtml);
		this.ihtml.getElement().appendChild(se);

		SpanElement se2 = DOM.createSpan().cast();
		se2.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;");
		this.ihtml.getElement().appendChild(se2);

		String text = "";
		if (this.geo.isIndependent())
		{
			text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
		}
		else
		{
			switch (this.kernel.getAlgebraStyle())
			{
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				text = this.geo.addLabelTextOrHTML(this.geo.getDefinitionDescription(StringTemplate.defaultTemplate));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				text = this.geo.addLabelTextOrHTML(this.geo.getCommandDescription(StringTemplate.defaultTemplate));
				break;
			}
		}

		// if enabled, render with LaTeX
		if (/* TODO av.isRenderLaTeX() && */this.kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE)
		{
			String latexStr = this.geo.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate);
			if ((latexStr != null) && this.geo.isLaTeXDrawableGeo(latexStr) && (this.geo.isGeoList() ? !((GeoList) this.geo).isMatrix() : true))
			{
				latexStr = inputLatexCosmetics(latexStr);
				this.seMayLatex = se;
				DrawEquationWeb.drawEquationAlgebraView(this.seMayLatex, latexStr, this.geo.getAlgebraColor(), GColor.white);
				this.LaTeX = true;
			}
			else
			{
				this.seNoLatex = se;
				this.seNoLatex.setInnerHTML(text);
			}
		}
		else
		{
			this.seNoLatex = se;
			this.seNoLatex.setInnerHTML(text);
		}
		// FIXME: geo.getLongDescription() doesn't work
		// geo.getKernel().getApplication().setTooltipFlag();
		// se.setTitle(geo.getLongDescription());
		// geo.getKernel().getApplication().clearTooltipFlag();
	}

	public void update()
	{
		// check for new LaTeX
		boolean newLaTeX = false;
		String text = null;
		if (/* TODO av.isRenderLaTeX() && */this.kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE)
		{
			text = this.geo.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate);
			if ((text != null) && this.geo.isLaTeXDrawableGeo(text) && (this.geo.isGeoList() ? !((GeoList) this.geo).isMatrix() : true))
			{
				newLaTeX = true;
			}
		}
		// check for new text
		if (!newLaTeX)
		{
			if (this.geo.isIndependent())
			{
				text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
			}
			else
			{
				switch (this.kernel.getAlgebraStyle())
				{
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = this.geo.getAlgebraDescriptionTextOrHTMLDefault();
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = this.geo.addLabelTextOrHTML(this.geo.getDefinitionDescription(StringTemplate.defaultTemplate));
					break;

				case Kernel.ALGEBRA_STYLE_COMMAND:
					text = this.geo.addLabelTextOrHTML(this.geo.getCommandDescription(StringTemplate.defaultTemplate));
					break;
				}
			}
		}

		// now we have text and how to display it (newLaTeX/LaTeX)
		if (this.LaTeX && newLaTeX)
		{
			text = inputLatexCosmetics(text);
			DrawEquationWeb.updateEquationMathQuill(text, this.seMayLatex);
		}
		else if (!this.LaTeX && !newLaTeX)
		{
			this.seNoLatex.setInnerHTML(text);
		}
		else if (newLaTeX)
		{
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
			this.ihtml.getElement().replaceChild(se, this.seNoLatex);
			text = inputLatexCosmetics(text);
			this.seMayLatex = se;
			DrawEquationWeb.drawEquationAlgebraView(this.seMayLatex, text, this.geo.getAlgebraColor(), GColor.white);
			this.LaTeX = true;
		}
		else
		{
			SpanElement se = DOM.createSpan().cast();
			se.getStyle().setProperty("display", "-moz-inline-box");
			se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			se.getStyle().setColor(GColor.getColorString(this.geo.getAlgebraColor()));
			this.ihtml.getElement().replaceChild(se, this.seMayLatex);
			this.seNoLatex = se;
			this.seNoLatex.setInnerHTML(text);
			this.LaTeX = false;
		}
	}

	public boolean isThisEdited()
	{
		return this.thisIsEdited;
	}

	// TODO
	public void cancelEditing()
	{
		// if (LaTeX) {
		// DrawEquationWeb.endEditingEquationMathQuill(this, seMayLatex);
		// } else {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(tb.getText());
		// }
	}

	public static String inputLatexCosmetics(String eqstring)
	{
		// make sure eg FractionText[] works (surrounds with {} which doesn't draw
		// well in MathQuill)
		if (eqstring.length() >= 2)
			if (eqstring.startsWith("{") && eqstring.endsWith("}"))
			{
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

		// remove $s
		eqstring = eqstring.trim();
		while (eqstring.startsWith("$"))
			eqstring = eqstring.substring(1).trim();
		while (eqstring.endsWith("$"))
			eqstring = eqstring.substring(0, eqstring.length() - 1).trim();

		// remove all \; and \,
		eqstring = eqstring.replace("\\;", "");
		eqstring = eqstring.replace("\\,", "");

		eqstring = eqstring.replace("\\left\\{", "\\lbrace");
		eqstring = eqstring.replace("\\right\\}", "\\rbrace");
		return eqstring;
	}

	public void startEditing()
	{
		// TODO
		// thisIsEdited = true;
		// if (LaTeX && !(geo.isGeoVector() && geo.isIndependent())) {
		// geogebra.web.main.DrawEquationWeb.editEquationMathQuill(this,seMayLatex);
		// } else {
		// remove(ihtml);
		// tb = new TextBox();
		// tb.setText( geo.getAlgebraDescriptionTextOrHTMLDefault() );
		// add(tb);
		// mout = false;
		// tb.setFocus(true);
		// Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
		// public void execute() {
		// tb.setFocus(true);
		// }
		// });
		// tb.addKeyDownHandler(new KeyDownHandler() {
		// public void onKeyDown(KeyDownEvent kevent) {
		// if (kevent.getNativeKeyCode() == 13) {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(tb.getText());
		// } else if (kevent.getNativeKeyCode() == 27) {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(null);
		// }
		// }
		// });
		// tb.addBlurHandler(new BlurHandler() {
		// public void onBlur(BlurEvent bevent) {
		// if (mout) {
		// remove(tb);
		// add(ihtml);
		// stopEditingSimple(null);
		// }
		// }
		// });
		// tb.addMouseOverHandler(new MouseOverHandler() {
		// public void onMouseOver(MouseOverEvent moevent) {
		// mout = false;
		// }
		// });
		// tb.addMouseOutHandler(new MouseOutHandler() {
		// public void onMouseOut(MouseOutEvent moevent) {
		// mout = true;
		// tb.setFocus(true);
		// }
		// });
		// }
	}

	public void stopEditingSimple(String newValue)
	{

		this.thisIsEdited = false;
		this.av.cancelEditing();

		if (newValue != null)
		{
			boolean redefine = !this.geo.isPointOnPath();
			GeoElement geo2 = this.kernel.getAlgebraProcessor().changeGeoElement(this.geo, newValue, redefine, true);
			if (geo2 != null)
			{
				this.geo = geo2;
			}
		}

		// maybe it's possible to enter something which is LaTeX
		// note: this should be OK for independent GeoVectors too
		update();
	}

	public void stopEditing(String newValue)
	{

		this.thisIsEdited = false;
		this.av.cancelEditing();

		if (newValue != null)
		{
			// Formula Hacks ... Currently only functions are considered
			StringBuilder sb = new StringBuilder();
			boolean switchw = false;
			for (int i = 0; i < newValue.length(); i++)
				if (newValue.charAt(i) != ' ')
				{
					if (newValue.charAt(i) != '|')
						sb.append(newValue.charAt(i));
					else if (switchw = !switchw)
						sb.append("abs(");
					else
						sb.append(")");
				}
			newValue = sb.toString();

			// Formula Hacks ended.
			boolean redefine = !this.geo.isPointOnPath();
			GeoElement geo2 = this.kernel.getAlgebraProcessor().changeGeoElement(this.geo, newValue, redefine, true);
			if (geo2 != null)
			{
				this.geo = geo2;
			}
		}

		// maybe it's possible to enter something which is non-LaTeX
		update();
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt)
	{

		if (this.av.isEditing())
			return;

		EuclidianViewInterfaceCommon ev = this.app.getActiveEuclidianView();
		this.app.clearSelectedGeos();
		ev.resetMode();
		if (this.geo != null && !evt.isControlKeyDown())
		{
			this.app.getAlgebraView().startEditing(this.geo, evt.isShiftKeyDown());
		}

		evt.stopPropagation();
		evt.preventDefault();
	}

	@Override
	public void onMouseDown(MouseDownEvent evt)
	{
		if (this.av.isEditing())
			return;

		evt.preventDefault();
		evt.stopPropagation();
	}

	@Override
	public void onClick(ClickEvent evt)
	{

		// TODO
		// if (av.isEditing())
		// return;
		//
		// App app = geo.getKernel().getApplication();
		// int mode = app.getActiveEuclidianView().getMode();
		// if (//!skipSelection &&
		// (mode == EuclidianConstants.MODE_MOVE || mode ==
		// EuclidianConstants.MODE_RECORD_TO_SPREADSHEET) ) {
		// // update selection
		// if (geo == null){
		// app.clearSelectedGeos();
		// }
		// else {
		// // handle selecting geo
		// if (evt.isControlKeyDown()) {
		// app.toggleSelectedGeo(geo);
		// if (app.getSelectedGeos().contains(geo)) av.lastSelectedGeo = geo;
		// } else if (evt.isShiftKeyDown() && av.lastSelectedGeo != null) {
		// boolean nowSelecting = true;
		// boolean selecting = false;
		// boolean aux = geo.isAuxiliaryObject();
		// boolean ind = geo.isIndependent();
		// boolean aux2 = av.lastSelectedGeo.isAuxiliaryObject();
		// boolean ind2 = av.lastSelectedGeo.isIndependent();
		//
		// if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
		// Iterator<GeoElement> it =
		// geo.getKernel().getConstruction().getGeoSetLabelOrder().iterator();
		// boolean direction = geo.getLabel(StringTemplate.defaultTemplate).
		// compareTo(av.lastSelectedGeo.getLabel(StringTemplate.defaultTemplate)) <
		// 0;
		//
		// while (it.hasNext()) {
		// GeoElement geo2 = it.next();
		// if ((geo2.isAuxiliaryObject() == aux && aux)
		// || (geo2.isAuxiliaryObject() == aux && geo2.isIndependent() == ind)) {
		//
		// if (direction && geo2.equals(av.lastSelectedGeo)) selecting = !selecting;
		// if (!direction && geo2.equals(geo)) selecting = !selecting;
		//
		// if (selecting) {
		// app.toggleSelectedGeo(geo2);
		// nowSelecting = app.getSelectedGeos().contains(geo2);
		// }
		// if (!direction && geo2.equals(av.lastSelectedGeo)) selecting =
		// !selecting;
		// if (direction && geo2.equals(geo)) selecting = !selecting;
		// }
		// }
		// }
		//
		// if (nowSelecting) {
		// app.addSelectedGeo(geo);
		// av.lastSelectedGeo = geo;
		// } else {
		// app.removeSelectedGeo(av.lastSelectedGeo);
		// av.lastSelectedGeo = null;
		// }
		// } else {
		// app.clearSelectedGeos(false); //repaint will be done next step
		// app.addSelectedGeo(geo);
		// av.lastSelectedGeo = geo;
		// }
		// }
		// }
		// else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
		// // let euclidianView know about the click
		// AbstractEvent event2 =
		// MouseEvent.wrapEvent(evt.getNativeEvent(),ZeroOffset.instance);
		// app.getActiveEuclidianView().clickedGeo(geo, event2);
		// //event.release();
		// } else
		// // tell selection listener about click
		// app.geoElementSelected(geo, false);
		//
		//
		// // Alt click: copy definition to input field
		// if (geo != null && evt.isAltKeyDown() && app.showAlgebraInput()) {
		// // F3 key: copy definition to input bar
		// app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);
		// }
		//
		// app.getActiveEuclidianView().mouseMovedOver(null);
		// evt.preventDefault();
		// evt.stopPropagation();
	}

	@Override
	public void onMouseMove(MouseMoveEvent evt)
	{
		// TODO: not required
	}
}