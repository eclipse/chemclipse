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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.EmptyDataFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter.DataTypeProcessing;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FilterEmptyDataWizardPage extends WizardPage implements IFilterWizardPage {

	final private DataBindingContext dbc = new DataBindingContext();
	private IObservableValue<DataTypeProcessing> dataTypeFiltration;

	protected FilterEmptyDataWizardPage(EmptyDataFilter emptyDataFilter) {

		super("Empty data filter");
		setTitle("Empty Data Filter");
		setDescription("Select rows, which contain just not-empty data");
		dataTypeFiltration = PojoProperties.value(AbstractFilter.class, "dataTypeProcessing", DataTypeProcessing.class).observe(emptyDataFilter);
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		//
		Label label = new Label(composite, SWT.None);
		label.setText("Select data type filtration");
		SelectObservableValue<DataTypeProcessing> selectedRadioButtonObservableProcessData = new SelectObservableValue<>();
		Button button = new Button(composite, SWT.RADIO);
		button.setText("Use on raw data");
		selectedRadioButtonObservableProcessData.addOption(DataTypeProcessing.RAW_DATA, WidgetProperties.selection().observe(button));
		button = new Button(composite, SWT.RADIO);
		button.setText("Use on modified data");
		selectedRadioButtonObservableProcessData.addOption(DataTypeProcessing.MODIFIED_DATA, WidgetProperties.selection().observe(button));
		dbc.bindValue(selectedRadioButtonObservableProcessData, dataTypeFiltration, new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);
		//
		setControl(composite);
	}

	@Override
	public void update() {

		dbc.updateModels();
	}
}
