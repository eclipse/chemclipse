/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.supplier.chemclipse.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.support.ui.swt.EnhancedCombo;
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

public class QuantitationDocumentPageESTD extends WizardPage {

	private List<String> peakTargetNames;
	private IQuantitationDatabase quantitationDatabase;
	//
	protected Button buttonMerge;
	protected Combo comboQuantitationCompoundNames;
	protected Text textConcentrationMerge;
	//
	protected Button buttonCreate;
	protected Combo comboPeakTargetNames;
	protected Text textConcentrationCreate;
	protected Text textConcentrationUnitCreate;
	protected Text textChemicalClassCreate;
	//
	private Label labelConcentrationUnitMerge;
	private Label label1;
	private Label label2;
	private Label label3;

	protected QuantitationDocumentPageESTD(String pageName, List<String> peakTargetNames, IQuantitationDatabase quantitationDatabase) {

		super(pageName);
		setTitle("Quantitation Support");
		setDescription("Create a quantitation component.");
		this.peakTargetNames = peakTargetNames;
		this.quantitationDatabase = quantitationDatabase;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		createMergePeakControls(composite);
		createAddPeakControls(composite);
		/*
		 * Set the control.
		 */
		enableMergePeakControls();
		setControl(composite);
	}

	private void createMergePeakControls(Composite parent) {

		buttonMerge = createButton(parent, "Add the peak to an existing Quantitation Component", false);
		comboQuantitationCompoundNames = createComboCompoundNames(parent);
		textConcentrationMerge = createText(parent);
		labelConcentrationUnitMerge = createLabel(parent, "", 2);
	}

	private void createAddPeakControls(Composite parent) {

		buttonCreate = createButton(parent, "Create a new Quantitation Component", false);
		comboPeakTargetNames = createComboTargetNames(parent);
		textConcentrationCreate = createText(parent);
		label1 = createLabel(parent, "Concentration", 1);
		textConcentrationUnitCreate = createText(parent);
		label2 = createLabel(parent, "Concentration Unit (e.g. mg/ml)", 1);
		textChemicalClassCreate = createText(parent);
		label3 = createLabel(parent, "Chemical Class", 1);
	}

	private Button createButton(Composite parent, String text, boolean selection) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText(text);
		button.setSelection(selection);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableMergePeakControls();
			}
		});
		//
		return button;
	}

	private Combo createComboCompoundNames(Composite parent) {

		Combo combo = EnhancedCombo.create(parent, SWT.READ_ONLY);
		//
		if(quantitationDatabase != null) {
			List<String> quantitationCompoundNames = quantitationDatabase.getCompoundNames();
			combo.setItems(quantitationCompoundNames.toArray(new String[quantitationCompoundNames.size()]));
		}
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(quantitationDatabase != null) {
					String name = combo.getText();
					IQuantitationCompound quantitationCompound = quantitationDatabase.getQuantitationCompound(name);
					if(quantitationCompound != null) {
						String concentrationUnit = quantitationCompound.getConcentrationUnit();
						labelConcentrationUnitMerge.setText(concentrationUnit);
					}
				}
			}
		});
		//
		return combo;
	}

	private Combo createComboTargetNames(Composite parent) {

		Combo combo = EnhancedCombo.create(parent, SWT.NONE);
		if(peakTargetNames.size() > 0) {
			combo.setItems(peakTargetNames.toArray(new String[peakTargetNames.size()]));
		}
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		combo.setLayoutData(gridData);
		//
		return combo;
	}

	private Label createLabel(Composite parent, String text, int horizontalSpan) {

		Label label = new Label(parent, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalSpan = horizontalSpan;
		label.setLayoutData(gridData);
		label.setText(text);
		return label;
	}

	private Text createText(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private void enableMergePeakControls() {

		boolean enable = buttonMerge.getSelection();
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
		//
		label1.setEnabled(enabled);
		label2.setEnabled(enabled);
		label3.setEnabled(enabled);
	}
}
