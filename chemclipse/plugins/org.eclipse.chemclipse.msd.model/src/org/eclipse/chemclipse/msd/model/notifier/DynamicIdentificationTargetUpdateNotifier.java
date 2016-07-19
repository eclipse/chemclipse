/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;

public class DynamicIdentificationTargetUpdateNotifier implements IIdentificationTargetUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;
	private Map<String, Object> map;

	public DynamicIdentificationTargetUpdateNotifier() {
		map = new HashMap<String, Object>();
	}

	@Override
	public void update(IScanMSD massSpectrum, IIdentificationTarget identificationTarget) {

		/*
		 * Don't use a new map each time, to prevent unnecessary object creation.
		 * Update View : Peak
		 */
		map.clear();
		map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, massSpectrum);
		map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, identificationTarget);
		eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
		MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
	}
}
