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
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.swt.PeakMassSpectrumEditUIWithLabel;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.ux.extension.msd.ui.views.AbstractChromatogramSelectionMSDView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PeakMassSpectrumEditView extends AbstractChromatogramSelectionMSDView {

	@Inject
	private Composite parent;
	private IEventBroker eventBroker;
	private PeakMassSpectrumEditUIWithLabel pinnedPeakEditUIWithLabel;

	@Inject
	public PeakMassSpectrumEditView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		this.eventBroker = eventBroker;
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		pinnedPeakEditUIWithLabel = new PeakMassSpectrumEditUIWithLabel(parent, SWT.NONE, MassValueDisplayPrecision.NOMINAL, eventBroker);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		pinnedPeakEditUIWithLabel.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		if(doUpdate(chromatogramSelection)) {
			pinnedPeakEditUIWithLabel.update(chromatogramSelection, forceReload);
		}
	}
}
