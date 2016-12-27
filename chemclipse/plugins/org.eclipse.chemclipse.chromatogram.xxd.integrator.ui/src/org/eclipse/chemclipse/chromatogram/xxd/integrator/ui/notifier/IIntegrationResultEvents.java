/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
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

import org.eclipse.e4.core.services.events.IEventBroker;

public interface IIntegrationResultEvents {

	String PROPERTY_INTEGRATION_RESULT_COMBINED = IEventBroker.DATA; // ICombinedIntegrationResult
	String PROPERTY_INTEGRATION_RESULT_CHROMATOGRAM = IEventBroker.DATA; // IChromatogramIntegrationResults
	String PROPERTY_INTEGRATION_RESULT_PEAK = IEventBroker.DATA; // IPeakIntegrationResults
	//
	String TOPIC_INTEGRATION_MSD_UPDATE_RESULT_COMBINED = "integration/msd/update/result/combined";
	String TOPIC_INTEGRATION_MSD_UPDATE_RESULT_CHROMATOGRAM = "integration/msd/update/result/chromatogram";
	String TOPIC_INTEGRATION_MSD_UPDATE_RESULT_PEAK = "integration/msd/update/result/peak";
}
