/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.runnables.LibraryServiceRunnable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public abstract class AbstractMassSpectrumLibraryView {

	private static final Logger logger = Logger.getLogger(AbstractMassSpectrumLibraryView.class);
	//
	private IEventBroker eventBroker;
	private MPart part;
	private EPartService partService;
	//
	private EventHandler eventHandler;

	public AbstractMassSpectrumLibraryView(MPart part, EPartService partService, IEventBroker eventBroker) {
		this.part = part;
		this.partService = partService;
		this.eventBroker = eventBroker;
		subscribe();
	}

	public boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	public void unsubscribe() {

		if(eventBroker != null) {
			if(eventHandler != null) {
				eventBroker.unsubscribe(eventHandler);
			}
		}
	}

	/**
	 * Override in a specialized view.
	 */
	public void update(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum, boolean forceReload) {

	}

	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Mass spectrum and target
			 */
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					/*
					 * Receive name and formula.
					 */
					IScanMSD unknownMassSpectrum = (IScanMSD)event.getProperty(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN);
					IIdentificationTarget identificationTarget = (IIdentificationTarget)event.getProperty(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY);
					update(unknownMassSpectrum, identificationTarget);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, eventHandler);
		}
	}

	private void update(IScanMSD unknownMassSpectrum, IIdentificationTarget identificationTarget) {

		if(isPartVisible()) {
			IRunnableWithProgress runnable = new LibraryServiceRunnable(this, unknownMassSpectrum, identificationTarget);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			try {
				monitor.run(true, true, runnable);
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
			}
		}
	}
}
