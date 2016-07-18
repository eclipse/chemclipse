/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.definitions;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.app.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ScanType implements EventHandler {

	public static final String SCAN_TYPE = "org.eclipse.chemclipse.ux.extension.ui.definitions.scanType";
	public static final String SCAN_SELECTION = "org.eclipse.chemclipse.ux.extension.ui.scanSelection";
	//
	public static final String SCAN_TYPE_MSD = "SCAN_TYPE_MSD";
	public static final String SCAN_TYPE_NONE = "SCAN_TYPE_NONE";

	@Override
	public void handleEvent(Event event) {

		String topic = event.getTopic();
		Object property = event.getProperty(IChemClipseEvents.PROPERTY_SCAN_SELECTION);
		/*
		 * Get the scan type.
		 */
		IScanMSD scanSelection = null;
		String scanType = SCAN_TYPE_NONE;
		//
		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_MSD_UPDATE_SELECTION)) {
			scanSelection = (IScanMSD)property;
			scanType = SCAN_TYPE_MSD;
		} else if(topic.equals(IChemClipseEvents.TOPIC_SCAN_MSD_UNLOAD_SELECTION)) {
			scanSelection = null;
			scanType = SCAN_TYPE_NONE;
		}
		/*
		 * Set the type, see:
		 * org.eclipse.core.expressions.definitions
		 * isScanTypeMSD
		 */
		IEclipseContext eclipseContext = ModelSupportAddon.getEclipseContext();
		if(eclipseContext != null) {
			eclipseContext.set(SCAN_SELECTION, scanSelection);
			eclipseContext.set(SCAN_TYPE, scanType);
		}
	}
}
