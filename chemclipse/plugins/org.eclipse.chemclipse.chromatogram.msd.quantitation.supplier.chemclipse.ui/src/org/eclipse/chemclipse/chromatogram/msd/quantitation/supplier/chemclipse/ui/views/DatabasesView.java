/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.swt.DatabasesUI;

public class DatabasesView {

	@Inject
	private Composite parent;
	@Inject
	private IEventBroker eventBroker;
	private DatabasesUI databasesUI;

	@PostConstruct
	public void createControl() {

		databasesUI = new DatabasesUI(parent, SWT.NONE, eventBroker);
	}

	@Focus
	public void setFocus() {

		databasesUI.setFocus();
	}
}
