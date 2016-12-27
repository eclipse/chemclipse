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
package org.eclipse.chemclipse.ux.extension.csd.ui.support;

import java.io.File;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.csd.ui.editors.ChromatogramEditorCSD;
import org.eclipse.chemclipse.ux.extension.csd.ui.internal.support.ChromatogramIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractChromatogramEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class ChromatogramEditorSupport extends AbstractChromatogramEditorSupport implements IChromatogramEditorSupport {

	public ChromatogramEditorSupport() {
		super(ChromatogramConverterCSD.getChromatogramConverterSupport().getSupplier());
	}

	@Override
	public String getType() {

		return TYPE_CSD;
	}

	@Override
	public void openEditor(final File file, EModelService modelService, MApplication application, EPartService partService) {

		/*
		 * Check that the selected file or directory is a valid chromatogram.
		 */
		if(ChromatogramIdentifier.isChromatogram(file) || ChromatogramIdentifier.isChromatogramDirectory(file)) {
			this.modelService = modelService;
			this.application = application;
			this.partService = partService;
			openEditor(file, null, ChromatogramEditorCSD.ID, ChromatogramEditorCSD.CONTRIBUTION_URI, ChromatogramEditorCSD.ICON_URI, ChromatogramEditorCSD.TOOLTIP);
		}
	}

	@Override
	public void openEditor(IChromatogram chromatogram, EModelService modelService, MApplication application, EPartService partService) {

		this.modelService = modelService;
		this.application = application;
		this.partService = partService;
		openEditor(null, chromatogram, ChromatogramEditorCSD.ID, ChromatogramEditorCSD.CONTRIBUTION_URI, ChromatogramEditorCSD.ICON_URI, ChromatogramEditorCSD.TOOLTIP);
	}

	@Override
	public void openOverview(final File file, IEventBroker eventBroker) {

		/*
		 * Check that the selected file or directory is a valid chromatogram.
		 */
		if(ChromatogramIdentifier.isChromatogram(file) || ChromatogramIdentifier.isChromatogramDirectory(file)) {
			/*
			 * Push an event
			 * IChromatogramEvents.PROPERTY_CHROMATOGRAM_OVERVIEW_FILE
			 */
			eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE, file);
		}
	}

	@Override
	public void openOverview(IChromatogramOverview chromatogramOverview, IEventBroker eventBroker) {

		eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW, chromatogramOverview);
	}
}
