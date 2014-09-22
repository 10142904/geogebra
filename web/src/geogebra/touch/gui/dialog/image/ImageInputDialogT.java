package geogebra.touch.gui.dialog.image;

import geogebra.common.main.App;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppW;
import geogebra.touch.PhoneGapManager;
import geogebra.web.gui.dialog.image.UploadImageDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.googlecode.gwtphonegap.client.camera.PictureCallback;
import com.googlecode.gwtphonegap.client.camera.PictureOptions;

/**
 *
 */
public class ImageInputDialogT extends UploadImageDialog {
	private static final String PREVIEW_HEIGHT = "155px";
	private static final String PREVIEW_WIDTH = "213px";
	private final int PICTURE_QUALITY = 25;
	private SimplePanel cameraPanel;
	private SimplePanel picturePanel;
	private Label camera;
	private String pictureFromCameraString = "";
	private String pictureFromFileString = "";
	private FlowPanel filePanel;
	private StandardButton chooseFromFile;
	private PictureOptions options;
	private boolean cameraIsActive;

	
	/**
	 * @param app {@link App}
	 */
	public ImageInputDialogT(App app) {
		super((AppW) app, PREVIEW_WIDTH, PREVIEW_HEIGHT);
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		listPanel.add(camera = new Label(""));
		
		initFilePanel();
		initCameraPanel();
	}
	
	private void initCameraPanel() {
		cameraPanel = new SimplePanel();
		cameraPanel.setStyleName("inputPanel");
		cameraPanel.setSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    }

	private void initFilePanel() {
		this.options = new PictureOptions(this.PICTURE_QUALITY);
		this.options.setSourceType(PictureOptions.PICTURE_SOURCE_TYPE_SAVED_PHOTO_ALBUM);//.PICTURE_SOURCE_TYPE_PHOTO_LIBRARY);
		
		filePanel = new FlowPanel();
		filePanel.add(chooseFromFile = new StandardButton(app.getMenu("ChooseFromFile")));
		chooseFromFile.addStyleName("gwt-Button");
		chooseFromFile.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				openNative();
			}
		});
		
		filePanel.add(picturePanel = new SimplePanel());
		picturePanel.setStyleName("inputPanel");
		picturePanel.setSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    }

	void openNative() {
		PhoneGapManager.getPhoneGap().getCamera().getPicture(options, new PictureCallback() {

					@Override
					public void onSuccess(final String pictureBase64) {
						setPicturePreview(pictureBase64);
					}

					@Override
					public void onFailure(final String arg0) {
						// TODO Auto-generated method stub
					}
				});
	}
	
	@Override
	protected void initActions() {
		super.initActions();
		camera.addClickHandler(this);
	}
	
	@Override
	public void setLabels() {
		super.setLabels();
		//TODO Translation
		camera.setText("Camera");
	}
	
	@Override
    public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == insertBtn) {
			if (!loc.isLabelSet()) {
	    		loc.setLabel(null);
	    	}
			if (this.cameraIsActive && !this.pictureFromCameraString.equals("")) {
		    	app.imageDropHappened("devicePicture", this.pictureFromCameraString, "", loc);
			} else if (!this.cameraIsActive && !this.pictureFromFileString.equals("")) {
				app.imageDropHappened("devicePicture", this.pictureFromFileString, "", loc);
			}
	    	hide();
	    } else if (source == cancelBtn) {
	    	hide();
	    } else if (source == upload) {
	    	uploadClicked();
	    } else if (source == camera) {
	    	cameraClicked();
	    }
    }

	@Override
	protected void uploadClicked() {
		if (this.pictureFromFileString != null && !this.pictureFromFileString.equals("")) {
			imageAvailable();
		} else {
			imageUnavailable();
		}
		this.cameraIsActive = false;
		this.upload.addStyleDependentName("highlighted");
		this.camera.removeStyleDependentName("highlighted");
		this.inputPanel.setWidget(this.filePanel);
	}
	
	private void cameraClicked() {
		if (this.pictureFromCameraString != null && !this.pictureFromCameraString.equals("")) {
			imageAvailable();
		} else {
			imageUnavailable();
		}
		this.cameraIsActive = true;
		this.camera.addStyleDependentName("highlighted");
		this.upload.removeStyleDependentName("highlighted");
		this.inputPanel.setWidget(this.cameraPanel);
		PhoneGapManager.getPhoneGap().getCamera().getPicture(new PictureOptions(this.PICTURE_QUALITY),
				new PictureCallback() {

					@Override
					public void onSuccess(final String pictureBase64) {
						setPicturePreview(pictureBase64);
					}

					
					@Override
					public void onFailure(final String arg0) {
						//TODO couldn't get camera
					}
				});
    }

	/**
	 * @param pictureBase64 String
	 */
	void setPicturePreview(String pictureBase64) {
		if (cameraIsActive) {
			this.pictureFromCameraString = "data:image/jpg;base64," + pictureBase64;
			this.cameraPanel.clear();
	        this.cameraPanel.getElement().getStyle().setBackgroundImage("url('" + this.pictureFromCameraString + "')");
		} else {
			this.pictureFromFileString = "data:image/jpg;base64," + pictureBase64;
			this.picturePanel.clear();
	        this.picturePanel.getElement().getStyle().setBackgroundImage("url('" + this.pictureFromFileString + "')");
		}

        imageAvailable();
    }
	
	@Override
	public void hide() {
		super.hide();
		this.cameraPanel.getElement().getStyle().setBackgroundImage("none");
		this.picturePanel.getElement().getStyle().setBackgroundImage("none");
		this.pictureFromCameraString = "";
		this.pictureFromFileString = "";
	}

}
