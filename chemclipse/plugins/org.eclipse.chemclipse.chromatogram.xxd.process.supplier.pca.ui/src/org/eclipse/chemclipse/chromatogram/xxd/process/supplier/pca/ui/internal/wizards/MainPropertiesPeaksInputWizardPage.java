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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class MainPropertiesPeaksInputWizardPage extends WizardPage {

	private DataBindingContext dbc = new DataBindingContext();
	private IObservableValue<Integer> numerOfComponents = new WritableValue<>();
	private IObservableValue<Integer> retentionTimeWindow = new WritableValue<>();

	protected MainPropertiesPeaksInputWizardPage(String pageName) {
		super(pageName);
		numerOfComponents.setValue(3);
		retentionTimeWindow.setValue(200);
	}

	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, true));
		Label label = new Label(composite, SWT.None);
		label.setText("Retention Time Windows (ms)");
		Text text = new Text(composite, SWT.None);
		UpdateValueStrategy targetToModel = UpdateValueStrategy.create(IConverter.create(String.class, Integer.class, o1 -> {
			try {
				return Integer.parseInt((String)o1);
			} catch(NumberFormatException e) {
			}
			return null;
		}));
		targetToModel.setBeforeSetValidator(o1 -> {
			if(o1 instanceof Integer) {
				Integer i = (Integer)o1;
				if(i > 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("Warning The value must be positive value");
		});
		UpdateValueStrategy modelToTarget = UpdateValueStrategy.create(IConverter.create(Integer.class, String.class, o1 -> Integer.toString(((Integer)o1))));
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), retentionTimeWindow, targetToModel, modelToTarget);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(composite, SWT.None);
		label.setText("Number of principal components");
		Spinner spinner = new Spinner(composite, SWT.NONE);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		spinner.setMinimum(3);
		dbc.bindValue(WidgetProperties.selection().observe(spinner), numerOfComponents);
		setControl(composite);
	}

	public int getNumerOfComponents() {

		return numerOfComponents.getValue();
	}

	public int getRetentionTimeWindow() {

		return retentionTimeWindow.getValue();
	}
}
