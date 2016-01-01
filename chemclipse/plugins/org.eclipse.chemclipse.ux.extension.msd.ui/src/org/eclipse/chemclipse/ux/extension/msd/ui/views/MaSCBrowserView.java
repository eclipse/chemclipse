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
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.chemclipse.swt.ui.browser.BrowserUI;

public class MaSCBrowserView {

	@Inject
	private Composite parent;
	private BrowserUI browserUI;
	private String url = "http://www.mascgroup.org";

	@PostConstruct
	private void postConstruct() {

		parent.setLayout(new FillLayout());
		browserUI = new BrowserUI(parent, SWT.NONE, url);
	}

	@PreDestroy
	private void preDestroy() {

	}

	@Focus
	public void setFocus() {

		browserUI.setFocus();
	}
}
