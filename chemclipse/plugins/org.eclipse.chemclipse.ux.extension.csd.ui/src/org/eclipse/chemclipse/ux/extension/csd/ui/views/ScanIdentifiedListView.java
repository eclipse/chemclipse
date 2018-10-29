/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ScanIdentifiedListView extends AbstractChromatogramSelectionCSDView {

	private Text targetsText;
	private DecimalFormat decimalFormat;

	@Inject
	public ScanIdentifiedListView(Composite parent, MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
		createControl(parent);
	}

	@Override
	public void update(IChromatogramSelectionCSD chromatogramSelection, boolean forceReload) {

		if(doUpdate(chromatogramSelection)) {
			StringBuilder builder = new StringBuilder();
			for(IScan scan : chromatogramSelection.getChromatogramCSD().getScans()) {
				int startRetentionTime = chromatogramSelection.getStartRetentionTime();
				int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
				//
				if(scan instanceof IScanCSD) {
					int retentionTime = scan.getRetentionTime();
					if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
						/*
						 * Get the entries.
						 */
						IScanCSD scanCSD = (IScanCSD)scan;
						if(scanCSD.getTargets().size() > 0) {
							for(IIdentificationTarget scanTargetCSD : scanCSD.getTargets()) {
								builder.append(scanTargetCSD.getLibraryInformation().getName());
								builder.append(" > ");
								builder.append(scanTargetCSD.getComparisonResult().getMatchFactor());
								builder.append("% | ");
								builder.append(decimalFormat.format(scanCSD.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
								builder.append(" min");
								builder.append(Text.DELIMITER);
							}
						}
					}
				}
			}
			targetsText.setText(builder.toString());
		}
	}

	private void createControl(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		parent.setBackground(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		//
		targetsText = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
		targetsText.setText("");
		targetsText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
