/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.ui.services;

import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.IAnnotationWidgetService;
import org.eclipse.chemclipse.xxd.classification.service.ClassificationDictionarySerializationService;
import org.eclipse.chemclipse.xxd.classification.ui.swt.ClassificationDictionaryEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

@Component(service = {IAnnotationWidgetService.class}, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ClassificationDictionaryAnnotationService extends ClassificationDictionarySerializationService implements IAnnotationWidgetService {

	private ClassificationDictionaryEditor classificationDictionaryEditor;

	@Override
	public Control createWidget(Composite parent, String description, Object currentSelection) {

		classificationDictionaryEditor = new ClassificationDictionaryEditor(parent, SWT.NONE);
		classificationDictionaryEditor.setToolTipText(description);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 200;
		classificationDictionaryEditor.setLayoutData(gridData);
		//
		if(currentSelection instanceof String text) {
			classificationDictionaryEditor.load(text);
		}
		//
		return classificationDictionaryEditor;
	}

	@Override
	public Object getValue(Object currentSelection) {

		return classificationDictionaryEditor.getClassificationDictionary();
	}
}
