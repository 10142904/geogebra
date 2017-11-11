package org.geogebra.stepbystep;

import org.geogebra.common.gui.view.algebra.StepGuiBuilder;
import org.geogebra.test.util.ReportBuilder;

/**
 * Builds step-by step report for multiple testcases, separated by headings into
 * categories
 *
 */
public class HtmlStepBuilder implements StepGuiBuilder {
	private StringBuilder sb = new StringBuilder()
			.append("<script src='https://beta.geogebra.org/scripts/jlatexmath/jlatexmath.js'></script>")
			.append("<script src='https://beta.geogebra.org/scripts/jlatexmath/jlatexmath-tube.js'></script>")
			.append("<style>#line {margin:2px;}</style>")
			.append("<script>window.addEventListener(\"load\",function(){els = document.getElementsByTagName(\"CANVAS\");for(var k in els){els[k].getAttribute && GGBT_jlatexmath.drawLatexOnCanvas(els[k])};})</script>")
			.append("<script type=\"text/javascript\"> "
					+ "function toggle_visibility(id) { "
					+ "   var e = document.getElementById('group' + id);"
					+ "   var button = document.getElementById('button' + id);"
					+ "   if(e.style.display == 'none') { "
					+ "      e.style.display = 'initial'; "
					+ "      button.setAttribute('data-content', '\\\\Delta'); "
					+ "      GGBT_jlatexmath.drawLatexOnCanvas(button);"
					+ "   } else { "
					+ "      e.style.display = 'none'; "
					+ "      button.setAttribute('data-content', '\\\\nabla'); "
					+ "      GGBT_jlatexmath.drawLatexOnCanvas(button);"
					+ "   } "
					+ "} "
					+ "</script>")
			.append("<script type=\"text/javascript\"> "
					+ "function switch_to_detailed(id) { "
					+ "   var def = document.getElementById('default' + id); "
					+ "   var detailed = document.getElementById('detailed' + id); "
					+ "   def.style.display = 'none'; "
					+ "   detailed.style.display = 'initial'; "
					+ "} "
					+ "</script>")	
			.append("<script type=\"text/javascript\"> "
					+ "function switch_to_default(id) { "
					+ "   var def = document.getElementById('default' + id);"
					+ "   var detailed = document.getElementById('detailed' + id);"
					+ "   def.style.display = 'initial'; "
					+ "   detailed.style.display = 'none'; "
					+ "} "
					+ "</script>");

	private int indent = 1;

	private int groupCnt;
	private int alternativeCnt;

	private boolean addDefaultButton;
	private boolean addDetailedButton;

	public void addPlainRow(String equations) {
		// TODO Auto-generated method stub

	}

	public void addLatexRow(String equations) {
		sb.append("<br><canvas class=\"latex\" id=\"line\" style=\"height:0.5em;margin-left:"
				+ indent
				+ "em\" data-content=\"" + equations + "\">" + equations
				+ "</canvas>\n");

		if (addDefaultButton) {
			sb.append("<canvas class='latex' onclick=\"switch_to_detailed('" + alternativeCnt
					+ "');\" data-content='\\Xi'>\\Xi</canvas>\n");
			addDefaultButton = false;
		}

		if (addDetailedButton) {
			sb.append("<canvas class='latex' onclick=\"switch_to_default('" + alternativeCnt
					+ "');\" data-content='\\Xi'>\\Xi</canvas>\n");
			addDetailedButton = false;
		}

	}

	public void show() {
		// TODO Auto-generated method stub

	}

	public void startGroup() {
		indent++;
		groupCnt++;
		
		sb.append("<canvas class='latex' id='button" + groupCnt + "' onclick=\"toggle_visibility('" + groupCnt
				+ "');\" data-content='\\nabla'>\\nabla</canvas>\n");
		
		sb.append("<span style='display: none' id='group" + groupCnt + "'>");
	}


	public void endGroup() {
		indent--;

		sb.append("</span>");
	}

	public void startDefault() {
		alternativeCnt++;

		sb.append("<span id = 'default" + alternativeCnt + "' >");
		addDefaultButton = true;
	}

	public void switchToDetailed() {
		sb.append("</span>");
		sb.append("<span id = 'detailed" + alternativeCnt + "' style='display: none'>");
		addDetailedButton = true;
	}

	public void endDetailed() {
		sb.append("</span>");
	}

	public void linebreak() {
		sb.append("<br>");
	}

	/**
	 * @return HTML content
	 */
	public String getHtml() {
		return sb.toString();
	}

	/**
	 * @param methodName
	 *            caller method name
	 * @param i
	 *            heading level
	 */
	public void addHeading(String methodName, int i) {
		sb.append("<h" + i + ">" + methodName + "</h" + i + ">");

	}

	/**
	 * Stores report in build/reports
	 * 
	 * @param filename
	 *            filename
	 */
	public void printReport(String filename) {
		ReportBuilder report = new ReportBuilder(filename);
		report.callback(getHtml());
		report.close();
	}
}
