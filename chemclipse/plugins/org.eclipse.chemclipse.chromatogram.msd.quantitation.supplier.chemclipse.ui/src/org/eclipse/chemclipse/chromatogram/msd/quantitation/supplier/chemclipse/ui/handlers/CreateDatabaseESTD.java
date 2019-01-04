/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.handlers;

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.converter.quantitation.QuantDBConverter;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.QuantitationDatabase;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class CreateDatabaseESTD {

	@Execute
	public void execute(Shell shell) {

		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterExtensions(QuantDBConverter.DEFAULT_QUANT_DB_FILE_EXTENSIONS);
		fileDialog.setFilterNames(QuantDBConverter.DEFAULT_QUANT_DB_FILE_NAMES);
		fileDialog.setFilterPath(PreferenceSupplier.getFilterPathNewQuantDB());
		fileDialog.setOverwrite(true);
		//
		String filePath = fileDialog.open();
		if(filePath != null) {
			File file = new File(filePath);
			PreferenceSupplier.setFilterPathNewQuantDB(file.getParent());
			IQuantitationDatabase quantitationDatabase = new QuantitationDatabase();
			quantitationDatabase.setOperator(UserManagement.getCurrentUser());
			quantitationDatabase.setDescription("Created: " + new Date().toString());
			QuantDBConverter.convert(file, quantitationDatabase, QuantDBConverter.DEFAULT_QUANT_DB_CONVERTER_ID, new NullProgressMonitor());
		}
	}
}
