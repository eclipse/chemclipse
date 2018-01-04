/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.ui.editors;

public interface IOLEApplication {

	/*
	 * Word
	 */
	String GUID_ATTRIBUTE_WORD = "{000209FE-0000-0000-C000-000000000046}";
	String PROG_ID_WORD = "Word.Document";
	/*
	 * Actions
	 */
	int WORD_DOCUMENT_STARTUP = 0x00000001;
	int WORD_DOCUMENT_QUIT = 0x00000002;
	int WORD_DOCUMENT_CHANGE = 0x00000003;
	int WORD_DOCUMENT_OPEN = 0x00000004;
	int WORD_DOCUMENT_BEFORE_CLOSE = 0x00000006;
	int WORD_DOCUMENT_BEFORE_PRINT = 0x00000007;
	int WORD_DOCUMENT_BEFORE_SAVE = 0x00000008;
	int WORD_DOCUMENT_NEW = 0x00000009;
	int WORD_WINDOW_ACTIVATE = 0x0000000a;
	int WORD_WINDOW_DEACTIVATE = 0x0000000b;
	int WORD_WINDOW_SELECTION_CHANGE = 0x0000000c;
	int WORD_WINDOW_BEFORE_RIGHT_CLICK = 0x0000000d;
	int WORD_WINDOW_BEFORE_DOUBLE_CLICK = 0x0000000e;
	/*
	 * Excel
	 */
	String GUID_ATTRIBUTE_EXCEL = "{00024413-0000-0000-C000-000000000046}";
	String PROG_ID_EXCEL = "Excel.Sheet";
	/*
	 * Actions
	 */
	int EXCEL_NEW_WORKBOOK = 0x0000061d;
	int EXCEL_SHEET_SELECTION_CHANGE = 0x00000616;
	int EXCEL_SHEET_BEFORE_DOUBLE_CLICK = 0x00000617;
	int EXCEL_SHEET_BEFORE_RIGHT_CLICK = 0x00000618;
	int EXCEL_SHEET_ACTIVATE = 0x00000619;
	int EXCEL_SHEET_DEACTIVATE = 0x0000061a;
	int EXCEL_SHEET_CALCULATE = 0x0000061b;
	int EXCEL_SHEET_CHANGE = 0x0000061c;
	int EXCEL_WORKBOOK_OPEN = 0x0000061f;
	int EXCEL_WORKBOOK_ACTIVATE = 0x00000620;
	int EXCEL_WORKBOOK_DEACTIVATE = 0x00000621;
	int EXCEL_WORKBOOK_BEFORE_CLOSE = 0x00000622;
	int EXCEL_WORKBOOK_BEFORE_SAVE = 0x00000623;
	int EXCEL_WORKBOOK_BEFORE_PRINT = 0x00000624;
	int EXCEL_WORKBOOK_NEW_SHEET = 0x00000625;
	int EXCEL_WORKBOOK_ADDIN_INSTALL = 0x00000626;
	int EXCEL_WORKBOOK_ADDIN_UNINSTALL = 0x00000627;
	int EXCEL_WINDOW_RESIZE = 0x00000612;
	int EXCEL_WINDOW_ACTIVATE = 0x00000614;
	int EXCEL_WINDOW_DEACTIVATE = 0x00000615;
	int EXCEL_SHEET_FOLLOW_HYPERLINK = 0x0000073e;
}
