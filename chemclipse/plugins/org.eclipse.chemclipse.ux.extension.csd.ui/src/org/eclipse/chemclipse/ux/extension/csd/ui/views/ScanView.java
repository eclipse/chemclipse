/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import java.text.DecimalFormat;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ScanView extends AbstractChromatogramSelectionCSDView {

	private Label scanNumberLabel;
	private Label retentionTimeLabel;
	private Label retentionIndexLabel;
	private Label totalSignalLabel;
	//
	private DecimalFormat decimalFormat;

	@Inject
	public ScanView(Composite parent, MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
		createControl(parent);
	}

	@Override
	public void update(IChromatogramSelectionCSD chromatogramSelection, boolean forceReload) {

		if(doUpdate(chromatogramSelection)) {
			IScanCSD selectedScan = chromatogramSelection.getSelectedScan();
			scanNumberLabel.setText(Integer.toString(selectedScan.getScanNumber()));
			retentionTimeLabel.setText(decimalFormat.format(selectedScan.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
			//
			if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
				retentionIndexLabel.setText(Integer.toString((int)selectedScan.getRetentionIndex()));
			} else {
				retentionIndexLabel.setText(decimalFormat.format(selectedScan.getRetentionIndex()));
			}
			//
			if(PreferenceSupplier.showAreaWithoutDecimals()) {
				totalSignalLabel.setText(Integer.toString((int)selectedScan.getTotalSignal()));
			} else {
				totalSignalLabel.setText(decimalFormat.format(selectedScan.getTotalSignal()));
			}
		}
	}

	private void createControl(Composite parent) {

		Label info;
		parent.setLayout(new GridLayout(2, false));
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		//
		info = new Label(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		FontDescriptor fontDescriptor = FontDescriptor.createFrom(info.getFont()).setStyle(SWT.BOLD).setHeight(15);
		info.setFont(fontDescriptor.createFont(info.getDisplay()));
		info.setLayoutData(gridData);
		info.setText("Selected Scan");
		//
		info = new Label(parent, SWT.NONE);
		info.setText("Scan Number: ");
		scanNumberLabel = new Label(parent, SWT.NONE);
		scanNumberLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		info = new Label(parent, SWT.NONE);
		info.setText("Retention Time (Min): ");
		retentionTimeLabel = new Label(parent, SWT.NONE);
		retentionTimeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		info = new Label(parent, SWT.NONE);
		info.setText("Retention Index: ");
		retentionIndexLabel = new Label(parent, SWT.NONE);
		retentionIndexLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		info = new Label(parent, SWT.NONE);
		info.setText("Total Signal: ");
		totalSignalLabel = new Label(parent, SWT.NONE);
		totalSignalLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
}
