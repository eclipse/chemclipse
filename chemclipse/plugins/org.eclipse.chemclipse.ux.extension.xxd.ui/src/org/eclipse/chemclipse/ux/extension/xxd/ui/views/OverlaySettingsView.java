/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class OverlaySettingsView extends AbstractChromatogramOverlayView {

	@Inject
	private Composite composite;
	private Text text;

	@Inject
	public OverlaySettingsView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		composite.setLayout(new GridLayout(1, true));
		text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setText("");
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void update(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		if(doUpdate(chromatogramSelection)) {
			/*
			 * Update the offset of the view. It necessary, the user must
			 * restart the workbench in case of a change otherwise.
			 */
			List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(chromatogramSelection);
			StringBuilder builder = new StringBuilder();
			for(IChromatogramSelection selection : chromatogramSelections) {
				builder.append(selection.getChromatogram().getName());
				builder.append("\n");
			}
			text.setText(builder.toString());
		}
	}
}
