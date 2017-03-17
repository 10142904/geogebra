/**********************************************
 * Based on SVG resource generator by Lukas Laag
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.geogebra.web.vectomatic;

import java.net.URL;

import org.geogebra.web.html5.util.LessResource;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.dev.util.Util;
import com.google.gwt.resources.ext.AbstractResourceGenerator;
import com.google.gwt.resources.ext.ResourceContext;
import com.google.gwt.resources.ext.ResourceGeneratorUtil;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.StringSourceWriter;

/**
 * Provides implementations of SVGResource.
 */
public class LessResourceGenerator extends AbstractResourceGenerator {

	@Override
	public String createAssignment(TreeLogger logger, ResourceContext context,
	        JMethod method) throws UnableToCompleteException {

		// Extract the SVG name from the @Source annotation
		URL[] resources = ResourceGeneratorUtil.findResources(logger, context,
		        method);
		if (resources.length != 1) {
			logger.log(TreeLogger.ERROR,
			        "Exactly one resource must be specified", null);
			throw new UnableToCompleteException();
		}
		URL resource = resources[0];

		String css = Util.readURLAsString(resource);

		LessEngine leg = new LessEngine();
		try {
			css = leg.compile(resource, true);
		} catch (LessException e) {
			logger.log(Type.ERROR, "Error processing " + method.getName(), null);
			if (Util.readURLAsString(resource) == null) {
				logger.log(Type.ERROR, method.getName() + " not found:"
						+ resource.toString(), null);
			}
			e.printStackTrace();
			throw new UnableToCompleteException();
		}
		logger.log(Type.INFO,
				method.getName() + ": " + css.length() + " bytes", null);


		SourceWriter sw = new StringSourceWriter();
		sw.println("new " + LessResource.class.getName() + "() {");
		sw.indent();
		sw.println("private String css=\"" + Generator.escape(css) + "\";");

		// Convenience when examining the generated code.
		sw.println("// " + resource.toExternalForm());

		sw.println("@Override");
		sw.println("public String getName() {");
		sw.indent();
		sw.println("return \"" + method.getName() + "\";");
		sw.outdent();
		sw.println("}");

		sw.println("@Override");
		sw.println("public String getText() {");
		sw.indent();
		sw.println("return css;");
		sw.outdent();
		sw.println("}");

		sw.outdent();
		sw.println("}");

		return sw.toString();
	}

}
