/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.MultipleChromatogramOffsetSelectedIonsUI;
import org.eclipse.chemclipse.swt.ui.support.IOffset;

public class ChromatogramOverlaySelectedIonsView extends AbstractChromatogramOverlayView {

	@Inject
	private Composite parent;
	private MultipleChromatogramOffsetSelectedIonsUI chromatogramOverlaySelectedIonsUI;

	@Inject
	public ChromatogramOverlaySelectedIonsView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		IOffset offset = getOffset();
		chromatogramOverlaySelectedIonsUI = new MultipleChromatogramOffsetSelectedIonsUI(parent, SWT.NONE, offset);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		chromatogramOverlaySelectedIonsUI.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			/*
			 * Update the offset of the view. It necessary, the user must
			 * restart the workbench in case of a change otherwise.
			 */
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(chromatogramSelection);
			IOffset offset = getOffset();
			chromatogramOverlaySelectedIonsUI.setOffset(offset);
			chromatogramOverlaySelectedIonsUI.updateSelection(chromatogramSelections, forceReload);
		}
	}
}