/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram.SelectedWavelengthChromatogramUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class SelectedWavelengthChromtogramView extends AbstractChromatogramSelectionWSDView {

	@Inject
	private Composite parent;
	private SelectedWavelengthChromatogramUI selectedWavelengthChromatogramUI;

	@Inject
	public SelectedWavelengthChromtogramView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		selectedWavelengthChromatogramUI = new SelectedWavelengthChromatogramUI(parent, SWT.NONE);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		selectedWavelengthChromatogramUI.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			selectedWavelengthChromatogramUI.update(chromatogramSelection, forceReload);
		}
	}
}
