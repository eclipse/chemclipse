/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
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

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.core.identifier.scan.IScanTargetCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class ScanIdentifiedListView extends AbstractChromatogramSelectionCSDView {

	private Text targetsText;

	@Inject
	public ScanIdentifiedListView(Composite parent, MPart part, EPartService partService, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
		createControl(parent);
	}

	@Override
	public void update(IChromatogramSelectionCSD chromatogramSelection, boolean forceReload) {

		if(doUpdate(chromatogramSelection)) {
			StringBuilder builder = new StringBuilder();
			for(IScan scan : chromatogramSelection.getChromatogramCSD().getScans()) {
				if(scan instanceof IScanCSD) {
					IScanCSD scanCSD = (IScanCSD)scan;
					if(scanCSD.getTargets().size() > 0) {
						for(IScanTargetCSD scanTargetCSD : scanCSD.getTargets()) {
							builder.append(scanTargetCSD.getLibraryInformation().getName() + " " + scanTargetCSD.getComparisonResult().getMatchFactor() + "\n");
						}
					}
				}
			}
			targetsText.setText(builder.toString());
		}
	}

	private void createControl(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		//
		targetsText = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		targetsText.setText("");
		targetsText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
