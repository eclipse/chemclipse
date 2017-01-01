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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.support.ui.swt.IListItemsRemoveListener;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class MassSpectrumSearchListUI extends Composite {

	private static final int MAX_SPECTRA_LOAD_COMPLETE = 5000;
	private static final int SIZE_SEARCH_RESTRICTION = 15000;
	//
	private Text textSearch;
	private Button checkboxCaseSensitive;
	private Label labelInfo;
	private MassSpectrumListUI massSpectrumListUI;
	//
	private int massSpectraSize = 0;
	private List<IListItemsRemoveListener> listItemRemoveListeners;

	public MassSpectrumSearchListUI(Composite parent, int style) {
		super(parent, style);
		listItemRemoveListeners = new ArrayList<IListItemsRemoveListener>();
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

	public void addListItemsRemoveListener(IListItemsRemoveListener listItemRemoveListener) {

		listItemRemoveListeners.add(listItemRemoveListener);
	}

	public void removeListItemsRemoveListener(IListItemsRemoveListener listItemRemoveListener) {

		listItemRemoveListeners.remove(listItemRemoveListener);
	}

	public void update(IMassSpectra massSpectra, boolean forceReload) {

		if(massSpectra != null) {
			//
			if(massSpectra.size() > MAX_SPECTRA_LOAD_COMPLETE) {
				String searchString = "Please use this filter";
				massSpectrumListUI.setSearchText(searchString, true);
				textSearch.setText(searchString);
			}
			massSpectrumListUI.setInput(massSpectra);
			massSpectraSize = massSpectra.size();
			updateLabel();
		}
	}

	public void clear() {

		massSpectrumListUI.setInput(null);
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

				if(massSpectraSize >= SIZE_SEARCH_RESTRICTION) {
					int length = textSearch.getText().length();
					if(length == 0) {
						search();
					} else if(length >= 5) {
						search();
					}
				} else {
					search();
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
		massSpectrumListUI = new MassSpectrumListUI(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		massSpectrumListUI.getTable().setLayoutData(gridData);
		massSpectrumListUI.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					deleteSelectedTargets();
				}
			}
		});
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

	private void deleteSelectedTargets() {

		Shell shell = Display.getCurrent().getActiveShell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
		messageBox.setText("Delete Selected Targets");
		messageBox.setMessage("Do you really want to delete the selected targets?");
		int decision = messageBox.open();
		if(SWT.YES == decision) {
			/*
			 * Delete the selected items.
			 */
			Table table = massSpectrumListUI.getTable();
			int[] indices = table.getSelectionIndices();
			/*
			 * Delete the selected targets.
			 */
			Object input = massSpectrumListUI.getInput();
			if(input instanceof IMassSpectra) {
				IMassSpectra massSpectra = (IMassSpectra)input;
				for(int i : indices) {
					massSpectra.getMassSpectrum(i + 1).removeAllTargets();
				}
			}
			/*
			 * Delete targets in table.
			 */
			table.remove(indices);
			fireUpdateListItemsRemoved();
		}
	}

	private void fireUpdateListItemsRemoved() {

		for(IListItemsRemoveListener listItemRemoveListener : listItemRemoveListeners) {
			listItemRemoveListener.update();
		}
	}

	private void updateLabel() {

		labelInfo.setText("Mass Spectra: " + massSpectraSize + " (Filtered Mass Spectra: " + massSpectrumListUI.getTable().getItemCount() + " [" + textSearch.getText().trim() + "])");
	}
}