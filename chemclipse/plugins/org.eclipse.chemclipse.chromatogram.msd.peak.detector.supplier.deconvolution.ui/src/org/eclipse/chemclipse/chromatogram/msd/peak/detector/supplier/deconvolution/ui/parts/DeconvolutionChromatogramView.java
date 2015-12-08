/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.DeconvHelper;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support.IArraysViewDeconv;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.ux.extension.msd.ui.views.AbstractChromatogramSelectionMSDView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class DeconvolutionChromatogramView extends AbstractChromatogramSelectionMSDView {

	@Inject
	private Composite parent;
	private DeconvChromatogramUI deconvChromatogramUI;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;
	//
	private static IArraysViewDeconv arraysViewDeconv;

	@Inject
	public DeconvolutionChromatogramView(EPartService partService, MPart part, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
		this.eventBroker = eventBroker;
		subscribe();
	}

	private void subscribe() {

		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					arraysViewDeconv = (IArraysViewDeconv)event.getProperty(IEventBroker.DATA);
					update(arraysViewDeconv, getChromatogramSelection(), false);
				}
			};
			eventBroker.subscribe(DeconvHelper.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAKDECONDETEC, eventHandler);
		}
	}

	@Override
	public void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		deconvChromatogramUI = new DeconvChromatogramUI(parent, SWT.NONE);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		deconvChromatogramUI.setFocus();
		update(arraysViewDeconv, getChromatogramSelection(), false);
	}

	public void update(IArraysViewDeconv arraysViewDeconv, IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		if(arraysViewDeconv != null && chromatogramSelection != null) {
			deconvChromatogramUI.updateSelection(chromatogramSelection, forceReload);
			System.out.println("arraysViewDeconv Uebergabe erfolgreich");
			deconvChromatogramUI.setView(arraysViewDeconv);
			System.out.println("ViewDeconv-Update");
		}
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		System.out.println("DeconvolutionChromatogramView --- ChromatogramSelection");
	}
}
