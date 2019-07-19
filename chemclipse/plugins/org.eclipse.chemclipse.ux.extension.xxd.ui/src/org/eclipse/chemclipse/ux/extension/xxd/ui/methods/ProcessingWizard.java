/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for different datatype sets
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ProcessingWizard extends Wizard {

	public static final Function<Composite, Collection<Button>> MASSSPECTRUM_DATATYPES = new Function<Composite, Collection<Button>>() {

		@Override
		public Collection<Button> apply(Composite parent) {

			Button checkboxCSD = createDataTypeCheckbox(parent, DataType.CSD, "CSD (FID, PPD, ...)", "Select the csd processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_CSD);
			Button checkboxMSD = createDataTypeCheckbox(parent, DataType.MSD, "MSD (Quadrupole, IonTrap, ...)", "Select the msd processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_MSD);
			Button checkboxWSD = createDataTypeCheckbox(parent, DataType.WSD, "WSD (UV/Vis, DAD, ...)", "Select the wsd processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_WSD);
			return Arrays.asList(checkboxCSD, checkboxMSD, checkboxWSD);
		}
	};
	public static final Function<Composite, Collection<Button>> NMR_DATATYPES = new Function<Composite, Collection<Button>>() {

		@Override
		public Collection<Button> apply(Composite parent) {

			return Collections.singleton(createDataTypeCheckbox(parent, DataType.NMR, "NMR (FID, Spectrum, ...)", "Select the NMR processor items", PreferenceConstants.P_METHOD_PROCESSOR_SELECTION_NMR));
		}
	};
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 400;
	public static final String PROCESSING_SECTION = "JsonSection";
	public static final String PROCESSING_SETTINGS = "JsonSettings";

	private ProcessingWizard() {
		setWindowTitle("Settings");
		setDialogSettings(new DialogSettings(PROCESSING_SECTION));
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {

		return true;
	}

	public static IProcessEntry open(Shell shell, ProcessTypeSupport processingSupport, Function<Composite, Collection<Button>> checkboxFunction) {

		ProcessingWizard wizard = new ProcessingWizard();
		ProcessingWizardPage wizardPage = new ProcessingWizardPage(processingSupport, checkboxFunction);
		wizard.addPage(wizardPage);
		WizardDialog wizardDialog = new WizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(ProcessingWizard.DEFAULT_WIDTH, ProcessingWizard.DEFAULT_HEIGHT);
		wizardDialog.create();
		//
		if(wizardDialog.open() == WizardDialog.OK) {
			// TODO show preferences dialog if necessary?
			return wizardPage.getProcessEntry();
		}
		return null;
	}

	private static Button createDataTypeCheckbox(Composite parent, DataType dataType, String text, String tooltip, String preferenceKey) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		Button button = new Button(parent, SWT.CHECK);
		button.setData(dataType);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.setSelection(preferenceStore.getBoolean(preferenceKey));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(preferenceKey, button.getSelection());
			}
		});
		return button;
	}
}
