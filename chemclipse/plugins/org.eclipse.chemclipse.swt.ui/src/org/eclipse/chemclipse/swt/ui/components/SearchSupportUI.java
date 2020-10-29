/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components;

import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SearchSupportUI extends Composite {

	private Text text;
	private Button checkbox;
	private ISearchListener searchListener;

	public SearchSupportUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void reset() {

		setSearchText("");
	}

	public void setSearchListener(ISearchListener searchListener) {

		this.searchListener = searchListener;
	}

	public void setSearchText(String searchText) {

		text.setText(searchText);
		runSearch();
	}

	public String getSearchText() {

		return text.getText().trim();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		text = createTextSearch(composite);
		checkbox = createCheckBoxCaseSensitive(composite);
	}

	private Text createTextSearch(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		text.setText("");
		text.setToolTipText("Type in the search items.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * Listen to search key event.
		 */
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					runSearch();
				} else if(text.getText().trim().equals("")) {
					/*
					 * Reset when empty.
					 */
					runSearch();
				}
			}
		});
		/*
		 * Click on the icons.
		 */
		text.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				if(e.detail == SWT.ICON_CANCEL) {
					text.setText("");
					runSearch();
				} else if(e.detail == SWT.ICON_SEARCH) {
					runSearch();
				}
			}
		});
		//
		return text;
	}

	private Button createCheckBoxCaseSensitive(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("");
		button.setToolTipText("Search Case Sensitive");
		button.setSelection(PreferenceSupplier.isSearchCaseSensitive());
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.setSearchCaseSensitive(button.getSelection());
				runSearch();
			}
		});
		//
		return button;
	}

	private void runSearch() {

		if(searchListener != null) {
			String searchText = text.getText().trim();
			boolean caseSensitive = checkbox.getSelection();
			searchListener.performSearch(searchText, caseSensitive);
		}
	}
}
