/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumListUI;
import org.eclipse.chemclipse.msd.swt.ui.converter.SeriesConverterMSD;
import org.eclipse.chemclipse.msd.swt.ui.support.MassSpectrumFileSupport;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class MassSpectraIdentifiedList extends AbstractChromatogramSelectionMSDView {

	private static final Logger logger = Logger.getLogger(MassSpectraIdentifiedList.class);
	//
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
		//
		Composite composite = new Composite(parent, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		/*
		 * Buttons
		 */
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		GridData gridDataComposite = new GridData(GridData.FILL_HORIZONTAL);
		gridDataComposite.horizontalAlignment = SWT.END;
		compositeButtons.setLayoutData(gridDataComposite);
		compositeButtons.setLayout(new GridLayout(1, false));
		//
		Button saveButton = new Button(compositeButtons, SWT.PUSH);
		saveButton.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		saveButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(chromatogramSelection != null) {
						IMassSpectra massSpectra = SeriesConverterMSD.getIdentifiedScans(chromatogramSelection, true);
						MassSpectrumFileSupport.saveMassSpectra(massSpectra);
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		/*
		 * List
		 */
		massSpectrumListUI = new MassSpectrumListUI(composite, SWT.NONE);
		massSpectrumListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		massSpectrumListUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(firstElement != null && firstElement instanceof IVendorMassSpectrum) {
					/*
					 * Activate the views.
					 */
					List<String> viewIds = new ArrayList<String>();
					viewIds.add(IPerspectiveAndViewIds.VIEW_OPTIMIZED_MASS_SPECTRUM);
					viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM_TARGETS);
					PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MSD, viewIds);
					/*
					 * Fire an update if an identified scan has been selected.
					 */
					IVendorMassSpectrum vendorMassSpectrum = (IVendorMassSpectrum)firstElement;
					MassSpectrumSelectionUpdateNotifier.fireUpdateChange(vendorMassSpectrum, true);
					chromatogramSelection.setSelectedIdentifiedScan(vendorMassSpectrum, false);
					ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelection, true);
					/*
					 * It's important to set the focus here.
					 * The PerspectiveSwitchHandler.focusPerspectiveAndView activates other views and sets the
					 * focus there. But when trying to press "DEL", the focus would be on the other views.
					 * Hence, it needs to be set back to this list.
					 */
					massSpectrumListUI.getTable().setFocus();
				}
			}
		});
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		massSpectrumListUI.getTable().setFocus();
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
			massSpectrumListUI.setInput(massSpectra);
		}
	}
}
