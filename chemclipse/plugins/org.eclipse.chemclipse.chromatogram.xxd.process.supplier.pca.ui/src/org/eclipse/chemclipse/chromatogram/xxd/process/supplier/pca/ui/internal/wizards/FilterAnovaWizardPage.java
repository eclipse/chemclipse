/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.AnovaFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.AbstractPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IPreprocessing.DATA_TYPE_PROCESSING;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FilterAnovaWizardPage extends WizardPage implements IFilterWizardPage {

	final private DataBindingContext dbc = new DataBindingContext();
	private IObservableValue<Double> observeAlfa;
	private IObservableValue<DATA_TYPE_PROCESSING> dataTypeFiltration;

	protected FilterAnovaWizardPage(AnovaFilter anovaFilter) {
		super("ANOVA Filter");
		setTitle("One-way Analysis of Variance Filter");
		setDescription("ANOVA filter works just with selected sampels, which are in group (contains group name)");
		observeAlfa = PojoProperties.value(AnovaFilter.class, "alpha", Double.class).observe(anovaFilter);
		dataTypeFiltration = PojoProperties.value(AbstractPreprocessing.class, "dataTypeProcessing", DATA_TYPE_PROCESSING.class).observe(anovaFilter);
	}

	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, true));
		//
		Label label = new Label(composite, SWT.None);
		label.setText("Select data type filtration");
		SelectObservableValue<DATA_TYPE_PROCESSING> selectedRadioButtonObservableProcessData = new SelectObservableValue<>();
		Button button = new Button(composite, SWT.RADIO);
		button.setText("Use on raw data");
		selectedRadioButtonObservableProcessData.addOption(DATA_TYPE_PROCESSING.RAW_DATA, WidgetProperties.selection().observe(button));
		button = new Button(composite, SWT.RADIO);
		button.setText("Use on modified data");
		selectedRadioButtonObservableProcessData.addOption(DATA_TYPE_PROCESSING.MODIFIED_DATA, WidgetProperties.selection().observe(button));
		dbc.bindValue(selectedRadioButtonObservableProcessData, dataTypeFiltration, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		//
		label = new Label(composite, SWT.None);
		label.setText("Select row in data table whose p-value is less than value (in %)");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);
		Text text = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		ISWTObservableValue targetObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
		targetToModel.setConverter(IConverter.create(String.class, Double.class, o1 -> {
			try {
				return Double.parseDouble((String)o1) / 100.0;
			} catch(NumberFormatException e) {
			}
			return null;
		}));
		targetToModel.setAfterConvertValidator(o1 -> {
			if(o1 instanceof Double) {
				Double d = (Double)o1;
				if(d <= 1 && d >= 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("error");
		});
		UpdateValueStrategy modelToTarget = UpdateValueStrategy.create(IConverter.create(Double.class, String.class, o1 -> Double.toString(((Double)o1) * 100.0)));
		dbc.bindValue(targetObservableValue, observeAlfa, targetToModel, modelToTarget);
		setControl(composite);
	}

	@Override
	public void update() {

		dbc.updateModels();
	}
}
