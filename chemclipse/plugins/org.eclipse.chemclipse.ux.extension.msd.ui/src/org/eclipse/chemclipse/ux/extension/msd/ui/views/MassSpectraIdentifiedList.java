/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumListUI;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.viewers.IListItemsRemoveListener;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MassSpectraIdentifiedList extends AbstractChromatogramSelectionMSDView {

	@Inject
	private Composite parent;
	private MassSpectrumListUI massSpectrumListUI;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;
	//
	private IChromatogramSelectionMSD chromatogramSelection;

	@Inject
	public MassSpectraIdentifiedList(MPart part, EPartService partService, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
		/*
		 * Receives and handles chromatogram selection updates.
		 */
		this.eventBroker = eventBroker;
		if(eventBroker != null) {
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					update(getChromatogramSelection(), true);
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRUM, eventHandler);
		}
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		massSpectrumListUI = new MassSpectrumListUI(parent, SWT.NONE);
		massSpectrumListUI.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(firstElement != null && firstElement instanceof IVendorMassSpectrum) {
					/*
					 * Fire an update if an identified scan has been selected.
					 */
					IVendorMassSpectrum vendorMassSpectrum = (IVendorMassSpectrum)firstElement;
					chromatogramSelection.setSelectedIdentifiedScan(vendorMassSpectrum, false);
					ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelection, true);
				}
			}
		});
		/*
		 * If identification results are deleted, update the chromatogram.
		 */
		massSpectrumListUI.addListItemsRemoveListener(new IListItemsRemoveListener() {

			@Override
			public void update() {

				ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelection, true);
			}
		});
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		massSpectrumListUI.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void unsubscribe() {

		super.unsubscribe();
		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			this.chromatogramSelection = chromatogramSelection;
			IMassSpectra massSpectra = SeriesConverterMSD.getIdentifiedScans(chromatogramSelection, true);
			massSpectrumListUI.update(massSpectra, forceReload);
		}
	}
}
