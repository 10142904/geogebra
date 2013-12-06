package geogebra.html5.gui.inputfield;

import geogebra.common.gui.util.TableSymbols;
import geogebra.common.gui.util.TableSymbolsLaTeX;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.Localization;
import geogebra.common.util.Unicode;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel with symbols and GeoElements to be inserted into the GeoText editor
 * 
 * @author G. Sturr
 * 
 */
public class TextEditAdvancedPanel extends TabLayoutPanel {

	private AppW app;
	protected ITextEditPanel editPanel;

	private ListBox geoPanel;
	private VerticalPanel symbolPanel;
	private VerticalPanel latexPanel;
	private TextPreviewPanelW previewer;
	private Localization loc;

	public TextEditAdvancedPanel(AppW app, ITextEditPanel editPanel) {
		super(30, Unit.PX);
		this.app = app;
		this.editPanel = editPanel;
		loc = app.getLocalization();

		addStyleName("textEditorAdvancedPanel");
		
		createGeoListBox();
		createSymbolPanel();
		createLatexPanel();

		getPreviewer();
		previewer.onResize();

		Image geoTabImage = new Image(AppResources.INSTANCE.geogebra()
		        .getSafeUri().asString());

		// create the tabs
		add(new ScrollPanel(getPreviewer().getPanel()),
		        loc.getMenu("Preview"));
		add(geoPanel, geoTabImage);
		add(new ScrollPanel(symbolPanel), Unicode.alphaBetaGamma + "");
		add(new ScrollPanel(latexPanel), loc.getMenu("LaTeXFormula"));

		registerListeners();
		setLabels();
	}

	private void registerListeners() {

		// update the geoPanel when selected
		addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem() == 1) {
					updateGeoList();
					geoPanel.setFocus(true);
				}
			}
		});
	}

	public TextPreviewPanelW getPreviewer() {
		if (previewer == null) {
			previewer = new TextPreviewPanelW(app.getKernel());
			previewer.getPanel().setStyleName("previewPanel");
		}
		return previewer;
	}

	public void setLabels() {
		setTabText(0, loc.getMenu("Preview"));
		setTabText(3, loc.getPlain("LaTeXFormula"));
	}

	// =====================================================
	// GeoElement panel
	// =====================================================

	private void createGeoListBox() {
		geoPanel = new ListBox(true);
		geoPanel.setWidth("100%");
		geoPanel.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		geoPanel.setVisibleItemCount(10);

		geoPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String label = geoPanel.getItemText(geoPanel.getSelectedIndex());
				editPanel.insertGeoElement(app.getKernel().lookupLabel(label));
			}
		});
	}

	public void updateGeoList() {
		geoPanel.clear();
		String[] geoLabels = getGeoObjectList(editPanel.getEditGeo());
		for (int i = 0; i < geoLabels.length; i++) {
			geoPanel.addItem(geoLabels[i]);
		}
	}

	/**
	 * Creates an array of labels of existing geos that can be inserted into the
	 * editor content
	 */
	private String[] getGeoObjectList(GeoText editGeo) {

		TreeSet<GeoElement> ts = app.getKernel().getConstruction()
		        .getGeoSetLabelOrder();
		ArrayList<String> list = new ArrayList<String>();

		// first possibility : create empty box
		list.add(app.getPlain("EmptyBox"));

		// add all geos
		Iterator<GeoElement> iter = ts.iterator();
		while (iter.hasNext()) {
			GeoElement g = iter.next();
			if (g.isLabelSet() && !g.equals(editGeo)) {
				list.add(g.getLabelSimple());
			}
		}
		String[] geoArray = new String[list.size()];
		geoArray = list.toArray(geoArray);
		return geoArray;
	}

	// =====================================================
	// Symbol panel
	// =====================================================

	private void createSymbolPanel() {

		int defaultRowSize = 15;

		symbolPanel = new VerticalPanel();
		symbolPanel.setWidth("100%");
		symbolPanel.setHeight("100%");

		addTable(TableSymbols.basicSymbols(app.getLocalization()), false,
		        defaultRowSize, false);
		addTable(TableSymbols.operators, false, defaultRowSize, true);
		addTable(TableSymbols.greekLettersPlusVariants(), false,
		        defaultRowSize, true);
		addTable(TableSymbols.analysis, false, defaultRowSize, true);
		addTable(TableSymbols.sets, false, defaultRowSize, true);
		addTable(TableSymbols.logical, false, defaultRowSize, true);
		addTable(TableSymbols.sub_superscripts, false, defaultRowSize, true);
		addTable(TableSymbols.basic_arrows, false, defaultRowSize, true);
		addTable(TableSymbols.otherArrows, false, defaultRowSize, true);
		addTable(TableSymbols.geometricShapes, false, defaultRowSize, true);
		addTable(TableSymbols.games_music, false, defaultRowSize, true);
		addTable(TableSymbols.currency, false, defaultRowSize, true);
		addTable(TableSymbols.handPointers, false, defaultRowSize, true);

	}

	private void addTable(String[] tableSymbols, boolean isLatex, int rowSize,
	        boolean addSeparator) {

		final SymbolTableW symTable = newSymbolTable(tableSymbols, isLatex,
		        rowSize);

		if (addSeparator) {
			symbolPanel.add(new HTML("<hr>"));
		}
		symbolPanel.add(symTable);
	}

	// =====================================================
	// LaTeX panel
	// =====================================================

	private void createLatexPanel() {

		int defaultRowSize = 15;

		latexPanel = new VerticalPanel();
		latexPanel.setWidth("100%");
		latexPanel.setHeight("100%");

		addLaTeXTable(TableSymbolsLaTeX.roots_fractions, "RootsAndFractions",
		        defaultRowSize, false);
		addLaTeXTable(TableSymbolsLaTeX.sums, "SumsAndIntegrals",
		        defaultRowSize, true);
		addLaTeXTable(TableSymbolsLaTeX.accents, "Accents", defaultRowSize,
		        true);
		addLaTeXTable(TableSymbolsLaTeX.accentsExtended, "AccentsExt",
		        defaultRowSize, true);
		addLaTeXTable(TableSymbolsLaTeX.brackets, "Brackets", defaultRowSize,
		        true);
		// addLaTeXTable(TableSymbolsLaTeX.matrices, "Matrices", defaultRowSize,
		// true);
		// addLaTeXTable(TableSymbolsLaTeX.mathfrak(), "FrakturLetters",
		// defaultRowSize, true);
		// addLaTeXTable(TableSymbolsLaTeX.mathcal(), "CalligraphicLetters",
		// defaultRowSize, true);
		// addLaTeXTable(TableSymbolsLaTeX.mathbb(), "BlackboardLetters",
		// defaultRowSize, true);
		// addLaTeXTable(TableSymbolsLaTeX.mathscr(), "CursiveLetters",
		// defaultRowSize, true);

	}

	private void addLaTeXTable(String[] tableSymbols, String header,
	        int rowSize, boolean addSeparator) {

		final SymbolTableW symTable = newSymbolTable(tableSymbols, true,
		        rowSize);

		if (addSeparator) {
			latexPanel.add(new HTML("<hr>"));
		}

		// latexPanel.add(new Label(header));
		latexPanel.add(symTable);
	}

	// =====================================================
	// Symbol table utilities
	// =====================================================

	private SymbolTableW newSymbolTable(String[] table, boolean isLatexSymbol,
	        int rowSize) {

		final boolean isLatex = isLatexSymbol;
		final SymbolTableW symTable = new SymbolTableW(table, null, isLatex,
		        rowSize);

		symTable.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Cell clickCell = ((HTMLTable) event.getSource())
				        .getCellForEvent(event);

				String text = symTable.getSymbolText(clickCell.getRowIndex(),
				        clickCell.getCellIndex());

				editPanel.insertTextString(text, isLatex);

				// editPanel.insertTextString(clickCell.getElement()
				// .getInnerText(), false);
			}
		});
		return symTable;
	}

}
