package geogebra.touch.gui.elements.header;

import geogebra.html5.gui.ResizeListener;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.dialogs.InfoDialog;
import geogebra.touch.gui.dialogs.InfoDialog.InfoType;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.TitleChangedListener;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Extends from {@link HorizontalPanel}.
 */
public class TabletHeaderPanel extends HorizontalPanel implements
		ResizeListener {
	private final TabletHeaderFile fileHeader;
	private final VerticalPanel titlePanel;
	private Panel underline;
	private TextBox worksheetTitle;
	private final TabletHeaderUndoRedo undoRedoHeader;
	private InfoDialog infoOverrideDialog;
	private TouchApp app;
	private FileManagerT fm;
	private boolean ignoreNextMouseUp;

	public TabletHeaderPanel(final TouchApp app, final TouchModel touchModel) {
		this.setStyleName("headerbar");

		this.app = app;
		this.fm = this.app.getFileManager();
		this.fileHeader = new TabletHeaderFile(app, touchModel, this);
		this.fileHeader.setStyleName("headerFirst");
		this.infoOverrideDialog = new InfoDialog(this.app, InfoType.Override);
		this.ignoreNextMouseUp = TouchEntryPoint.getLookAndFeel()
				.receivesDoubledEvents();

		this.titlePanel = new VerticalPanel();

		this.worksheetTitle = new TextBox();
		this.worksheetTitle.setText(app.getConstructionTitle());

		this.app.addTitleChangedListener(new TitleChangedListener() {
			@Override
			public void onTitleChange(final String title) {
				setConstructionTitle(title);
			}
		});

		this.undoRedoHeader = new TabletHeaderUndoRedo(app, touchModel);
		this.undoRedoHeader.setStyleName("headerSecond");
		this.worksheetTitle.setStyleName("worksheetTitle");

		this.worksheetTitle.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					onChangeTitle();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					onCancel();
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					event.preventDefault();
					return;
				}
			}
		});

		this.worksheetTitle.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(final MouseOutEvent event) {
				onCancel();
			}
		});

		this.worksheetTitle.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				Scheduler.get().scheduleDeferred(
						new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								onFocusTitle();
							}
						});
			}
		});

		this.worksheetTitle.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(final BlurEvent event) {
				onBlurTitle();
			}
		});

		this.titlePanel.add(this.worksheetTitle);

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");
		this.titlePanel.getElement().setAttribute("width", "100%");

		this.titlePanel.add(this.underline);

		this.add(this.fileHeader);

		this.add(this.titlePanel);

		this.add(this.undoRedoHeader);

	}

	protected void onChangeTitle() {
		this.fm.hasFile(this.worksheetTitle.getText(),
				new Callback<Boolean, Boolean>() {

					@Override
					public void onSuccess(final Boolean hasFile) {
						if (hasFile) {
							TabletHeaderPanel.this.infoOverrideDialog
									.setConsTitle(TabletHeaderPanel.this.worksheetTitle
											.getText());
							TabletHeaderPanel.this.infoOverrideDialog.show();
						} else if (TabletHeaderPanel.this.worksheetTitle
								.getText().equals("")) {
							TabletHeaderPanel.this.worksheetTitle
									.setText(TabletHeaderPanel.this.app
											.getConstructionTitle());
							TabletHeaderPanel.this.worksheetTitle
									.setFocus(false);
						} else {
							TabletHeaderPanel.this.app
									.setConstructionTitle(TabletHeaderPanel.this.worksheetTitle
											.getText());
							TabletHeaderPanel.this.fm
									.saveFile(TabletHeaderPanel.this.app);
							TabletHeaderPanel.this.worksheetTitle
									.setFocus(false);
						}

					}

					@Override
					public void onFailure(final Boolean reason) {
						// TODO Auto-generated method stub

					}
				});
	}

	protected void onCancel() {
		this.worksheetTitle.setText(this.app.getConstructionTitle());
		this.worksheetTitle.setFocus(false);
	}

	protected void onFocusTitle() {
		this.worksheetTitle.setFocus(true);
		this.worksheetTitle.selectAll();
		this.underline.removeStyleName("inactive");
		this.underline.addStyleName("active");
	}

	protected void onBlurTitle() {
		if (this.ignoreNextMouseUp) {
			this.ignoreNextMouseUp = false;
			this.worksheetTitle.setFocus(true);
			return;
		}

		this.ignoreNextMouseUp = TouchEntryPoint.getLookAndFeel()
				.receivesDoubledEvents();
		this.worksheetTitle.setFocus(false);
		this.underline.removeStyleName("active");
		this.underline.addStyleName("inactive");
	}

	public void editTitle() {
		this.worksheetTitle.setFocus(true);
		this.worksheetTitle.selectAll();
	}

	/**
	 * Enable or disable the buttons redo, undo and save.
	 * 
	 */
	public void enableDisableButtons() {
		this.fileHeader.enableDisableSave();
		this.undoRedoHeader.enableDisableRedo();
		this.undoRedoHeader.enableDisableUndo();
	}

	public String getConstructionTitle() {
		return this.worksheetTitle.getText();
	}

	protected void setConstructionTitle(final String title) {
		this.worksheetTitle.setText(title);
	}

	public TabletHeaderFile getLeftHeader() {
		return this.fileHeader;
	}

	public TabletHeaderUndoRedo getRightHeader() {
		return this.undoRedoHeader;
	}

	@Override
	public void onResize() {
		this.setWidth(Window.getClientWidth() + "px");
	}

	public void setLabels() {
		this.fileHeader.setLabels();
		this.infoOverrideDialog.setLabels();
	}
}