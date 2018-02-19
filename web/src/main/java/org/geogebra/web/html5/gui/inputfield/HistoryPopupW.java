package org.geogebra.web.html5.gui.inputfield;

import java.util.ArrayList;

import org.geogebra.web.html5.gui.GPopupPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.himamis.retex.editor.share.util.GWTKeycodes;

public class HistoryPopupW extends GPopupPanel implements ClickHandler,
        KeyUpHandler, ChangeHandler {

	private AutoCompleteW textField;
	private boolean downPopup;
	private ListBox historyList;
	private String originalTextEditorContent;

	public HistoryPopupW(AutoCompleteW autoCompleteTextField, Panel root) {
		super(root, autoCompleteTextField.getApplication());
		this.textField = autoCompleteTextField;

		historyList = new ListBox();
		historyList.addChangeHandler(this);
		historyList.addKeyUpHandler(this);
		historyList.addClickHandler(this);
		historyList.addStyleName("historyList");

		add(historyList);
		addStyleName("GeoGebraPopup");
		setAutoHideEnabled(true);
	}

	public void setDownPopup(boolean isDownPopup) {
		this.downPopup = isDownPopup;
	}

	public void showPopup() {
		ArrayList<String> list = textField.getHistory();
		if (list.isEmpty()) {
			return;
		}

		originalTextEditorContent = textField.getText();
		historyList.clear();
		historyList.setVisibleItemCount(Math.min(Math.max(list.size(), 2), 10));

		for (String link : list) {
			historyList.addItem(link);
		}

		show();
		setPopupPosition(textField.getAbsoluteLeft(),
		        textField.getAbsoluteTop() - getOffsetHeight());

		historyList.setSelectedIndex(list.size() - 1);

		// focus one extra time in case the setText method would freeze
		// e.g. due to bad formula string
		historyList.setFocus(true);

		textField.setText(historyList.getItemText(historyList
		        .getSelectedIndex()));

		historyList.setFocus(true);
	}

	public boolean isDownPopup() {
		return downPopup;
	}

	@Override
	public void onChange(ChangeEvent event) {
		textField.setText(historyList.getItemText(historyList
		        .getSelectedIndex()));
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		int charCode = event.getNativeKeyCode();
		switch (charCode) {
		default:
			// do nothing
			break;
		case GWTKeycodes.KEY_ESCAPE:
			hide();
			textField.setText(originalTextEditorContent);
			textField.setFocus(true, false);
			break;
		case GWTKeycodes.KEY_ENTER:
			hide();
			textField.setFocus(true, false);
			break;
		}

	}

	@Override
	public void onClick(ClickEvent event) {
		hide();
	}

}
