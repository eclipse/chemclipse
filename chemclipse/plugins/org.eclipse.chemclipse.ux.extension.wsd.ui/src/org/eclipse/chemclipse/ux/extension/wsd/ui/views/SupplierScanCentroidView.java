/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.wsd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.swt.ui.components.scan.CentroidScanUI;

public class SupplierScanCentroidView extends AbstractChromatogramSelectionWSDView {

	@Inject
	private Composite parent;
	private CentroidScanUI centroidScanUI;

	@Inject
	public SupplierScanCentroidView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		centroidScanUI = new CentroidScanUI(parent, SWT.NONE);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		centroidScanUI.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		if(doUpdate(chromatogramSelection)) {
			centroidScanUI.update(chromatogramSelection, forceReload);
		}
	}
}
