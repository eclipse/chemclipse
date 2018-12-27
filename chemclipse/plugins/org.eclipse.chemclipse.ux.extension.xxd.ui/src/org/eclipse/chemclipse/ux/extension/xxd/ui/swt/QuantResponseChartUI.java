/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.CalibrationChart;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class QuantResponseChartUI extends Composite {

	private CalibrationChart calibrationChart;

	public QuantResponseChartUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createCalibrationChart(composite);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		createSettingsButton(composite);
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageProcessing = new PreferencePage();
				preferencePageProcessing.setTitle("Processing");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageProcessing));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						//
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void createCalibrationChart(Composite parent) {

		calibrationChart = new CalibrationChart(parent, SWT.NONE);
		calibrationChart.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
