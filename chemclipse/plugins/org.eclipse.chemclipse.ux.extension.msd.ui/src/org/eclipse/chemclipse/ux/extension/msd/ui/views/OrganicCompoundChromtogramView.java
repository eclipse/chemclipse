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

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.OrganicCompoundChromatogramUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class OrganicCompoundChromtogramView extends AbstractChromatogramSelectionMSDView {

	@Inject
	private Composite parent;
	private IChromatogramSelectionMSD chromatogramSelection;
	private OrganicCompoundChromatogramUI organicCompoundChromatogramUI;

	@Inject
	public OrganicCompoundChromtogramView(EPartService partService, MPart part, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		IMarkedIons compoundIons = new MarkedIons();
		compoundIons.add(new MarkedIon(57));
		compoundIons.add(new MarkedIon(71));
		compoundIons.add(new MarkedIon(85));
		organicCompoundChromatogramUI = new OrganicCompoundChromatogramUI(parent, SWT.NONE, compoundIons);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		organicCompoundChromatogramUI.setFocus();
		update(chromatogramSelection, false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			organicCompoundChromatogramUI.update(chromatogramSelection, forceReload);
		}
	}
}
