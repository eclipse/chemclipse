/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.preferences;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.Activator;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.DoubleFieldEditor;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.SpacerFieldEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageSampleQuant extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageSampleQuant() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Sample Quant Report");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {

		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new FileFieldEditor(PreferenceSupplier.P_SAMPLEQUANT_FILTER_PATH_TARGETS, "targets.txt", getFieldEditorParent()));
		Button buttonCreateTargets = new Button(getFieldEditorParent(), SWT.PUSH);
		buttonCreateTargets.setText("Create demo targets.txt");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		buttonCreateTargets.setLayoutData(gridData);
		buttonCreateTargets.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Save a demo targets.txt
				 */
				Shell shell = Display.getCurrent().getActiveShell();
				FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
				fileDialog.setText("Demo targets.txt");
				fileDialog.setFilterExtensions(new String[]{"*.txt"});
				fileDialog.setFilterNames(new String[]{"Targets *.txt"});
				fileDialog.setFileName("targets.txt");
				//
				String targetsFile = fileDialog.open();
				if(targetsFile != null) {
					try {
						PrintWriter printWriter = new PrintWriter(new FileWriter(new File(targetsFile)));
						printWriter.println("#NAME	CAS");
						printWriter.println("Styrene	100-42-5");
						printWriter.println("Benzene	71-43-2");
						printWriter.flush();
						printWriter.close();
						//
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
						messageBox.setText("Export Demo targets.txt");
						messageBox.setMessage("Export of demo targets.txt file finished successfully.");
						messageBox.open();
						//
					} catch(Exception e1) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
						messageBox.setText("Export Demo targets.txt");
						messageBox.setMessage("Something has gone wrong whilst exporting the demo targets.txt file.");
						messageBox.open();
					}
				}
			}
		});
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		addField(new DoubleFieldEditor(PreferenceSupplier.P_SAMPLEQUANT_MIN_MATCH_QUALITY, "Min Match Quality", PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MIN, PreferenceSupplier.SAMPLEQUANT_MATCH_QUALITY_MAX, getFieldEditorParent()));
		addField(new ComboFieldEditor(PreferenceSupplier.P_SAMPLEQUANT_SCAN_IDENTIFIER, "Scan Identifier:", PreferenceSupplier.getMassSpectrumIdentifier(), getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_SAMPLEQUANT_SEARCH_CASE_SENSITIVE, "Case sensitive search: ", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

	}
}
