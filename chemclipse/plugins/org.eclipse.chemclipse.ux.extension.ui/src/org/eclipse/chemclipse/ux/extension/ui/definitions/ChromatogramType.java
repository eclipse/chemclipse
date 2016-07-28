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

public class ChromatogramType implements EventHandler {

	public static final String CHROMATOGRAM_TYPE = "org.eclipse.chemclipse.ux.extension.ui.definitions.chromatogramType";
	public static final String CHROMATOGRAM_SELECTION = "org.eclipse.chemclipse.ux.extension.ui.chromatogramSelection";
	//
	public static final String CHROMATOGRAM_TYPE_MSD = "CHROMATOGRAM_TYPE_MSD";
	public static final String CHROMATOGRAM_TYPE_CSD = "CHROMATOGRAM_TYPE_CSD";
	public static final String CHROMATOGRAM_TYPE_WSD = "CHROMATOGRAM_TYPE_WSD";
	public static final String CHROMATOGRAM_TYPE_XXD = "CHROMATOGRAM_TYPE_XXD";
	public static final String CHROMATOGRAM_TYPE_NONE = "CHROMATOGRAM_TYPE_NONE";

	@Override
	public void handleEvent(Event event) {

		String topic = event.getTopic();
		Object property = event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		/*
		 * Get the chromatogram selection.
		 */
		IChromatogramSelection chromatogramSelection = null;
		String chromatogramType = CHROMATOGRAM_TYPE_NONE;
		//
		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionMSD)property;
			chromatogramType = CHROMATOGRAM_TYPE_MSD;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionCSD)property;
			chromatogramType = CHROMATOGRAM_TYPE_CSD;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = (IChromatogramSelectionWSD)property;
			chromatogramType = CHROMATOGRAM_TYPE_WSD;
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION)) {
			chromatogramSelection = null;
			chromatogramType = CHROMATOGRAM_TYPE_NONE;
		}
		/*
		 * Set the type, see:
		 * org.eclipse.core.expressions.definitions
		 * isChromatogramTypeMSD
		 */
		IEclipseContext eclipseContext = ModelSupportAddon.getEclipseContext();
		if(eclipseContext != null) {
			eclipseContext.set(CHROMATOGRAM_SELECTION, chromatogramSelection);
			eclipseContext.set(CHROMATOGRAM_TYPE, chromatogramType);
		}
	}

	/**
	 * Get the current chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelection}
	 */
	public static IChromatogramSelection getChromatogramSelection() {

		IEclipseContext eclipseContext = ModelSupportAddon.getEclipseContext();
		Object object = eclipseContext.get(ChromatogramType.CHROMATOGRAM_SELECTION);
		//
		IChromatogramSelection chromatogramSelection = null;
		if(object != null && object instanceof IChromatogramSelection) {
			chromatogramSelection = (IChromatogramSelection)object;
		}
		//
		return chromatogramSelection;
	}
}
