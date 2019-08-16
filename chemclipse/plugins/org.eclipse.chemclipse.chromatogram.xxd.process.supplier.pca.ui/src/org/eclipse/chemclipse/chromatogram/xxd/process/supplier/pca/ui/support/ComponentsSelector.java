/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH..
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ComponentsSelector {

	private Combo comboX;
	private Combo comboY;
	private Composite composite;
	private final String PRINCIPAL_COMPONET = "PC ";

	public ComponentsSelector(Composite parent, Object layoutData) {
		initialize(parent, layoutData);
	}

	public int getX() {

		return comboX.getSelectionIndex();
	}

	public int getY() {

		return comboY.getSelectionIndex() + 1;
	}

	private void initialize(Composite parent, Object layoutData) {

		composite = new Composite(parent, SWT.None);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(4, false));
		Label label = new Label(composite, SWT.None);
		label.setText("Axis X: ");
		comboX = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		comboX.add(PRINCIPAL_COMPONET);
		label = new Label(composite, SWT.None);
		label.setText("Axis Y: ");
		comboY = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		comboY.add(PRINCIPAL_COMPONET);
	}

	private void setComboX(int number) {

		comboX.removeAll();
		comboX.add("--");
		for(int i = 1; i <= number; i++) {
			comboX.add(PRINCIPAL_COMPONET + i);
		}
		comboX.select(1);
	}

	private void setComboY(int number) {

		comboY.removeAll();
		for(int i = 1; i <= number; i++) {
			comboY.add(PRINCIPAL_COMPONET + i);
		}
		if(number > 1) {
			comboY.select(1);
		} else {
			comboY.select(0);
		}
	}

	public void update(IPcaResults pcaResults) {

		int number = pcaResults.getPcaSettings().getNumberOfPrincipalComponents();
		if(number > 0) {
			setComboX(number);
			setComboY(number);
			composite.layout(false);
			composite.redraw();
		}
	}
}
