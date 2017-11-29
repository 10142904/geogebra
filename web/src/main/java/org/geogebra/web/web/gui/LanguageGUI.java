package org.geogebra.web.web.gui;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.lang.Language;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.options.OptionsGlobalW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Dialog for language switching
 *
 */
public class LanguageGUI extends MyHeaderPanel implements SetLabels {

	/**
	 * App
	 */
	final AppW app;
	private LanguageHeaderPanel header;
	private Label activeLanguage = new Label();
	private FlowPanel fp = new FlowPanel();
	private ArrayList<Label> labels;
	private int cols;

	/**
	 * @param app
	 *            application
	 */
	public LanguageGUI(AppW app) {
		this.app = app;
		this.setStyleName("languageGUI");
		addHeader();
		addContent();
	}

	private void addContent() {
		fp.setStyleName("contentPanel");

		labels = new ArrayList<>();
		cols = estimateCols((int) app.getWidth());
		for (Language l : Language.values()) {
			if (!l.fullyTranslated && app.has(Feature.ALL_LANGUAGES)) {
				continue;
			}

			StringBuilder sb = new StringBuilder();

			String text = l.name;

			if (text != null) {

				char ch = text.toUpperCase().charAt(0);
				if (ch == Unicode.LEFT_TO_RIGHT_MARK
				        || ch == Unicode.RIGHT_TO_LEFT_MARK) {
					ch = text.charAt(1);
				} else {
					// make sure brackets are correct in Arabic, ie not )US)
					sb.setLength(0);
					sb.append(Unicode.LEFT_TO_RIGHT_MARK);
					sb.append(text);
					sb.append(Unicode.LEFT_TO_RIGHT_MARK);
					text = sb.toString();
				}

				final Label label = new Label(text);
				final Language current = l;

				if (current.localeGWT.equals(app.getLocalization()
				        .getLocaleStr())) {
					this.activeLanguage = label;
					activeLanguage.addStyleName("activeLanguage");
				}
				label.addClickHandler(getHandler(current, label));
				labels.add(label);
			}
		}
		placeLabels();

		this.setContentWidget(fp);
	}

	private void placeLabels() {
		int rows = (int) Math.ceil(labels.size() / (double) cols);
		for (int i = 0; i < rows * cols; i++) {
			int col = i % cols;
			int row = i / cols;
			if (col * rows + row < labels.size()) {
				fp.add(labels.get(col * rows + row));
			} else {
				// filler -- in last column we may need to skip some lines
				fp.add(new Label("\u00A0"));
			}
		}

		FlowPanel clear = new FlowPanel();
		clear.setStyleName("clear");
		fp.add(clear);

	}

	@Override
	public void onResize() {
		resizeCols((int) app.getWidth());
		super.onResize();
	}

	private void resizeCols(int width) {
		int newCols = estimateCols(width);
		if (newCols != cols) {
			cols = newCols;
			fp.clear();
			placeLabels();
		}

	}

	private int estimateCols(int appWidth) {
		int width = fp.getOffsetWidth(); // this one does not include scrollbar
		if (width == 0) {
			width = appWidth; // incl. scrollbar, but maybe fp not
											// attached yet
		}
		return Math.max(1, (width - 40) / 350);
	}

	private ClickHandler getHandler(final Language current, final Label label) {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				OptionsGlobalW.switchLanguage(current.localeGWT, app);

				LanguageGUI.this.setActiveLabel(label);
				LanguageGUI.this.close();
			}
		};
	}

	/**
	 * @param label
	 *            label to mark as active
	 */
	protected void setActiveLabel(Label label) {
		activeLanguage.removeStyleName("activeLanguage");
		activeLanguage = label;
		activeLanguage.addStyleName("activeLanguage");

	}

	private void addHeader() {
		this.header = new LanguageHeaderPanel(app.getLocalization(), this);

		this.setHeaderWidget(this.header);
		// this.addResizeListener(this.header);

	}

	@Override
	public void setLabels() {
		if (this.header != null) {
			this.header.setLabels();
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}

	@Override
	public void resizeTo(int width, int height) {
		resizeCols(width);

	}
}
