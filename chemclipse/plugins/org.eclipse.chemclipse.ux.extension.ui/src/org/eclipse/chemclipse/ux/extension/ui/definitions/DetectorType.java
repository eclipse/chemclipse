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

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.app.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DetectorType implements EventHandler {

	public static final String DETECTOR_TYPE = "org.eclipse.chemclipse.ux.extension.ui.definitions.detectorType";
	public static final String CHROMATOGRAM_SELECTION = "org.eclipse.chemclipse.ux.extension.ui.chromatogramSelection";
	//
	public static final String MSD = "MSD";
	public static final String CSD = "CSD";
	public static final String WSD = "WSD";
	public static final String XXD = "XXD";
	public static final String NONE = "NONE";

	@Override
	public void handleEvent(Event event) {

		String value;
		String topic = event.getTopic();
		Object property = event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		IChromatogramSelection chromatogramSelection;
		/*
		 * Get the chromaotgram selection.
		 */
		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionMSD)property;
			value = MSD;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionCSD)property;
			value = CSD;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionWSD)property;
			value = WSD;
		} else {
			chromatogramSelection = null;
			value = NONE;
		}
		/*
		 * Set the type, see:
		 * org.eclipse.core.expressions.definitions
		 * isDetectorTypeMSD
		 */
		IEclipseContext eclipseContext = ModelSupportAddon.getEclipseContext();
		if(eclipseContext != null) {
			eclipseContext.set(DETECTOR_TYPE, value);
			eclipseContext.set(CHROMATOGRAM_SELECTION, chromatogramSelection);
		}
	}
}
