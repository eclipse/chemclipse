/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.notifier;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;

public class DynamicChromatogramAndPeakSelectionUpdateNotifier implements IChromatogramAndPeakSelectionUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;
	private Map<String, Object> map;

	public DynamicChromatogramAndPeakSelectionUpdateNotifier() {
		map = new HashMap<String, Object>();
	}

	@Override
	public void update(IChromatogramSelectionCSD chromatogramSelection, IChromatogramPeakCSD chromatogramPeak, boolean forceReload) {

		/*
		 * Don't use a new map each time, to prevent unnecessary object creation.
		 */
		map.clear();
		map.put(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, chromatogramSelection);
		map.put(IChemClipseEvents.PROPERTY_CHROMATOGRAM_PEAK_CSD, chromatogramPeak);
		map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, forceReload);
		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_AND_PEAK_SELECTION, map);
	}
}
