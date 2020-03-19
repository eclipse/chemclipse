/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class ExtendedScorePlot extends Composite {

	private ScorePlot plot;
	private Spinner spinnerX;
	private Spinner spinnerY;
	//
	private EvaluationPCA evaluationPCA = null;

	public ExtendedScorePlot(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		this.evaluationPCA = evaluationPCA;
		updateWidgets();
		updatePlot();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		plot = createPlot(this);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createLabel(composite, "PCX:");
		spinnerX = createSpinnerPCX(composite);
		createLabel(composite, "PCY:");
		spinnerY = createSpinnerPCY(composite);
		createSettingsButton(composite);
	}

	private ScorePlot createPlot(Composite parent) {

		ScorePlot plot = new ScorePlot(parent, SWT.NONE);
		plot.setLayoutData(new GridData(GridData.FILL_BOTH));
		return plot;
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Spinner createSpinnerPCX(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setToolTipText("PC (X)");
		spinner.setMinimum(1);
		spinner.setIncrement(1);
		spinner.setSelection(1);
		spinner.setMaximum(PreferenceSupplier.getNumberOfComponents());
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updatePlot();
			}
		});
		//
		return spinner;
	}

	private Spinner createSpinnerPCY(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setToolTipText("PC (Y)");
		spinner.setMinimum(1);
		spinner.setIncrement(1);
		spinner.setSelection(2);
		spinner.setMaximum(PreferenceSupplier.getNumberOfComponents());
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updatePlot();
			}
		});
		//
		return spinner;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePage()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		updatePlot();
	}

	private void updateWidgets() {

		if(evaluationPCA != null) {
			IAnalysisSettings analysisSettings = evaluationPCA.getSamples().getAnalysisSettings();
			int numberOfPrincipalComponents = analysisSettings.getNumberOfPrincipalComponents();
			//
			int selectionX = spinnerX.getSelection();
			spinnerX.setSelection(selectionX <= numberOfPrincipalComponents ? selectionX : 1);
			spinnerX.setMaximum(numberOfPrincipalComponents);
			//
			int selectionY = spinnerY.getSelection();
			spinnerY.setSelection(selectionY <= numberOfPrincipalComponents ? selectionY : 1);
			spinnerY.setMaximum(numberOfPrincipalComponents);
		} else {
			spinnerX.setSelection(1);
			spinnerX.setMaximum(PreferenceSupplier.getNumberOfComponents());
			spinnerY.setSelection(1);
			spinnerY.setMaximum(PreferenceSupplier.getNumberOfComponents());
		}
	}

	private void updatePlot() {

		int pcX = spinnerX.getSelection();
		int pcY = spinnerY.getSelection();
		//
		if(evaluationPCA != null) {
			plot.setInput(evaluationPCA, pcX, pcY);
		} else {
			plot.setInput(null, pcX, pcY);
		}
	}
}
