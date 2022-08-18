/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - improvements settings
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ExtractionOption;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Algorithm;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class PeakSettingsWizardPage extends AbstractAnalysisWizardPage {

	private ExtractionOption extractionOption = ExtractionOption.RETENTION_TIME_MS;
	private int groupWindow = 1500; // 1.5 sec
	//
	private DataBindingContext dataBindingContext = new DataBindingContext();
	private IObservableValue<Integer> groupValueWindow = new WritableValue<>();
	//
	private Label labelGroupValue;
	private Text textGroupValue;

	public PeakSettingsWizardPage() {

		super("Main Properties");
		setTitle("PCA");
		setDescription("Set main PCA parameters.");
		//
		groupValueWindow.setValue(groupWindow);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		WizardPageSupport.create(this, dataBindingContext);
		//
		createLabel(composite, "Title:");
		createTextTitle(composite, 1);
		createLabel(composite, "Group By:");
		createComboViewerExtractionOption(composite);
		labelGroupValue = createLabel(composite, "Retention Time Window [ms]:");
		textGroupValue = createVariableSection(composite);
		createLabel(composite, "Number of PCs:");
		createSpinnerPrincipleComponents(composite);
		createLabel(composite, "Algorithm:");
		createComboViewerAlgorithm(composite);
		//
		updateWidgets();
		//
		setControl(composite);
	}

	public ExtractionOption getExtractionOption() {

		return extractionOption;
	}

	public int getGroupValueWindow() {

		return groupValueWindow.getValue();
	}

	private ComboViewer createComboViewerExtractionOption(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ExtractionOption) {
					return ((ExtractionOption)element).label();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select an extraction option.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ExtractionOption) {
					extractionOption = (ExtractionOption)object;
					updateWidgets();
				}
			}
		});
		//
		comboViewer.setInput(ExtractionOption.values());
		combo.select(0);
		//
		return comboViewer;
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Text createVariableSection(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		UpdateValueStrategy<String, Integer> widgetToModel = UpdateValueStrategy.create(IConverter.create(String.class, Integer.class, o1 -> {
			try {
				return Integer.parseInt((String)o1);
			} catch(NumberFormatException e) {
				// No message
			}
			return null;
		}));
		//
		widgetToModel.setBeforeSetValidator(o1 -> {
			if(o1 instanceof Integer) {
				Integer i = (Integer)o1;
				if(i > 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("Warning: The value must be positive.");
		});
		//
		UpdateValueStrategy<Integer, String> modelToWidget = UpdateValueStrategy.create(IConverter.create(Integer.class, String.class, o1 -> Integer.toString(((Integer)o1))));
		dataBindingContext.bindValue(WidgetProperties.text(SWT.Modify).observe(text), groupValueWindow, widgetToModel, modelToWidget);
		//
		return text;
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

				analysisSettings.setNumberOfPrincipalComponents(spinner.getSelection());
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
					analysisSettings.setAlgorithm((Algorithm)object);
				}
			}
		});
		//
		combo.select(getSelectedAlgorithmIndex());
		//
		return comboViewer;
	}

	private int getSelectedAlgorithmIndex() {

		for(int i = 0; i < algorithms.length; i++) {
			Algorithm algorithm = algorithms[i];
			if(algorithm.equals(analysisSettings.getAlgorithm())) {
				return i;
			}
		}
		return -1;
	}

	private void updateWidgets() {

		boolean enabled = false;
		switch(extractionOption) {
			case RETENTION_TIME_MS:
				enabled = true;
				labelGroupValue.setText("Retention Time Window [ms]:");
				break;
			case RETENTION_INDEX:
				enabled = true;
				labelGroupValue.setText("Retention Index Window:");
				break;
			default:
				break;
		}
		//
		labelGroupValue.setEnabled(enabled);
		textGroupValue.setEnabled(enabled);
	}
}