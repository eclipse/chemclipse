/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.io.File;

import org.eclipse.chemclipse.converter.processing.chromatogram.IChromatogramOverviewImportConverterProcessingInfo;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public abstract class AbstractOverviewUpdateSupport extends AbstractDataUpdateSupport implements IOverviewUpdateSupport {

	private static final Logger logger = Logger.getLogger(AbstractOverviewUpdateSupport.class);

	public AbstractOverviewUpdateSupport(MPart part) {
		super(part);
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_RAWFILE);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_RAWFILE);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_RAWFILE);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_XXD_RAWFILE);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_OVERVIEW);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_OVERVIEW);
		registerEvent(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_OVERVIEW);
	}

	@Override
	public void updateObject(Object object, String topic) {

		if(object instanceof IChromatogramOverview) {
			IChromatogramOverview chromatogramOverview = (IChromatogramOverview)object;
			updateChromatogramOverview(chromatogramOverview);
		} else if(object instanceof File) {
			File file = (File)object;
			IChromatogramOverview chromatogramOverview = getChromatogramOverview(file, topic);
			updateChromatogramOverview(chromatogramOverview);
		} else {
			updateChromatogramOverview(null);
		}
	}

	private IChromatogramOverview getChromatogramOverview(File file, String topic) {

		IChromatogramOverview chromatogramOverview = null;
		IChromatogramOverviewImportConverterProcessingInfo processingInfo = null;
		switch(topic) {
			case IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE:
				processingInfo = ChromatogramConverterMSD.convertOverview(file, new NullProgressMonitor());
				break;
			case IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE:
				processingInfo = ChromatogramConverterCSD.convertOverview(file, new NullProgressMonitor());
				break;
			case IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE:
				processingInfo = ChromatogramConverterWSD.convertOverview(file, new NullProgressMonitor());
				break;
		}
		//
		try {
			if(processingInfo != null) {
				chromatogramOverview = processingInfo.getChromatogramOverview();
			}
		} catch(TypeCastException e) {
			logger.warn(e);
		}
		//
		return chromatogramOverview;
	}
}
