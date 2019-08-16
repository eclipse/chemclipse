/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.wizards;

import org.eclipse.core.resources.IContainer;

public class WizardElements implements IWizardElements {

	private IContainer container;
	private String fileName = "";

	@Override
	public IContainer getContainer() {

		return container;
	}

	@Override
	public void setContainer(IContainer container) {

		this.container = container;
	}

	@Override
	public String getFileName() {

		return fileName;
	}

	@Override
	public void setFileName(String fileName) {

		if(fileName == null) {
			this.fileName = "";
		} else {
			this.fileName = fileName;
		}
	}
}
