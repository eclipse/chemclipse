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
package org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.manual.ui.internal.wizards;

import java.text.NumberFormat;
import java.text.ParseException;

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

public class IdentificationPage extends WizardPage {

	private Text textIdentificationName;
	private Text textCasNumber;
	private Text textComments;
	private Text textFormula;
	private Text textMolWeight;
	private NumberFormat numberFormat;

	public IdentificationPage(String pageName) {
		super(pageName);
		setTitle("Manual Peak Identification");
		setDescription("Set an identification entry manually.");
		setErrorMessage("Please set an identification name.");
		numberFormat = ValueFormat.getNumberFormatEnglish();
	}

	/**
	 * Returns the name.
	 * 
	 * @return String
	 */
	protected String getIdentificationName() {

		return textIdentificationName.getText();
	}

	/**
	 * Returns the cas number.
	 * 
	 * @return String
	 */
	protected String getCasNumber() {

		return textCasNumber.getText();
	}

	/**
	 * Returns the comments.
	 * 
	 * @return String
	 */
	protected String getComments() {

		return textComments.getText();
	}

	/**
	 * Returns the formula.
	 * 
	 * @return String
	 */
	protected String getFormula() {

		return textFormula.getText();
	}

	/**
	 * Returns the mol weight.
	 * 
	 * @return double
	 */
	protected double getMolWeight() {

		try {
			return numberFormat.parse(textMolWeight.getText()).doubleValue();
		} catch(ParseException e) {
			return 0.0d;
		}
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
		label.setText("Name*:");
		textIdentificationName = new Text(composite, SWT.BORDER);
		textIdentificationName.setLayoutData(gridData);
		textIdentificationName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				String name = textIdentificationName.getText();
				if(name == null || name.equals("")) {
					setErrorMessage("Please set an identification name.");
				} else {
					setErrorMessage(null);
				}
			}
		});
		//
		label = new Label(composite, SWT.NONE);
		label.setText("CAS:");
		textCasNumber = new Text(composite, SWT.BORDER);
		textCasNumber.setLayoutData(gridData);
		//
		label = new Label(composite, SWT.NONE);
		label.setText("Comments:");
		textComments = new Text(composite, SWT.BORDER);
		textComments.setLayoutData(gridData);
		//
		label = new Label(composite, SWT.NONE);
		label.setText("Formula:");
		textFormula = new Text(composite, SWT.BORDER);
		textFormula.setLayoutData(gridData);
		//
		label = new Label(composite, SWT.NONE);
		label.setText("Mol Weight:");
		textMolWeight = new Text(composite, SWT.BORDER);
		textMolWeight.setLayoutData(gridData);
		//
		setControl(composite);
	}
}
