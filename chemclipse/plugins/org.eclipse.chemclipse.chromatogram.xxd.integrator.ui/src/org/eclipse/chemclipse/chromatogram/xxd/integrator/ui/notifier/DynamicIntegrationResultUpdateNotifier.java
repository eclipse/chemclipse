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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;

public class DynamicIntegrationResultUpdateNotifier implements IIntegrationResultUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;

	@Override
	public void update(ICombinedIntegrationResult combinedIntegrationResult) {

		eventBroker.send(IIntegrationResultEvents.TOPIC_INTEGRATION_MSD_UPDATE_RESULT_COMBINED, combinedIntegrationResult);
	}

	@Override
	public void update(IChromatogramIntegrationResults chromatogramIntegrationResults) {

		eventBroker.send(IIntegrationResultEvents.TOPIC_INTEGRATION_MSD_UPDATE_RESULT_CHROMATOGRAM, chromatogramIntegrationResults);
	}

	@Override
	public void update(IPeakIntegrationResults peakIntegrationResults) {

		eventBroker.send(IIntegrationResultEvents.TOPIC_INTEGRATION_MSD_UPDATE_RESULT_PEAK, peakIntegrationResults);
	}
}
