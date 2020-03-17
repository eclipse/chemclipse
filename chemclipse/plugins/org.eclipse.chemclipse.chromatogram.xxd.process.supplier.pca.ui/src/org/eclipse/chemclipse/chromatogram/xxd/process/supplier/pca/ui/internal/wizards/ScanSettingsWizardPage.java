/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - Optimization UI
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ScansExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class ScanSettingsWizardPage extends WizardPage {

	private IAnalysisSettings analysisSettings = new AnalysisSettings();
	//
	private DataBindingContext dataBindingContext = new DataBindingContext();
	private ExtractionType extractionType;
	private IObservableValue<Integer> maximalNumberScans = new WritableValue<>();
	private IObservableValue<Double> retentionTimeWindow = new WritableValue<>();
	private boolean useDefaultProperties;
	//
	private Algorithm[] algorithms = new Algorithm[]{Algorithm.SVD, Algorithm.NIPALS, Algorithm.OPLS};

	public ScanSettingsWizardPage() {
		super("Main Parameters");
		setTitle("Set Main Parameters");
		retentionTimeWindow.setValue(1.0);
		maximalNumberScans.setValue(5000);
		extractionType = ExtractionType.CLOSEST_SCAN;
		useDefaultProperties = true;
	}

	public int getRetentionTimeWindow() {

		return (int)Math.round(retentionTimeWindow.getValue() * 1000);
	}

	public IAnalysisSettings getAnalysisSettings() {

		return analysisSettings;
	}

	public ExtractionType getExtractionType() {

		return extractionType;
	}

	public int getMaximalNumberScans() {

		return maximalNumberScans.getValue();
	}

	public boolean isUseDefaultProperties() {

		return useDefaultProperties;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		//
		WizardPageSupport.create(this, dataBindingContext);
		//
		createLabel(composite, "Retention Time Windows [s]:");
		createVariableSection(composite);
		createLabel(composite, "Maximum Number of Scans:");
		createSpinnerNumberOfScans(composite);
		createCheckBoxDefaultProperties(composite);
		createRadioButtonClosestScan(composite, true);
		createRadioButtonInterpolate(composite, false);
		//
		createLabel(composite, "Number of PCs:");
		createSpinnerPrincipleComponents(composite);
		createLabel(composite, "Algorithm:");
		createComboViewerAlgorithm(composite);
		//
		setControl(composite);
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private void createVariableSection(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		UpdateValueStrategy widgetToModel = UpdateValueStrategy.create(IConverter.create(String.class, Double.class, o1 -> {
			try {
				return Double.parseDouble((String)o1);
			} catch(NumberFormatException e) {
				// No message
			}
			return null;
		}));
		//
		widgetToModel.setBeforeSetValidator(o1 -> {
			if(o1 instanceof Double) {
				Double i = (Double)o1;
				if(i > 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("Warning: The value must be positive.");
		});
		//
		UpdateValueStrategy modelToWidget = UpdateValueStrategy.create(IConverter.create(Double.class, String.class, o1 -> Double.toString(((Double)o1))));
		dataBindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(text), retentionTimeWindow, widgetToModel, modelToWidget);
	}

	private Spinner createSpinnerNumberOfScans(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spinner.setMinimum(1);
		spinner.setIncrement(1000);
		spinner.setMaximum(Integer.MAX_VALUE);
		dataBindingContext.bindValue(WidgetProperties.selection().observe(spinner), maximalNumberScans);
		//
		return spinner;
	}

	private Button createCheckBoxDefaultProperties(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("Use default properties if it is possible (chromatogram have same start retention time and same scan interval)");
		button.setToolTipText("Default Properties");
		button.setSelection(true);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				useDefaultProperties = button.getSelection();
			}
		});
		//
		return button;
	}

	private Button createRadioButtonClosestScan(Composite parent, boolean selected) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText("Select the closest scan");
		button.setToolTipText("Closest Scan");
		button.setSelection(selected);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				extractionType = ExtractionType.CLOSEST_SCAN;
			}
		});
		//
		return button;
	}

	private Button createRadioButtonInterpolate(Composite parent, boolean selected) {

		Button button = new Button(parent, SWT.RADIO);
		button.setText("Interpolate scan");
		button.setToolTipText("Interpolation");
		button.setSelection(selected);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				extractionType = ExtractionType.LINEAR_INTERPOLATION_SCAN;
			}
		});
		//
		return button;
	}

	private Spinner createSpinnerPrincipleComponents(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setToolTipText("Number of Principal Components");
		spinner.setMinimum(PreferenceSupplier.MIN_NUMBER_OF_COMPONENTS);
		spinner.setIncrement(1);
		spinner.setSelection(analysisSettings.getNumberOfPrincipalComponents());
		spinner.setMaximum(PreferenceSupplier.MAX_NUMBER_OF_COMPONENTS);
		spinner.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(analysisSettings != null) {
					analysisSettings.setNumberOfPrincipalComponents(spinner.getSelection());
				}
			}
		});
		//
		return spinner;
	}

	private ComboViewer createComboViewerAlgorithm(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setInput(algorithms);
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof Algorithm) {
					return ((Algorithm)element).getName();
				}
				return null;
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText("PCA Algorithm");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof Algorithm) {
					if(analysisSettings != null) {
						analysisSettings.setAlgorithm((Algorithm)object);
					}
				}
			}
		});
		//
		combo.select(getSelectedAlgorithmIndex(comboViewer));
		//
		return comboViewer;
	}

	private int getSelectedAlgorithmIndex(ComboViewer comboViewer) {

		for(int i = 0; i < algorithms.length; i++) {
			Algorithm algorithm = algorithms[i];
			if(algorithm.equals(analysisSettings.getAlgorithm())) {
				return i;
			}
		}
		return -1;
	}
}
