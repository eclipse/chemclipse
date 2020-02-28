/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SubtractScanPage extends WizardPage {

	public SubtractScanPage() {
		super(SubtractScanPage.class.getName());
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		//
		createLabel(composite);
		createButtonMultiSubtract(composite);
		createButtonShowDialog(composite);
		//
		setControl(composite);
	}

	private Label createLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("Select a scan in the chromatogram. It will be subtracted from the current selection.");
		//
		return label;
	}

	private Button createButtonMultiSubtract(Composite parent) {

		Image imageSubtractOne = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_SUBTRACT_SCAN_ONE, IApplicationImage.SIZE_128x68);
		Image imageSubtractMany = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_SUBTRACT_SCAN_MANY, IApplicationImage.SIZE_128x68);
		boolean isSubtractMany = PreferenceSupplier.isEnableMultiSubtract();
		String textSubtractOne = "Modus: One Scan Subtract";
		String textSubtractMany = "Modus: Multi Scan Subtract";
		//
		Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText(isSubtractMany ? textSubtractMany : textSubtractOne);
		button.setImage(isSubtractMany ? imageSubtractMany : imageSubtractOne);
		button.setSelection(isSubtractMany);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean isSubtractMany = button.getSelection();
				PreferenceSupplier.setEnableMultiSubtract(isSubtractMany);
				button.setText(isSubtractMany ? textSubtractMany : textSubtractOne);
				button.setImage(isSubtractMany ? imageSubtractMany : imageSubtractOne);
			}
		});
		//
		return button;
	}

	private Button createButtonShowDialog(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));
		button.setText("Show this dialog");
		button.setSelection(PreferenceSupplier.isShowSubtractDialog());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.setShowSubtractDialog(button.getSelection());
			}
		});
		//
		return button;
	}
}
