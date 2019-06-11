/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.handlers;

import java.io.File;
import java.util.List;

import javax.inject.Named;

import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings.InputDataType;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class OpenHandlerCSD {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		InputWizardSettings inputWizardSettings = new InputWizardSettings(InputDataType.CSD_CHROMATOGRAM);
		inputWizardSettings.setTitle("Open Chromatogram(s) [CSD]");
		inputWizardSettings.setDescription("You can select one or more chromatograms to be opened.");
		inputWizardSettings.setPathPreferences(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.P_FILTER_PATH_CHROMATOGRAM_CSD);
		//
		InputEntriesWizard inputWizard = new InputEntriesWizard(inputWizardSettings);
		WizardDialog wizardDialog = new WizardDialog(shell, inputWizard);
		wizardDialog.create();
		//
		if(wizardDialog.open() == WizardDialog.OK) {
			ISupplierEditorSupport supplierEditorSupport = new SupplierEditorSupport(org.eclipse.chemclipse.model.types.DataType.CSD);
			IChromatogramWizardElements chromatogramWizardElements = inputWizard.getChromatogramWizardElements();
			List<String> selectedChromatograms = chromatogramWizardElements.getSelectedChromatograms();
			if(selectedChromatograms.size() > 0) {
				/*
				 * If it contains at least 1 element, add it to the input files list.
				 */
				for(String chromatogram : selectedChromatograms) {
					File file = new File(chromatogram);
					supplierEditorSupport.openEditor(file, true);
				}
			}
		}
	}
}
