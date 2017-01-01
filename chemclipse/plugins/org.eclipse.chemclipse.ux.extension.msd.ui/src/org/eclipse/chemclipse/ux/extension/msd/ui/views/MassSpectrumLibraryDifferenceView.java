/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.LibraryMassSpectrumDifferenceUI;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumLibraryDifferenceView extends AbstractMassSpectrumLibraryView {

	@Inject
	private Composite parent;
	//
	private LibraryMassSpectrumDifferenceUI libraryMassSpectrumDifferenceUI;

	@Inject
	public MassSpectrumLibraryDifferenceView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		libraryMassSpectrumDifferenceUI = new LibraryMassSpectrumDifferenceUI(parent, SWT.NONE, MassValueDisplayPrecision.NOMINAL);
	}

	@Focus
	public void setFocus() {

		libraryMassSpectrumDifferenceUI.setFocus();
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Override
	public void update(IScanMSD unknownMassSpectrum, IScanMSD libraryMassSpectrum, boolean forceReload) {

		libraryMassSpectrumDifferenceUI.update(unknownMassSpectrum, libraryMassSpectrum, true);
	}
}