/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import java.io.File;
import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.ChromatogramOverviewUI;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ChromatogramOverviewView {

	private static final Logger logger = Logger.getLogger(ChromatogramOverviewView.class);
	@Inject
	private MPart part;
	@Inject
	private EPartService partService;
	private ChromatogramOverviewUI chromatogramOverviewUI;

	@Inject
	public ChromatogramOverviewView(Composite parent, IEventBroker eventBroker) {
		chromatogramOverviewUI = new ChromatogramOverviewUI(parent, SWT.NONE);
		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram overview updates.
			 */
			EventHandler eventHandlerFileOverview = new EventHandler() {

				public void handleEvent(Event event) {

					try {
						Object object = event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_RAWFILE);
						if(object instanceof File) {
							setChromatogram((File)object);
						}
					} catch(Exception e) {
						logger.warn(e);
					}
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE, eventHandlerFileOverview);
			/*
			 * Receives and handles chromatogram instances overview updates.
			 */
			EventHandler eventHandlerInstanceOverview = new EventHandler() {

				public void handleEvent(Event event) {

					try {
						Object object = event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_OVERVIEW);
						if(object instanceof IChromatogramOverview) {
							IChromatogramOverview chromatogramOverview = (IChromatogramOverview)object;
							updateChromatogram(chromatogramOverview);
						}
					} catch(Exception e) {
						logger.warn(e);
					}
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW, eventHandlerInstanceOverview);
		}
	}

	@Focus
	public void setFocus() {

		chromatogramOverviewUI.setFocus();
	}

	/**
	 * Sets the chromatogram overview.
	 * 
	 * @param chromatogramOverview
	 */
	private void updateChromatogram(IChromatogramOverview chromatogramOverview) {

		if(partService.isPartVisible(part) && chromatogramOverview != null) {
			chromatogramOverviewUI.showChromatogramOverview(chromatogramOverview);
		}
	}

	/**
	 * Try to show the overview of the given chromatogram overview.
	 * 
	 * @param file
	 * @throws FileIsEmptyException
	 * @throws FileIsNotReadableException
	 * @throws NoChromatogramConverterAvailableException
	 * @throws FileNotFoundException
	 */
	private void setChromatogram(File file) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException {

		/*
		 * Update the ui only if the actual view part is visible.
		 */
		if(partService.isPartVisible(part)) {
			/*
			 * Load the chromatogram overview.
			 */
			IProcessingInfo processingInfo = ChromatogramConverterCSD.getInstance().convertOverview(file, new NullProgressMonitor());
			try {
				IChromatogramOverview chromatogramOverview = processingInfo.getProcessingResult(IChromatogramOverview.class);
				if(chromatogramOverview != null) {
					updateChromatogram(chromatogramOverview);
				}
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
	}
}
