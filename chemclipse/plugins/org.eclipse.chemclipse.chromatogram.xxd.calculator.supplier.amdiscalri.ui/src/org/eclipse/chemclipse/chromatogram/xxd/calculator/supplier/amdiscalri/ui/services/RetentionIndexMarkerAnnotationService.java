/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.services;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.service.RetentionIndexMarkerSerializationService;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.swt.RetentionIndexMarkerEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.IAnnotationWidgetService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(service = {IAnnotationWidgetService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class RetentionIndexMarkerAnnotationService extends RetentionIndexMarkerSerializationService implements IAnnotationWidgetService {

	private RetentionIndexMarkerEditor editor;

	@Override
	public Control createWidget(Composite parent, String description, Object currentSelection) {

		editor = new RetentionIndexMarkerEditor(parent, SWT.NONE);
		editor.setToolTipText(description);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 250;
		editor.setLayoutData(gridData);
		//
		if(currentSelection instanceof String) {
			editor.load((String)currentSelection);
		}
		//
		return editor;
	}

	@Override
	public Object getValue(Object currentSelection) {

		return editor.getRetentionIndexMarker();
	}
}