/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class PrincipalComponentUI extends Composite {

	public static final int SPINNER_NONE = 0;
	public static final int SPINNER_X = 1 << 0;
	public static final int SPINNER_Y = 1 << 1;
	public static final int SPINNER_Z = 1 << 2;
	//
	private int options = SPINNER_NONE;
	int maximum = 3;
	//
	private Spinner spinnerX;
	private Spinner spinnerY;
	private Spinner spinnerZ;
	//
	private ISelectionListenerPCs selectionListener = null;

	public PrincipalComponentUI(Composite parent, int style) {

		this(parent, style, SPINNER_X | SPINNER_Y | SPINNER_Z);
	}

	public PrincipalComponentUI(Composite parent, int style, int options) {

		super(parent, style);
		this.options = options;
		createControl();
	}

	public void setSelectionListener(ISelectionListenerPCs selectionListener) {

		this.selectionListener = selectionListener;
	}

	public int getPCX() {

		return spinnerX.getSelection();
	}

	public int getPCY() {

		return spinnerY.getSelection();
	}

	public int getPCZ() {

		return spinnerZ.getSelection();
	}

	public void setInput(IAnalysisSettings analysisSettings) {

		if(analysisSettings != null) {
			/*
			 * Max PCs
			 */
			int numberPrincipalComponents = analysisSettings.getNumberOfPrincipalComponents();
			//
			int selectionX = spinnerX.getSelection();
			spinnerX.setSelection(selectionX <= numberPrincipalComponents ? selectionX : 1);
			spinnerX.setMaximum(numberPrincipalComponents);
			spinnerX.setEnabled(isSpinnerActive(SPINNER_X));
			//
			int selectionY = spinnerY.getSelection();
			spinnerY.setSelection(selectionY <= numberPrincipalComponents ? selectionY : 1);
			spinnerY.setMaximum(numberPrincipalComponents);
			spinnerY.setEnabled(isSpinnerActive(SPINNER_Y));
			//
			int selectionZ = spinnerZ.getSelection();
			spinnerZ.setSelection(selectionZ <= numberPrincipalComponents ? selectionZ : 1);
			spinnerZ.setMaximum(numberPrincipalComponents);
			spinnerZ.setEnabled(isSpinnerActive(SPINNER_Z));
		} else {
			spinnerX.setSelection(1);
			spinnerX.setMaximum(maximum);
			spinnerX.setEnabled(false);
			//
			spinnerY.setSelection(2);
			spinnerY.setMaximum(maximum);
			spinnerY.setEnabled(false);
			//
			spinnerZ.setSelection(3);
			spinnerZ.setMaximum(maximum);
			spinnerZ.setEnabled(false);
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(6, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createLabel(this, "PC (X):");
		spinnerX = createSpinner(this, "Select the PC for the x axis.", maximum, isSpinnerActive(SPINNER_X), 1);
		//
		createLabel(this, "PC (Y):");
		spinnerY = createSpinner(this, "Select the PC for the y axis.", maximum, isSpinnerActive(SPINNER_Y), 2);
		//
		createLabel(this, "PC (Z):");
		spinnerZ = createSpinner(this, "Select the PC for the z axis.", maximum, isSpinnerActive(SPINNER_Z), 3);
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Spinner createSpinner(Composite parent, String tooltip, int maximum, boolean enabled, int selection) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setToolTipText(tooltip);
		spinner.setMinimum(1);
		spinner.setIncrement(1);
		spinner.setSelection(selection);
		spinner.setMaximum(maximum);
		spinner.setEnabled(enabled);
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				fireUpdate();
			}
		});
		//
		return spinner;
	}

	private boolean isSpinnerActive(int constant) {

		return (options & constant) == constant;
	}

	private void fireUpdate() {

		if(selectionListener != null) {
			selectionListener.update(spinnerX.getSelection(), spinnerY.getSelection(), spinnerZ.getSelection());
		}
	}
}
