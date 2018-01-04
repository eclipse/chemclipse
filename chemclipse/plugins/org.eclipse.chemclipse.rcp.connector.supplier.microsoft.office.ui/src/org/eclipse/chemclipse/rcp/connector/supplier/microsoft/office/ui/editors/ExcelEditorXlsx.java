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

public class ExcelEditorXlsx extends OLEEditor {

	public ExcelEditorXlsx() {
		super(IOLEApplication.PROG_ID_EXCEL, "xlsx", "Microsoft Excel 2007 (*.xlsx)");
	}
}
