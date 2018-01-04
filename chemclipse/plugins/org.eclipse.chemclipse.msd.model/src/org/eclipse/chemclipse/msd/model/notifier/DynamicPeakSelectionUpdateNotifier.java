/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.notifier;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;

public class DynamicPeakSelectionUpdateNotifier implements IPeakMSDSelectionUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;
	private Map<String, Object> map;

	public DynamicPeakSelectionUpdateNotifier() {
		map = new HashMap<String, Object>();
	}

	@Override
	public void update(IPeakMSD peakMSD, boolean forceReload) {

		/*
		 * Don't use a new map each time, to prevent unnecessary object creation.
		 * Update View : Peak
		 */
		map.clear();
		map.put(IChemClipseEvents.PROPERTY_PEAK_MSD, peakMSD);
		map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, forceReload);
		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK, map);
		/*
		 * Update View : Simple Mass Spectrum
		 */
		if(peakMSD != null) {
			map.clear();
			map.put(IChemClipseEvents.PROPERTY_MASSPECTRUM, peakMSD.getExtractedMassSpectrum());
			map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, true);
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRUM, map);
		}
	}
}
