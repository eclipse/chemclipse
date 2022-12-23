/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
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

import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class AddPeaksPageESTD extends WizardPage {

	private static final Logger logger = Logger.getLogger(AddPeaksPageESTD.class);
	//
	private double concentration = 0.0d;
	private String concentrationUnit = "";
	private String chemicalClass = "";
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public AddPeaksPageESTD(String pageName) {

		super(pageName);
		setTitle("Peaks to Quantitation Table");
		setDescription("Set the concentration details.");
		setErrorMessage("Please add a concentration.");
	}

	protected double getConcentration() {

		return concentration;
	}

	protected String getConcentrationUnit() {

		return concentrationUnit;
	}

	protected String getChemicalClass() {

		return chemicalClass;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		createLabel(composite, "Concentration*:");
		createTextConcentration(composite);
		//
		createLabel(composite, "Concentration Unit*:");
		createTextConcentrationUnit(composite);
		//
		createLabel(composite, "Chemical Class:");
		createTextChemicalClass(composite);
		//
		setControl(composite);
	}

	private void createTextConcentration(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String value = text.getText();
				if(value == null || value.equals("")) {
					setErrorMessage("Please set a concentration, e.g. 2.5.");
				} else {
					try {
						concentration = decimalFormat.parse(value).doubleValue();
						setErrorMessage(null);
					} catch(ParseException e1) {
						logger.warn(e1);
						setErrorMessage("Please type in a valid concentration.");
					}
				}
			}
		});
	}

	private void createTextConcentrationUnit(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String value = text.getText();
				if(value == null || value.equals("")) {
					setErrorMessage("Please set a concentration unit, e.g. mg/kg.");
				} else {
					concentrationUnit = value;
					setErrorMessage(null);
				}
			}
		});
	}

	private void createTextChemicalClass(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String value = text.getText();
				if(value != null && !value.equals("")) {
					chemicalClass = value;
				}
			}
		});
	}

	private void createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
	}
}
