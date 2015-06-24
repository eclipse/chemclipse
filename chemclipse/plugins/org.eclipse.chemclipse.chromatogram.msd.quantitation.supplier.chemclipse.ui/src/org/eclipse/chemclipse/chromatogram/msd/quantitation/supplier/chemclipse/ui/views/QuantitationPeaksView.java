/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationCompoundDocument;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.documents.IQuantitationPeakDocument;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.peak.StackedPeakUI;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesMassScale;

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
	public void update(IQuantitationCompoundDocument quantitationCompoundDocument, IQuantDatabase database) {

		if(doUpdate()) {
			if(quantitationCompoundDocument != null && database != null) {
				/*
				 * Extract and display the compound peaks stacked.
				 */
				List<IQuantitationPeakDocument> quantitationPeakDocuments = database.getQuantitationPeakDocuments(quantitationCompoundDocument);
				List<IPeakMSD> peakListMSD = getPeakList(quantitationPeakDocuments);
				stackedPeakUI.update(peakListMSD, true);
			}
		}
	}

	private List<IPeakMSD> getPeakList(List<IQuantitationPeakDocument> quantitationPeakDocuments) {

		assert quantitationPeakDocuments != null : "The peak documents list must be not null.";
		List<IPeakMSD> peakListMSD = new ArrayList<IPeakMSD>();
		for(IQuantitationPeakDocument quantitationPeakDocument : quantitationPeakDocuments) {
			peakListMSD.add(quantitationPeakDocument.getPeakMSD());
		}
		return peakListMSD;
	}
}
