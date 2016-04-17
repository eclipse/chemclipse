/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.notifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.notifier.IChromatogramSelectionUpdateNotifier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.core.services.events.IEventBroker;

public class DynamicChromatogramSelectionUpdateNotifier implements IChromatogramSelectionUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;
	private Map<String, Object> map;

	public DynamicChromatogramSelectionUpdateNotifier() {
		map = new HashMap<String, Object>();
	}

	@Override
	public void updateSelection(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		/*
		 * Don't use a new map each time, to prevent unnecessary object creation.
		 */
		map.clear();
		map.put(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, chromatogramSelection);
		map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, forceReload);
		//
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, map);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, map);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, map);
		} else {
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION, map);
		}
	}

	@Override
	public void updateSelection(List<IChromatogramSelection> chromatogramSelections, boolean forceReload) {

	}
}
