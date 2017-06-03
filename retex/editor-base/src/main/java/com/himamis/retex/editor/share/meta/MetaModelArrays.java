package com.himamis.retex.editor.share.meta;

import static com.himamis.retex.editor.share.meta.MetaArray.APOSTROPHES;
import static com.himamis.retex.editor.share.meta.MetaArray.CEIL;
import static com.himamis.retex.editor.share.meta.MetaArray.CLOSE;
import static com.himamis.retex.editor.share.meta.MetaArray.CURLY;
import static com.himamis.retex.editor.share.meta.MetaArray.FIELD;
import static com.himamis.retex.editor.share.meta.MetaArray.FLOOR;
import static com.himamis.retex.editor.share.meta.MetaArray.LINE;
import static com.himamis.retex.editor.share.meta.MetaArray.MATRIX;
import static com.himamis.retex.editor.share.meta.MetaArray.OPEN;
import static com.himamis.retex.editor.share.meta.MetaArray.REGULAR;
import static com.himamis.retex.editor.share.meta.MetaArray.ROW;
import static com.himamis.retex.editor.share.meta.MetaArray.SQUARE;

import java.util.ArrayList;
import java.util.List;

public class MetaModelArrays {

	private static MetaCharacter createArrayComponent(String name, char cas,
			String defaultTex) {
		String tex = defaultTex == null ? cas + "" : defaultTex;

		return new MetaCharacter(name, tex, cas, cas,
				MetaCharacter.CHARACTER);
    }

	private static MetaCharacter createArrayComponent(String name, char cas) {
        return createArrayComponent(name, cas, null);
    }

	public static final char LCEIL = '\u2308';
	public static final char RCEIL = '\u2309';
	public static final char LFLOOR = '\u230a';
	public static final char RFLOOR = '\u230b';

    MetaGroup createArraysGroup() {
        List<MetaComponent> components = new ArrayList<MetaComponent>();
		MetaArray curly = new MetaArray(1, CURLY);
		curly.setOpen(createArrayComponent(OPEN, '{', "\\left\\lbrace "));
		curly.setClose(createArrayComponent(CLOSE, '}', "\\right\\rbrace "));
		curly.setField(createArrayComponent(FIELD, ','));
		curly.setRow(createArrayComponent(ROW, ';'));
		components.add(curly);

		MetaArray regular = new MetaArray(1, REGULAR);
		regular.setOpen(createArrayComponent(OPEN, '(', "\\left("));
		regular.setClose(createArrayComponent(CLOSE, ')', "\\right)"));
		regular.setField(createArrayComponent(FIELD, ','));
		regular.setRow(createArrayComponent(ROW, ';'));
		components.add(regular);

		MetaArray square = new MetaArray(1, SQUARE);
		square.setOpen(createArrayComponent(OPEN, '[', "\\left["));
		square.setClose(createArrayComponent(CLOSE, ']', "\\right]"));
		square.setField(createArrayComponent(FIELD, ','));
		square.setRow(createArrayComponent(ROW, ';'));
		components.add(square);

		MetaArray apostrophes = new MetaArray(1, APOSTROPHES);
		apostrophes.setOpen(createArrayComponent(OPEN, '\"', " \\text{\""));
		apostrophes.setClose(createArrayComponent(CLOSE, '\"', "\"} "));
		apostrophes.setField(createArrayComponent(FIELD, '\0'));
		apostrophes.setRow(createArrayComponent(ROW, '\0'));
		components.add(apostrophes);

		MetaArray line = new MetaArray(1, LINE);
		line.setOpen(createArrayComponent(OPEN, '|', "|"));
		line.setClose(createArrayComponent(CLOSE, '|', "|"));
		line.setField(createArrayComponent(FIELD, ','));
		line.setRow(createArrayComponent(ROW, ';'));
		components.add(line);

		MetaArray ceil = new MetaArray(1, CEIL);
		ceil.setOpen(createArrayComponent(OPEN, LCEIL, "\\left\\lceil "));
		ceil.setClose(createArrayComponent(CLOSE, RCEIL, "\\right\\rceil "));
		ceil.setField(createArrayComponent(FIELD, ','));
		ceil.setRow(createArrayComponent(ROW, ';'));
		components.add(ceil);

		MetaArray floor = new MetaArray(1, FLOOR);
		floor.setOpen(createArrayComponent(OPEN, LFLOOR, "\\left\\lfloor "));
		floor.setClose(
				createArrayComponent(CLOSE, RFLOOR, "\\right\\rfloor "));
		floor.setField(createArrayComponent(FIELD, ','));
		floor.setRow(createArrayComponent(ROW, ';'));
		components.add(floor);

        return new ListMetaGroup(MetaModel.ARRAYS, MetaModel.ARRAYS, components);
    }

    MetaGroup createMatrixGroup() {
		MetaArray matrix = new MetaArray(2, MATRIX);
		matrix.setOpen(createArrayComponent(OPEN, '{', "\\begin{pmatrix} "));
		matrix.setClose(createArrayComponent(CLOSE, '}', " \\end{pmatrix}"));
		matrix.setField(createArrayComponent(FIELD, ',', " & "));
		matrix.setRow(createArrayComponent(ROW, ',', " \\\\ "));

		return matrix;
    }
}
