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
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.swt.ui.components.chromatogram.ChromatogramOverviewUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramOverviewView extends AbstractChromatogramOverviewView implements IChromatogramOverviewView {

	private ChromatogramOverviewUI chromatogramOverviewUI;

	@Inject
	public ChromatogramOverviewView(Composite parent, MPart part, EPartService partService, IEventBroker eventBroker) {
		/*
		 * Create the overview chart.
		 */
		super(part, partService, eventBroker);
		chromatogramOverviewUI = new ChromatogramOverviewUI(parent, SWT.NONE);
	}

	@Focus
	public void setFocus() {

		chromatogramOverviewUI.setFocus();
	}

	@Override
	public void updateChromatogram(IChromatogramOverview chromatogramOverview) {

		if(doUpdate(chromatogramOverview)) {
			chromatogramOverviewUI.showChromatogramOverview(chromatogramOverview);
		}
	}
}
