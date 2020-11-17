/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedPeakQuantitationListUI;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class PeakQuantitationListPart extends AbstractPart<ExtendedPeakQuantitationListUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION;
	private PeakQuantitationsExtractor peakQuantitationsExtractor = new PeakQuantitationsExtractor();

	@Inject
	public PeakQuantitationListPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedPeakQuantitationListUI createControl(Composite parent) {

		return new ExtendedPeakQuantitationListUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

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
					getControl().update(peakQuantitationsExtractor.extract(peaks));
					return true;
				} else {
					getControl().update(null);
					return false;
				}
			} else {
				getControl().update(null);
				return false;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic);
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}
}
