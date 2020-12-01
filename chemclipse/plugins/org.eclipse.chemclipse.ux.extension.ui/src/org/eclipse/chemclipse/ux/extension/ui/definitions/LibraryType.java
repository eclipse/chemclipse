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

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class LibraryType implements EventHandler {

	public static final String LIBRARY_TYPE = "org.eclipse.chemclipse.ux.extension.ui.definitions.libraryType";
	public static final String LIBRARY_SELECTION = "org.eclipse.chemclipse.ux.extension.ui.librarySelection";
	//
	public static final String LIBRARY_TYPE_MSD = "LIBRARY_TYPE_MSD";
	public static final String LIBRARY_TYPE_NONE = "LIBRARY_TYPE_NONE";

	@Override
	public void handleEvent(Event event) {

		String topic = event.getTopic();
		Object property = event.getProperty(IChemClipseEvents.EVENT_BROKER_DATA);
		/*
		 * Get the library selection.
		 */
		IMassSpectra librarySelection = null;
		String libraryType = LIBRARY_TYPE_NONE;
		//
		if(topic.equals(IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE_SELECTION)) {
			librarySelection = (IMassSpectra)property;
			libraryType = LIBRARY_TYPE_MSD;
		} else if(topic.equals(IChemClipseEvents.TOPIC_LIBRARY_MSD_UNLOAD_SELECTION)) {
			librarySelection = null;
			libraryType = LIBRARY_TYPE_NONE;
		}
		/*
		 * Set the type, see:
		 * org.eclipse.core.expressions.definitions
		 * isLibraryTypeMSD
		 */
		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		if(eclipseContext != null) {
			eclipseContext.set(LIBRARY_SELECTION, librarySelection);
			eclipseContext.set(LIBRARY_TYPE, libraryType);
		}
	}

	/**
	 * Get the current library selection.
	 * 
	 * @return {@link IMassSpectra}
	 */
	public static IMassSpectra getMassSpectra() {

		IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
		Object object = eclipseContext.get(LIBRARY_SELECTION);
		//
		IMassSpectra massSpectra = null;
		if(object != null && object instanceof IChromatogramSelection) {
			massSpectra = (IMassSpectra)object;
		}
		//
		return massSpectra;
	}
}
