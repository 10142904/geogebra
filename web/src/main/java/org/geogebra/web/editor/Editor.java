package org.geogebra.web.editor;

import org.geogebra.keyboard.web.KeyboardResources;
import org.geogebra.keyboard.web.UpdateKeyBoardListener;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.keyboard.OnscreenTabbedKeyboard;
import org.geogebra.web.resources.StyleInjector;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.web.JlmEditorLib;
import com.himamis.retex.editor.web.MathFieldW;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.web.CreateLibrary;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.font.opentype.Opentype;

/**
 * Standalone editor with GGB keyboard
 * 
 * @author Zbynek
 *
 */
public class Editor implements EntryPoint, MathFieldListener {
	private JlmEditorLib library;
	private Opentype opentype;

	@Override
	public void onModuleLoad() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderGWT());
		}
		library = new JlmEditorLib();
		opentype = Opentype.INSTANCE;
		CreateLibrary.exportLibrary(library, opentype);
		addEditorFunction(this);
		StyleInjector.inject(KeyboardResources.INSTANCE.keyboardStyle());
	}

	/**
	 * @param parent
	 *            editor parent
	 */
	public void edit(Element parent) {
		Canvas canvas = Canvas.createIfSupported();
		String id = "JlmEditorKeyboard" + DOM.createUniqueId();
		parent.setId(id);
		RootPanel parentWidget = RootPanel.get(id);
		Element el = DOM.createDiv();
		el.appendChild(canvas.getCanvasElement());
		MathFieldW fld = new MathFieldW(null, parentWidget,
				canvas,
				this, false, null);
		final OnscreenTabbedKeyboard kb = new OnscreenTabbedKeyboard(
				new KeyboardContext());
		kb.setListener(new UpdateKeyBoardListener() {

			@Override
			public boolean keyBoardNeeded(boolean show,
					MathKeyboardListener textField) {
				return true;
				// no real app frame
			}

			@Override
			public void doShowKeyBoard(boolean b,
					MathKeyboardListener textField) {
				// no real app frame
			}
		});
		kb.setProcessing(new MathFieldProcessing(fld));

		parentWidget.add(fld.asWidget());
		parentWidget.add(kb);
		kb.show();
		// Timer t = new Timer() {
		//
		// @Override
		// public void run() {
		// kb.updateSize();
		// kb.setStyleName();
		// }
		// };
		// t.schedule(0);
		// fld.requestViewFocus();
	}

	private native void addEditorFunction(Editor lib) /*-{
		$wnd.tryEditor = function(el) {
			lib.@org.geogebra.web.editor.Editor::edit(Lcom/google/gwt/dom/client/Element;)(el);
		}
	}-*/;

	@Override
	public void onEnter() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onKeyTyped() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCursorMove() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDownKeyPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpKeyPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public String serialize(MathSequence selectionText) {
		return GeoGebraSerializer.serialize(selectionText);
	}

	@Override
	public void onInsertString() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onEscape() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTab(boolean shiftDown) {
		// TODO Auto-generated method stub
	}

}
