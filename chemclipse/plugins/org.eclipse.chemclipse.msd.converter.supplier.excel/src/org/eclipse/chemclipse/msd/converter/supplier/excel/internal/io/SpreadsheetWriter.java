/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.excel.internal.io;

import java.io.IOException;
import java.io.Writer;

import org.apache.poi.ss.util.CellReference;

public class SpreadsheetWriter {

	private final Writer writer;
	private int rowNumber;
	private String xmlEncoding;

	public SpreadsheetWriter(Writer writer, String xmlEncoding) {
		this.writer = writer;
		this.xmlEncoding = xmlEncoding;
	}

	public void beginSheet() throws IOException {

		writer.write("<?xml version=\"1.0\" encoding=\"" + xmlEncoding + "\"?>" + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
		writer.write("<sheetData>\n");
	}

	public void endSheet() throws IOException {

		writer.write("</sheetData>");
		writer.write("</worksheet>");
	}

	public void insertRow(int rowNumber) throws IOException {

		writer.write("<row r=\"" + (rowNumber + 1) + "\">\n");
		this.rowNumber = rowNumber;
	}

	public void endRow() throws IOException {

		writer.write("</row>\n");
	}

	// ---------------------------------------------------------STRING
	public void createCell(int columnNumber, String value, int styleIndex) throws IOException {

		String ref = new CellReference(rowNumber, columnNumber).formatAsString();
		writer.write("<c r=\"" + ref + "\" t=\"inlineStr\"");
		if(styleIndex != -1) {
			writer.write(" s=\"" + styleIndex + "\"");
		}
		writer.write(">");
		writer.write("<is><t>" + value + "</t></is>");
		writer.write("</c>");
	}

	public void createCell(int columnIndex, String value) throws IOException {

		createCell(columnIndex, value, -1);
	}

	// ---------------------------------------------------------DOUBLE
	public void createCell(int columnNumber, double value, int styleIndex) throws IOException {

		String ref = new CellReference(rowNumber, columnNumber).formatAsString();
		writer.write("<c r=\"" + ref + "\" t=\"n\"");
		if(styleIndex != -1) {
			writer.write(" s=\"" + styleIndex + "\"");
		}
		writer.write(">");
		writer.write("<v>" + value + "</v>");
		writer.write("</c>");
	}

	public void createCell(int columnNumber, double value) throws IOException {

		createCell(columnNumber, value, -1);
	}
}
