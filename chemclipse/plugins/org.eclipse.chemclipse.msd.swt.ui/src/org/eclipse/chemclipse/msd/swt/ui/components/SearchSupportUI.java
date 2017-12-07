/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
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
	//
	private ISearchListener searchListener;

	public SearchSupportUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setSearchListener(ISearchListener searchListener) {

		this.searchListener = searchListener;
	}

	public void setSearchText(String searchText) {

		text.setText(searchText);
		runSearch();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		createTextSearch(composite);
		createButtonSearch(composite);
		createButtonReset(composite);
		createCheckBoxCaseSensitive(composite);
	}

	private void createTextSearch(Composite parent) {

		text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Type in the search items.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					runSearch();
				}
			}
		});
	}

	private void createButtonSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Search");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runSearch();
			}
		});
	}

	private void createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				text.setText("");
				runSearch();
			}
		});
	}

	private void createCheckBoxCaseSensitive(Composite parent) {

		checkbox = new Button(parent, SWT.CHECK);
		checkbox.setText("Case sensitive");
		checkbox.setSelection(true);
		checkbox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runSearch();
			}
		});
	}

	private void runSearch() {

		if(searchListener != null) {
			String searchText = text.getText().trim();
			boolean caseSensitive = checkbox.getSelection();
			searchListener.performSearch(searchText, caseSensitive);
		}
	}
}
