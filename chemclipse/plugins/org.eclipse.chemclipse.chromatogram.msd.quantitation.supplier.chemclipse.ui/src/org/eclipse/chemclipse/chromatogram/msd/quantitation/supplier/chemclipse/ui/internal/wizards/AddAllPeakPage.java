/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
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

public class AddAllPeakPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(AddAllPeakPage.class);
	//
	private Text textConcentration;
	private Text textConcentrationUnit;
	private Text textChemicalClass;
	private double concentration;
	private String concentrationUnit;
	private String chemicalClass;
	//
	private DecimalFormat decimalFormat;

	public AddAllPeakPage(String pageName) {
		super(pageName);
		setTitle("Peaks to Quantitation Table");
		setDescription("Set the concentration details.");
		concentration = 0.0d;
		concentrationUnit = "";
		chemicalClass = "";
		setErrorMessage("Please add a concentration.");
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
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
		composite.setLayout(new GridLayout());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		//
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		//
		Label label;
		//
		label = new Label(composite, SWT.NONE);
		label.setText("Concentration*:");
		textConcentration = new Text(composite, SWT.BORDER);
		textConcentration.setLayoutData(gridData);
		textConcentration.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String value = textConcentration.getText();
				if(value == null || value.equals("")) {
					setErrorMessage("Please set a concentration.");
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
		//
		label = new Label(composite, SWT.NONE);
		label.setText("Concentration Unit*:");
		textConcentrationUnit = new Text(composite, SWT.BORDER);
		textConcentrationUnit.setLayoutData(gridData);
		textConcentrationUnit.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String value = textConcentrationUnit.getText();
				if(value == null || value.equals("")) {
					setErrorMessage("Please set a concentration unit.");
				} else {
					concentrationUnit = value;
					setErrorMessage(null);
				}
			}
		});
		//
		label = new Label(composite, SWT.NONE);
		label.setText("Chemical Class:");
		textChemicalClass = new Text(composite, SWT.BORDER);
		textChemicalClass.setLayoutData(gridData);
		textChemicalClass.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String value = textChemicalClass.getText();
				if(value != null && !value.equals("")) {
					chemicalClass = value;
				}
			}
		});
		//
		setControl(composite);
	}
}
