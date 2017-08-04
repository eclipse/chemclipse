/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.AbundanceFilter;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.SelectObservableValue;
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

public class FilterAbundanceWizardPage extends WizardPage {

	final private DataBindingContext dbc = new DataBindingContext();
	private IObservableValue<Integer> observableFilterType;
	private IObservableValue<Integer> observableLimitType;
	private IObservableValue<Double> observeLimitValue;

	protected FilterAbundanceWizardPage(AbundanceFilter abundanceFilter) {
		super("Abundance Filter");
		setTitle("Abundance Filter");
		setDescription("Abundance filter works wiht all seleted samples");
		observableLimitType = PojoProperties.value(AbundanceFilter.class, "limitType", Integer.class).observe(abundanceFilter);
		observeLimitValue = PojoProperties.value(AbundanceFilter.class, "limitValue", Double.class).observe(abundanceFilter);
		observableFilterType = PojoProperties.value(AbundanceFilter.class, "filterType", Integer.class).observe(abundanceFilter);
	}

	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		Label label = new Label(composite, SWT.None);
		label.setText("Select row in data table wholes abundance for");
		SelectObservableValue<Integer> selectedRadioButtonObservable = new SelectObservableValue<>();
		Button button = new Button(composite, SWT.RADIO);
		button.setText("All value");
		button.setSelection(true);
		selectedRadioButtonObservable.addOption(AbundanceFilter.ALL_VALUE, WidgetProperties.selection().observe(button));
		button = new Button(composite, SWT.RADIO);
		button.setText("Any value");
		selectedRadioButtonObservable.addOption(AbundanceFilter.ANY_VALUE, WidgetProperties.selection().observe(button));
		dbc.bindValue(selectedRadioButtonObservable, observableFilterType);
		Composite compareComposite = new Composite(composite, SWT.None);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(compareComposite);
		compareComposite.setLayout(new GridLayout(3, false));
		label = new Label(compareComposite, SWT.None);
		label.setText("is");
		Combo combo = new Combo(compareComposite, SWT.READ_ONLY);
		combo.add("greater then");
		combo.add("less then");
		combo.select(0);
		ISWTObservableValue comboLimitType = WidgetProperties.singleSelectionIndex().observe(combo);
		dbc.bindValue(comboLimitType, observableLimitType);
		Text text = new Text(compareComposite, SWT.None);
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(text);
		ISWTObservableValue txtSecondAttributeObservable = WidgetProperties.text(SWT.Modify).observe(text);
		dbc.bindValue(txtSecondAttributeObservable, observeLimitValue);
		setControl(composite);
	}
}
