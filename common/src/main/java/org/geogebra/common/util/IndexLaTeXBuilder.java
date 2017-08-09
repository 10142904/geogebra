package org.geogebra.common.util;

public class IndexLaTeXBuilder extends IndexHTMLBuilder {

    public IndexLaTeXBuilder() {
        super(false);
    }

    @Override
    public void startIndex() {
        append("$_");
    }

    @Override
    public void endIndex() {
        append("$");
    }
}
