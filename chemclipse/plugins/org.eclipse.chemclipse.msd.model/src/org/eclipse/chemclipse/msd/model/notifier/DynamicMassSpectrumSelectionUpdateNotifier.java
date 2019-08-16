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
package org.eclipse.chemclipse.msd.model.notifier;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;

public class DynamicMassSpectrumSelectionUpdateNotifier implements IMassSpectrumSelectionUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;
	private Map<String, Object> map;

	public DynamicMassSpectrumSelectionUpdateNotifier() {
		map = new HashMap<String, Object>();
	}

	@Override
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		/*
		 * Don't use a new map each time, to prevent unnecessary object creation.
		 * Update View : Peak
		 */
		map.clear();
		map.put(IChemClipseEvents.PROPERTY_MASSPECTRUM, massSpectrum);
		map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, forceReload);
		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRUM, map);
		/*
		 * Update new Data Analysis Perspective.
		 */
		eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, massSpectrum);
	}
}
