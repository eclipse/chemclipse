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
package org.eclipse.chemclipse.rcp.connector.supplier.microsoft.office.ui.wizards;

import org.eclipse.jface.viewers.ISelection;

public class WordFileDocWizardPage extends OfficeFileWizardPage {

	public WordFileDocWizardPage(ISelection selection) {
		super(selection, "Microsoft Office Word File", "This wizard creates a new Microsoft Office Word (*.doc) file.", "report.doc", "doc");
	}
}