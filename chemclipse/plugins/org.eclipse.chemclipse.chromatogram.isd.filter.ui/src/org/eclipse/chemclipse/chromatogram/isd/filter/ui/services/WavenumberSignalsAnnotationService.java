/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.isd.filter.ui.services;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.chromatogram.isd.filter.service.WavenumberSignalsSerializationService;
import org.eclipse.chemclipse.chromatogram.isd.filter.ui.swt.WavenumberSignalsEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.IAnnotationWidgetService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(service = {IAnnotationWidgetService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class WavenumberSignalsAnnotationService extends WavenumberSignalsSerializationService implements IAnnotationWidgetService {

	private AtomicReference<WavenumberSignalsEditor> editorControl = new AtomicReference<>();

	@Override
	public Control createWidget(Composite parent, String description, Object currentSelection) {

		WavenumberSignalsEditor wavenumberSignalsEditor = new WavenumberSignalsEditor(parent, SWT.NONE);
		wavenumberSignalsEditor.setToolTipText(description);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 250;
		wavenumberSignalsEditor.setLayoutData(gridData);
		//
		if(currentSelection instanceof String text) {
			wavenumberSignalsEditor.load(text);
		}
		//
		editorControl.set(wavenumberSignalsEditor);
		return wavenumberSignalsEditor;
	}

	@Override
	public Object getValue(Object currentSelection) {

		return editorControl.get().getWavenumberSignals();
	}
}