/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumStackUI;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.EventHandler;

public class MassSpectrumStackPart extends AbstractMassSpectrumComparisonPart {

	@Inject
	private Composite parent;
	private MassSpectrumStackUI massSpectrumStackUI;

	@Inject
	public MassSpectrumStackPart(EPartService partService, MPart part, IEventBroker eventBroker, EventHandler eventHandler) {
		super(partService, part, eventBroker, eventHandler);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		massSpectrumStackUI = new MassSpectrumStackUI(parent, SWT.BORDER, MassValueDisplayPrecision.NOMINAL, "REFERENCE", "COMPARISON");
		subscribe();
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		massSpectrumStackUI.setFocus();
		update();
	}

	@Override
	public void update() {

		if(doUpdate()) {
			IScanMSD referenceMassSpectrum = getReferenceMassSpectrum();
			IScanMSD comparisonMassSpectrum = getComparisonMassSpectrum();
			massSpectrumStackUI.update(referenceMassSpectrum, comparisonMassSpectrum, true);
		}
	}
}
