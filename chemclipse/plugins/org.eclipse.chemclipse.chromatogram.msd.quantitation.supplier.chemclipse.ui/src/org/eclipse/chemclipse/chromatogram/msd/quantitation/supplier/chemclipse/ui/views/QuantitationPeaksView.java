/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.views;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.peak.StackedPeakUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesMassScale;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class QuantitationPeaksView extends AbstractQuantitationCompoundSelectionView {

	@Inject
	private Composite parent;
	private StackedPeakUI stackedPeakUI;

	@Inject
	public QuantitationPeaksView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	public void createControl() {

		stackedPeakUI = new StackedPeakUI(parent, SWT.NONE, new AxisTitlesMassScale());
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		stackedPeakUI.setFocus();
	}

	@Override
	public void update(IQuantitationCompound quantitationCompoundMSD, IQuantDatabase database) {

		if(doUpdate()) {
			if(quantitationCompoundMSD != null && database != null) {
				/*
				 * Extract and display the compound peaks stacked.
				 */
				List<IQuantitationPeak> quantitationPeaks = database.getQuantitationPeaks(quantitationCompoundMSD);
				List<IPeak> peakList = getPeakList(quantitationPeaks);
				List<IPeakMSD> peakListMSD = new ArrayList<>();
				for(IPeak peak : peakList) {
					if(peak instanceof IPeakMSD) {
						peakListMSD.add((IPeakMSD)peak);
					}
				}
				stackedPeakUI.update(peakListMSD, true);
			}
		}
	}

	private List<IPeak> getPeakList(List<IQuantitationPeak> quantitationPeaks) {

		assert quantitationPeaks != null : "The peak documents list must be not null.";
		List<IPeak> peakListMSD = new ArrayList<IPeak>();
		for(IQuantitationPeak quantitationPeakDocument : quantitationPeaks) {
			peakListMSD.add(quantitationPeakDocument.getReferencePeak());
		}
		return peakListMSD;
	}
}
