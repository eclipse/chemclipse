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
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.PeakQuantitationsExtractor;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.components.peaks.PeakQuantitationListUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PeakQuantitationListPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private PeakQuantitationListUI peakQuantitationListUI;
	private PeakQuantitationsExtractor peakQuantitationsExtractor = new PeakQuantitationsExtractor();

	@Inject
	public PeakQuantitationListPart(Composite parent, MPart part) {
		super(part);
		parent.setLayout(new FillLayout());
		peakQuantitationListUI = new PeakQuantitationListUI(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	@Focus
	public void setFocus() {

		updateObjects(getObjects(), getTopic());
		peakQuantitationListUI.getTable().setFocus();
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			Object object = null;
			if(!isUnloadEvent(topic)) {
				object = objects.get(0);
				if(object instanceof IChromatogramSelection) {
					@SuppressWarnings("rawtypes")
					IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
					@SuppressWarnings("rawtypes")
					IChromatogram chromatogram = chromatogramSelection.getChromatogram();
					List<? extends IPeak> peaks = null;
					if(chromatogram instanceof IChromatogramMSD) {
						IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
						peaks = chromatogramMSD.getPeaks((IChromatogramSelectionMSD)chromatogramSelection);
					} else if(chromatogram instanceof IChromatogramCSD) {
						IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
						peaks = chromatogramCSD.getPeaks((IChromatogramSelectionCSD)chromatogramSelection);
					} else if(chromatogram instanceof IChromatogramWSD) {
						IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
						peaks = chromatogramWSD.getPeaks((IChromatogramSelectionWSD)chromatogramSelection);
					}
					peakQuantitationListUI.update(peakQuantitationsExtractor.extract(peaks));
				} else {
					peakQuantitationListUI.update(null);
				}
			} else {
				peakQuantitationListUI.update(null);
			}
		}
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}
}
