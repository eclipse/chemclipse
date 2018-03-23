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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
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
import org.eclipse.swt.widgets.Text;

public class MainPropertiesPeaksInputWizardPage extends WizardPage {

	private DataBindingContext dbc = new DataBindingContext();
	private IObservableValue<Double> retentionTimeWindow = new WritableValue<>();

	protected MainPropertiesPeaksInputWizardPage(String pageName) {
		super(pageName);
		setTitle("Set Main Parameters");
		retentionTimeWindow.setValue(0.1);
	}

	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, true));
		Label label = new Label(composite, SWT.None);
		label.setText("Retention Time Windows (min)");
		Text text = new Text(composite, SWT.BORDER);
		UpdateValueStrategy targetToModel = UpdateValueStrategy.create(IConverter.create(String.class, Double.class, o1 -> {
			try {
				return Double.parseDouble((String)o1);
			} catch(NumberFormatException e) {
			}
			return null;
		}));
		targetToModel.setBeforeSetValidator(o1 -> {
			if(o1 instanceof Double) {
				Double i = (Double)o1;
				if(i > 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("Warning The value must be positive value");
		});
		UpdateValueStrategy modelToTarget = UpdateValueStrategy.create(IConverter.create(Double.class, String.class, o1 -> Double.toString(((Double)o1))));
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(text), retentionTimeWindow, targetToModel, modelToTarget);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setControl(composite);
	}

	public int getRetentionTimeWindow() {

		return (int)Math.round(retentionTimeWindow.getValue() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
	}
}
