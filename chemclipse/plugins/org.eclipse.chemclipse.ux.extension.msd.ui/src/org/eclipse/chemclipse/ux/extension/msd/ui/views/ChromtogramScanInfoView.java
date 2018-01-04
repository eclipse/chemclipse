/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.chromatogram.ChromtogramScanInfoUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ChromtogramScanInfoView extends AbstractChromatogramSelectionMSDView {

	@Inject
	private Composite parent;
	private ChromtogramScanInfoUI chromtogramScanInfoUI;
	private IChromatogramSelectionMSD chromatogramSelection;
	/*
	 * Skip local update requests
	 */
	private boolean doUpdate = true;

	@Inject
	public ChromtogramScanInfoView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		chromtogramScanInfoUI = new ChromtogramScanInfoUI(parent, SWT.NONE);
		chromtogramScanInfoUI.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = chromtogramScanInfoUI.getStructuredSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof IScanMSD && chromatogramSelection != null) {
					doUpdate = false;
					chromatogramSelection.setSelectedScan((IScanMSD)object);
					ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(chromatogramSelection, false);
				}
			}
		});
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		chromtogramScanInfoUI.getTable().setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		if(doUpdate) {
			this.chromatogramSelection = chromatogramSelection;
			if(doUpdate(chromatogramSelection)) {
				chromtogramScanInfoUI.update(chromatogramSelection, forceReload);
			}
		} else {
			doUpdate = true;
		}
	}
}
