/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ScanType implements EventHandler {

	public static final String SCAN_TYPE = "org.eclipse.chemclipse.ux.extension.ui.definitions.scanType";
	public static final String SCAN_SELECTION = "org.eclipse.chemclipse.ux.extension.ui.scanSelection";
	//
	public static final String SCAN_TYPE_MSD = "SCAN_TYPE_MSD";
	public static final String SCAN_TYPE_CSD = "SCAN_TYPE_CSD";
	public static final String SCAN_TYPE_WSD = "SCAN_TYPE_WSD";
	public static final String SCAN_TYPE_XXD = "SCAN_TYPE_XXD";
	public static final String SCAN_TYPE_NONE = "SCAN_TYPE_NONE";

	@Override
	public void handleEvent(Event event) {

		String topic = event.getTopic();
		Object property = event.getProperty(IChemClipseEvents.EVENT_BROKER_DATA);
		/*
		 * Get the scan type.
		 */
		IScan scanSelection = null;
		String scanType = SCAN_TYPE_NONE;
		//
		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
			if(property instanceof IScanMSD scanMSD) {
				scanSelection = scanMSD;
				scanType = SCAN_TYPE_MSD;
			} else if(property instanceof IScanCSD scanCSD) {
				scanSelection = scanCSD;
				scanType = SCAN_TYPE_CSD;
			} else if(property instanceof IScanWSD scanWSD) {
				scanSelection = scanWSD;
				scanType = SCAN_TYPE_WSD;
			} else {
				scanSelection = null;
				scanType = SCAN_TYPE_NONE;
			}
		} else if(IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_CLOSE.equals(topic) || IChemClipseEvents.TOPIC_EDITOR_LIBRARY_CLOSE.equals(topic)) {
			scanSelection = null;
			scanType = SCAN_TYPE_NONE;
		}
		/*
		 * Set the type, see:
		 * org.eclipse.core.expressions.definitions
		 * isScanTypeMSD
		 */
		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		if(eclipseContext != null) {
			eclipseContext.set(SCAN_SELECTION, scanSelection);
			eclipseContext.set(SCAN_TYPE, scanType);
		}
	}

	/**
	 * Get the current scan selection.
	 * 
	 * @return IScan
	 */
	public static IScan getSelectedScan() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(SCAN_SELECTION);
		//
		IScan scan = null;
		if(object != null && object instanceof IScan selectedScan) {
			scan = selectedScan;
		}
		//
		return scan;
	}

	/**
	 * Get the current scan selection.
	 * 
	 * @return IScanMSD
	 */
	public static IScanMSD getSelectedScanMSD() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(SCAN_SELECTION);
		//
		IScanMSD scanMSD = null;
		if(object != null && object instanceof IScanMSD selectedScanMSD) {
			scanMSD = selectedScanMSD;
		}
		//
		return scanMSD;
	}

	/**
	 * Get the current scan selection.
	 * 
	 * @return IScanCSD
	 */
	public static IScanCSD getSelectedScanCSD() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(SCAN_SELECTION);
		//
		IScanCSD scanCSD = null;
		if(object != null && object instanceof IScanCSD selectedScanCSD) {
			scanCSD = selectedScanCSD;
		}
		//
		return scanCSD;
	}

	/**
	 * Get the current scan selection.
	 * 
	 * @return IScanWSD
	 */
	public static IScanWSD getSelectedScanWSD() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(SCAN_SELECTION);
		//
		IScanWSD scanWSD = null;
		if(object != null && object instanceof IScanWSD selectedScanWSD) {
			scanWSD = selectedScanWSD;
		}
		//
		return scanWSD;
	}
}
