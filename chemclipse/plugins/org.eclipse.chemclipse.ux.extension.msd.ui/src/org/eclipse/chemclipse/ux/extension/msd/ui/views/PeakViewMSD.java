/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.peak.ExtendedPeakUI;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.support.AxisTitlesMassScale;

public class PeakViewMSD extends AbstractPeakMSDSelectionView {

	@Inject
	private Composite parent;
	private ExtendedPeakUI extendedPeakUI;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;

	@Inject
	public PeakViewMSD(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		this.eventBroker = eventBroker;
		subscribe();
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		extendedPeakUI = new ExtendedPeakUI(parent, SWT.NONE, new AxisTitlesMassScale());
		extendedPeakUI.setIncludeBackground(true);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Override
	public void unsubscribe() {

		super.unsubscribe();
	}

	@Focus
	public void setFocus() {

		extendedPeakUI.setFocus();
		update(getPeak(), false);
	}

	@Override
	public void update(IPeakMSD peak, boolean forceReload) {

		if(isPartVisible() && peak != null) {
			extendedPeakUI.update(peak, forceReload);
		}
	}

	/**
	 * Subscribes the selection update events.
	 */
	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram selection updates.
			 */
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					IChromatogramSelectionMSD chromatogramSelection = (IChromatogramSelectionMSD)event.getProperty(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
					boolean forceReload = (Boolean)event.getProperty(IChemClipseEvents.PROPERTY_FORCE_RELOAD);
					/*
					 * Try to get the actual peak selection.
					 */
					if(chromatogramSelection != null) {
						IPeakMSD peak = chromatogramSelection.getSelectedPeak();
						setPeak(peak);
						update(peak, forceReload);
					}
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, eventHandler);
		}
	}
}
