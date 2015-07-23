/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.SimpleMassSpectrumUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class SimpleOptimizedNominalMassSpectrumView extends AbstractMassSpectrumSelectionView {

	@Inject
	private Composite parent;
	private SimpleMassSpectrumUI simpleMassSpectrumUI;

	@Inject
	public SimpleOptimizedNominalMassSpectrumView(EPartService partService, MPart part, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		simpleMassSpectrumUI = new SimpleMassSpectrumUI(parent, SWT.NONE, MassValueDisplayPrecision.NOMINAL);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		simpleMassSpectrumUI.setFocus();
		update(getMassSpectrum(), false);
	}

	@Override
	public void update(IScanMSD massSpectrum, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(isPartVisible() && massSpectrum != null) {
			if(massSpectrum.getOptimizedMassSpectrum() != null) {
				simpleMassSpectrumUI.update(massSpectrum.getOptimizedMassSpectrum(), forceReload);
			} else {
				simpleMassSpectrumUI.update(massSpectrum, forceReload);
			}
		}
	}
}
