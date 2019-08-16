/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.identification.SynonymsEditUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class SynonymsView extends AbstractMassSpectrumSelectionView {

	@Inject
	private Composite parent;
	private SynonymsEditUI synonymsEditUI;

	@Inject
	public SynonymsView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		synonymsEditUI = new SynonymsEditUI(parent, SWT.NONE);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		synonymsEditUI.update(getLibraryInformation(getMassSpectrum()), true);
	}

	@Override
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		if(doUpdate(massSpectrum)) {
			synonymsEditUI.update(getLibraryInformation(massSpectrum), true);
		}
	}

	private ILibraryInformation getLibraryInformation(IScanMSD massSpectrum) {

		ILibraryInformation libraryInformation = null;
		if(massSpectrum instanceof ILibraryMassSpectrum) {
			ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)massSpectrum;
			libraryInformation = libraryMassSpectrum.getLibraryInformation();
		}
		return libraryInformation;
	}
}
