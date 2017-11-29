/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MassSpectrumSearchListUI extends Composite {

	/*
	 * Only do a dynamic search when less than the given
	 * amount of mass spectra is stored. Otherwise, use the search button.
	 */
	private static final int SIZE_DYNAMIC_SEARCH_RESTRICTION = 15000;
	//
	private Text textSearch;
	private Button checkboxCaseSensitive;
	private Label labelInfo;
	private MassSpectrumListUI massSpectrumListUI;

	public MassSpectrumSearchListUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void setSearchSelection(String searchString) {

		textSearch.setText(searchString);
		search();
	}

	@Override
	public boolean setFocus() {

		return massSpectrumListUI.getTable().setFocus();
	}

	public void update(IMassSpectra massSpectra) {

		if(massSpectra != null) {
			massSpectrumListUI.setInput(massSpectra);
			updateLabel();
		} else {
			clear();
		}
	}

	public void clear() {

		massSpectrumListUI.setInput(null);
		updateLabel();
	}

	public TableViewer getTableViewer() {

		return massSpectrumListUI;
	}

	private void initialize() {

		this.setLayout(new GridLayout(3, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		textSearch = new Text(this, SWT.BORDER);
		textSearch.setText("");
		textSearch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					search();
				} else {
					if(getItemSize() <= SIZE_DYNAMIC_SEARCH_RESTRICTION) {
						search();
					}
				}
			}
		});
		//
		Button button = new Button(this, SWT.PUSH);
		button.setText("Search");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				search();
			}
		});
		//
		checkboxCaseSensitive = new Button(this, SWT.CHECK);
		checkboxCaseSensitive.setText("Case sensitive");
		checkboxCaseSensitive.setSelection(true);
		checkboxCaseSensitive.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				search();
			}
		});
		/*
		 * Table
		 */
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		//
		massSpectrumListUI = new MassSpectrumListUI(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		massSpectrumListUI.getTable().setLayoutData(gridData);
		/*
		 * Table
		 */
		labelInfo = new Label(this, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void search() {

		String searchText = textSearch.getText().trim();
		boolean caseSensitive = checkboxCaseSensitive.getSelection();
		massSpectrumListUI.setSearchText(searchText, caseSensitive);
		updateLabel();
	}

	private void updateLabel() {

		labelInfo.setText("Stored Mass Spectra: " + getItemSize());
	}

	private int getItemSize() {

		return massSpectrumListUI.getTable().getItemCount();
	}
}