/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.preferences.PreferenceSupplierModelMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
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

	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public SubtractScanPage() {

		super(SubtractScanPage.class.getName());
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		//
		createLabel(composite);
		createButtonNominalMZ(composite);
		createButtonNormalize(composite);
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

	private Button createButtonNominalMZ(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText("Nominal m/z");
		button.setToolTipText("Select to use nomimal m/z.");
		button.setSelection(PreferenceSupplierModelMSD.isUseNominalMZ());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean useNominalMZ = button.getSelection();
				PreferenceSupplierModelMSD.setUseNominalMZ(useNominalMZ);
			}
		});
		//
		return button;
	}

	private Button createButtonNormalize(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText("Normalize Intensities");
		button.setToolTipText("Select to use normalize the intensities.");
		button.setSelection(PreferenceSupplierModelMSD.isUseNormalizedScan());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean useNormalizedScan = button.getSelection();
				PreferenceSupplierModelMSD.setUseNormalizedScan(useNormalizedScan);
			}
		});
		//
		return button;
	}

	private Button createButtonMultiSubtract(Composite parent) {

		Image imageSubtractOne = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_SUBTRACT_SCAN_ONE, IApplicationImage.SIZE_128x68);
		Image imageSubtractMany = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_SUBTRACT_SCAN_MANY, IApplicationImage.SIZE_128x68);
		boolean isSubtractMany = preferenceStore.getBoolean(PreferenceSupplier.P_ENABLE_MULTI_SUBTRACT);
		String textSubtractOne = "Modus: One Scan Subtract";
		String textSubtractMany = "Modus: Multi Scan Subtract";
		//
		Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.setText(isSubtractMany ? textSubtractMany : textSubtractOne);
		button.setToolTipText("Select whether to subtract one or many scans.");
		button.setImage(isSubtractMany ? imageSubtractMany : imageSubtractOne);
		button.setSelection(isSubtractMany);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean isSubtractMany = button.getSelection();
				preferenceStore.setValue(PreferenceSupplier.P_ENABLE_MULTI_SUBTRACT, isSubtractMany);
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
		button.setSelection(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_SUBTRACT_DIALOG));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				preferenceStore.setValue(PreferenceSupplier.P_SHOW_SUBTRACT_DIALOG, button.getSelection());
			}
		});
		//
		return button;
	}
}
