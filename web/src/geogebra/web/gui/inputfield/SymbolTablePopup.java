package geogebra.web.gui.inputfield;

import geogebra.common.gui.util.TableSymbols;
import geogebra.web.main.Application;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;

public class SymbolTablePopup extends PopupPanel implements ClickHandler {
	
	Grid symbolTable = null;
	private Application app;
	private AutoCompleteTextField textField;

	public SymbolTablePopup(Application app,
            AutoCompleteTextField autoCompleteTextField) {
	   this.app = app;
	   this.textField = autoCompleteTextField;
	   createSymbolTable();
	   registerListeners();
    }
	
	private void createSymbolTable() {
		String [] icons = TableSymbols.basicSymbols(app);
		String [] iconshelp = TableSymbols.basicSymbolsToolTips(app);
		symbolTable = new Grid((int) Math.ceil(icons.length/10),10);
		symbolTable.addStyleName("SymbolTable");
		
		
		for (int i = 0; i < icons.length; i++) {
			int col = (int) Math.floor(i % 10);
			int row = (int) Math.floor(i / 10);
			Anchor a = new Anchor(icons[i]);
			a.setTitle(iconshelp[i]);
	        a.addClickHandler(this);
	        symbolTable.setWidget(row, col, a);
		}
		add(symbolTable);
	}
	
	private void registerListeners() {
		
	}

	public void onClick(ClickEvent event) {
	   Anchor target = (Anchor) event.getSource();
	   textField.insertString(target.getText());
	   textField.toggleSymbolButton(false);
	   hide();
    }

}
