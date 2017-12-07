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
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
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

		this.setLayout(new GridLayout(4, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createTextSearch(this);
		createButtonSearch(this);
		createButtonReset(this);
		createCheckBoxCaseSensitive(this);
		//
		createMassSpectrumTable(this);
		createLabelInfo(this);
	}

	private void createTextSearch(Composite parent) {

		textSearch = new Text(parent, SWT.BORDER);
		textSearch.setText("");
		textSearch.setToolTipText("Type in the search items.");
		textSearch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					search();
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

				search();
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

				textSearch.setText("");
				search();
			}
		});
	}

	private void createCheckBoxCaseSensitive(Composite parent) {

		checkboxCaseSensitive = new Button(parent, SWT.CHECK);
		checkboxCaseSensitive.setText("Case sensitive");
		checkboxCaseSensitive.setSelection(true);
		checkboxCaseSensitive.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				search();
			}
		});
	}

	private void createMassSpectrumTable(Composite parent) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		//
		massSpectrumListUI = new MassSpectrumListUI(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		massSpectrumListUI.getTable().setLayoutData(gridData);
	}

	private void createLabelInfo(Composite parent) {

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		//
		labelInfo = new Label(parent, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(gridData);
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