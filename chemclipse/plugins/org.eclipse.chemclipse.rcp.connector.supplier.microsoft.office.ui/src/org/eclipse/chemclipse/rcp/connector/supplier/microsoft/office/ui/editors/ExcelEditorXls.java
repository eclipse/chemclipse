/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.ui.editors;

public class ExcelEditorXls extends OLEEditor {

	public ExcelEditorXls() {
		super(IOLEApplication.PROG_ID_EXCEL, "xls", "Microsoft Excel 2003 (*.xls)");
	}
}
