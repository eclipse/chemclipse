/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.PeakQuantitationsExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.swt.ui.components.peaks.PeakQuantitationListUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PeakQuantitationListView extends AbstractChromatogramSelectionView {

	@Inject
	private Composite parent;
	private PeakQuantitationListUI peakQuantitationListUI;
	private PeakQuantitationsExtractor peakQuantitationsExtractor;

	@Inject
	public PeakQuantitationListView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		peakQuantitationsExtractor = new PeakQuantitationsExtractor();
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		peakQuantitationListUI = new PeakQuantitationListUI(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		peakQuantitationListUI.getTable().setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			List<? extends IPeak> peaks = null;
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				peaks = chromatogramMSD.getPeaks((IChromatogramSelectionMSD)chromatogramSelection);
			} else if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				peaks = chromatogramCSD.getPeaks((IChromatogramSelectionCSD)chromatogramSelection);
			}
			peakQuantitationListUI.update(peakQuantitationsExtractor.extract(peaks));
		}
	}
}
