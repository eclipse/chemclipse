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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.AbstractFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.AbundanceFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter.DATA_TYPE_PROCESSING;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FilterAbundanceWizardPage extends WizardPage implements IFilterWizardPage {

	final private DataBindingContext dbc = new DataBindingContext();
	private IObservableValue<Integer> observableFilterType;
	private IObservableValue<Integer> observableLimitType;
	private IObservableValue<Double> observeLimitValue;
	private IObservableValue<DATA_TYPE_PROCESSING> dataTypeFiltration;

	protected FilterAbundanceWizardPage(AbundanceFilter abundanceFilter) {

		super("Abundance Filter");
		setTitle("Abundance Filter");
		observableLimitType = PojoProperties.value(AbundanceFilter.class, "limitType", Integer.class).observe(abundanceFilter);
		observeLimitValue = PojoProperties.value(AbundanceFilter.class, "limitValue", Double.class).observe(abundanceFilter);
		observableFilterType = PojoProperties.value(AbundanceFilter.class, "filterType", Integer.class).observe(abundanceFilter);
		dataTypeFiltration = PojoProperties.value(AbstractFilter.class, "dataTypeProcessing", DATA_TYPE_PROCESSING.class).observe(abundanceFilter);
	}

	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		//
		Composite groupDataSelection = new Composite(composite, SWT.None);
		groupDataSelection.setLayout(new GridLayout(1, false));
		Label label = new Label(groupDataSelection, SWT.None);
		label.setText("Select data type filtration");
		SelectObservableValue<DATA_TYPE_PROCESSING> selectedRadioButtonObservableProcessData = new SelectObservableValue<>();
		Button button = new Button(groupDataSelection, SWT.RADIO);
		button.setText("Use on raw data");
		selectedRadioButtonObservableProcessData.addOption(DATA_TYPE_PROCESSING.RAW_DATA, WidgetProperties.selection().observe(button));
		button = new Button(groupDataSelection, SWT.RADIO);
		button.setText("Use on modified data");
		selectedRadioButtonObservableProcessData.addOption(DATA_TYPE_PROCESSING.MODIFIED_DATA, WidgetProperties.selection().observe(button));
		dbc.bindValue(selectedRadioButtonObservableProcessData, dataTypeFiltration, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		//
		label = new Label(composite, SWT.None);
		label.setText("Select row in data table under the condition");
		SelectObservableValue<Integer> selectedRadioButtonObservable = new SelectObservableValue<>();
		button = new Button(composite, SWT.RADIO);
		button.setText("All value in row are");
		button.setSelection(true);
		selectedRadioButtonObservable.addOption(AbundanceFilter.ALL_VALUE, WidgetProperties.selection().observe(button));
		button = new Button(composite, SWT.RADIO);
		button.setText("At least one value  in row is");
		selectedRadioButtonObservable.addOption(AbundanceFilter.ANY_VALUE, WidgetProperties.selection().observe(button));
		dbc.bindValue(selectedRadioButtonObservable, observableFilterType, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		Composite compareComposite = new Composite(composite, SWT.None);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(compareComposite);
		compareComposite.setLayout(new GridLayout(3, false));
		label = new Label(compareComposite, SWT.None);
		Combo combo = new Combo(compareComposite, SWT.READ_ONLY);
		combo.add("greater than");
		combo.add("less than");
		combo.select(0);
		ISWTObservableValue comboLimitType = WidgetProperties.singleSelectionIndex().observe(combo);
		dbc.bindValue(comboLimitType, observableLimitType, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		Text text = new Text(compareComposite, SWT.BORDER);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(text);
		ISWTObservableValue txtSecondAttributeObservable = WidgetProperties.text(SWT.Modify).observe(text);
		UpdateValueStrategy targetToModel = new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT);
		targetToModel.setConverter(IConverter.create(String.class, Double.class, o1 -> {
			try {
				return Double.parseDouble((String)o1);
			} catch(NumberFormatException e) {
			}
			return null;
		}));
		targetToModel.setAfterConvertValidator(o1 -> {
			if(o1 instanceof Double) {
				return ValidationStatus.ok();
			}
			return ValidationStatus.error("Error");
		});
		UpdateValueStrategy modelToTarget = UpdateValueStrategy.create(IConverter.create(Double.class, String.class, o1 -> Double.toString((Double)o1)));
		dbc.bindValue(txtSecondAttributeObservable, observeLimitValue, targetToModel, modelToTarget);
		setControl(composite);
	}

	@Override
	public void update() {

		dbc.updateModels();
	}
}
