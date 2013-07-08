package geogebra.touch.gui.dialogs;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.StandardImageButton;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Abstract class for open / save Dialogs. Provides consistent GUI and methods
 * for both Dialogs
 * 
 * @author Matthias Meisinger
 */
public abstract class FileDialog extends PopupPanel
{
	protected TouchApp app;

	private VerticalPanel dialogPanel;
	private Label title;

	protected FileManagerM fm;

	protected ListBox fileList;
	protected TextBox textBox;

	private HorizontalPanel buttonContainer;

//	private StandardImageButton okButton = new StandardImageButton(CommonResources.INSTANCE.dialog_ok());
	protected Anchor okButton = new Anchor();
	private StandardImageButton cancelButton = new StandardImageButton(CommonResources.INSTANCE.dialog_cancel());
	private StandardImageButton deleteButton = new StandardImageButton(CommonResources.INSTANCE.dialog_trash());

	public FileDialog(TouchApp app)
	{
		// hide when clicked outside and set modal
		super(true, true);
		this.setGlassEnabled(true);

		this.app = app;
		this.fm = new FileManagerM();
		this.dialogPanel = new VerticalPanel();
		this.title = new Label();
		this.textBox = new TextBox();
		this.textBox.addKeyUpHandler(new KeyUpHandler()
		{

			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					FileDialog.this.onOK();
				}
			}
		});

		this.fileList = new ListBox();
		this.fileList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (FileDialog.this.fileList.getSelectedIndex() != -1)
				{
					String selectedText = FileDialog.this.fileList.getItemText(FileDialog.this.fileList.getSelectedIndex());
					FileDialog.this.textBox.setText(selectedText);
				}
			}
		});

		this.buttonContainer = new HorizontalPanel();
		this.buttonContainer.setWidth("100%");

		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.dialogPanel.add(this.title);

		this.dialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.fileList.setVisibleItemCount(5);
		this.fileList.setWidth("100%");
		this.dialogPanel.add(this.fileList);

		this.textBox.setVisibleLength(50);
		this.dialogPanel.add(this.textBox);

		this.buttonContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		HorizontalPanel left = new HorizontalPanel();
		left.add(this.deleteButton);
		this.buttonContainer.add(left);

		this.buttonContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		HorizontalPanel right = new HorizontalPanel();
		right.add(this.cancelButton);
		right.add(this.okButton);
		this.buttonContainer.add(right);
		this.dialogPanel.add(this.buttonContainer);

		populateFileList();

		this.add(this.dialogPanel);

		initDeleteButton();
		initCancelButton();
		initOKButton();
	}

	/**
	 * Populates the fileList with available files
	 */
	protected void populateFileList()
	{
		this.fm.toList(this.fileList);
	}

	private void initDeleteButton()
	{
		this.deleteButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				FileDialog.this.onDelete();
			}
		});
	}

	private void initCancelButton()
	{
		this.cancelButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				FileDialog.this.onCancel();
			}
		}, ClickEvent.getType());
	}

	private void initOKButton()
	{
		String html = "<img src=\"" + CommonResources.INSTANCE.dialog_ok().getSafeUri().asString() + "\" style=\"height:48px; width:48px; margin:auto;\">";
		this.okButton.getElement().setInnerHTML(html);
		this.okButton.getElement().setAttribute("style", "display:block;");
		this.okButton.setStyleName("gwt-PushButton");
		this.okButton.addStyleName("gwt-PushButton-up");
		
		this.okButton.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				FileDialog.this.onOK();
			}
		}, ClickEvent.getType());
	}

	protected abstract void onOK();

	protected abstract void onCancel();

	protected void onDelete()
	{
		this.fm.delete(this.textBox.getText());
	}

	@Override
	public void show()
	{
		super.show();
		super.center();
		this.populateFileList();
		this.textBox.setFocus(true);
	}
	
	public abstract void setLabels();

	public FileManagerM getFileManager() {
		return this.fm;
	}
}