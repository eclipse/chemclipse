/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.notifier;

import org.eclipse.chemclipse.model.Activator;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.e4.core.services.events.IEventBroker;

public class UpdateNotifier {

	public static void update(String topic, Object object) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			eventBroker.send(topic, object);
		}
	}

	public static void update(IChromatogramSelection<?, ?> chromatogramSelection) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(chromatogramSelection != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, chromatogramSelection);
			} else {
				eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION, null);
			}
		}
	}

	public static void update(IPeak peak) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(peak != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
			} else {
				eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, null);
			}
		}
	}

	public static void update(IScan scan) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(scan != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
			} else {
				eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, null);
			}
		}
	}

	public static void update(IIdentificationTarget identificationTarget) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(identificationTarget != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, identificationTarget);
			}
		}
	}

	public static void update(ITargetSupplier targetSupplier) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(targetSupplier != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION, targetSupplier);
			}
		}
	}

	public static void update(IScan scan1, IIdentificationTarget identificationTarget) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(scan1 != null && identificationTarget != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_SCAN_TARGET_UPDATE_COMPARISON, new Object[]{scan1, identificationTarget});
			}
		}
	}

	public static void update(IScan scan1, IScan scan2) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(scan1 != null && scan2 != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_SCAN_REFERENCE_UPDATE_COMPARISON, new Object[]{scan1, scan2});
			}
		}
	}

	public static void update(IEditHistory editHistory) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(editHistory != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_EDIT_HISTORY_UPDATE, editHistory);
			}
		}
	}

	public static void update(IQuantitationCompound quantitationCompound) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			if(quantitationCompound != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_QUANT_DB_COMPOUND_UPDATE, quantitationCompound);
			}
		}
	}
}
