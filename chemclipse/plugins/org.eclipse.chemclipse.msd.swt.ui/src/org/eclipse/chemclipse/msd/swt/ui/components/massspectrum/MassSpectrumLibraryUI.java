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
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.rcp.app.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumLibraryUI extends Composite {

	private static final Logger logger = Logger.getLogger(MassSpectrumLibraryUI.class);
	//
	private MassSpectrumListUI massSpectrumListUI;
	private IEventBroker eventBroker;
	private IComparisonResult comparisonResult;

	public MassSpectrumLibraryUI(Composite parent, int style) {
		super(parent, style);
		eventBroker = ModelSupportAddon.getEventBroker();
		comparisonResult = ComparisonResult.createBestMatchComparisonResult();
		initialize(parent);
	}

	public void update(IMassSpectra massSpectra, boolean forceReload) {

		massSpectrumListUI.update(massSpectra, true);
	}

	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		//
		massSpectrumListUI = new MassSpectrumListUI(this, SWT.NONE);
		massSpectrumListUI.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(firstElement != null && firstElement instanceof IScanMSD) {
					/*
					 * Activate the views.
					 */
					List<String> viewIds = new ArrayList<String>();
					viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM);
					viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM_TARGETS);
					PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MS_LIBRARY, viewIds);
					/*
					 * Fire an update if an identified scan has been selected.
					 */
					IScanMSD massSpectrum = (IScanMSD)firstElement;
					MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
					//
					IIdentificationTarget identificationTarget = getIdentificationTarget(massSpectrum);
					eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, identificationTarget);
					/*
					 * It's important to set the focus here.
					 * The PerspectiveSwitchHandler.focusPerspectiveAndView activates other views and sets the
					 * focus there. But when trying to press "DEL", the focus would be on the other views.
					 * Hence, it needs to be set back to this list.
					 */
					massSpectrumListUI.setFocus();
				}
			}
		});
	}

	private IIdentificationTarget getIdentificationTarget(IScanMSD scanMSD) {

		IIdentificationTarget identificationTarget = null;
		//
		ILibraryInformation libraryInformation = null;
		if(scanMSD instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)scanMSD;
			libraryInformation = libraryMassSpectrum.getLibraryInformation();
			if(libraryInformation != null) {
				try {
					identificationTarget = new MassSpectrumTarget(libraryInformation, comparisonResult);
				} catch(ReferenceMustNotBeNullException e) {
					logger.warn(e);
				}
			}
		}
		//
		return identificationTarget;
	}
}
