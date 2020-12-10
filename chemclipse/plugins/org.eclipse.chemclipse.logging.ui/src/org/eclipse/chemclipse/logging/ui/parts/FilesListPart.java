/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - migrate from InputPart to Part
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.parts;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.ui.swt.ExtendedFilesList;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class FilesListPart {

	private ExtendedFilesList extendedFilesList;

	@Inject
	public FilesListPart(Composite parent, EPartService partService, EModelService modelService, MApplication application) {

		extendedFilesList = new ExtendedFilesList(parent, SWT.NONE);
		extendedFilesList.update(partService, modelService, application);
	}

	@Focus
	public void setFocus() {

		extendedFilesList.setFocus();
	}
}
