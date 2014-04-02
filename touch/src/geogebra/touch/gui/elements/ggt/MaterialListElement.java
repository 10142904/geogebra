package geogebra.touch.gui.elements.ggt;

import geogebra.common.kernel.commands.CmdGetTime;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.util.Unicode;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppWeb;
import geogebra.touch.FileManagerT;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.DefaultResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends HorizontalPanel implements
		ResizeListener {
	private SimplePanel image;
	private VerticalPanel infos;
	private VerticalPanel links;
	private Label title, date;
	private Label sharedBy;
	private final Material material;
	private final AppWeb app;
	private final FileManagerT fm;
	private HorizontalPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private boolean isSelected = false;

	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel()
			.getIcons();
	private final StandardButton openButton = new StandardButton(
			LafIcons.document_viewer());
	private final StandardButton editButton = new StandardButton(
			LafIcons.document_edit());
	private final StandardButton deleteButton = new StandardButton(
			LafIcons.dialog_trash());

	MaterialListElement(final Material m, final AppWeb app) {

		this.app = app;
		this.material = m;
		this.fm = ((TouchApp) app).getFileManager();
		this.setStyleName("browserFile");
		TouchEntryPoint.getBrowseGUI().addResizeListener(this);

		this.initButtons();
		this.initConfirmDeletePanel();
		this.initMaterialInfos();

		final VerticalPanel centeredContent = new VerticalPanel();
		centeredContent.setStyleName("centeredContent");
		centeredContent.add(this.infos);

		if (this.isLocalFile()) {
			centeredContent.add(this.confirmDeletePanel);
		}
		this.add(centeredContent);
		this.add(this.links);

		// clearPanel clears flow layout (needed for styling)
		final LayoutPanel clearPanel = new LayoutPanel();
		clearPanel.setStyleName("fileClear");
		this.add(clearPanel);

		this.markUnSelected();

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				materialSelected();

			}
		}, ClickEvent.getType());
	}

	void materialSelected() {
		if (this.isSelected) {
			if (this.isLocalFile()) {
				onEdit();
			} else {
				onOpen();
			}
		} else {
			this.markSelected();
		}
	}

	private void initMaterialInfos() {
		this.image = new SimplePanel();
		this.image.addStyleName("fileImage");
		this.infos = new VerticalPanel();
		this.infos.setStyleName("fileDescription");

		this.title = new Label(this.material.getTitle());
		this.title.setStyleName("fileTitle");
		this.infos.add(this.title);

		this.add(this.image);
		if (!this.isLocalFile()) {
			this.image
					.getElement()
					.getStyle()
					.setBackgroundImage(
							"url(http:" + this.material.getThumbnail() + ")");

			// no shared Panel for local files
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
			this.infos.add(this.sharedBy);

		} else {
			this.image
					.getElement()
					.getStyle()
					.setBackgroundImage(
							"url(" + this.material.getThumbnail() + ")");
		}

		final String format = this.app.getLocalization()
				.isRightToLeftReadingOrder() ? "\\Y " + Unicode.LeftToRightMark
				+ "\\F" + Unicode.LeftToRightMark + " \\j" : "\\j \\F \\Y";

		this.date = new Label(CmdGetTime.buildLocalizedDate(format,
				this.material.getDate(), this.app.getLocalization()));
		this.infos.add(this.date);
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain(
				"Delete"));
		this.confirm.addStyleName("confirmButton");
		this.confirm.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onConfirmDelete();
			}
		});
		this.cancel = new StandardButton(this.app.getLocalization().getPlain(
				"Cancel"));
		this.cancel.addStyleName("confirmCancelButton");
		this.cancel.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onCancel();
			}
		});

		this.confirmDeletePanel = new HorizontalPanel();
		this.confirmDeletePanel.add(this.confirm);
		this.confirmDeletePanel.add(this.cancel);
		this.confirmDeletePanel.setStyleName("confirmDelete");
		this.confirmDeletePanel.setVisible(false);
	}

	private void initButtons() {
		this.links = new VerticalPanel();
		this.links.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.links.setStyleName("fileLinks");

		this.initOpenButton();
		this.initEditButton();
		// remote material should not have this visible
		if (this.isLocalFile()) {
			this.initDeleteButton();
		}
	}

	private void initDeleteButton() {

		this.links.add(this.deleteButton);
		this.deleteButton.addStyleName("delete");
		this.deleteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onDelete();
			}
		});
	}

	void onDelete() {
		this.confirmDeletePanel.setVisible(true);
		this.links.setVisible(false);
	}

	private void initEditButton() {
		this.links.add(this.editButton);
		this.editButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onEdit();
			}
		});
	}

	void onEdit() {
		this.fm.getMaterial(this.material, this.app);
		TouchEntryPoint.allowEditing(true);
		TouchEntryPoint.goBack();
	}

	private void initOpenButton() {
		this.links.add(this.openButton);
		this.openButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onOpen();
			}
		});
	}

	void onOpen() {
		TouchEntryPoint.showWorksheetGUI(this.material);
	}

	private void markSelected() {
		this.isSelected = true;
		TouchEntryPoint.getBrowseGUI().unselectMaterials();
		this.addStyleName("selected");
		this.links.setVisible(true);
		this.confirmDeletePanel.setVisible(false);
		TouchEntryPoint.getBrowseGUI().rememberSelected(this);
	}

	void onConfirmDelete() {
		this.fm.delete(this.material);
	}

	void onCancel() {
		this.links.setVisible(true);
		this.confirmDeletePanel.setVisible(false);
	}

	public void markUnSelected() {
		this.isSelected = false;
		this.removeStyleName("selected");
		this.links.setVisible(false);
		this.confirmDeletePanel.setVisible(false);
	}

	void setLabels() {
		this.sharedBy.setText(this.app.getLocalization().getPlain("SharedByA",
				this.material.getAuthor()));
	}

	private boolean isLocalFile() {
		return this.material.getId() <= 0;
	}

	@Override
	public void onResize() {
		if (Window.getClientWidth() < 780) {
			this.image.addStyleName("scaleImage");
		} else {
			this.image.removeStyleName("scaleImage");
		}
	}

	public String getMaterialTitle() {
		return this.material.getTitle();
	}

	public Material getMaterial() {
		return this.material;
	}
}