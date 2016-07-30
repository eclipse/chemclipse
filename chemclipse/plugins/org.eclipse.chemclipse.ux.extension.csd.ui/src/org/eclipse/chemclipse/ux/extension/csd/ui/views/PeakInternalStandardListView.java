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
package org.eclipse.chemclipse.ux.extension.csd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.swt.ui.components.peaks.PeakInternalStandardsEditListUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PeakInternalStandardListView extends AbstractPeakCSDSelectionView {

	@Inject
	private Composite parent;
	private PeakInternalStandardsEditListUI peakInternalStandardsEditListUI;

	@Inject
	public PeakInternalStandardListView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		peakInternalStandardsEditListUI = new PeakInternalStandardsEditListUI(parent, SWT.NONE);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		peakInternalStandardsEditListUI.setFocus();
		update(getPeak(), false);
	}

	@Override
	public void update(IPeakCSD peak, boolean forceReload) {

		if(isPartVisible() && peak != null) {
			peakInternalStandardsEditListUI.update(peak, forceReload);
		}
	}
}
