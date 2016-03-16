package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.inputfield.HasSymbolPopup;
import org.geogebra.web.html5.gui.inputfield.HistoryPopupW;
import org.geogebra.web.html5.gui.util.BasicIcons;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.DrawEquationW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPanelW;
import org.geogebra.web.web.gui.inputbar.InputBarHelpPopup;
import org.geogebra.web.web.gui.layout.panels.AlgebraDockPanelW;
import org.geogebra.web.web.gui.util.ButtonPopupMenu;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * NewRadioButtonTreeItem for creating new formulas in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class InputTreeItem extends RadioTreeItem implements
		HasSymbolPopup, FocusHandler, BlurHandler {

	// How large this number should be (e.g. place on the screen, or
	// scrollable?) Let's allow practically everything

	// create special formula button (matrix, piecewise function, parametric
	// curve)
	protected PushButton pButton = null;

	HistoryPopupW historyPopup;
	ButtonPopupMenu specialPopup;
	EquationEditor editor;
	Label dummyLabel;

	private Label piecewiseLabel, matrixLabel, curveLabel;

	InputBarHelpPopup helpPopup;

	ToggleButton btnHelpToggle;

	public InputTreeItem(Kernel kern) {
		super(kern);

		editor = new EquationEditor(app, this);


		//should depend on number of previoous elements?
		addHistoryPopup(true);

		if (app.has(Feature.INPUT_SHOWN_IN_AV)) {
			SimplePanel sp = new SimplePanel();
			btnHelpToggle = new ToggleButton(new NoDragImage(
					GuiResources.INSTANCE.menu_icon_help().getSafeUri()
							.asString()), new NoDragImage(
					GuiResources.INSTANCE.menu_icon_help().getSafeUri()
.asString()));

			btnHelpToggle.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					if (btnHelpToggle.isDown()) {
						setShowInputHelpPanel(true);
					} else {
						setShowInputHelpPanel(false);
					}

				}

			});
			sp.setStyleName("avHelpButtonParent");
			sp.setWidget(btnHelpToggle);
			btnHelpToggle.addStyleName("algebraHelpButton");
			main.insert(sp, 0);
		}

		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("AlgebraViewObjectStylebar");
		// buttonPanel.addStyleName("MouseDownDoesntExitEditingFeature");
		buttonPanel.addStyleName("BlurDoesntUpdateGUIFeature");


		// code copied from AutoCompleteTextFieldW,
		// with some modifications!
		xButton = new PushButton(new Image(
				GuiResources.INSTANCE.algebra_delete()));
		xButton.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.algebra_delete_hover()));
		String id = DOM.createUniqueId();
		// textField.setShowSymbolElement(this.XButton.getElement());
		xButton.getElement().setId(id + "_SymbolButton");
		xButton.getElement().setAttribute("data-visible", "false");
		// XButton.getElement().setAttribute("style", "display: none");
		// XButton.setText("X");
		xButton.addStyleName("XButton");
		xButton.addMouseDownHandler(new MouseDownHandler() {
			// ClickHandler changed to MouseDownHandler,
			// but maybe it's not that important here
			@Override
			public void onMouseDown(MouseDownEvent event) {
				DrawEquationW.stornoFormulaMathQuillGGB(
				        InputTreeItem.this, latexItem);
				InputTreeItem.this.setFocus(true);
				event.stopPropagation();
				// event.preventDefault();
			}
		});
		xButton.addTouchStartHandler(new TouchStartHandler() {
			// ClickHandler changed to MouseDownHandler,
			// but maybe it's not that important here
			@Override
			public void onTouchStart(TouchStartEvent event) {
				DrawEquationW.stornoFormulaMathQuillGGB(
						InputTreeItem.this, latexItem);
				InputTreeItem.this.setFocus(true);
				event.stopPropagation();
				event.preventDefault();
			}
		});

		if (app.has(Feature.ADD_NEW_OBJECT_BUTTON)) {
			// from now on, we'll check this Feature
			// by (pButton != null) but that influences
			// buttonPanel as well, because if both
			// pButton and xButton are invisible, then
			// buttonPanel should show either, e.g.
			// by adding an additional CSS class to it
			pButton = new PushButton(new Image(
					GuiResources.INSTANCE.algebra_new()));
			pButton.getUpHoveringFace().setImage(
					new Image(GuiResources.INSTANCE.algebra_new_hover()));
			pButton.getElement().setAttribute("data-visible", "false");
			pButton.addStyleName("XButtonNeighbour");
			pButton.addMouseDownHandler(new MouseDownHandler() {
				// ClickHandler changed to MouseDownHandler,
				// but maybe it's not that important here
				@Override
				public void onMouseDown(MouseDownEvent event) {
					event.stopPropagation();

					// although this does not seem to help in itself,
					// why not prevent default action? maybe the same
					// bug has two distint causes, both needs to be fixed
					// but r41773 is undone for problems on emulated tablet
					// event.preventDefault();

					if (specialPopup != null) {
						if (EuclidianStyleBarW.CURRENT_POP_UP != specialPopup
								|| !app.wasPopupJustClosed()) {
							if (EuclidianStyleBarW.CURRENT_POP_UP != null) {
								EuclidianStyleBarW.CURRENT_POP_UP.hide();
							}
							EuclidianStyleBarW.CURRENT_POP_UP = specialPopup;

							app.registerPopup(specialPopup);
							specialPopup.showRelativeTo(pButton);
							specialPopup.getFocusPanel().getElement().focus();
						} else {
							specialPopup.setVisible(false);
							EuclidianStyleBarW.CURRENT_POP_UP = null;
						}
					}
				}
			});
			// pButton.addClickHandler(CancelEvents.instance);
			// pButton.addMouseUpHandler(CancelEvents.instance);

			specialPopup = new ButtonPopupMenu(app.getPanel()) {
				@Override
				public void setVisible(boolean visible) {
					super.setVisible(visible);

					// if another button is pressed only the visibility is
					// changed,
					// by firing the event we can react as if it was closed
					CloseEvent.fire(this, this, false);
				}

				@Override
				public void hide() {
					super.hide();
					if (EuclidianStyleBarW.CURRENT_POP_UP.equals(this)) {
						EuclidianStyleBarW.CURRENT_POP_UP = null;
					}
				}
			};
			specialPopup.setAutoHideEnabled(true);
			specialPopup.getPanel().addStyleName("AVmenuListContainer");
			// specialPopup.addStyleName("MouseDownDoesntExitEditingFeature");
			specialPopup.addStyleName("BlurDoesntUpdateGUIFeature");

			UnorderedList itemList = new UnorderedList();
			itemList.setStyleName("AVmenuListContent");
			specialPopup.getPanel().add(itemList);

			ListItem actual = new ListItem();
			actual.add(new Image(GuiResources.INSTANCE.algebra_new_piecewise()));
			actual.add(piecewiseLabel = new Label(app
					.getPlain("PiecewiseFunction")));
			// ClickHandler is Okay here, but maybe MouseDownHandler is better?
			actual.addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent ce) {
					ce.stopPropagation();
					specialPopup.setVisible(false);
					EuclidianStyleBarW.CURRENT_POP_UP = null;

					// TODO: only create it in the input bar!!!
					final GeoFunction fun = CondFunctionTreeItem
							.createBasic(app.getKernel());
					if (fun != null) {
						// in theory, fun is never null, but what if?
						// same code as for matrices, see comments there
						Timer tim = new Timer() {
							public void run() {
								app.getAlgebraView().startEditing(fun);
							}
						};
						tim.schedule(500);
					}
					updateGUIfocus(InputTreeItem.this, false);
				}
			}, ClickEvent.getType());
			itemList.add(actual);

			actual = new ListItem();
			actual.add(new Image(GuiResources.INSTANCE.algebra_new_matrix()));
			actual.add(matrixLabel = new Label(app.getMenu("Matrix")));
			actual.addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent ce) {
					ce.stopPropagation();
					specialPopup.setVisible(false);
					EuclidianStyleBarW.CURRENT_POP_UP = null;

					// TODO: only create it in the input bar!!!
					final GeoList mat = MatrixTreeItem
							.create2x2IdentityMatrix(app.getKernel());
					// scheduleDeferred alone does not work well!
					Timer tim2 = new Timer() {
						public void run() {
							app.getAlgebraView().startEditing(mat);
						}
					};
					// on a good machine, 500ms was usually not enough,
					// but 1000ms was usually enough... however, it turned
					// out this is due to a setTimeout in
					// DrawEquationWeb.drawEquationMathQuillGGB...
					// so we could spare at least 500ms by clearing that timer,
					tim2.schedule(500);

					// but now I'm experimenting with even less timeout, i.e.
					// tim.schedule(200);
					// 200ms is not enough, and as this is a good machine
					// let us say that 500ms is just right, or maybe too little
					// on slow machines -> shall we use scheduleDeferred too?
					updateGUIfocus(InputTreeItem.this, false);
				}
			}, ClickEvent.getType());
			itemList.add(actual);

			actual = new ListItem();
			actual.add(new Image(GuiResources.INSTANCE.algebra_new_parametric()));
			actual.add(curveLabel = new Label(app.getPlain("CurveCartesian")));
			actual.addDomHandler(new ClickHandler() {
				public void onClick(ClickEvent ce) {
					ce.stopPropagation();
					specialPopup.setVisible(false);
					EuclidianStyleBarW.CURRENT_POP_UP = null;

					// TODO: only create it in the input bar!!!
					final GeoCurveCartesianND curve = ParCurveTreeItem
							.createBasic(app.getKernel());
					if (curve != null) {
						// in theory, fun is never null, but what if?
						// same code as for matrices, see comments there
						Timer tim = new Timer() {
							public void run() {
								app.getAlgebraView().startEditing(curve);
							}
						};
						tim.schedule(500);
					}
					updateGUIfocus(InputTreeItem.this, false);
				}
			}, ClickEvent.getType());
			itemList.add(actual);
		}

		ClickStartHandler.init(xButton, new ClickStartHandler(false, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// nothing to do here; just makes sure that
				// event.stopPropagation is called
			}
		});

		if (pButton != null) {
			ClickStartHandler.init(pButton, new ClickStartHandler(false, true) {
				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					// nothing to do here; just makes sure that
					// event.stopPropagation is called
				}
			});
		}

		try{
			//TRY-CATCH needed for Win8 app //TODO find better solution
			xButton.setFocus(false);
			if (pButton != null) {
				pButton.setFocus(false);
			}
		}catch(Throwable t){
		}
		// add(textField);// done in super()

		// it seems this would be part of the Tree, not of TreeItem...
		// why? web programming knowledge helps: we should add position:
		// relative! to ".GeoGebraFrame .gwt-Tree .gwt-TreeItem .elem"

		main.add(buttonPanel);// dirty hack of adding it two times!

		if (pButton != null) {
			this.buttonPanel.add(pButton);
		}

		this.buttonPanel.add(xButton);

		// this was necessary earlier in conjuction with add(xButton)
		// ihtml.getElement().appendChild(xButton.getElement());
		// but later this.replaceXButtonDOM() should be used instead

		xButton.getElement().setAttribute("data-visible", "true");
		if (pButton != null) {
			pButton.getElement().setAttribute("data-visible", "true");
		}
		addStyleName("SymbolCanBeShown");

		// this would give initial focus to Web.html, but not to tablet.html,
		// moreover, it would make Apple IPad emulation in Chrome wrong
		// ensureEditing();

		// but another stopNewFormulaCreation would fix that... so maybe this
		// is just called too early for some reason?

		Timer tim = new Timer() {
			@Override
			public void run() {
				ensureEditing();

				// on Tablets, this is needed to change the stylebar icon
				// to the + sign, but unfortunately, the borders are still gray
				// (on Desktop, this does no harm, although maybe inefficient)
				if (!app.isApplet()) {
					onFocus(null);
					// not sure why this is needed in case of tablet.html,
					// but the following hack fixes it:
					if (app.getGuiManager() != null
							&& app.getGuiManager().getLayout() != null
							&& app.getGuiManager().getLayout().getDockManager() != null
							&& app.getGuiManager().getLayout().getDockManager()
									.getPanel(App.VIEW_ALGEBRA) != null) {
						// in every case we use NewRadioButtonTreeItem, we use
						// DockPanelW anyway
						AlgebraDockPanelW ad = (AlgebraDockPanelW) app
								.getGuiManager().getLayout().getDockManager()
								.getPanel(App.VIEW_ALGEBRA);
						ad.getAbsolutePanel()
								.addStyleName("NoHorizontalScroll");
						// Now borders are blue!!!
						// by the way, the next blur event cancels this in
						// theory
						// just from now, we suppose that it's initially focused
					}
				}

				// just there is no reliable way to distinguish between
				// Web and Tablet at this point, although we could use some
				// heuristic later, just it is not settled what that heuristic
				// should be, probably not the same as MathQuillGGB's
				// disabledTextarea
				// and for the same reason, the Timer is good at 500ms timeout,
				// I think, for app.html and tablet.html... although we could
				// decrease it in case of Web.html (app.isApplet())
			}
		};
		if (app.getGuiManager().hasAlgebraViewShowing()) {
			// onFocus is not called in any other case,
			// just from the JavaScript focus event
			if (app.isApplet()) {
				// timeout is looking too big, and not even needed,
				// as this is not about the tablet bug for sure
				tim.schedule(0);
			} else {
				// either app.html or tablet.html, seems Okay
				// as we cannot tell the two apart yet, until mouse is pressed
				// or touch events are used (although we could use heuristic?)
				tim.schedule(500);
			}
		}
	}

	public void setShowInputHelpPanel(boolean show) {

		if (show) {
			dummyLabel.addStyleName("hidden");
			InputBarHelpPanelW helpPanel = (InputBarHelpPanelW) app
					.getGuiManager().getInputHelpPanel();
			helpPanel.updateGUI();

			if (helpPopup == null && app != null) {
				helpPopup = new InputBarHelpPopup(this.app, this);
				helpPopup.addAutoHidePartner(this.getElement());
				helpPopup.addCloseHandler(new CloseHandler<GPopupPanel>() {

					public void onClose(CloseEvent<GPopupPanel> event) {
						dummyLabel.removeStyleName("hidden");
						ihtml.getElement().getElementsByTagName("textarea")
								.getItem(0).focus();
					}
					
				});

				if (btnHelpToggle != null) {
					helpPopup.setBtnHelpToggle(btnHelpToggle);
				}
			}

			helpPopup
					.setPopupPositionAndShow(new GPopupPanel.PositionCallback() {
						public void setPosition(int offsetWidth,
								int offsetHeight) {
							helpPopup
									.getElement()
									.getStyle()
									.setProperty(
											"left",
											(btnHelpToggle.getAbsoluteLeft() + btnHelpToggle
													.getOffsetWidth()) + "px");

							if (btnHelpToggle.getAbsoluteTop() < Window
									.getClientHeight() / 2) {
								helpPopup
										.getElement()
										.getStyle()
										.setProperty(
												"top",
												(btnHelpToggle.getParent()
														.getAbsoluteTop() + btnHelpToggle
														.getParent()
														.getOffsetHeight())
														+ "px");
								helpPopup.getElement().getStyle()
										.setProperty("bottom", "auto");
							} else {
								helpPopup
										.getElement()
										.getStyle()
										.setProperty(
												"bottom",
												(Window.getClientHeight() - btnHelpToggle
														.getParent()
														.getAbsoluteTop())
														+ "px");
								helpPopup.getElement().getStyle()
										.setProperty("top", "auto");
							}

							helpPopup.show();
						}
					});

		} else if (helpPopup != null) {
			helpPopup.hide();
		}
	}

	public void replaceXButtonDOM() {
		getWidget().getElement().getParentElement()
				.appendChild(buttonPanel.getElement());
		// Internet Explorer seems to also require this lately:
		if (pButton != null) {
			buttonPanel.getElement().appendChild(pButton.getElement());
		}
		buttonPanel.getElement().appendChild(getXbutton().getElement());
	}

	/**
	 * This is the interface of bringing up a popup of suggestions, from a query
	 * string "sub"... in AutoCompleteTextFieldW, this is supposed to be
	 * triggered automatically by SuggestBox, but in NewRadioButtonTreeItem we
	 * have to call this every time for the actual word in the formula (i.e.
	 * updateCurrentWord(true)), when the formula is refreshed a bit! e.g.
	 * DrawEquationWeb.editEquationMathQuillGGB.onKeyUp or something, so this
	 * will be a method to override!
	 */
	@Override
	public boolean popupSuggestions() {
		// on-screen keyboard should use showOrHideSuggestions instead!
		return editor.popupSuggestions();
	}

	@Override
	public boolean hideSuggestions() {
		return editor.hideSuggestions();
	}

	@Override
	public void showOrHideSuggestions() {
		if ((latexItem != null)
				&& latexItem.getElement().hasParentElement()) {
			DrawEquationW.showOrHideSuggestions(this, latexItem.getElement());
		}
	}

	/**
	 * In case the suggestion list is showing, shuffle its selected element
	 * up/down, otherwise consider up/down event for the history popup!
	 */
	@Override
	public void shuffleSuggestions(boolean down) {
		if (editor.shuffleSuggestions(down)) {

			return;
		} else if (down) {
			if (historyPopup != null && historyPopup.isDownPopup()) {
				// this would give the focus to the historyPopup,
				// which should catch the key events itself, but maybe it's
				// not everything all right here!
				historyPopup.showPopup();
			} else {
				String text = editor.getNextInput();
				if (text != null) {
					editor.setText(text, true);
				}
			}
		} else {
			if (historyPopup != null && !historyPopup.isDownPopup()) {
				historyPopup.showPopup();
			} else {
				String text = editor.getPreviousInput();
				if (text != null)
					editor.setText(text, true);
			}
		}
	}

	@Override
	public boolean stopNewFormulaCreation(String newValue0, String latex,
	        AsyncOperation callback) {
		if (editor.needsEnterForSuggestion()) {
			return false;
		}
		return super.stopNewFormulaCreation(newValue0, latex, callback);
	}

	public boolean getAutoComplete() {
		return true;
	}

	/**
	 * Note that this method should set the text of the MathQuillGGB-editing box
	 * in MathQuillGGB text() format, not latex()... that's why we should have a
	 * mapping from text() format formulas to latex() format formulas, and keep
	 * it in the historyMap class, which should be filled the same time when
	 * addToHistory is filled!
	 */
	public void setText(String s) {
		editor.setText(s, false);
	}

	public List<String> resetCompletions() {
		return editor.resetCompletions();
	}











	public List<String> getCompletions() {
		return editor.getCompletions();
	}

	@Override
	public void setFocus(boolean b, boolean scheduledVersion) {
		if (AlgebraViewW.hasAvex() && b) {
			app.getSelectionManager().clearSelectedGeos();
			getAV().updateSelection();
		}
		if (scheduledVersion) {
			// these booleans are for governing setFocus so that
			// when it is called from a timeout or schedule, and
			// a blur event is called meanwhile, then it shall not
			// really set the focus...
			boolean setFocusAllowed = app.getGuiManager().focusScheduled(false,
					false, true);
			boolean setFocusScheduled = app.getGuiManager().focusScheduled(
					false, true, false);

			if (setFocusAllowed || !setFocusScheduled) {
				DrawEquationW.focusEquationMathQuillGGB(latexItem, b);
			}

			app.getGuiManager().focusScheduled(true, false, true);
		} else {
			DrawEquationW.focusEquationMathQuillGGB(latexItem, b);
		}
	}

	public void showPopup(boolean show) {
		if (this.buttonPanel == null) {
			return;
		}
		buttonPanel.setVisible(show);
		setButtonVisible(getXbutton(), show);
		setButtonVisible(pButton, show);
	}

	/**
	 * Earlier methods used the 'shown' class to show the xButton, but later
	 * code used simply xButton.setVisible(true)... to be compatible with both,
	 * this method is here
	 * 
	 * @param visible
	 */
	public void setButtonVisible(final PushButton button, boolean show) {
		if (button == null) {
			return;
		}
		Element showSymbolElement = button.getElement();
		if (showSymbolElement != null
				&& "true"
						.equals(showSymbolElement.getAttribute("data-visible"))) {
			if (show) {
				showSymbolElement.addClassName("shown");
			} else {
				if (!"true".equals(showSymbolElement
						.getAttribute("data-persist"))) {
					showSymbolElement.removeClassName("shown");
				}
			}
		}
		button.setVisible(show);
	}

	/**
	 * This is looking like a GWT FocusHandler method, and really it is, but it
	 * is not added to any GWT widget yet, just called from DrawEquationWeb
	 * JQuery focus handlers, or other places
	 */
	@Override
	public void onFocus(FocusEvent event) {
		super.onFocus(event);
		if (app.has(Feature.AV_EXTENSIONS)) {
			getAV().getSelectionCtrl().clear();
			RadioTreeItem.closeMinMaxPanel();
		}
		// earlier this method was mainly called from setFocus,
		// and now it is also called from there, but in an
		// indirect way: first MathQuillGGB textarea gets focus,
		// then its onfocus handler gets called, which calls this
		if (dummyLabel != null) {
			// it can only be non-null when
			// app.has(Feature.INPUT_SHOWN_IN_INPUTBAR)
			// if (dummyLabel.getElement().hasParentElement()) {
			if ((dummyLabel.getElement() != null)
					&& ihtml.getElement().isOrHasChild(dummyLabel.getElement())) {
				ihtml.getElement().removeChild(dummyLabel.getElement());
			}
		}

		if (((AlgebraViewW) av).isNodeTableEmpty()) {
			// #5245#comment:8, cases B and C excluded
			updateGUIfocus(event == null ? this : event.getSource(), false);
		} else if (((AlgebraViewW) av).nodeTable.size() == 1) {
			// maybe a new element has just been created?
			// note: we are not doing this on blur!
			updateGUIfocus(event == null ? this : event.getSource(), false);
		} else {
			// note: we are not doing this on blur!
			typing(false);
		}

		app.getSelectionManager().clearSelectedGeos();

		// this.focused = true; // hasFocus is not needed, AFAIK
	}

	/**
	 * This method does update the buttonPanel, xButton, pButton visibility
	 * entirely
	 * 
	 * @param source
	 *            : usually means "this"
	 * @param blurtrue
	 *            : whether we are in blurred mode
	 */
	protected void updateGUIfocus(Object source, boolean blurtrue) {
		// deselects current selection
		((AlgebraViewW) av).setActiveTreeItem(null);

		boolean emptyCase = ((AlgebraViewW) av).isNodeTableEmpty()
				&& !this.getAlgebraDockPanel().hasLongStyleBar();

		// update style bar icon look
		if (emptyCase) {
			getAlgebraDockPanel().showStyleBarPanel(blurtrue);
		} else {
			getAlgebraDockPanel().showStyleBarPanel(true);
		}

		// always show popup, except (blurtrue && emptyCase) == true

		// this basically calls the showPopup method, like:
		// showPopup(!blurtrue || !emptyCase);
		AutoCompleteTextFieldW.showSymbolButtonIfExists(source, !blurtrue
				|| !emptyCase);

		// afterwards, if the popup shall be showing,
		// then all of our three icons are visible in theory
		// except pButton, if it is null...
		if (!blurtrue || !emptyCase) {
			typing(false);
		}
	}

	/**
	 * This is looking like a GWT BlurHandler method, and really it is, but it
	 * is not added to any GWT widget yet, just called from DrawEquationWeb
	 * JQuery blur handlers, or other places
	 */
	@Override
	public void onBlur(BlurEvent event) {
		if (app.getGuiManager().focusScheduled(false, true, false)) {
			app.getGuiManager().focusScheduled(true, true, false);
		}

		if (!DrawEquationW.targetHasFeature(getElement(),
				"BlurDoesntUpdateGUIFeature", true)) {

			if (isEmpty() && app.has(Feature.INPUT_SHOWN_IN_INPUTBAR))
				addDummyLabel();

			if (((AlgebraViewW) av).isNodeTableEmpty()) {
				// #5245#comment:8, cases B and C excluded
				updateGUIfocus(event == null ? this : event.getSource(), true);
			}
		}
	}

	private void addDummyLabel() {
		if (dummyLabel == null) {
			dummyLabel = new Label(app.getPlain("InputLabel")
					+ Unicode.ellipsis);
			dummyLabel.addStyleName("avDummyLabel");
		}
		//if (dummyLabel.getElement() != null) {
			//if (dummyLabel.getElement().hasParentElement()) {
				// in theory, this is done in insertFirst,
				// just making sure here as well
				//dummyLabel.getElement().removeFromParent();
			//}
			ihtml.getElement().insertFirst(dummyLabel.getElement());
		//}
	}

	public ArrayList<String> getHistory() {
		return editor.getHistory();
	}

	/**
	 * Add a history popup list and an embedded popup button. See
	 * AlgebraInputBar
	 */
	public void addHistoryPopup(boolean isDownPopup) {

		if (historyPopup == null)
			historyPopup = new HistoryPopupW(this, app.getPanel());

		historyPopup.setDownPopup(isDownPopup);

		ClickHandler al = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// AGString cmd = event.;
				// AGif (cmd.equals(1 + BorderButton.cmdSuffix)) {
				// TODO: should up/down orientation be tied to InputBar?
				// show popup
				historyPopup.showPopup();

			}
		};
		setBorderButton(1, BasicIcons.createUpDownTriangleIcon(false, true), al);
		this.setBorderButtonVisible(1, false);
	}

	private void setBorderButtonVisible(int i, boolean b) {
		App.debug("setBorderVisible() implementation needed"); // TODO
		                                                       // Auto-generated
	}

	private void setBorderButton(int i, ImageData createUpDownTriangleIcon,
	        ClickHandler al) {
		App.debug("setBorderButton() implementation needed"); // TODO
		                                                      // Auto-generated
	}





	@Override
	public void addToHistory(String str, String latex) {
		editor.addToHistory(str, latex);
	}

	@Override
	public boolean isSuggesting() {
		return editor.isSuggesting();
	}

	@Override
	public void requestFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void typing(boolean heuristic) {
		if (xButton != null) {
			if (heuristic || !isEmpty()) {
				if (pButton == null) {
					buttonPanel.setVisible(true);
				}
				setButtonVisible(xButton, true);
			} else {
				setButtonVisible(xButton, false);
				if (pButton == null) {
					buttonPanel.setVisible(false);
				}
			}
		}
	}

	protected boolean isEmpty() {
		return "".equals(getText().trim());
	}

	@Override
	public void updatePosition(ScrollableSuggestionDisplay sug) {
		sug.setPositionRelativeTo(ihtml);
	}

	public void setLabels() {
		editor.resetLanguage();
		if (dummyLabel != null) {
			dummyLabel.setText(app.getPlain("InputLabel") + Unicode.ellipsis);
		}
		if (piecewiseLabel != null) {
			piecewiseLabel.setText(app.getPlain("PiecewiseFunction"));
			curveLabel.setText(app.getPlain("CurveCartesian"));
			matrixLabel.setText(app.getMenu("Matrix"));
		}
	}

	public void removeFromParent() {
		main.removeFromParent();
	}

	public void autocomplete(String s) {
		editor.autocomplete(s, false);
	}

}
