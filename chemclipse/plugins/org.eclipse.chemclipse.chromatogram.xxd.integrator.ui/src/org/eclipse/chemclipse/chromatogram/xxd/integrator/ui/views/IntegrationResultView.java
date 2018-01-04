/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.ICombinedIntegrationResult;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier.IIntegrationResultEvents;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.notifier.IIntegrationResultUpdateNotifier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt.IntegrationResultsTabFolderUI;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.Event;

public class IntegrationResultView implements IIntegrationResultUpdateNotifier {

	@Inject
	private Composite parent;
	private IntegrationResultsTabFolderUI resultsTabFolderUI;
	@Inject
	private EPartService partService;
	@Inject
	MPart part;
	@Inject
	private IEventBroker eventBroker;
	private EventHandler eventHandlerCombined;
	private EventHandler eventHandlerChromatogram;
	private EventHandler eventHandlerPeak;

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		resultsTabFolderUI = new IntegrationResultsTabFolderUI(parent, SWT.NONE);
		subscribe();
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		resultsTabFolderUI.setFocus();
	}

	public boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * ICombinedIntegrationResult
			 */
			eventHandlerCombined = new EventHandler() {

				public void handleEvent(Event event) {

					ICombinedIntegrationResult combinedIntegrationResult = (ICombinedIntegrationResult)event.getProperty(IIntegrationResultEvents.PROPERTY_INTEGRATION_RESULT_COMBINED);
					update(combinedIntegrationResult);
				}
			};
			eventBroker.subscribe(IIntegrationResultEvents.TOPIC_INTEGRATION_MSD_UPDATE_RESULT_COMBINED, eventHandlerCombined);
			/*
			 * IChromatogramIntegrationResults
			 */
			eventHandlerChromatogram = new EventHandler() {

				public void handleEvent(Event event) {

					IChromatogramIntegrationResults chromatogramIntegrationResult = (IChromatogramIntegrationResults)event.getProperty(IIntegrationResultEvents.PROPERTY_INTEGRATION_RESULT_CHROMATOGRAM);
					update(chromatogramIntegrationResult);
				}
			};
			eventBroker.subscribe(IIntegrationResultEvents.TOPIC_INTEGRATION_MSD_UPDATE_RESULT_CHROMATOGRAM, eventHandlerChromatogram);
			/*
			 * IPeakIntegrationResults
			 */
			eventHandlerPeak = new EventHandler() {

				public void handleEvent(Event event) {

					IPeakIntegrationResults peakIntegrationResults = (IPeakIntegrationResults)event.getProperty(IIntegrationResultEvents.PROPERTY_INTEGRATION_RESULT_PEAK);
					update(peakIntegrationResults);
				}
			};
			eventBroker.subscribe(IIntegrationResultEvents.TOPIC_INTEGRATION_MSD_UPDATE_RESULT_PEAK, eventHandlerPeak);
		}
	}

	public void unsubscribe() {

		if(eventBroker != null) {
			if(eventHandlerCombined != null) {
				eventBroker.unsubscribe(eventHandlerCombined);
			}
			if(eventHandlerChromatogram != null) {
				eventBroker.unsubscribe(eventHandlerChromatogram);
			}
			if(eventHandlerPeak != null) {
				eventBroker.unsubscribe(eventHandlerPeak);
			}
		}
	}

	@Override
	public void update(ICombinedIntegrationResult combinedIntegrationResult) {

		if(isPartVisible()) {
			resultsTabFolderUI.update(combinedIntegrationResult);
		}
	}

	@Override
	public void update(IChromatogramIntegrationResults chromatogramIntegrationResults) {

		if(isPartVisible()) {
			resultsTabFolderUI.update(chromatogramIntegrationResults);
		}
	}

	@Override
	public void update(IPeakIntegrationResults peakIntegrationResults) {

		if(isPartVisible()) {
			resultsTabFolderUI.update(peakIntegrationResults);
		}
	}
}
