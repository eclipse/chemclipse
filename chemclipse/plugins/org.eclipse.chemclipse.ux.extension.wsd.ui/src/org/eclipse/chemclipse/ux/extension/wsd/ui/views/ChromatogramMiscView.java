/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ChromatogramMiscView extends AbstractChromatogramSelectionWSDView {

	@Inject
	private Composite parent;
	private Text textMiscellaneous;

	@Inject
	public ChromatogramMiscView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		textMiscellaneous = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		textMiscellaneous.setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionWSD chromatogramSelection, boolean forceReload) {

		if(doUpdate(chromatogramSelection)) {
			textMiscellaneous.setText(chromatogramSelection.getChromatogramWSD().getMiscInfo());
		}
	}
}
