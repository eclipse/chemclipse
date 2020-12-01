/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
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

	@SuppressWarnings("rawtypes")
	@Override
	public void handleEvent(Event event) {

		String topic = event.getTopic();
		IChromatogramSelection chromatogramSelection = null;
		String chromatogramType = CHROMATOGRAM_TYPE_NONE;
		//
		if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION)) {
			Object object = event.getProperty(IChemClipseEvents.EVENT_BROKER_DATA);
			if(object instanceof IChromatogramSelection) {
				chromatogramSelection = (IChromatogramSelection)object;
				if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
					chromatogramType = CHROMATOGRAM_TYPE_CSD;
				} else if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
					chromatogramType = CHROMATOGRAM_TYPE_MSD;
				} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
					chromatogramType = CHROMATOGRAM_TYPE_WSD;
				}
			}
		} else if(topic.equals(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION)) {
			chromatogramSelection = null;
			chromatogramType = CHROMATOGRAM_TYPE_NONE;
		}
		/*
		 * Set the type, see:
		 * org.eclipse.core.expressions.definitions
		 * isChromatogramTypeMSD
		 */
		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
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
	public static IChromatogramSelection<?, ?> getChromatogramSelection() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(ChromatogramType.CHROMATOGRAM_SELECTION);
		//
		IChromatogramSelection<?, ?> chromatogramSelection = null;
		if(object != null && object instanceof IChromatogramSelection) {
			chromatogramSelection = (IChromatogramSelection<?, ?>)object;
		}
		//
		return chromatogramSelection;
	}

	/**
	 * Get the current chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelectionMSD}
	 */
	public static IChromatogramSelectionMSD getChromatogramSelectionMSD() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(ChromatogramType.CHROMATOGRAM_SELECTION);
		//
		IChromatogramSelectionMSD chromatogramSelectionMSD = null;
		if(object != null && object instanceof IChromatogramSelectionMSD) {
			chromatogramSelectionMSD = (IChromatogramSelectionMSD)object;
		}
		//
		return chromatogramSelectionMSD;
	}

	/**
	 * Get the current chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelectionCSD}
	 */
	public static IChromatogramSelectionCSD getChromatogramSelectionCSD() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(ChromatogramType.CHROMATOGRAM_SELECTION);
		//
		IChromatogramSelectionCSD chromatogramSelectionCSD = null;
		if(object != null && object instanceof IChromatogramSelectionCSD) {
			chromatogramSelectionCSD = (IChromatogramSelectionCSD)object;
		}
		//
		return chromatogramSelectionCSD;
	}

	/**
	 * Get the current chromatogram selection.
	 * 
	 * @return {@link IChromatogramSelectionWSD}
	 */
	public static IChromatogramSelectionWSD getChromatogramSelectionWSD() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(ChromatogramType.CHROMATOGRAM_SELECTION);
		//
		IChromatogramSelectionWSD chromatogramSelectionWSD = null;
		if(object != null && object instanceof IChromatogramSelectionWSD) {
			chromatogramSelectionWSD = (IChromatogramSelectionWSD)object;
		}
		//
		return chromatogramSelectionWSD;
	}
}
