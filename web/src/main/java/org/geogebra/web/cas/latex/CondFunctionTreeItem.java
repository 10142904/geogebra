package org.geogebra.web.cas.latex;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.web.html5.gui.util.CancelEvents;
import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * CondFunRadioButtonTreeItem for creating piecewise functions (conditional
 * functions, .isGeoFunctionConditional()) in the algebra view
 * 
 * File created by Arpad Fekete
 */
public class CondFunctionTreeItem extends MathQuillTreeItem {

	PushButton pButton;

	/**
	 * Creating a SpecialRadioButtonTreeItem from existing construction as we
	 * should allow special buttons for them, too... see
	 * RadioButtonTreeItem.create, which may call this constructor depending on
	 * situation (e.g. why not after NewRadioButtonTreeItem?)
	 */
	public CondFunctionTreeItem(final GeoElement ge) {
		super(ge);
		
		pButton = new PushButton(new Image(
				GuiResources.INSTANCE.algebra_new()));
		pButton.getUpHoveringFace().setImage(
				new Image(GuiResources.INSTANCE.algebra_new_hover()));
		pButton.addStyleName("XButtonNeighbour");
		pButton.addStyleName("shown");
		pButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent mde) {
				mde.preventDefault();
				mde.stopPropagation();
				addNewRow();
			}
		});
		pButton.addStyleName("MouseDownDoesntExitEditingFeature");
		pButton.addStyleName("BlurDoesntUpdateGUIFeature");

		// basically, everything except onClick,
		// static to prevent more instances
		pButton.addClickHandler(CancelEvents.instance);
		pButton.addDoubleClickHandler(CancelEvents.instance);
		// btnRow.addMouseDownHandler(cancelEvents);
		pButton.addMouseUpHandler(CancelEvents.instance);
		pButton.addMouseMoveHandler(CancelEvents.instance);
		// btnRow.addMouseOverHandler(cancelEvents);
		// pButton.addMouseOutHandler(CancelEvents.instance);

		// do not redefine TouchStartHandlers, as they simulate
		// mouse event handlers, so it would be harmful
	}

	@Override
	protected PushButton getPButton() {
		return pButton;
	}

	@Override
	protected void maybeSetPButtonVisibility(boolean bool) {
		pButton.setVisible(bool);
	}

	public static GeoFunction createBasic(Kernel kern) {
		boolean oldVal = kern.isUsingInternalCommandNames();
		kern.setUseInternalCommandNames(true);
		GeoElementND[] ret = kern.getAlgebraProcessor().processAlgebraCommand(
				"If[x<1,x,x^2]", false);
		kern.setUseInternalCommandNames(oldVal);
		if ((ret != null) && (ret.length > 0) && (ret[0] != null)
				&& (ret[0] instanceof GeoFunction)) {
			return (GeoFunction) ret[0];
		}
		return null;
	}

	public void addNewRow() {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {

				// could probably implement this for non-editing case
				// better (like in MatrixRadioButtonTreeItem),
				// but now it's only used in editing mode anyway
				if (!commonEditingCheck()) {
					ensureEditing();
				}

				MathQuillHelper.appendRowToMatrix(latexItem);
			}
		});
	}

	protected boolean hasXVar() {
		GeoFunction fun = (GeoFunction) geo;
		if (fun.getFunctionVariables().length == 0) {
			return false;
		} else if (fun.getFunctionVariables()[0] == null) {
			return false;
		}
		if ("x".equals((fun.getFunctionVariables()[0]).getSetVarString())) {
			return true;
		}
		return false;
	}
}
