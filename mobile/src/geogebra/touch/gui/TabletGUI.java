package geogebra.touch.gui;

import geogebra.common.kernel.Kernel;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanelLeft;
import geogebra.touch.gui.elements.header.TabletHeaderPanelRight;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.model.TouchModel;

import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * Coordinates the GUI of the tablet.
 * 
 */
public class TabletGUI extends LayoutPanel implements GeoGebraTouchGUI
{
	EuclidianViewPanel euclidianViewPanel;
	LayoutPanel evAVpanel;
	TabletHeaderPanel headerPanel;
	TabletHeaderPanelLeft leftHeader;
	TabletHeaderPanelRight rightHeader;
	AlgebraViewPanel algebraViewPanel;
	ToolBar toolBar;
	StylingBar stylingBar;

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	public TabletGUI()
	{
		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		// this will create a link element at the end of head
		MGWTStyle.getTheme().getMGWTClientBundle().getMainCss().ensureInjected();

		// append your own css as last thing in the head
		MGWTStyle.injectStyleSheet("TabletGUI.css");

		// Handle orientation changes
		MGWT.addOrientationChangeHandler(new OrientationChangeHandler()
		{
			@Override
			public void onOrientationChanged(OrientationChangeEvent event)
			{
				// TODO update whatever is shown right now, not necessarily the
				// euclidianViewPanel,
				// this is just a temporary workaround
				TabletGUI.this.euclidianViewPanel.getEuclidianView().updateSize();
				TabletGUI.this.euclidianViewPanel.repaint();
			}
		});

		// required to start the kernel
		this.euclidianViewPanel = new EuclidianViewPanel();
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel()
	{
		return this.euclidianViewPanel;
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel()
	{
		return this.algebraViewPanel;
	}

	/**
	 * Creates a new instance of {@link TouchController} and
	 * {@link MobileAlgebraController} and initializes the
	 * {@link EuclidianViewPanel euclidianViewPanel} and {@link AlgebraViewPanel
	 * algebraViewPanel} according to these instances.
	 * 
	 * @param kernel
	 *          Kernel
	 */
	@Override
	public void initComponents(final Kernel kernel)
	{
		TouchModel touchModel = new TouchModel(kernel);

		// Initialize GUI Elements
		this.headerPanel = new TabletHeaderPanel();
		this.leftHeader = new TabletHeaderPanelLeft(this, kernel, touchModel.getGuiModel());

		this.rightHeader = new TabletHeaderPanelRight(kernel);
		this.toolBar = new ToolBar();
		this.algebraViewPanel = new AlgebraViewPanel();

		TouchController ec = new TouchController(touchModel, kernel.getApplication());
		ec.setKernel(kernel);
		this.evAVpanel = new LayoutPanel();
		this.evAVpanel.getElement().setClassName("evAVpanel");
		this.euclidianViewPanel.initEuclidianView(ec);
		touchModel.getGuiModel().setEuclidianView(this.euclidianViewPanel.getEuclidianView());

		this.stylingBar = new StylingBar(touchModel, this.euclidianViewPanel.getEuclidianView());
		touchModel.getGuiModel().setStylingBar(this.stylingBar);

		this.algebraViewPanel.initAlgebraView(ec, kernel);
		this.toolBar.makeTabletToolBar(touchModel);
		this.evAVpanel.add(this.euclidianViewPanel);

		this.add(this.evAVpanel);
		this.add(this.headerPanel);
		this.add(this.leftHeader);
		this.add(this.rightHeader);
		this.add(this.stylingBar);

		this.evAVpanel.add(this.algebraViewPanel);

		this.add(this.toolBar);
	}

	public TabletHeaderPanel getTabletHeaderPanel()
	{
		return this.headerPanel;
	}
}
