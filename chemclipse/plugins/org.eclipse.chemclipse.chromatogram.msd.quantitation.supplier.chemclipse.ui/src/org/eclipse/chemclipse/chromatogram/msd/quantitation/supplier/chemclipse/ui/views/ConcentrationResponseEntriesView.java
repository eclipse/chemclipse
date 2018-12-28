/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt.ConcentrationResponseEntriesUI;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ConcentrationResponseEntriesView extends AbstractQuantitationCompoundSelectionView {

	@Inject
	private Composite parent;
	private ConcentrationResponseEntriesUI concentrationResponseEntriesUI;
	private IEventBroker eventBroker;

	@Inject
	public ConcentrationResponseEntriesView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		this.eventBroker = eventBroker;
	}

	@PostConstruct
	public void createControl() {

		concentrationResponseEntriesUI = new ConcentrationResponseEntriesUI(parent, SWT.NONE, eventBroker);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		concentrationResponseEntriesUI.setFocus();
	}

	@Override
	public void update(IQuantitationCompound quantitationCompoundMSD, IQuantDatabase database) {

		if(doUpdate()) {
			concentrationResponseEntriesUI.update(quantitationCompoundMSD, database);
		}
	}
}
