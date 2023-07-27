/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
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

	private AtomicReference<Text> textControl = new AtomicReference<>();
	private AtomicReference<Button> buttonControl = new AtomicReference<>();
	//
	private ISearchListener searchListener;
	private boolean caseSensitive = PreferenceSupplier.isSearchCaseSensitive();

	public SearchSupportUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			updateButtonCaseSensitive();
		}
	}

	public void reset() {

		setSearchText("");
	}

	public void setSearchListener(ISearchListener searchListener) {

		this.searchListener = searchListener;
	}

	public void setSearchText(String searchText) {

		textControl.get().setText(searchText);
		runSearch();
	}

	/**
	 * Use this e.g. for content proposal listeners.
	 * The search text shall be set via the setSearchText method.
	 * 
	 * @return Text
	 */
	public Text getText() {

		return textControl.get();
	}

	public String getSearchText() {

		return textControl.get().getText().trim();
	}

	public boolean isSearchCaseSensitive() {

		return caseSensitive;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		createButtonSearch(composite);
		createTextSearch(composite);
		createButtonCaseSensitive(composite);
		//
		initialize();
	}

	private void initialize() {

		updateButtonCaseSensitive();
	}

	private Button createButtonSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Run the search");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runSearch();
			}
		});
		//
		return button;
	}

	private void createTextSearch(Composite parent) {

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
		textControl.set(text);
	}

	private void createButtonCaseSensitive(Composite parent) {

		Button button = new Button(parent, SWT.TOGGLE);
		button.setText("");
		button.setToolTipText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CASE_SENSITIVE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				caseSensitive = !caseSensitive;
				PreferenceSupplier.setSearchCaseSensitive(caseSensitive);
				updateButtonCaseSensitive();
				runSearch();
			}
		});
		//
		buttonControl.set(button);
	}

	private void updateButtonCaseSensitive() {

		Button button = buttonControl.get();
		if(button != null) {
			button.setToolTipText(caseSensitive ? "Search: Case Sensitive" : "Search: Case Insensitive");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CASE_SENSITIVE, IApplicationImageProvider.SIZE_16x16));
		}
	}

	private void runSearch() {

		if(searchListener != null) {
			String searchText = textControl.get().getText().trim();
			searchListener.performSearch(searchText, caseSensitive);
		}
	}
}
