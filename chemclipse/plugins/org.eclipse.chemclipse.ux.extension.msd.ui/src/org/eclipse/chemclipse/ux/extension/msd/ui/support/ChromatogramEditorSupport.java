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
package org.eclipse.chemclipse.ux.extension.msd.ui.support;

import java.io.File;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractChromatogramEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;

public class ChromatogramEditorSupport extends AbstractChromatogramEditorSupport implements IChromatogramEditorSupport {

	@Override
	public void openEditor(final File file, EModelService modelService, MApplication application, EPartService partService) {

		/*
		 * Check that the selected file or directory is a valid chromatogram.
		 */
		if(ChromatogramSupport.getInstanceIdentifier().isChromatogram(file) || ChromatogramSupport.getInstanceIdentifier().isChromatogramDirectory(file)) {
			this.modelService = modelService;
			this.application = application;
			this.partService = partService;
			openEditor(file, null, ChromatogramEditorMSD.ID, ChromatogramEditorMSD.CONTRIBUTION_URI, ChromatogramEditorMSD.ICON_URI, ChromatogramEditorMSD.TOOLTIP);
		}
	}

	@Override
	public void openEditor(IChromatogram chromatogram, EModelService modelService, MApplication application, EPartService partService) {

		this.modelService = modelService;
		this.application = application;
		this.partService = partService;
		openEditor(null, chromatogram, ChromatogramEditorMSD.ID, ChromatogramEditorMSD.CONTRIBUTION_URI, ChromatogramEditorMSD.ICON_URI, ChromatogramEditorMSD.TOOLTIP);
	}

	@Override
	public void openOverview(final File file, IEventBroker eventBroker) {

		/*
		 * Check that the selected file or directory is a valid chromatogram.
		 */
		if(ChromatogramSupport.getInstanceIdentifier().isChromatogram(file) || ChromatogramSupport.getInstanceIdentifier().isChromatogramDirectory(file)) {
			/*
			 * Push an event
			 * IChromatogramEvents.PROPERTY_CHROMATOGRAM_OVERVIEW_FILE
			 */
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE, file);
		}
	}

	@Override
	public void openOverview(IChromatogramOverview chromatogramOverview, IEventBroker eventBroker) {

		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW, chromatogramOverview);
	}
}
