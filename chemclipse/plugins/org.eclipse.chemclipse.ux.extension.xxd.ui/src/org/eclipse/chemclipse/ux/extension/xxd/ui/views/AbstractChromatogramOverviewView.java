/*******************************************************************************
 * Copyright (c) 2016, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractChromatogramOverviewView implements IChromatogramOverviewView {

	private static final Logger logger = Logger.getLogger(AbstractChromatogramOverviewView.class);
	//
	private MPart part;
	private EPartService partService;
	private IEventBroker eventBroker;
	//
	private List<EventHandler> registeredEventHandler;

	public AbstractChromatogramOverviewView(MPart part, EPartService partService, IEventBroker eventBroker) {

		this.part = part;
		this.partService = partService;
		this.eventBroker = eventBroker;
		//
		registeredEventHandler = new ArrayList<>();
		registerEventBroker(eventBroker);
	}

	@Override
	public boolean doUpdate(IChromatogramOverview chromatogramOverview) {

		if(partService.isPartVisible(part) && chromatogramOverview != null) {
			return true;
		} else {
			return false;
		}
	}

	@PreDestroy
	private void preDestroy() {

		if(eventBroker != null) {
			for(EventHandler eventHandler : registeredEventHandler) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
	}

	private void registerEventBroker(IEventBroker eventBroker) {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram instances overview updates.
			 */
			EventHandler eventHandler;
			//
			eventHandler = registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_RAWFILE, IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE);
			registeredEventHandler.add(eventHandler);
			eventHandler = registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_RAWFILE, IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE);
			registeredEventHandler.add(eventHandler);
			eventHandler = registerEventHandlerFile(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_RAWFILE, IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE);
			registeredEventHandler.add(eventHandler);
			//
			eventHandler = registerEventHandlerInstance(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_OVERVIEW, IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW);
			registeredEventHandler.add(eventHandler);
			eventHandler = registerEventHandlerInstance(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_OVERVIEW, IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW);
			registeredEventHandler.add(eventHandler);
			eventHandler = registerEventHandlerInstance(eventBroker, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_OVERVIEW, IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW);
			registeredEventHandler.add(eventHandler);
		}
	}

	private EventHandler registerEventHandlerFile(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				try {
					Object object = event.getProperty(property);
					if(object instanceof File) {
						setChromatogram((File)object, topic);
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
	}

	private EventHandler registerEventHandlerInstance(IEventBroker eventBroker, String property, String topic) {

		EventHandler eventHandler = new EventHandler() {

			@Override
			public void handleEvent(Event event) {

				try {
					Object object = event.getProperty(property);
					if(object instanceof IChromatogramOverview) {
						IChromatogramOverview chromatogramOverview = (IChromatogramOverview)object;
						updateChromatogram(chromatogramOverview);
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		};
		eventBroker.subscribe(topic, eventHandler);
		return eventHandler;
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
	private void setChromatogram(File file, String topic) throws FileNotFoundException, NoChromatogramConverterAvailableException, FileIsNotReadableException, FileIsEmptyException {

		/*
		 * Update the UI only if the actual view part is visible.
		 */
		if(partService.isPartVisible(part)) {
			/*
			 * Load the chromatogram overview.
			 */
			IProcessingInfo<IChromatogramOverview> processingInfo = null;
			switch(topic) {
				case IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE:
					processingInfo = ChromatogramConverterMSD.getInstance().convertOverview(file, new NullProgressMonitor());
					break;
				case IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE:
					processingInfo = ChromatogramConverterCSD.getInstance().convertOverview(file, new NullProgressMonitor());
					break;
				case IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE:
					processingInfo = ChromatogramConverterWSD.getInstance().convertOverview(file, new NullProgressMonitor());
					break;
			}
			//
			try {
				if(processingInfo != null) {
					IChromatogramOverview chromatogramOverview = processingInfo.getProcessingResult(IChromatogramOverview.class);
					if(chromatogramOverview != null) {
						updateChromatogram(chromatogramOverview);
					}
				}
			} catch(TypeCastException e) {
				logger.warn(e);
			}
		}
	}
}
