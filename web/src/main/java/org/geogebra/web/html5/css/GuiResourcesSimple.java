package org.geogebra.web.html5.css;

import org.geogebra.web.html5.util.LessResource;
import org.geogebra.web.resources.LessReference;
import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface GuiResourcesSimple extends ClientBundle {

	GuiResourcesSimple INSTANCE = GWT.create(GuiResourcesSimple.class);

	@Source("org/geogebra/web/resources/js/zipjs/deflate.js")
	TextResource deflateJs();

	@Source("org/geogebra/common/icons_play/p24/nav_play_circle.png")
	ImageResource icons_play_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_play_circle_hover.png")
	ImageResource icons_play_circle_hover();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle.png")
	ImageResource icons_play_pause_circle();

	@Source("org/geogebra/common/icons_play/p24/nav_pause_circle_hover.png")
	ImageResource icons_play_pause_circle_hover();

	@Source("org/geogebra/web/resources/js/jquery-1.7.2.min.js")
	TextResource jQueryJs();

	@Source("org/geogebra/web/resources/js/zipjs/inflate.js")
	TextResource inflateJs();

	@Source("org/geogebra/web/resources/js/zipjs/arraybuffer.js")
	TextResource arrayBufferJs();

	@Source("org/geogebra/web/resources/js/zipjs/zip-ext.js")
	TextResource dataViewJs();

	@Source("org/geogebra/web/resources/js/zipjs/zip.js")
	TextResource zipJs();

	// used by ExamUtil (eg toggleFullScreen)
	@Source("org/geogebra/web/resources/js/visibility.js")
	TextResource visibilityJs();

	@Source("org/geogebra/web/resources/js/jquery-ui.js")
	TextResource jqueryUI();

	@Source("org/geogebra/web/resources/js/domvas.js")
	TextResource domvas();

//	@Source("org/geogebra/web/resources/js/WebMIDIAPIWrapper.js")
//	TextResource webMidiAPIWrapperJs();
//
//	@Source("org/geogebra/web/resources/js/midi/jasmid.js")
//	TextResource jasmidJs();


	@Source("org/geogebra/web/resources/css/web-styles.less")
	LessResource modernStyle();

	@Source("org/geogebra/web/resources/css/web-styles-global.less")
	LessResource modernStyleGlobal();

	// New less resources
	@Source("org/geogebra/web/resources/css/definitions.less")
	LessReference lessDefinitions();

	@Source("org/geogebra/web/resources/css/functions.less")
	LessReference lessFunctions();

	@Source("org/geogebra/web/resources/css/general.ltr.less")
	LessResource generalStyleLTR();

	@Source("org/geogebra/web/resources/css/general.rtl.less")
	LessResource generalStyleRTL();

	@Source("org/geogebra/web/resources/css/general.less")
	LessReference generalStyle();

	@Source("org/geogebra/web/resources/css/headerbar.ltr.less")
	LessResource headerbarStyleLTR();

	@Source("org/geogebra/web/resources/css/headerbar.rtl.less")
	LessResource headerbarStyleRTL();

	@Source("org/geogebra/web/resources/css/headerbar.less")
	LessReference headerbarStyle();

	@Source("org/geogebra/web/resources/css/av.ltr.less")
	LessResource avStyleLTR();

	@Source("org/geogebra/web/resources/css/av.rtl.less")
	LessResource avStyleRTL();

	@Source("org/geogebra/web/resources/css/av.less")
	LessReference avStyle();

	@Source("org/geogebra/web/resources/scss/av-styles.scss")
	SassResource avStyleScss();

	@Source("org/geogebra/web/resources/scss/ev-styles.scss")
	SassResource evStyleScss();
	
	@Source("org/geogebra/web/resources/scss/colors.scss")
	SassResource colorsScss();

	@Source("org/geogebra/web/resources/scss/toolbar-styles.scss")
	SassResource toolBarStyleScss();

	@Source("org/geogebra/web/resources/scss/menu-styles.scss")
	SassResource menuStyleScss();

	@Source("org/geogebra/web/resources/scss/settings-styles.scss")
	SassResource settingsStyleScss();

	@Source("org/geogebra/web/resources/scss/perspectives-popup.scss")
	SassResource perspectivesPopupScss();


	@Source("org/geogebra/web/resources/scss/layout.scss")
	SassResource layoutScss();
	
	@Source("org/geogebra/web/resources/scss/snackbar.scss")
	SassResource snackbarScss();

	// don't include these anywhere!
	// they are only here, because otherwise they are not compiled on browser
	// refresh and then I cannot see the changes!
	// ---------------------------------------------------------------
	// @Source("org/geogebra/web/resources/css/functions.less")
	// LessResource styleFunctions();
	//
	// @Source("org/geogebra/web/resources/css/directions.less")
	// LessResource styleDirections();
	//
	// @Source("org/geogebra/web/resources/css/definitions.less")
	// LessResource styleDefinitions();
	//
	// @Source("org/geogebra/web/resources/css/general.less")
	// LessResource generalStyle();
	//
	// @Source("org/geogebra/web/resources/css/av.less")
	// LessResource avStyle();
	//
	// @Source("org/geogebra/web/resources/css/av.ltr.less")
	// LessResource avStyleLTR();
	//
	// @Source("org/geogebra/web/resources/css/av.rtl.less")
	// LessResource avStyleRTL();

	// @Source("org/geogebra/web/resources/css/headerbar.less")
	// LessResource headerbarStyle();

	// EXAM
	// @Source("org/geogebra/web/exam/css/exam.less")
	// LessResource examStyle();

	// don't include files above
	// -----------------------------------------------------------------

	@Source("org/geogebra/web/resources/css/jquery-ui.css")
	TextResource jqueryStyle();

	@Source("org/geogebra/common/icons/png/view_refresh.png")
	ImageResource viewRefresh();

	@Source("org/geogebra/web/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();

	// @Source("org/geogebra/web/resources/images/spinner.html")
	// TextResource ggbSpinnerHtml();

	@Source("org/geogebra/web/resources/images/ggbSplash.html")
	TextResource ggbSplashHtml();

	// INFO, WARNING, QUESTION, ERROR
	@Source("org/geogebra/common/icons/png/web/dialog-error.png")
	ImageResource dialog_error();

	@Source("org/geogebra/common/icons/png/web/dialog-info.png")
	ImageResource dialog_info();

	@Source("org/geogebra/common/icons/png/web/dialog-question.png")
	ImageResource dialog_question();

	@Source("org/geogebra/common/icons/png/web/dialog-warning.png")
	ImageResource dialog_warning();

	@Source("org/geogebra/common/icons/png/web/icon-dialog-warning.png")
	ImageResource icon_dialog_warning();

	@Source("org/geogebra/common/icons/png/web/question-mark.png")
	ImageResource questionMark();

	@Source("org/geogebra/common/icons/png/web/mode_toggle_numeric.png")
	ImageResource modeToggleNumeric();

	@Source("org/geogebra/common/icons/png/web/mode_toggle_symbolic.png")
	ImageResource modeToggleSymbolic();

	// TODO we need another picture
	@Source("org/geogebra/common/icons/png/android/document_viewer.png")
	ImageResource viewSaved();

	@Source("org/geogebra/web/resources/js/promise-1.0.0.min.js")
	TextResource promiseJs();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_down.png")
	ImageResource icons_fillings_arrow_big_down();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_up.png")
	ImageResource icons_fillings_arrow_big_up();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_left.png")
	ImageResource icons_fillings_arrow_big_left();

	@Source("org/geogebra/common/icons_fillings/p18/filling_arrow_big_right.png")
	ImageResource icons_fillings_arrow_big_right();

	@Source("org/geogebra/common/icons_fillings/p18/filling_fastforward.png")
	ImageResource icons_fillings_fastforward();

	@Source("org/geogebra/common/icons_fillings/p18/filling_rewind.png")
	ImageResource icons_fillings_rewind();

	@Source("org/geogebra/common/icons_fillings/p18/filling_skipback.png")
	ImageResource icons_fillings_skipback();

	@Source("org/geogebra/common/icons_fillings/p18/filling_skipforward.png")
	ImageResource icons_fillings_skipforward();

	@Source("org/geogebra/common/icons_fillings/p18/filling_play.png")
	ImageResource icons_fillings_play();

	@Source("org/geogebra/common/icons_fillings/p18/filling_pause.png")
	ImageResource icons_fillings_pause();

	@Source("org/geogebra/common/icons_fillings/p18/filling_cancel.png")
	ImageResource icons_fillings_cancel();

	@Source("org/geogebra/web/resources/scss/reset.scss")
	SassResource reset();
}
