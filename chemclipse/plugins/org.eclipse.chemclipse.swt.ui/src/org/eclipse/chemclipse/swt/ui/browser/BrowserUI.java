/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.browser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class BrowserUI extends Composite {

	private Composite headerComposite;
	private Browser browser;
	private String url;

	public BrowserUI(Composite parent, int style, String url) {
		super(parent, style);
		this.url = url;
		initialize(parent);
	}

	private void initialize(Composite parent) {

		GridData layoutData;
		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new GridLayout(1, true));
		/*
		 * Header
		 */
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		headerComposite = new Composite(composite, SWT.NONE);
		headerComposite.setLayout(new GridLayout(1, true));
		headerComposite.setLayoutData(layoutData);
		Composite buttonComposite = new Composite(headerComposite, SWT.NONE);
		buttonComposite.setLayout(new FillLayout());
		/*
		 * Home
		 */
		Button buttonHome = new Button(buttonComposite, SWT.NONE);
		buttonHome.setText("Home");
		buttonHome.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				browser.setUrl(url);
			}
		});
		/*
		 * Back
		 */
		Button buttonBack = new Button(buttonComposite, SWT.NONE);
		buttonBack.setText("Back");
		buttonBack.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				browser.back();
			}
		});
		/*
		 * Forward
		 */
		Button buttonForward = new Button(buttonComposite, SWT.NONE);
		buttonForward.setText("Forward");
		buttonForward.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				browser.forward();
			}
		});
		/*
		 * Clear session
		 */
		Button buttonClear = new Button(buttonComposite, SWT.NONE);
		buttonClear.setText("Clear Sessions");
		buttonClear.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("static-access")
			@Override
			public void widgetSelected(SelectionEvent e) {

				browser.clearSessions();
				browser.setUrl(url);
			}
		});
		/*
		 * Browser
		 */
		browser = new Browser(composite, SWT.FILL | SWT.BORDER);
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser.setJavascriptEnabled(true);
		browser.setUrl(url);
	}
}
