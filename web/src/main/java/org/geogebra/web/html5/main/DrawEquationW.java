package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.gui.view.algebra.GeoContainer;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Insets;
import com.himamis.retex.renderer.web.DrawingFinishedCallback;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.graphics.ColorW;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

public class DrawEquationW extends DrawEquation {

	static boolean scriptloaded = false;
 
	private static GeoContainer currentWidget;

	private static Element currentElement;

	private static Element currentHover;

	private static Object initJLaTeXMath = null;

	public DrawEquationW() {
	}

	/**
	 * Draws an equation on the algebra view in display mode (not editing).
	 * Color is supposed to be handled in outer span element.
	 * 
	 * @param parentElement
	 *            adds the equation as the child of this element
	 * @param latexString
	 *            the equation in LaTeX
	 */
	public static void drawEquationAlgebraView(Widget w, String latexString,
			boolean nonGeneral) {
		drawEquationAlgebraView(w.getElement(), latexString, nonGeneral);
	}

	public static void drawEquationAlgebraView(Element parentElement,
			String latexString, boolean nonGeneral) {
		// no scriptloaded check yet (is it necessary?)

		// logging takes too much time
		// App.debug("Algebra View: "+eqstring);

		DivElement ih = DOM.createDiv().cast();
		ih.getStyle().setPosition(Style.Position.RELATIVE);
		ih.setDir("ltr");

		int el = latexString.length();
		String eqstring = stripEqnArray(latexString);
		drawEquationMathQuillGGB(ih, eqstring, 0, 0, parentElement, true,
		        el == eqstring.length(), true, 0, nonGeneral);
	}
	
	public static TeXIcon xcreateIcon(String latex, int size,
			int texIconStyle) {

		TeXFormula formula = null;
		try {
			formula = new TeXFormula(latex);
		} catch (Throwable t) {
			String[] msg = t.getMessage().split("\\n");
			formula = new TeXFormula("\\text{" + msg[msg.length - 1] + "}");
		}

		TeXIcon icon = null;
		try {
			icon = formula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setType(texIconStyle)
					.setSize(size).build();

			icon.setInsets(new Insets(5, 5, 5, 5));
			return icon;
		} catch (Exception e) {
			formula = new TeXFormula("\\text{Invalid LaTeX syntax.}");
			icon = formula.new TeXIconBuilder()
					.setStyle(TeXConstants.STYLE_DISPLAY).setType(texIconStyle)
					.setSize(size).build();

			icon.setInsets(new Insets(5, 5, 5, 5));
		}
		return icon;
	}

	@Override
	public GDimension drawEquation(App app1, GeoElementND geo,
			final GGraphics2D g2,
	        int x, int y, String latexString0, GFont font, boolean serif,
	        final GColor fgColor, GColor bgColor, boolean useCache,
			boolean updateAgain, final Runnable callback) {

			String eqstring = latexString0;

		TeXIcon icon = createIcon(eqstring, convertColor(fgColor), font,
				font.getLaTeXStyle(serif),
				null, null, app1);
			Graphics2DW g3 = new Graphics2DW(((GGraphics2DW) g2).getContext());
			g3.setDrawingFinishedCallback(new DrawingFinishedCallback() {

				public void onDrawingFinished() {
					((GGraphics2DW) g2).updateCanvasColor();
					if (callback != null) {
						callback.run();
					}

				}
			});
			icon.paintIcon(new HasForegroundColor() {
				@Override
				public Color getForegroundColor() {
					return FactoryProvider.INSTANCE.getGraphicsFactory()
							.createColor(fgColor.getRed(), fgColor.getGreen(),
									fgColor.getBlue());
				}
			}, g3, x, y);
			((GGraphics2DW) g2).updateCanvasColor();
			g3.maybeNotifyDrawingFinishedCallback();
			return new GDimensionW(icon.getIconWidth(), icon.getIconHeight());

	}

	private static void ensureJLMFactoryExists() {
		if (FactoryProvider.INSTANCE == null) {
			FactoryProvider.INSTANCE = new FactoryProviderGWT();
		}
	}

	/**
	 * The JavaScript/$ggbQuery bit of drawing an equation with MathQuillGGB
	 * More could go into GWT, but it was easier with JSNI
	 * 
	 * @param el
	 *            the element which should be drawn
	 * @param htmlt
	 *            the equation
	 * @param parentElement
	 *            parent of el
	 * @param addOverlay
	 *            true to add an overlay div
	 * @param noEqnArray
	 *            true = normal LaTeX, false = LaTeX with \begin{eqnarray} in
	 *            the beginning
	 */
	public static native void drawEquationMathQuillGGB(Element el,
	        String htmlt, int fontSize, int fontSizeRel, Element parentElement,
	        boolean addOverlay, boolean noEqnArray, boolean visible,
	        double rotateDegree, boolean nonav) /*-{

		el.style.cursor = "default";
		if (typeof el.style.MozUserSelect != "undefined") {
			el.style.MozUserSelect = "-moz-none";
		} else if (typeof el.style.webkitUserSelect != "undefined") {
			el.style.webkitUserSelect = "none";
		} else if (typeof el.style.khtmlUserSelect != "undefined") {
			el.style.khtmlUserSelect = "none";
		} else if (typeof el.style.oUserSelect != "undefined") {
			el.style.oUserSelect = "none";
		} else if (typeof el.style.userSelect != "undefined") {
			el.style.userSelect = "none";
		} else if (typeof el.onselectstart != "undefined") {
			el.onselectstart = function(event) {
				return false;
			}
			if (nonav) {
				el.ondragstart = function(event) {
					return false;
				}
			}
		}
		if (nonav) {
			el.onmousedown = function(event) {
				if (event.preventDefault)
					event.preventDefault();
				return false;
			}
		}
		if (addOverlay) {
			var elfirst = $doc.createElement("div");
			el.appendChild(elfirst);
		}

		var elSecond = $doc.createElement("div");

		if (addOverlay) {
			var elthirdBefore = $doc.createElement("span");
			elthirdBefore.style.position = "absolute";
			elthirdBefore.style.zIndex = 2;
			elthirdBefore.style.top = "0px";
			elthirdBefore.style.bottom = "0px";
			elthirdBefore.style.left = "0px";
			elthirdBefore.style.right = "0px";
			elSecond.appendChild(elthirdBefore);
		}

		var elSecondInside = $doc.createElement("span");
		elSecondInside.innerHTML = htmlt;

		if (fontSizeRel != 0) {
			elSecond.style.fontSize = fontSizeRel + "px";
		}
		elSecond.appendChild(elSecondInside);
		el.appendChild(elSecond);

		if (!visible) {
			el.style.visibility = "hidden";
		}

		parentElement.appendChild(el);

		if (noEqnArray) {
			$wnd.$ggbQuery(elSecondInside).mathquillggb();

			// Make sure the length of brackets and square roots are OK
			elSecondInside.timeoutId = $wnd.setTimeout(function() {
				$wnd.$ggbQuery(elSecondInside).mathquillggb('latex', htmlt);
			}, 500);

			// it's not ok for IE8, but it's good for ie9 and above
			//$doc.addEventListener('readystatechange', function() {
			//	if ($doc.readyState === 'complete' ||
			//		$doc.readyState === 'loaded') {
			//		$wnd.$ggbQuery(elSecond).mathquillggb('latex', htmlt);
			//	}
			//}, false);
		} else {
			$wnd.$ggbQuery(elSecondInside).mathquillggb('eqnarray');

			// Make sure the length of brackets and square roots are OK
			//			$wnd.setTimeout(function() {
			//				// TODO: this needs more testing,
			//				// also for the editing of it
			//				//$wnd.$ggbQuery(elSecond).mathquillggb('latex', htmlt);
			//				$wnd.$ggbQuery(elSecond).mathquillggb('eqnarray');
			//			});
		}

		if ((fontSize != 0) && (fontSizeRel != 0) && (fontSize != fontSizeRel)) {
			// floating point division in JavaScript!
			var sfactor = "scale(" + (fontSize / fontSizeRel) + ")";

			elSecond.style.transform = sfactor;
			elSecond.style.MozTransform = sfactor;
			elSecond.style.MsTransform = sfactor;
			elSecond.style.OTransform = sfactor;
			elSecond.style.WebkitTransform = sfactor;

			elSecond.style.transformOrigin = "0px 0px";
			elSecond.style.MozTransformOrigin = "0px 0px";
			elSecond.style.MsTransformOrigin = "0px 0px";
			elSecond.style.OTransformOrigin = "0px 0px";
			elSecond.style.WebkitTransformOrigin = "0px 0px";
		}

		if (rotateDegree != 0) {
			var rfactor = "rotate(-" + rotateDegree + "deg)";

			elSecondInside.style.transform = rfactor;
			elSecondInside.style.MozTransform = rfactor;
			elSecondInside.style.MsTransform = rfactor;
			elSecondInside.style.OTransform = rfactor;
			elSecondInside.style.WebkitTransform = rfactor;

			elSecondInside.style.transformOrigin = "center center";
			elSecondInside.style.MozTransformOrigin = "center center";
			elSecondInside.style.MsTransformOrigin = "center center";
			elSecondInside.style.OTransformOrigin = "center center";
			elSecondInside.style.WebkitTransformOrigin = "center center";

			if (addOverlay) {
				elthirdBefore.style.transform = rfactor;
				elthirdBefore.style.MozTransform = rfactor;
				elthirdBefore.style.MsTransform = rfactor;
				elthirdBefore.style.OTransform = rfactor;
				elthirdBefore.style.WebkitTransform = rfactor;

				elthirdBefore.style.transformOrigin = "center center";
				elthirdBefore.style.MozTransformOrigin = "center center";
				elthirdBefore.style.MsTransformOrigin = "center center";
				elthirdBefore.style.OTransformOrigin = "center center";
				elthirdBefore.style.WebkitTransformOrigin = "center center";
			}
		}

	}-*/;

	public static native Element getCurrentMouseHover() /*-{
		var highestHover = null;
		if (!$wnd.$ggbQuery) {
			return null;
		}
		var setHighestHover = function(elm) {
			if (elm.tagName !== 'IMG') {
				highestHover = elm;
			} else if (elm.className) {
				if (elm.className.indexOf('gwt-Image') < 0) {
					// okay, not our case
					highestHover = elm;
				} else if (elm.parentNode) {
					// if it does not have parent node,
					// then it should not be highest hover either!
					if (elm.parentNode.className) {
						if (elm.parentNode.className
								.indexOf('XButtonNeighbour') < 0) {
							// we're just doing bugfixing for this specific case now
							// but similar bugs may be fixed in a similar way
							highestHover = elm;
						}
					} else {
						highestHover = elm;
					}
				}
			} else {
				// has no className, okay
				highestHover = elm;
			}
		}
		$wnd.$ggbQuery(':hover').each(function(idx, elm) {
			if (elm) {
				if ($wnd.$ggbQuery(elm).is(':visible')) {
					// CSS display:none shall be excluded, of course!

					// ... is this the best way of avoiding it?
					// on the net, they write that :visible check might
					// not always give the best results, e.g. Chrome,
					// ... we only need this in Internet Explorer, so
					// might be Okay... if not, then a
					// if ($wnd.$ggbQuery(elm).parents(':visible').length)
					// check would also be needed...

					if (highestHover) {
						if ($wnd.$ggbQuery.contains(highestHover, elm)) {
							setHighestHover(elm);
						}
					} else {
						if ($wnd.$ggbQuery.contains($doc.body, elm)) {
							setHighestHover(elm);
						}
					}
				}
			}
		});
		return highestHover;
	}-*/;

	public static void escEditingHoverTapWhenElsewhere(NativeEvent natEv,
			boolean isTouch) {
		// At first, update currentHover which shall be done anyway!
		// once it's updated, it can be used cross mobile/web, and
		// currentHover can be used later in other code...
		if (isTouch) {
			// heuristic for hovering
			Element targ = null;
			if ((natEv.getChangedTouches() != null) &&
				(natEv.getChangedTouches().length() > 0) &&
				(natEv.getChangedTouches().get(0) != null)) {
				// in theory, getTarget is an Element,
				// but if it is not, then we don't want to esc editing
				JavaScriptObject obj = natEv.getChangedTouches().get(0).getTarget();
				if (Element.is(obj)) {
					targ = Element.as(obj);
				}
			}
			if (targ != null) {
				currentHover = targ;
			} else {
				currentHover = null;
				return;
			}
		} else {
			// not being sure if natEv.currentEventTarget would return the
			// right thing in case of event capturing, so instead, trying
			// to detect the "last" hovered element in some way by jQuery
			Element el = getCurrentMouseHover();
			if (el != null) {
				currentHover = el;
			} else {
				currentHover = null;
				return;
			}
		}

		// Secondly, if currentWidget is not null, do the escEditing action!
		if (currentWidget != null) {
			// cases that do not escape editing:
			if (targetHasFeature(currentWidget.getElement(),
					"MouseDownDoesntExitEditingFeature", true)) {
				// 1. the widget itself... currentWidget.getElement()
				// 2. any KeyboardButton "MouseDownDoesntExitEditingFeature"
				// 3. any AV helper icon "MouseDownDoesntExitEditingFeature"
				return;
			}

			// in this case, escape
			DrawEquationW.escEditingEquationMathQuillGGB(currentWidget,
					currentElement);
			// the above method will do these too
			// currentWidget = null;
			// currentElement = null;
		}
	}

	public static boolean targetHasFeature(Element el, String pure, boolean such) {
		if (currentHover == null) {
			// possible place for debugging
			return such;
		}
		return targetHasFeature(currentHover, el, pure);
	}

	/**
	 * If mouse is currently over Element el, OR mouse is currently over an
	 * element with CSS class pure, e.g. "MouseDownDoesntExitEditingFeature" in
	 * theory, this method shall only be called from mobile browsers
	 * 
	 * @param el
	 * @param pure
	 * @return
	 */
	public static native boolean targetHasFeature(Element targ,
			Element el, String pure) /*-{

		if ((targ === null) || (targ === undefined)) {
			return false;
		}

		var jqo = null;
		if (el) {
			jqo = $wnd.$ggbQuery(targ);
			if (jqo.is(el)) {
				return true;
			} else if (jqo.parents().is(el)) {
				return true;
			}
		}
		if (pure) {
			if (jqo === null) {
				jqo = $wnd.$ggbQuery(targ);
			}
			if (jqo.is("." + pure)) {
				return true;
			} else if (jqo.parents().is("." + pure)) {
				return true;
			}
		}

		// no CSS class provided, or it is empty, mouse is over nothing significant
		return false;
	}-*/;

	public static void escEditing() {
		if (currentWidget != null) {
			DrawEquationW.escEditingEquationMathQuillGGB(currentWidget,
			        currentElement);
			// the above method will do these too
			// currentWidget = null;
			// currentElement = null;
		}
	}

	/**
	 * Only sets currentWidget if we are not in newCreationMode, to avoid
	 * closing newCreationMode when we should not also good not to confuse
	 * things in GeoGebraFrame
	 * 
	 * @param rbti
	 * @param parentElement
	 */
	private static void setCurrentWidget(GeoContainer rbti,
	        Element parentElement) {
		if (currentWidget != rbti) {
			DrawEquationW.escEditing();
		}
		currentWidget = rbti;
		currentElement = parentElement;
	}

	/**
	 * In case we're in (editing) newCreationMode, then this method shall decide
	 * whether to show the autocomplete suggestions or hide them...
	 */
	public static native void showOrHideSuggestions(GeoContainer rbti,
			Element parentElement) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var querr = elSecond.lastChild;

		if (querr.GeoGebraSuggestionPopupCanShow !== undefined) {
			// when the suggestions should pop up, we make them pop up,
			// when not, there may be two possibilities: we should hide the old,
			// or we should not hide the old... e.g. up/down arrows should not hide...
			// is there any other case? (up/down will unset later here)
			if (querr.GeoGebraSuggestionPopupCanShow === true) {
				@org.geogebra.web.html5.main.DrawEquationW::popupSuggestions(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;)(rbti);
			} else {
				@org.geogebra.web.html5.main.DrawEquationW::hideSuggestions(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;)(rbti);
			}
		}
	}-*/;

	/**
	 * Edits a MathQuillGGB equation which was created by
	 * drawEquationMathQuillGGB
	 * 
	 * @param rbti
	 *            the tree item for callback
	 * @param parentElement
	 *            the same element as in drawEquationMathQuillGGB
	 * @param newCreationMode
	 */
	public static void editEquationMathQuillGGB(GeoContainer rbti, Widget w,
			boolean newCreationMode) {
		editEquationMathQuillGGB(rbti, w.getElement(), newCreationMode);
	}

	public static native void editEquationMathQuillGGB(GeoContainer rbti,
			Element parentElement, boolean newCreationMode) /*-{

		var DrawEquation = @org.geogebra.web.html5.main.DrawEquationW::getNonStaticCopy(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;)(rbti);

		var elfirst = parentElement.firstChild.firstChild || parentElement;

		
		var elSecond = elfirst.nextSibling;

		var elSecondInside = elSecond.lastChild;
		// if we go to editing mode, this timer is not relevant any more,
		// and also harmful in case it runs after editing mode is set
		if (elSecondInside.timeoutId) {
			$wnd.clearTimeout(elSecondInside.timeoutId);
		}
		if (elSecondInside.timeoutId2) {
			$wnd.clearTimeout(elSecondInside.timeoutId2);
		}

		$wnd.$ggbQuery(elSecondInside).mathquillggb('revert').mathquillggb(
				'editable').focus();

		if (newCreationMode) {
			if (elSecondInside.keyDownEventListenerAdded) {
				// event listeners are already added
				return;
			}
		} else {
			@org.geogebra.web.html5.main.DrawEquationW::setCurrentWidget(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
		}

		$wnd
				.$ggbQuery(elSecondInside)
				.keyup(
						function(event) {
							// in theory, the textarea inside elSecondInside will be
							// selected, and it will capture other key events before
							// these execute

							// Note that for keys like Hungarian U+00F3 or &oacute;
							// both event.keyCode and event.which, as well as event.charCode
							// will be zero, that's why we should not leave the default
							// value of "var code" at 13, but it should be 0 instead
							// never mind, as this method only does something for 13 and 27
							// otherwise, MathQuill still listens to keypress which will
							// capture the &oacute;
							var code = event.keyCode || event.which || 0;

							if (event.keyCode == 0) {
								var textarea = $wnd.$ggbQuery(elSecondInside)
										.find('textarea');
								if (textarea.attr("disabled")) {
									code = textarea[0].lastPressCode || 0;
									textarea[0].lastPressCode = 0;
								}
							}
							if (code == 13) {//enter
								if (newCreationMode) {
									@org.geogebra.web.html5.main.DrawEquationW::newFormulaCreatedMathQuillGGB(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								} else {
									@org.geogebra.web.html5.main.DrawEquationW::endEditingEquationMathQuillGGB(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								}
							} else if (code == 27) {//esc
								if (newCreationMode) {
									@org.geogebra.web.html5.main.DrawEquationW::stornoFormulaMathQuillGGB(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								} else {
									@org.geogebra.web.html5.main.DrawEquationW::escEditingEquationMathQuillGGB(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								}
							} else {
								if ((code == 8) || (code == 32) || (code == 9)) { // backspace
									rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::typing(Z)(false);
								} else {
									rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::typing(Z)(true);
								}
								// it would be counterproductive to call autoScroll and history popup
								// after the editing/new formula creation ends! so put their code here

								// we should also auto-scroll the cursor in the formula!
								// but still better to put this in keypress later,
								// just it should be assigned in the bubbling phase of keypress
								// after MathQuillGGB has executed its own code, just it is not easy...
								@org.geogebra.web.html5.main.DrawEquationW::scrollCursorIntoView(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;Z)(rbti,parentElement,newCreationMode);

								if (newCreationMode) {
									// the same method can be called from the on-screen keyboard!
									@org.geogebra.web.html5.main.DrawEquationW::showOrHideSuggestions(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;)(rbti,parentElement);
								}
							}

							event.stopPropagation();
							event.preventDefault();
							return false;
						})
				.keypress(
						function(event2) {
							rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onKeyPress(Ljava/lang/String;)(String.fromCharCode(event2.charCode));
							// the main reason of calling stopPropagation here
							// is to prevent calling preventDefault later
							// code style is not by me, but automatic formatting
							event2.stopPropagation();
						})
				.keydown(function(event3) {
					// to prevent focus moving away
					event3.stopPropagation();
				})
				.select(
						function(event7) {
							@org.geogebra.web.html5.main.DrawEquationW::scrollSelectionIntoView(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;Z)(rbti,parentElement,newCreationMode);
						});

		if (!newCreationMode) {
			// not being sure whether we need these in not-new-creation mode
			$wnd.$ggbQuery(elSecondInside).mousedown(function(event4) {
				event4.stopPropagation();
			}).mouseup(function(event41) {
				event41.stopPropagation();
			}).click(function(event6) {
				event6.stopPropagation();
			});

			// hacking to deselect the editing when the user does something else like in Desktop
			// all of this code is moved to GeoGebraFrame constructor AND
			// DrawEquationWeb.escEditingWhenMouseDownElsewhere
		} else {
			// if newCreationMode is active, we should catch some Alt-key events!
			var keydownfun = function(event) {
				var code = 0;
				if (event.keyCode) {
					code = event.keyCode;
				} else if (event.which) {// not sure this would be right here
					code = event.which;
				}
				if (code == 38) {//up-arrow
					// in this case, .GeoGebraSuggestionPopupCanShow may be its old value,
					// so let's change it:
					delete elSecondInside.GeoGebraSuggestionPopupCanShow;

					@org.geogebra.web.html5.main.DrawEquationW::shuffleSuggestions(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Z)(rbti, false);
					event.stopPropagation();
					event.preventDefault();
					return false;
				} else if (code == 40) {//down-arrow
					// in this case, .GeoGebraSuggestionPopupCanShow may be its old value,
					// so let's change it:
					delete elSecondInside.GeoGebraSuggestionPopupCanShow;

					@org.geogebra.web.html5.main.DrawEquationW::shuffleSuggestions(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Z)(rbti, true);
					event.stopPropagation();
					event.preventDefault();
					return false;
				}
				var captureSuccess = @org.geogebra.web.html5.main.DrawEquationW::specKeyDown(IZZZLcom/google/gwt/dom/client/Element;)(code, event.altKey, event.ctrlKey, event.shiftKey, parentElement);
				if (captureSuccess) {
					// in this case, .GeoGebraSuggestionPopupCanShow may be its old value,
					// so let's change it: (it should not be true for pi, o and i!)
					delete elSecondInside.GeoGebraSuggestionPopupCanShow;

					// to prevent MathQuillGGB adding other kind of Alt-shortcuts,
					// e.g. unlaut a besides our alpha, or more accurately,
					// call preventDefault because it is a default action here
					event.stopPropagation();
					event.preventDefault();
					return false;
				}
			}
			if (elSecondInside.addEventListener) {//IE9 OK
				// event capturing before the event handlers of MathQuillGGB
				elSecondInside.addEventListener("keydown", keydownfun, true);
				elSecondInside.keyDownEventListenerAdded = true;
			}

			// Also switching off the AV horizontal scrollbar when this has focus
			// multiple focus/blur handlers can be attached to the same event in JQuery
			// but for the blur() event this did not work, so moved this code to
			// mathquillggb.js, textarea.focus and blur handlers - "NoHorizontalScroll"
			// style in web-styles.css... but at least set newCreationMode here!
			elSecondInside.newCreationMode = true;

			var textareaJQ = $wnd.$ggbQuery(elSecondInside).find('textarea');
			if (textareaJQ && textareaJQ.length) {
				var textareaDOM = textareaJQ[0];
				// we don't know whether we're in touch mode until the user first taps to focus/blur
				// so the disabledTextarea might still get more accurate in the future
				// which is based on whether there is touch screen "INSTEAD OF" keyboard
				// letting this decision being made by MathQuillGGB, although we can have
				// something similar at @org.geogebra.web.html5.Browser::hasTouchScreen()
				// which only tells whether any touchstart event happened on the device
				// this shall be added again in storno, because the textarea is recreated...

				// but in theory, in disabledTextarea case these events won't even fire
				textareaJQ
						.blur(
								function(eee) {
									if (!textareaDOM.disabledTextarea) {
										rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onBlur(Lcom/google/gwt/event/dom/client/BlurEvent;)(null);
									}
								})
						.focus(
								function(fff) {
									if (!textareaDOM.disabledTextarea) {
										rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onFocus(Lcom/google/gwt/event/dom/client/FocusEvent;)(null);
									}
								});
			}
			// as disabledTextarea might be updated, add this anyway, but check for it in the handlers
			$wnd
					.$ggbQuery(elSecondInside)
					.blur(
							function(eee) {
								if (textareaDOM.disabledTextarea) {
									rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onBlur(Lcom/google/gwt/event/dom/client/BlurEvent;)(null);
								}
							})
					.focus(
							function(fff) {
								if (textareaDOM.disabledTextarea) {
									rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onFocus(Lcom/google/gwt/event/dom/client/FocusEvent;)(null);
								}
							});
		}
	}-*/
			;

	public static boolean specKeyDown(int keyCode, boolean altDown,
			boolean ctrlDown, boolean shiftDown, Element parentElement) {


		// !ctrlDown so we know it's not AltGr
		if (altDown && !ctrlDown) {


			// eg alt-2 -> power of 2
			String s = GlobalKeyDispatcherW.processAltCode(keyCode);
			;

			if (s != null && !"".equals(s)) {
				for (int i = 0; i < s.length(); i++) {
					triggerPaste(parentElement, "" + s.charAt(i));
				}
				// triggerPaste(parentElement, s);
				// writeLatexInPlaceOfCurrentWord(parentElement, s, "", false);
				return true;
			}
		}
		return false;
	}

	/**
	 * Simulates a paste event, or anything that happens on pasting/entering
	 * text most naturally used with single characters, but string may be okay
	 * as well, provided that they are interpreted as pasting and not
	 * necessarily latex
	 */
	public static native void triggerPaste(Element parentElement, String str) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		if (elSecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elSecondInside.GeoGebraSuggestionPopupCanShow;
		}

		$wnd.$ggbQuery(elSecondInside).mathquillggb('simpaste', str);
	}-*/;

	public static void appendRowToMatrix(Widget w) {
		addNewRowToMatrix(w.getElement());
	}

	private static native void addNewRowToMatrix(Element parentElement) /*-{
		if (parentElement) {
			//var elfirst = parentElement.firstChild.firstChild;
			var elSecond = parentElement.firstChild.firstChild.nextSibling;
			var elSecondInside = elSecond.lastChild;

			$wnd.$ggbQuery(elSecondInside).mathquillggb('matrixsize', 1);
		}
	}-*/;

	/**
	 * This method should add a new (zero) row to the first matrix in formula
	 */
	public static void appendColToMatrix(Widget w) {
		addNewColToMatrix(w.getElement());
	}

	private static native void addNewColToMatrix(Element parentElement) /*-{
		if (parentElement) {
			//var elfirst = parentElement.firstChild.firstChild;
			var elSecond = parentElement.firstChild.firstChild.nextSibling;
			var elSecondInside = elSecond.lastChild;

			$wnd.$ggbQuery(elSecondInside).mathquillggb('matrixsize', 3);
		}
	}-*/;

	/**
	 * This method should add a new (zero) column to the first matrix in formula
	 */
	public static void removeLastColFromMatrix(Widget w) {
		removeColFromMatrix(w.getElement());
	}

	/**
	 * This method should add a new (zero) column to the first matrix in formula
	 */
	public static native void removeColFromMatrix(Element parentElement) /*-{
		if (parentElement) {
			//var elfirst = parentElement.firstChild.firstChild;
			var elSecond = parentElement.firstChild.firstChild.nextSibling;
			var elSecondInside = elSecond.lastChild;

			$wnd.$ggbQuery(elSecondInside).mathquillggb('matrixsize', 4);
		}
	}-*/;

	public static void removeLastRowFromMatrix(Widget w) {
		removeRowFromMatrix(w.getElement());
	}

	/**
	 * This method should add a new (zero) column to the first matrix in formula
	 */
	private static native void removeRowFromMatrix(Element parentElement) /*-{
		if (parentElement) {
			//var elfirst = parentElement.firstChild.firstChild;
			var elSecond = parentElement.firstChild.firstChild.nextSibling;
			var elSecondInside = elSecond.lastChild;

			$wnd.$ggbQuery(elSecondInside).mathquillggb('matrixsize', 2);
		}
	}-*/;

	// documentation in RadioButtonTreeItem.keydown
	public static native void triggerKeydown(GeoContainer rbti,
			Element parentElement,
	        int keycode, boolean altk, boolean ctrlk, boolean shiftk) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		if (elSecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elSecondInside.GeoGebraSuggestionPopupCanShow;
		}

		var textarea = $wnd.$ggbQuery(elSecondInside).find('textarea');
		if ((textarea !== undefined) && (textarea[0] !== undefined)) {
			var evt = $wnd.$ggbQuery.Event("keydown", {
				keyCode : keycode,
				which : keycode,
				altKey : altk,
				ctrlKey : ctrlk,
				shiftKey : shiftk
			});
			textarea.trigger(evt);

			if (rbti) {
				// it might be backspace! but maybe we have to wait for it...
				setTimeout(function() {
					// first trying to wait for just a little
					rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::typing(Z)(false);
				});
			}
		}
	}-*/;

	// documentation in RadioButtonTreeItem.keypress
	public static native void triggerKeypress(GeoContainer rbti,
			Element parentElement,
	        int charcode, boolean altk, boolean ctrlk, boolean shiftk, boolean more) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		if (elSecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elSecondInside.GeoGebraSuggestionPopupCanShow;
		}

		var textarea = $wnd.$ggbQuery(elSecondInside).find('textarea');
		if ((textarea !== undefined) && (textarea[0] !== undefined)) {
			// MathQuillGGB will actually look for the character here
			textarea.val(String.fromCharCode(charcode));

			// this will tell MathQuillGGB not to do keydown / handleKey
			// as well, for a different key pressed earlier
			textarea[0].simulatedKeypress = true;
			if (more) {
				textarea[0].simulatedKeypressMore = true;
			} else {
				textarea[0].simulatedKeypressMore = false;
			}

			var evt = $wnd.$ggbQuery.Event("keypress", {
				charCode : charcode,
				which : charcode,
				// maybe the following things are not necessary
				altKey : altk,
				ctrlKey : ctrlk,
				shiftKey : shiftk
			});
			textarea.trigger(evt);

			if (rbti) {
				// it might be a lot of kinds of keys that add! but maybe we have to wait for it...
				setTimeout(function() {
					// first trying to wait for just a little
					rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::typing(Z)(true);
					rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onKeyPress(Ljava/lang/String;)(String.fromCharCode(charcode));
				});
			}
		}
	}-*/;

	public static native void triggerKeyUp(Element parentElement, int keycode,
	        boolean altk, boolean ctrlk, boolean shiftk) /*-{
		var elfirst = parentElement.firstChild.firstChild;
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		if (elSecondInside.GeoGebraSuggestionPopupCanShow) {
			delete elSecondInside.GeoGebraSuggestionPopupCanShow;
		}

		var textarea = $wnd.$ggbQuery(elSecondInside).find('textarea');
		if ((textarea !== undefined) && (textarea[0] !== undefined)) {
			var evt = $wnd.$ggbQuery.Event("keyup", {
				keyCode : keycode,
				which : keycode,
				altKey : altk,
				ctrlKey : ctrlk,
				shiftKey : shiftk
			});
			textarea.trigger(evt);
		}
	}-*/;

	public static void popupSuggestions(GeoContainer rbti) {
		rbti.popupSuggestions();
	}

	public static void hideSuggestions(GeoContainer rbti) {
		rbti.hideSuggestions();
	}

	public static void shuffleSuggestions(GeoContainer rbti, boolean down) {
		rbti.shuffleSuggestions(down);
	}

	public static void focusEquationMathQuillGGB(Widget w, boolean focus) {
		focusEquationMathQuillGGB(w.getElement(), focus);
	}

	public static native void focusEquationMathQuillGGB(Element parentElement,
	        boolean focus) /*-{
		var edl = $wnd.$ggbQuery(parentElement).find(".mathquillggb-editable");

		if (edl.length) {
			if (focus) {
				if (edl[0].focusMathQuillGGB) {
					edl[0].focusMathQuillGGB();
				} else {
					edl[0].focus();
				}
			} else {
				edl[0].blur();
			}
		}
	}-*/;

	public static native void newFormulaCreatedMathQuillGGB(GeoContainer rbti,
			Element parentElement) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elSecondInside);
		var latexq = thisjq.mathquillggb('text');
		var latexx = thisjq.mathquillggb('latex');

		//elSecond.previousSibling.style.display = "block"; // this does not apply here!!

		@org.geogebra.web.html5.main.DrawEquationW::newFormulaCreatedMathQuillGGB(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Lcom/google/gwt/dom/client/Element;Ljava/lang/String;Ljava/lang/String;)(rbti,parentElement,latexq,latexx);

		// this method also takes care of calling more JSNI code in a callback,
		// that originally belonged here: newFormulaCreatedMathQuillGGBCallback
	}-*/;

	public static void stornoFormulaMathQuillGGB(GeoContainer rbti, Widget w) {
		stornoFormulaMathQuillGGB(rbti, w.getElement());
	}
	public static native void stornoFormulaMathQuillGGB(GeoContainer rbti,
	        Element parentElement) /*-{
		// in theory, this is only called from new formula creation mode!!!
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		$wnd.$ggbQuery(elSecondInside).mathquillggb('revert');
		elSecondInside.innerHTML = '';
		$wnd.$ggbQuery(elSecondInside).mathquillggb('latex', '');
		$wnd.$ggbQuery(elSecondInside).mathquillggb('editable').focus();

		var textareaJQ = $wnd.$ggbQuery(elSecondInside).find('textarea');
		if (textareaJQ && textareaJQ.length) {
			var textareaDOM = textareaJQ[0];
			// see comments at DrawEquationWeb.editEquationMathQuillGGB, at the end

			textareaJQ
					.blur(
							function(eee) {
								if (!textareaDOM.disabledTextarea) {
									rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onBlur(Lcom/google/gwt/event/dom/client/BlurEvent;)(null);
								}
							})
					.focus(
							function(fff) {
								if (!textareaDOM.disabledTextarea) {
									rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::onFocus(Lcom/google/gwt/event/dom/client/FocusEvent;)(null);
								}
							});
		}
	}-*/;

	public static native void updateEditingMathQuillGGB(Element parentElement,
			String newFormula, boolean shallfocus) /*-{
		// this method must not freeze, otherwise the historyPopup would not
		// get focus! It is necessary, however, to get focus
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		$wnd.$ggbQuery(elSecondInside).mathquillggb('revert');
		elSecondInside.innerHTML = newFormula;

		// note: we use this from historyPopup, so it should not ask focus!
		var whattofocus = $wnd.$ggbQuery(elSecondInside).mathquillggb(
				'editable');
		if (shallfocus) {
			whattofocus.focus();
		}
	}-*/;

	public static native String getActualEditedValue(Element parentElement,
			boolean asLaTeX) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elSecondInside);
		return thisjq.mathquillggb(asLaTeX ? 'latex' : 'text');
	}-*/;

	public static native int getCaretPosInEditedValue(Element parentElement) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elSecondInside);
		var str1 = thisjq.mathquillggb('text');
		var str2 = thisjq.mathquillggb('textpluscursor');
		var inx = 0;
		while (inx < str1.length && inx < str2.length
				&& str1.charAt(inx) === str2.charAt(inx)) {
			inx++;
		}
		return inx - 1;
	}-*/;

	public static native void writeLatexInPlaceOfCurrentWord(GeoContainer rbti,
	        Element parentElement, String latex, String currentWord,
	        boolean command) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elSecondInside);
		var eqstring = latex;

		if ((eqstring === null) || (eqstring === undefined)) {
			eqstring = "";
		}

		if (command) {
			// we should write \\left[ instead of [
			// and \\right] instead of ]
			eqstring = eqstring.replace("[", "\\left[");
			eqstring = eqstring.replace("\\left\\left[", "\\left[");
			// in case of typo
			eqstring = eqstring.replace("\\right\\left[", "\\right[");
			eqstring = eqstring.replace("]", "\\right]");
			eqstring = eqstring.replace("\\right\\right]", "\\right]");
			// in case of typo
			eqstring = eqstring.replace("\\left\\right]", "\\left]");

			// ln(<x>)
			eqstring = eqstring.replace("(", "\\left(");
			eqstring = eqstring.replace("\\left\\left(", "\\left(");
			// in case of typo
			eqstring = eqstring.replace("\\right\\left(", "\\right(");
			eqstring = eqstring.replace(")", "\\right)");
			eqstring = eqstring.replace("\\right\\right)", "\\right)");
			// in case of typo
			eqstring = eqstring.replace("\\left\\right)", "\\left)");
		}

		// IMPORTANT! although the following method is called with
		// 1+3 parameters, it is assumed that there is a fourth kind
		// of input added, which is the place of the Cursor
		thisjq.mathquillggb('replace', eqstring, currentWord, command);

		// this does not work, why?
		// make sure the length of brackets (e.g. Quotation marks) are Okay
		//$wnd.setTimeout(function() {
		//	$wnd.$ggbQuery(elSecondInside).mathquillggb('redraw');
		//}, 500);

		if (rbti) {
			if (latex) {
				rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::typing(Z)(true);
			} else {
				rbti.@org.geogebra.web.html5.gui.view.algebra.GeoContainer::typing(Z)(false);
			}
		}
	}-*/;

	public static boolean newFormulaCreatedMathQuillGGB(
	        final GeoContainer rbti, final Element parentElement,
	        final String input, final String latex) {
		AsyncOperation callback = new AsyncOperation() {
			@Override
			public void callback(Object o) {
				// this should only be called when the new formula creation
				// is really successful! i.e. return true as old behaviour
				stornoFormulaMathQuillGGB(rbti, parentElement);
				// now update GUI!
				// rbti.typing(true, false);
			}
		};
		// return value is not reliable, callback is
		return rbti.stopNewFormulaCreation(input, latex, callback);
	}

	private static native void escEditingEquationMathQuillGGB(GeoContainer rbti,
	        Element parentElement) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;

		var elSecondInside = elSecond.lastChild;
		var thisjq = $wnd.$ggbQuery(elSecondInside);

		var latexq = null;
		elSecond.previousSibling.style.display = "block";
		@org.geogebra.web.html5.main.DrawEquationW::endEditingEquationMathQuillGGB(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(rbti,latexq,function(){});
		thisjq.mathquillggb('revert').mathquillggb();
	}-*/;

	public static void endEditingEquationMathQuillGGB(GeoContainer rbti,
			Widget w) {
		endEditingEquationMathQuillGGB(rbti, w.getElement());
	}

	public static native void endEditingEquationMathQuillGGB(GeoContainer rbti,
	        Element parentElement) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		var thisjq = $wnd.$ggbQuery(elSecondInside);
		var latexq = thisjq.mathquillggb('text');
		elSecond.previousSibling.style.display = "block";
		var onError = function() {
			thisjq.mathquillggb('revert').mathquillggb();
		};
		var rett = @org.geogebra.web.html5.main.DrawEquationW::endEditingEquationMathQuillGGB(Lorg/geogebra/web/html5/gui/view/algebra/GeoContainer;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(rbti,latexq,onError);

	}-*/;

	public static void endEditingEquationMathQuillGGB(GeoContainer rbti,
			String latex, final JavaScriptObject onError) {
		currentWidget = null;
		currentElement = null;
		rbti.stopEditing(latex, new AsyncOperation<GeoElement>() {

			@Override
			public void callback(GeoElement obj) {
				if (obj == null) {
					ScriptManagerW.runCallback(onError);
				}

			}
		});
	}


	public static native JavaScriptObject grabCursorForScrollIntoView(
	        Element parentElement) /*-{
		var elSecond = parentElement.firstChild.firstChild.nextSibling;
		var elSecondInside = elSecond.lastChild;

		var jQueryObject = $wnd.$ggbQuery(elSecondInside).find('.cursor');
		if ((jQueryObject !== undefined) && (jQueryObject.length > 0)) {
			return jQueryObject[0];
		}
		return null;
	}-*/;

	public static native JavaScriptObject grabSelectionFocusForScrollIntoView(
	        Element parentElement) /*-{

		var jqel = $wnd.$ggbQuery(parentElement).find('.selection');

		if ((jqel !== undefined) && (jqel.length !== undefined)
				&& (jqel.length > 0)) {
			return jqel[0];
		} else {
			return null;
		}
	}-*/;/*

		// The following code (based on $wnd.getSelection) does not work!
		var selectionRang = $wnd.getSelection();
		var resultNode = null;
		if (selectionRang.rangeCount > 1) {
			// select the range that is not the textarea!
			for (var ii = 0; ii < selectionRang.rangeCount; ii++) {
				resultNode = selectionRang.getRangeAt(ii).endContainer;
				// it is probably a textNode, so let's get its parent node!
				while (resultNode.nodeType === 3) {
					resultNode = resultNode.parentNode;
				}
				// now if it is the textarea, then continue,
				// otherwise break!
				if (resultNode.nodeName.toLowerCase() === 'textarea') {
					continue;
				} else {
					break;
				}
			}
		} else if (selectionRang.rangeCount == 1) {
			resultNode = selectionRang.focusNode;
			// selectionRang is probably a textNode, so let's get its parent node!
			while (resultNode.nodeType === 3) {
				resultNode = resultNode.parentNode;
			}
		} else {
			return null;
		}
		if (resultNode.nodeName.toLowerCase() === 'textarea') {
			// now what? return null...
			return null;
		}
		//resultNode.style.backgroundColor = 'red';
		//resultNode.className += ' redimportant';
		return resultNode;
	}-*//*;*/

	public static void scrollSelectionIntoView(GeoContainer rbti,
	        Element parentElement, boolean newCreationMode) {
		JavaScriptObject jo = grabSelectionFocusForScrollIntoView(parentElement);
		if (jo != null)
			scrollJSOIntoView(jo, rbti, parentElement, false);
	}

	/**
	 * This is an autoScroll to the edited formula in theory, so it could be
	 * just a _scrollToBottom_ in practice, but there is a case when the
	 * construction is long and a formula on its top is edited...
	 * 
	 * It's lucky that GWT's Element.scrollIntoView exists, so we can call that
	 * method...
	 * 
	 * Moreover, we also need to scroll to the cursor, which can be done in one
	 * operation in cases we need that...
	 */
	public static void scrollCursorIntoView(GeoContainer rbti,
	        Element parentElement, boolean newCreationMode) {
		JavaScriptObject jo = grabCursorForScrollIntoView(parentElement);
		if (jo != null) {
			scrollJSOIntoView(jo, rbti, parentElement, newCreationMode);
		} else {
			rbti.scrollIntoView();
		}
	}

	private static void scrollJSOIntoView(JavaScriptObject jo,
	        GeoContainer rbti, Element parentElement,
	        boolean newCreationMode) {

		Element joel = Element.as(jo);
		joel.scrollIntoView();
		Element el = rbti.getScrollElement();
		// Note: the following hacks should only be made in
		// new creation mode! so boolean introduced...
		if (newCreationMode) {
			// if the cursor is on the right or on the left,
			// it would be good to scroll some more, to show the "X" closing
			// sign and the blue border of the window! How to know that?
			// let's compare their places, and if the difference is little,
			// scroll to the left/right!
			if (joel.getAbsoluteLeft() - parentElement.getAbsoluteLeft() < 50) {
				// InputTreeItem class in theory
				el.setScrollLeft(0);
			} else if (parentElement.getAbsoluteRight()
			        - joel.getAbsoluteRight() < 50) {
				// InputTreeItem class in theory
				el.setScrollLeft(el.getScrollWidth() - el.getClientWidth());
			} else if (joel.getAbsoluteLeft() - el.getAbsoluteLeft() < 50) {
				// we cannot show the "X" sign all the time anyway!
				// but it would be good not to keep the cursor on the
				// edge...
				// so if it is around the edge by now, scroll!
				el.setScrollLeft(el.getScrollLeft() - 50
						+ joel.getAbsoluteLeft() - el.getAbsoluteLeft());
			} else if (el.getAbsoluteRight()
			        - joel.getAbsoluteRight() < 50) {
				// similarly
				el.setScrollLeft(el.getScrollLeft() + 50
						- el.getAbsoluteRight()
				                + joel.getAbsoluteRight());
			}
		}
	}

	/**
	 * Removes the "\begin{eqnarray}" and "\end{eqnarray}" notations from the
	 * beginning and end of the string, or returns the string kept intact
	 * 
	 * @param htmlt
	 *            LaTeX equation string
	 * @return input without "\begin{eqnarray}" and "\end{eqnarray}"
	 */
	public static String stripEqnArray(String htmlt) {
		if (htmlt.startsWith("\\begin{eqnarray}")
		        && htmlt.endsWith("\\end{eqnarray}")) {
			return htmlt.substring(16, htmlt.length() - 14);
		}
		return htmlt;
	}

	public static DrawEquationW getNonStaticCopy(GeoContainer rbti) {
		return (DrawEquationW) rbti.getApplication().getDrawEquation();
	}

	/*
	 * needed for avoid the pixelated appearance of LaTeX texts at printing
	 */
	private static double printScale = 1;

	public static void setPrintScale(double t) {
		printScale = t;
	}

	public static Canvas paintOnCanvas(GeoElement geo, String text0, Canvas c,
			int fontSize) {
		if (geo == null) {
			return c == null ? Canvas.createIfSupported() : c;
		}
		final GColor fgColor = geo.getAlgebraColor();
		if (c == null) {
			c = Canvas.createIfSupported();
		} else {
			c.getContext2d().fillRect(0, 0, c.getCoordinateSpaceWidth(),
					c.getCoordinateSpaceHeight());
		}
		JLMContext2d ctx = (JLMContext2d) c.getContext2d();
		AppW app = ((AppW) geo.getKernel().getApplication());
		app.getDrawEquation().checkFirstCall(app);
		GFont font = AwtFactory.prototype.newFont("geogebra", GFont.PLAIN,
				fontSize - 3);
		TeXIcon icon = app.getDrawEquation().createIcon(
				"\\mathsf{\\mathrm {" + text0 + "}}",
				app.getDrawEquation().convertColor(fgColor),
				font, font.getLaTeXStyle(false),
				null, null, app);
		Graphics2DInterface g3 = new Graphics2DW(ctx);
		double ratio = app.getPixelRatio() * printScale;
		c.setCoordinateSpaceWidth((int) (icon.getIconWidth() * ratio));
		c.setCoordinateSpaceHeight((int) (icon.getIconHeight() * ratio));
		c.getElement().getStyle().setWidth(icon.getIconWidth(), Unit.PX);
		c.getElement().getStyle().setHeight(icon.getIconHeight(), Unit.PX);
		// c.getElement().getStyle().setMargin(4, Unit.PX);
		ctx.scale2(ratio, ratio);

		icon.paintIcon(new HasForegroundColor() {
			@Override
			public Color getForegroundColor() {
				return FactoryProvider.INSTANCE.getGraphicsFactory()
						.createColor(fgColor.getRed(), fgColor.getGreen(),
								fgColor.getBlue());
			}
		}, g3, 0, 0);
		return c;
	}

	public static String inputLatexCosmetics(String eqstringin) {

		if (eqstringin == null) {
			// at least to avoid possible exception in case
			// of wrong usage... but this looks buggy as well,
			// which is good, for the bug shall be fixed elsewhere
			return "";
		}

		String eqstring = eqstringin;

		eqstring = eqstring.replace('\n', ' ');

		eqstring = eqstring.replace("\\%", "%");

		if (eqstring.indexOf("{\\it ") > -1) {

			// replace {\it A} by A
			// (not \italic{A} )

			RegExp italic = RegExp.compile("(.*)\\{\\\\it (.*?)\\}(.*)");

			MatchResult matcher;

			while ((matcher = italic.exec(eqstring)) != null) {

				eqstring = matcher.getGroup(1) + " " + matcher.getGroup(2)
						+ matcher.getGroup(3);
			}
		}

		// make sure eg FractionText[] works (surrounds with {} which doesn't
		// draw well in MathQuillGGB)
		if (eqstring.length() >= 2)
			if (eqstring.startsWith("{") && eqstring.endsWith("}")) {
				eqstring = eqstring.substring(1, eqstring.length() - 1);
			}

		// remove $s
		eqstring = eqstring.trim();
		if (eqstring.length() > 2) {
			while (eqstring.startsWith("$"))
				eqstring = eqstring.substring(1).trim();
			while (eqstring.endsWith("$"))
				eqstring = eqstring.substring(0, eqstring.length() - 1).trim();
		} else if ("$$".equals(eqstring)) {
			eqstring = "";
			// the rest cases: do not remove single $
		} else if ("\\$".equals(eqstring)) {
			eqstring = "\\text{$}";
		}

		eqstring = eqstring.replace("\\\\", "\\cr ");

		// remove all \; and \,
		// doesn't work inside \text eg \text{some\;text}
		eqstring = eqstring.replace("\\;", "\\space ");
		eqstring = eqstring.replace("\\:", "\\space ");
		eqstring = eqstring.replace("\\,", "\\space ");
		eqstring = eqstring.replace("\\ ", "\\space ");

		// negative space is not implemented, let it be positive space
		// the following code might avoid e.g. x\\!1
		eqstring = eqstring.replace("\\! ", " ");
		eqstring = eqstring.replace(" \\!", " ");
		eqstring = eqstring.replace("\\!", " ");

		// substitute every \$ with $
		eqstring = eqstring.replace("\\$", "$");

		// eqstring = eqstring.replace("\\left\\{", "\\lbrace ");
		// eqstring = eqstring.replace("\\right\\}", "\\rbrace ");

		// this might remove necessary space
		// eqstring = eqstring.replace(" ", "");

		// this does not work
		// eqstring = eqstring.replace("\\sqrt[ \\t]+\\[", "\\sqrt[");

		// that's why this programmatically slower solution:
		while ((eqstring.indexOf("\\sqrt ") != -1)
				|| (eqstring.indexOf("\\sqrt\t") != -1)) {
			eqstring = eqstring.replace("\\sqrt ", "\\sqrt");
			eqstring = eqstring.replace("\\sqrt\t", "\\sqrt");
		}

		// exchange \\sqrt[x]{y} with \\nthroot{x}{y}
		int index1 = 0, index2 = 0;
		while ((index1 = eqstring.indexOf("\\sqrt[")) != -1) {
			index2 = eqstring.indexOf("]", index1);
			eqstring = eqstring.substring(0, index1) + "\\nthroot{"
					+ eqstring.substring(index1 + 6, index2) + "}"
					+ eqstring.substring(index2 + 1);
		}

		// avoid grey rectangle
		if (eqstring.trim().equals("")) {
			eqstring = "\\text{}";
		}

		// and now, only for presentational purposes (blue highlighting)
		// we can make every ( to \left( and every ) to \right), etc.
		// eqstring = eqstring.replace("(", "\\left(");
		// eqstring = eqstring.replace("\\left\\left(", "\\left(");
		// in case of typo
		// eqstring = eqstring.replace("\\right\\left(", "\\right(");
		// eqstring = eqstring.replace(")", "\\right)");
		// eqstring = eqstring.replace("\\right\\right)", "\\right)");
		// in case of typo
		// eqstring = eqstring.replace("\\left\\right)", "\\left)");
		// but we do not do it as editing x in f(x)=x+1 gives error anyway
		// so not having \\left there seems to be a feature, not a bug
		// otherwise, Line[A,B] and {1,2,3,4} are working, so probably Okay

		return eqstring;
	}

	@Override
	public GDimension measureEquation(App app, GeoElement geo0, int minValue,
			int minValue2, String text, GFont font, boolean serif) {
		return this.measureEquationJLaTeXMath(app, geo0, 0, 0, text, font,
				serif, null, null);
	}

	@Override
	public void checkFirstCall(App app) {
		ensureJLMFactoryExists();
		if (initJLaTeXMath == null) {

			StringBuilder initJLM = DrawEquation.getJLMCommands();
			initJLaTeXMath = new TeXFormula(initJLM.toString());
		}

	}

	@Override
	public Color convertColor(GColor color) {
		return new ColorW(color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	protected Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret) {
		// TODO Auto-generated method stub
		return null;
	}
}
