/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

import geogebra.common.awt.GColor;
import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.common.main.SelectionManager;
import geogebra.common.util.AsyncOperation;
import geogebra.common.util.IndexHTMLBuilder;
import geogebra.html5.event.PointerEvent;
import geogebra.html5.event.ZeroOffset;
import geogebra.html5.gui.textbox.GTextBox;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.LongTouchManager;
import geogebra.html5.gui.util.LongTouchTimer.LongTouchHandler;
import geogebra.html5.main.AppW;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.html5.util.EventUtil;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.view.algebra.Marble.GeoContainer;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;

/**
 * RadioButtonTreeItem for the items of the algebra view tree
 * and also for the event handling which is copied from Desktop/AlgebraController.java
 *
 * File created by Arpad Fekete
 */

public class RadioButtonTreeItem extends HorizontalPanel
	implements DoubleClickHandler, ClickHandler, MouseMoveHandler, MouseDownHandler, 
        MouseOverHandler, MouseOutHandler, GeoContainer,
        geogebra.html5.gui.view.algebra.RadioButtonTreeItem,
	TouchStartHandler, TouchMoveHandler, TouchEndHandler, LongTouchHandler {

	private GeoElement geo;
	private Kernel kernel;
	protected AppW app;
	private SelectionManager selection; 
	private AlgebraView av;
	private boolean LaTeX = false;
	private boolean thisIsEdited = false;
	private boolean newCreationMode = false;
	boolean mout = false;

	protected SpanElement seMayLatex;
	private SpanElement seNoLatex;

	private Marble radio;
	InlineHTML ihtml;
	TextBox tb;
	private boolean needsUpdate;
	
	private LongTouchManager longTouchManager;
		
	public void updateOnNextRepaint(){
		this.needsUpdate = true;
	}

	/*private class RadioButtonHandy extends RadioButton {
		public RadioButtonHandy() {
			super(DOM.createUniqueId());
		}

		@Override
		public void onBrowserEvent(Event event) {

			if (av.isEditing())
				return;

			if (event.getTypeInt() == Event.ONCLICK) {
				// Part of AlgebraController.mouseClicked in Desktop
				if (Element.is(event.getEventTarget())) {
					if (Element.as(event.getEventTarget()) == getElement().getFirstChild()) {
						setValue(previouslyChecked = !previouslyChecked);
						geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
						geo.update();
						geo.getKernel().getApplication().storeUndoInfo();
						geo.getKernel().notifyRepaint();
						return;
					}
				}
			}
		}
	}*/
	private IndexHTMLBuilder getBuilder(final SpanElement se){
		return new IndexHTMLBuilder(false){
			Element sub = null;
			@Override
            public void append(String s){

				if(sub == null){
					se.appendChild(Document.get().createTextNode(s));
				}else{
					sub.appendChild(Document.get().createTextNode(s));
				}
			}
			@Override
            public void startIndex(){
				sub = Document.get().createElement("sub");
				sub.getStyle().setFontSize((int)(app.getFontSize() *0.8), Unit.PX);
			}
			@Override
            public void endIndex(){
				if(sub != null){
					se.appendChild(sub);
				}
				sub = null;				
			}
			@Override
            public String toString(){
				if(sub != null){
					endIndex();
				}
				return se.getInnerHTML();
			}
			@Override
            public void clear(){
				se.removeAllChildren();
				sub = null;
			}
			
			@Override
            public boolean canAppendRawHtml() {
				return false;
			}
			
			@Override
			public void appendHTML(String str) {
				append(str);
			}
		};
	}

	/**
	 * Creates a new RadioButtonTreeItem for displaying/editing an existing GeoElement
	 * @param ge the existing GeoElement to display/edit
	 * @param showUrl the marble to be shown when the GeoElement is visible
	 * @param hiddenUrl the marble to be shown when the GeoElement is invisible
	 */
	public RadioButtonTreeItem(GeoElement ge,SafeUri showUrl,SafeUri hiddenUrl) {
		super();
		geo = ge;
		kernel = geo.getKernel();
		app = (AppW)kernel.getApplication();
		av = app.getAlgebraView();
		selection = app.getSelectionManager();
		this.setStyleName("elem");

		//setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		

		radio = new Marble(showUrl, hiddenUrl,this);
		radio.setStyleName("marble");
		radio.setEnabled(ge.isEuclidianShowable());
		radio.setChecked(ge.isEuclidianVisible());
		add(radio);

		SpanElement se = DOM.createSpan().cast();
		updateNewStatic(se);
		updateColor(se);
		ihtml = new InlineHTML();
		ihtml.addDoubleClickHandler(this);
		ihtml.addClickHandler(this);
		ihtml.addMouseMoveHandler(this);
		ihtml.addMouseDownHandler(this);
		ihtml.addMouseOverHandler(this);
		ihtml.addMouseOutHandler(this);
		ihtml.addTouchStartHandler(this);
		ihtml.addTouchMoveHandler(this);
		ihtml.addTouchEndHandler(this);
		add(ihtml);
		ihtml.getElement().appendChild(se);

		SpanElement se2 = DOM.createSpan().cast();
		se2.appendChild(Document.get().createTextNode("\u00A0\u00A0\u00A0\u00A0"));
		ihtml.getElement().appendChild(se2);
		//String text = "";
		if (geo.isIndependent()) {
			geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				geo.addLabelTextOrHTML(
					geo.getDefinitionDescription(StringTemplate.defaultTemplate),getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				geo.addLabelTextOrHTML(
					geo.getCommandDescription(StringTemplate.defaultTemplate), getBuilder(se));
				break;
			}
		}
		// if enabled, render with LaTeX
		if (av.isRenderLaTeX() && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String latexStr = geo.getLaTeXAlgebraDescription(true,
			        StringTemplate.latexTemplateMQ);
			seNoLatex = se;
			if ((latexStr != null) &&
				geo.isLaTeXDrawableGeo() &&
				(geo.isGeoList() ? !((GeoList)geo).isMatrix() : true) ) {
				this.needsUpdate = true;
				av.repaintView();
			}
		} else {
			seNoLatex = se;
		}
		//FIXME: geo.getLongDescription() doesn't work
		//geo.getKernel().getApplication().setTooltipFlag();
		//se.setTitle(geo.getLongDescription());
		//geo.getKernel().getApplication().clearTooltipFlag();
		longTouchManager = LongTouchManager.getInstance();
	}

	/**
	 * Creates a new RadioButtonTreeItem for creating a brand new GeoElement or
	 * executing a new command which might not result in any GeoElement(s) ...
	 * no marble, no input GeoElement here. But this will be called from
	 * NewRadioButtonTreeItem(kernel), for there are many extras
	 */
	public RadioButtonTreeItem(Kernel kern) {
		super();

		// this method is still not able to show an editing box!
		newCreationMode = true;

		//geo = ge;
		kernel = kern;
		app = (AppW)kernel.getApplication();
		av = app.getAlgebraView();
		selection = app.getSelectionManager();
		this.setStyleName("elem");

		//setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		//add(radio);

		SpanElement se = DOM.createSpan().cast();
		updateNewStatic(se);

		ihtml = new InlineHTML();
		ihtml.addDoubleClickHandler(this);
		ihtml.addClickHandler(this);
		ihtml.addMouseMoveHandler(this);
		ihtml.addMouseDownHandler(this);
		ihtml.addMouseOverHandler(this);
		ihtml.addMouseOutHandler(this);
		ihtml.addTouchStartHandler(this);
		ihtml.addTouchMoveHandler(this);
		ihtml.addTouchEndHandler(this);
		add(ihtml);
		ihtml.getElement().appendChild(se);
		ihtml.getElement().addClassName("hasCursorPermanent");

		setCellVerticalAlignment(ihtml, HasVerticalAlignment.ALIGN_MIDDLE);
		setCellHorizontalAlignment(ihtml, HasHorizontalAlignment.ALIGN_LEFT);
		setCellWidth(ihtml, "100%");
		getElement().getStyle().setWidth(100, Style.Unit.PCT);

		// making room for the TitleBarPanel (top right of the AV)
		SpanElement se2 = DOM.createSpan().cast();
		se2.appendChild(Document.get().createTextNode(
		        "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"));
		ihtml.getElement().appendChild(se2);

		//String text = "";
		/*if (geo.isIndependent()) {
			geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				geo.addLabelTextOrHTML(
					geo.getDefinitionDescription(StringTemplate.defaultTemplate),getBuilder(se));
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				geo.addLabelTextOrHTML(
					geo.getCommandDescription(StringTemplate.defaultTemplate), getBuilder(se));
				break;
			}
		}*/
		// if enabled, render with LaTeX
		if (av.isRenderLaTeX()) {
			seNoLatex = se;
			this.needsUpdate = true;

			// here it complains that geo is undefined
			doUpdate();
			startEditing();
		} else {
			seNoLatex = se;
		}
		//FIXME: geo.getLongDescription() doesn't work
		//geo.getKernel().getApplication().setTooltipFlag();
		//se.setTitle(geo.getLongDescription());
		//geo.getKernel().getApplication().clearTooltipFlag();
		longTouchManager = LongTouchManager.getInstance();
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	public boolean popupSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	public boolean hideSuggestions() {
		return false;
	}

	/**
	 * Method to be overridden in NewRadioButtonTreeItem
	 */
	public boolean shuffleSuggestions(boolean down) {
		return false;
	}

	/**
	 * This method can be used to invoke a keydown event on MathQuillGGB, e.g.
	 * key=8,alt=false,ctrl=false,shift=false will trigger a Backspace event
	 * 
	 * @param key
	 *            keyCode of the event, which is the same as "event.which", used
	 *            at keydown
	 * @param alt
	 *            boolean
	 * @param ctrl
	 *            boolean
	 * @param shift
	 *            boolean
	 */
	public void keydown(int key, boolean alt, boolean ctrl, boolean shift) {
		if (av.isEditing() || isThisEdited() || newCreationMode) {
			geogebra.html5.main.DrawEquationWeb.triggerKeydown(seMayLatex, key,
			        alt, ctrl, shift);
		}
	}

	@Override
    public void handleLongTouch(int x, int y) {
		onRightClick(x, y);
	}

	public void repaint() {
		if(needsUpdate)
			doUpdate();
	}
	
	private void doUpdate() {
		// check for new LaTeX
		needsUpdate = false;
		boolean newLaTeX = false;
		
		if (av.isRenderLaTeX()
		        && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {
			String text = "";
			if (geo != null) {
				text = geo.getLaTeXAlgebraDescription(true,
				        StringTemplate.latexTemplateMQ);
				if ((text != null) && geo.isLaTeXDrawableGeo()
						&& (geo.isGeoList() ? !((GeoList) geo).isMatrix() : true)) {
					newLaTeX = true;
				}
			} else {
				newLaTeX = true;
			}
			// now we have text and how to display it (newLaTeX/LaTeX)
			if (LaTeX && newLaTeX) {
				text = DrawEquationWeb.inputLatexCosmetics(text);
				int tl = text.length();
				text = DrawEquationWeb.stripEqnArray(text);
				updateColor(seMayLatex);
				DrawEquationWeb.updateEquationMathQuillGGB("\\mathrm{"+text+"}", seMayLatex,
				        tl == text.length());
				updateColor(seMayLatex);
			} else if (newLaTeX) {
				SpanElement se = DOM.createSpan().cast();
				updateNewStatic(se);
				updateColor(se);
				ihtml.getElement().replaceChild(se, seNoLatex);
				text = DrawEquationWeb.inputLatexCosmetics(text);
				seMayLatex = se;
				if (newCreationMode) {
					// in editing mode, we shall avoid letting an invisible, but
					// harmful element!
					DrawEquationWeb.drawEquationAlgebraView(seMayLatex, "");
				} else {
					DrawEquationWeb.drawEquationAlgebraView(seMayLatex,
					        "\\mathrm {" + text + "}");
				}
				LaTeX = true;
			}
		} else if (geo == null) {
			newLaTeX = true;
		}
		// check for new text
		if (!newLaTeX) {
			if (geo.isIndependent()) {
				geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(seNoLatex));
			} else {
				switch (kernel.getAlgebraStyle()) {
				case Kernel.ALGEBRA_STYLE_VALUE:
					 geo.getAlgebraDescriptionTextOrHTMLDefault(getBuilder(seNoLatex));
					break;

				case Kernel.ALGEBRA_STYLE_DEFINITION:
					geo
					        .addLabelTextOrHTML(geo
					                .getDefinitionDescription(StringTemplate.defaultTemplate),getBuilder(seNoLatex));
					break;

				case Kernel.ALGEBRA_STYLE_COMMAND:
					geo
					        .addLabelTextOrHTML(geo
					                .getCommandDescription(StringTemplate.defaultTemplate),getBuilder(seNoLatex));
					break;
				}
			}
			// now we have text and how to display it (newLaTeX/LaTeX)
			if (!LaTeX) {
				updateColor(seNoLatex);
			}  else {
				SpanElement se = DOM.createSpan().cast();
				updateNewStatic(se);
				updateColor(se);
				ihtml.getElement().replaceChild(se, seMayLatex);
				seNoLatex = se;
				LaTeX = false;
			}
		}

		if (geo != null && radio != null) {
			radio.setChecked(geo.isEuclidianVisible());
		}
	}

	private void updateNewStatic(SpanElement se) {
		se.getStyle().setProperty("display", "-moz-inline-box");
		se.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		se.setDir("ltr");
	}

	private void updateColor(SpanElement se){
		if (geo != null) {
			se.getStyle().setColor(GColor.getColorString(geo.getAlgebraColor()));
		}
	}

	public boolean isThisEdited() {
		return thisIsEdited;
	}

	public void cancelEditing() {
		// as this method is only called from AlgebraViewWeb.update,
		// and in that context, this should not cancel editing in case of newCreationMode,
		// we can put an if check here safely for the present time
		if (!newCreationMode) {
			if (LaTeX) {
				DrawEquationWeb.endEditingEquationMathQuillGGB(this, seMayLatex);
			} else {
				remove(tb);
				add(ihtml);
				stopEditingSimple(tb.getText());
			}
		}
	}

	public void startEditing() {
		thisIsEdited = true;
		if (newCreationMode) {
			geogebra.html5.main.DrawEquationWeb.editEquationMathQuillGGB(this,
			        seMayLatex, true);
		} else if (LaTeX && !(geo.isGeoVector() && geo.isIndependent())) {
			geogebra.html5.main.DrawEquationWeb.editEquationMathQuillGGB(this,
			        seMayLatex, false);
		} else {
			remove(ihtml);
			tb = new GTextBox();
			tb.setText( geo.getAlgebraDescriptionDefault() );
			add(tb);
			mout = false;
			tb.setFocus(true);
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				@Override
                public void execute() {
					tb.setFocus(true);
				}
			});
			tb.addKeyDownHandler(new KeyDownHandler() {
				@Override
                public void onKeyDown(KeyDownEvent kevent) {
					if (kevent.getNativeKeyCode() == 13) {
						remove(tb);
						add(ihtml);
						stopEditingSimple(tb.getText());
					} else if (kevent.getNativeKeyCode() == 27) {
						remove(tb);
						add(ihtml);
						stopEditingSimple(null);
					}
				}
			});
			tb.addBlurHandler(new BlurHandler() {
				@Override
                public void onBlur(BlurEvent bevent) {
					if (mout) {
						remove(tb);
						add(ihtml);
						stopEditingSimple(null);
					}
				}
			});
			tb.addMouseOverHandler(new MouseOverHandler() {
				@Override
                public void onMouseOver(MouseOverEvent moevent) {
					mout = false;
				}
			});
			tb.addMouseOutHandler(new MouseOutHandler() {
				@Override
                public void onMouseOut(MouseOutEvent moevent) {
					mout = true;
					tb.setFocus(true);
				}
			});
		}
	}

	public void stopEditingSimple(String newValue) {

		thisIsEdited = false;
		av.cancelEditing();

		if (newValue != null) {
			if (geo != null) {
				boolean redefine = !geo.isPointOnPath();
				GeoElement geo2 = kernel.getAlgebraProcessor().changeGeoElement(
						geo, newValue, redefine, true);
				if (geo2 != null)
					geo = geo2;
			} else {
				// TODO create new GeoElement
			}
		}

		// maybe it's possible to enter something which is LaTeX
		// note: this should be OK for independent GeoVectors too
		doUpdate();
	}

	@Override
    public void stopEditing(String newValue0) {

		thisIsEdited = false;
		av.cancelEditing();
		
		if (newValue0 != null) {

			// String newValue = newValue0.replace("space *", " ");
			// newValue = newValue.replace("* space", " ");

			// newValue = newValue.replace("space*", " ");
			// newValue = newValue.replace("*space", " ");

			String newValue = newValue0.replace("space ", " ");
			newValue = newValue.replace(" space", " ");
			newValue = newValue.replace("space", " ");

			// Formula Hacks ... Currently only functions are considered
			StringBuilder sb = new StringBuilder();
			boolean switchw = false;
			//ignore first and last bracket, they come from mathrm
			int skip = newValue.startsWith("(") ? 1 : 0;
			boolean inLHS = true;
			for (int i = skip; i < newValue.length() - skip; i++){
				//on lhs a*b(x) actually means ab(x)
				// fixed in a different way, and considered harmful now!
				// if(inLHS && (newValue.charAt(i)=='*')){
				// continue;
				// }
				if (newValue.charAt(i) != ' ') {
					if (newValue.charAt(i) != '|')
						sb.append(newValue.charAt(i));
					else  {
						switchw = !switchw;
						sb.append(switchw ? "abs(" : ")");
					}
				}
				if(newValue.charAt(i) == ':' || newValue.charAt(i) == '='){
					inLHS = false;
				}
			}
			// Formula Hacks ended.
			if (geo != null) {
				boolean redefine = !geo.isPointOnPath();
				GeoElement geo2 = kernel.getAlgebraProcessor().changeGeoElement(
						geo, sb.toString(), redefine, true);
				if (geo2 != null)
					geo = geo2;
			} else {
				// TODO: create new GeoElement!
				
			}
		}

		// maybe it's possible to enter something which is non-LaTeX
		doUpdate();
	}

	/**
	 * Stop new formula creation Much of this code is copied from
	 * AlgebraInputW.onKeyUp
	 * 
	 * @param newValue0
	 * @return boolean whether it was successful
	 */
	public boolean stopNewFormulaCreation(String newValue0) {

		// TODO: move to NewRadioButtonTreeItem? Wouldn't help much...

		String newValue = newValue0;

		if (newValue0 != null) {

			// newValue = newValue0.replace("space *", " ");
			// newValue = newValue.replace("* space", " ");

			// newValue = newValue.replace("space*", " ");
			// newValue = newValue.replace("*space", " ");

			newValue = newValue.replace("space ", " ");
			newValue = newValue.replace(" space", " ");
			newValue = newValue.replace("space", " ");

			// Formula Hacks ... Currently only functions are considered
			StringBuilder sb = new StringBuilder();
			boolean switchw = false;
			// ignore first and last bracket, they come from mathrm
			int skip = newValue.startsWith("(") ? 1 : 0;
			boolean inLHS = true;
			for (int i = skip; i < newValue.length() - skip; i++) {
				// on lhs a*b(x) actually means ab(x)
				// fixed in a different way, and considered harmful now!
				// if (inLHS && (newValue.charAt(i) == '*')) {
				// continue;
				// }
				if (newValue.charAt(i) != ' ') {
					if (newValue.charAt(i) != '|')
						sb.append(newValue.charAt(i));
					else {
						switchw = !switchw;
						sb.append(switchw ? "abs(" : ")");
					}
				}
				if (newValue.charAt(i) == ':' || newValue.charAt(i) == '=') {
					inLHS = false;
				}
			}
			// Formula Hacks ended.
		}

		app.getKernel().clearJustCreatedGeosInViews();
		final String input = newValue;
		if (input == null || input.length() == 0) {
			app.getActiveEuclidianView().requestFocusInWindow(); // Michael
			                                                     // Borcherds
			                                                     // 2008-05-12
			return false;
		}

		app.setScrollToShow(true);

		try {
			AsyncOperation callback = new AsyncOperation() {

				@Override
				public void callback(Object obj) {

					if (!(obj instanceof GeoElement[])) {
						// inputField.getTextBox().setFocus(true);
						setFocus(true);
						return;
					}
					GeoElement[] geos = (GeoElement[]) obj;

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet) {
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

					// create texts in the middle of the visible view
					// we must check that size of geos is not 0 (ZoomIn,
					// ZoomOut, ...)
					if (geos.length > 0 && geos[0] != null
					        && geos[0].isGeoText()) {
						GeoText text = (GeoText) geos[0];
						if (!text.isTextCommand()
						        && text.getStartPoint() == null) {

							Construction cons = text.getConstruction();
							EuclidianViewInterfaceCommon ev = app
							        .getActiveEuclidianView();

							boolean oldSuppressLabelsStatus = cons
							        .isSuppressLabelsActive();
							cons.setSuppressLabelCreation(true);
							GeoPoint p = new GeoPoint(text.getConstruction(),
							        null, (ev.getXmin() + ev.getXmax()) / 2,
							        (ev.getYmin() + ev.getYmax()) / 2, 1.0);
							cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

							try {
								text.setStartPoint(p);
								text.update();
							} catch (CircularDefinitionException e1) {
								e1.printStackTrace();
							}
						}
					}

					app.setScrollToShow(false);

					// inputField.addToHistory(input); // that is not relevant
					// here
					// inputField.setText(null); // that comes after boolean
					// return true

					// inputField.setIsSuggestionJustHappened(false); // that is
					// not relevant here
				}

			};

			app.getKernel()
			        .getAlgebraProcessor()
			        .processAlgebraCommandNoExceptionHandling(input, true,
			                false, true, true, callback);

		} catch (Exception ee) {
			// TODO: better exception handling
			// GOptionPaneW.setCaller(inputField.getTextBox());// we have no
			// good FocusWidget
			// app.showError(ee, inputField);
			app.showError(ee.getMessage());// we use what we have
			return false;
		} catch (MyError ee) {
			// TODO: better error handling
			// GOptionPaneW.setCaller(inputField.getTextBox());// we have no
			// good FocusWidget
			// inputField.showError(ee);
			app.showError(ee);// we use what we have
			return false;
		}
		return true;
	}

	@Override
    public void onDoubleClick(DoubleClickEvent evt) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		if (av.isEditing() || isThisEdited() || newCreationMode)
			return;

		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		selection.clearSelectedGeos();
		ev.resetMode();
		if (geo != null && !evt.isControlKeyDown()) {
			av.startEditing(geo, evt.isShiftKeyDown());
		}
	}

	@Override
    public void onMouseDown(MouseDownEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event, ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		event.preventDefault();
		event.stopPropagation();
	}

	@Override
    public void onClick(ClickEvent evt) {
		if (app.isPrerelease()) {
			app.showKeyboard(this);
		}

		if (newCreationMode) {
			setFocus(true);
		}
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt, ZeroOffset.instance);
		onPointerUp(wrappedEvent);
	}

	@Override
    public void onMouseMove(MouseMoveEvent evt) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt, ZeroOffset.instance);
		onPointerMove(wrappedEvent);
	}

	
	@Override
    public void onMouseOver(MouseOverEvent event) {
		if (geo != null) {
			ToolTipManagerW.sharedInstance().showToolTip(geo.getLongDescriptionHTML(true, true));
		}
	}

	@Override
    public void onMouseOut(MouseOutEvent event) {
		ToolTipManagerW.sharedInstance().showToolTip(null);	    
    }
	
	@Override
    public GeoElement getGeo() {
	    return geo;
    }

	@Override
    public void onTouchEnd(TouchEndEvent event) {
		if (newCreationMode) {
			setFocus(true);
		}
	    longTouchManager.cancelTimer();
	    JsArray<Touch> changed = event.getChangedTouches();
	    AbstractEvent wrappedEvent = PointerEvent.wrapEvent(changed.get(0), ZeroOffset.instance);
	    onPointerUp(wrappedEvent);
	    CancelEventTimer.touchEventOccured();
    }

	@Override
    public void onTouchMove(TouchMoveEvent event) {
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		longTouchManager.rescheduleTimerIfRunning(this, x, y);
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(targets.get(0), ZeroOffset.instance);
		onPointerMove(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	@Override
    public void onTouchStart(TouchStartEvent event) {
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		longTouchManager.scheduleTimer(this, x, y);
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(targets.get(0), ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		CancelEventTimer.touchEventOccured();
    }
	
	private void onPointerDown(AbstractEvent event) {
		if (event.isRightClick()) {
			onRightClick(event.getX(), event.getY());
		}
	}
	
	private void onPointerUp(AbstractEvent event) {
		if (av.isEditing() || isThisEdited() || newCreationMode) {
			return;
		}
		int mode = app.getActiveEuclidianView().getMode();
		if (//!skipSelection && 
			(mode == EuclidianConstants.MODE_MOVE) ) {
			// update selection	
			if (geo == null) {
				selection.clearSelectedGeos();
			}
			else {					
				// handle selecting geo
				if (event.isControlDown()) {
					selection.toggleSelectedGeo(geo); 													
					if (selection.getSelectedGeos().contains(geo)) {
						av.setLastSelectedGeo(geo);
					}
				} else if (event.isShiftDown() && av.getLastSelectedGeo() != null) {
					boolean nowSelecting = true;
					boolean selecting = false;
					boolean aux = geo.isAuxiliaryObject();
					boolean ind = geo.isIndependent();
					boolean aux2 = av.getLastSelectedGeo().isAuxiliaryObject();
					boolean ind2 = av.getLastSelectedGeo().isIndependent();

					if ((aux == aux2 && aux) || (aux == aux2 && ind == ind2)) {
						Iterator<GeoElement> it = kernel.getConstruction().getGeoSetLabelOrder().iterator();
						boolean direction = geo.getLabel(StringTemplate.defaultTemplate).
								compareTo(av.getLastSelectedGeo().getLabel(StringTemplate.defaultTemplate)) < 0;

						while (it.hasNext()) {
							GeoElement geo2 = it.next();
							if ((geo2.isAuxiliaryObject() == aux && aux)
									|| (geo2.isAuxiliaryObject() == aux && geo2.isIndependent() == ind)) {

								if (direction && geo2.equals(av.getLastSelectedGeo())) selecting = !selecting;
								if (!direction && geo2.equals(geo)) selecting = !selecting;

								if (selecting) {
									selection.toggleSelectedGeo(geo2);
									nowSelecting = selection.getSelectedGeos().contains(geo2);
								}
								if (!direction && geo2.equals(av.getLastSelectedGeo())) selecting = !selecting;
								if (direction && geo2.equals(geo)) selecting = !selecting;
							}
						}
					}

					if (nowSelecting) {
						selection.addSelectedGeo(geo); 
						av.setLastSelectedGeo(geo);
					} else {
						selection.removeSelectedGeo(av.getLastSelectedGeo());
						av.setLastSelectedGeo(null);
					}
				} else {							
					selection.clearSelectedGeos(false); //repaint will be done next step
					selection.addSelectedGeo(geo);
					av.setLastSelectedGeo(geo);
				}
			}
		} 
		else if (mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			if (geo != null) {
				app.getActiveEuclidianView().clickedGeo(geo, event.isControlDown());
			}
			//event.release();
		} else 
			// tell selection listener about click
			if (geo != null) {
				app.geoElementSelected(geo, false);
			}


		// Alt click: copy definition to input field
		if (geo != null && event.isAltDown() && app.showAlgebraInput()) {
			// F3 key: copy definition to input bar
			app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);			
		}

		app.getActiveEuclidianView().mouseMovedOver(null);

		// this should not give the focus to AV instead of the current formula!
		// except if we are not in editing mode! That's why better condition was
		// needed at the beginning of this method!
		av.setFocus(true);
	}
	
	private void onPointerMove(AbstractEvent event) {
		if (av.isEditing() || isThisEdited() || newCreationMode)
			return;

		// tell EuclidianView to handle mouse over
		EuclidianViewInterfaceCommon ev = kernel.getApplication().getActiveEuclidianView();
		if (geo != null) {
			ev.mouseMovedOver(geo);
		}

		// highlight the geos
		//getElement().getStyle().setBackgroundColor("rgb(200,200,245)");
			
		// implemented by HTML title attribute on the label
		//FIXME: geo.getLongDescription() doesn't work
		//if (geo != null) {
		//	geo.getKernel().getApplication().setTooltipFlag();
		//	se.setTitle(geo.getLongDescription());
		//	geo.getKernel().getApplication().clearTooltipFlag();
		//} else {
		//	se.setTitle("");
		//}
	}
	
	private void onRightClick(int x, int y) {
		if (av.isEditing() || isThisEdited() || newCreationMode)
			return;

		SelectionManager selection = app.getSelectionManager();
		GPoint point = new GPoint(x + Window.getScrollLeft(), y
		        + Window.getScrollTop());
		if (geo != null) {
			if (selection.containsSelectedGeo(geo)) {// popup
			                                     // menu for
			                                     // current
			                                     // selection
			                                     // (including
			                                     // selected
			                                     // object)
				((GuiManagerW) app.getGuiManager()).showPopupMenu(
						selection.getSelectedGeos(), av, point);
			} else {// select only this object and popup menu
				selection.clearSelectedGeos(false);
				selection.addSelectedGeo(geo, true, true);
				ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
				temp.add(geo);

				((GuiManagerW) app.getGuiManager()).showPopupMenu(temp, av, point);
			}
		}
	}

	/**
	 * As adding focus handlers to JavaScript code would be too complex, let's
	 * do it even before they actually get focus, i.e. make a method that
	 * triggers focus, and then override it if necessary
	 * 
	 * @param b
	 *            focus (false: blur)
	 */
	public void setFocus(boolean b) {
		geogebra.html5.main.DrawEquationWeb.focusEquationMathQuillGGB(
		        seMayLatex, b);
	}
}
