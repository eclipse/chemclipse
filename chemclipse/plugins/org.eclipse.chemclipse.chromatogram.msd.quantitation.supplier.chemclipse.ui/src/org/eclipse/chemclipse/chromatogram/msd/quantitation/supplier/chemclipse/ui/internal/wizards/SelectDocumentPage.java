/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SelectDocumentPage extends WizardPage {

	private List<String> peakTargetNames;
	private IQuantitationDatabase quantitationDatabase;
	//
	protected Button buttonMerge;
	protected Combo comboQuantitationCompoundNames;
	protected Text textConcentrationMerge;
	private Label labelConcentrationUnitMerge;
	//
	protected Button buttonCreate;
	protected Combo comboPeakTargetNames;
	protected Text textConcentrationCreate;
	protected Text textConcentrationUnitCreate;
	protected Text textChemicalClassCreate;
	private Label label1;
	private Label label2;
	private Label label3;

	protected SelectDocumentPage(String pageName, List<String> peakTargetNames, IQuantitationDatabase quantitationDatabase) {
		super(pageName);
		setTitle("Quantitation Support");
		setDescription("Create a quantitation document.");
		this.peakTargetNames = peakTargetNames;
		this.quantitationDatabase = quantitationDatabase;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		//
		createMergePeakControls(composite, gridData);
		createAddPeakControls(composite, gridData);
		enableMergePeakControls(true);
		/*
		 * Set the control.
		 */
		setControl(composite);
	}

	private void createMergePeakControls(Composite parent, GridData gridData) {

		GridData gridDataFill = new GridData(GridData.FILL_HORIZONTAL);
		gridDataFill.grabExcessHorizontalSpace = true;
		/*
		 * Merge the peak with an existing quantitation compound.
		 */
		buttonMerge = new Button(parent, SWT.RADIO);
		buttonMerge.setText("Add peak to existing component");
		buttonMerge.setSelection(true);
		buttonMerge.setLayoutData(gridData);
		buttonMerge.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableMergePeakControls(buttonMerge.getSelection());
			}
		});
		//
		comboQuantitationCompoundNames = new Combo(parent, SWT.NONE);
		if(quantitationDatabase != null) {
			List<String> quantitationCompoundNames = quantitationDatabase.getCompoundNames();
			comboQuantitationCompoundNames.setItems(quantitationCompoundNames.toArray(new String[quantitationCompoundNames.size()]));
		}
		comboQuantitationCompoundNames.setLayoutData(gridData);
		comboQuantitationCompoundNames.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Set the concentration unit label.
				 */
				if(quantitationDatabase != null) {
					String name = comboQuantitationCompoundNames.getText();
					IQuantitationCompound quantitationCompound = quantitationDatabase.getQuantitationCompound(name);
					if(quantitationCompound != null) {
						String concentrationUnit = quantitationCompound.getConcentrationUnit();
						labelConcentrationUnitMerge.setText(concentrationUnit);
					}
				}
			}
		});
		//
		textConcentrationMerge = new Text(parent, SWT.BORDER);
		//
		labelConcentrationUnitMerge = new Label(parent, SWT.NONE);
		labelConcentrationUnitMerge.setText("");
		labelConcentrationUnitMerge.setLayoutData(gridDataFill);
	}

	private void createAddPeakControls(Composite parent, GridData gridData) {

		/*
		 * Create a new quantitation compound.
		 */
		buttonCreate = new Button(parent, SWT.RADIO);
		buttonCreate.setLayoutData(gridData);
		buttonCreate.setText("Create a new component");
		buttonCreate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * The selection of button merge will be checked
				 * to enable/disable the controls.
				 */
				enableMergePeakControls(buttonMerge.getSelection());
			}
		});
		//
		comboPeakTargetNames = new Combo(parent, SWT.NONE);
		if(peakTargetNames.size() > 0) {
			comboPeakTargetNames.setItems(peakTargetNames.toArray(new String[peakTargetNames.size()]));
		}
		comboPeakTargetNames.setLayoutData(gridData);
		//
		textConcentrationCreate = new Text(parent, SWT.BORDER);
		label1 = new Label(parent, SWT.NONE);
		label1.setText("Concentration");
		//
		textConcentrationUnitCreate = new Text(parent, SWT.BORDER);
		label2 = new Label(parent, SWT.NONE);
		label2.setText("Concentration Unit (e.g. mg/ml)");
		//
		textChemicalClassCreate = new Text(parent, SWT.BORDER);
		label3 = new Label(parent, SWT.NONE);
		label3.setText("Chemical Class");
	}

	private void enableMergePeakControls(boolean enable) {

		modifyMergeControls(enable);
		modifyAddControls(!enable);
	}

	private void modifyMergeControls(boolean enabled) {

		comboQuantitationCompoundNames.setEnabled(enabled);
		textConcentrationMerge.setEnabled(enabled);
		labelConcentrationUnitMerge.setEnabled(enabled);
	}

	private void modifyAddControls(boolean enabled) {

		comboPeakTargetNames.setEnabled(enabled);
		textConcentrationCreate.setEnabled(enabled);
		textConcentrationUnitCreate.setEnabled(enabled);
		textChemicalClassCreate.setEnabled(enabled);
		label1.setEnabled(enabled);
		label2.setEnabled(enabled);
		label3.setEnabled(enabled);
	}
}
