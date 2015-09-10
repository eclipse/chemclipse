/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListFilter;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.MassSpectrumListTableComparator;
import org.eclipse.chemclipse.support.ui.swt.viewers.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.viewers.IListItemsRemoveListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class MassSpectrumListUI extends Composite {

	private static final String POPUP_MENU_ID = "#PopUpMenu"; // $NON-NLS-1$
	private static final String POPUP_MENU_POSTFIX = "PopUpMenu"; // $NON-NLS-1$
	private static final int MAX_SPECTRA_LOAD_COMPLETE = 5000;
	//
	private Text text;
	private ExtendedTableViewer tableViewer;
	private Label label;
	private MassSpectrumListTableComparator massSpectrumTableComparator;
	private MassSpectrumListFilter massSpectrumListFilter;
	private String[] titles = {"Retention Time", "Retention Index", "Base Peak", "Base Peak Abundance", "Number of Ions", "Name", "CAS", "MW", "Formula"};
	private int bounds[] = {100, 100, 100, 100, 100, 100, 100, 100, 100};
	private int massSpectraSize = 0;
	private List<IListItemsRemoveListener> listItemRemoveListeners;

	public MassSpectrumListUI(Composite parent, int style) {

		super(parent, style);
		//
		listItemRemoveListeners = new ArrayList<IListItemsRemoveListener>();
		//
		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		text = new Text(this, SWT.BORDER);
		text.setText("");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button button = new Button(this, SWT.PUSH);
		button.setText("Search");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String searchText = text.getText().trim();
				massSpectrumListFilter.setSearchText(searchText);
				tableViewer.refresh();
				updateLabel();
			}
		});
		/*
		 * Table
		 */
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		//
		tableViewer = new ExtendedTableViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.getTable().setLayoutData(gridData);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new MassSpectrumListContentProvider());
		tableViewer.setLabelProvider(new MassSpectrumListLabelProvider());
		massSpectrumListFilter = new MassSpectrumListFilter();
		tableViewer.setFilters(new ViewerFilter[]{massSpectrumListFilter});
		/*
		 * Copy and delete
		 */
		tableViewer.getControl().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					tableViewer.copyToClipboard(titles);
					//
				} else if(e.keyCode == 127 && e.stateMask == 0) {
					/*
					 * Press "DEL" button.
					 */
					deleteSelectedTargets();
				}
			}
		});
		/*
		 * Sorting the table.
		 */
		massSpectrumTableComparator = new MassSpectrumListTableComparator();
		tableViewer.setComparator(massSpectrumTableComparator);
		/*
		 * Table
		 */
		label = new Label(this, SWT.NONE);
		label.setText("");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		initContextMenu();
	}

	@Override
	public boolean setFocus() {

		return tableViewer.getTable().setFocus();
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
				massSpectrumListFilter.setSearchText(searchString);
				text.setText(searchString);
			}
			tableViewer.setInput(massSpectra);
			massSpectraSize = massSpectra.size();
			updateLabel();
		}
	}

	public void clear() {

		tableViewer.setInput(null);
	}

	public TableViewer getTableViewer() {

		return tableViewer;
	}

	// -----------------------------------------private methods
	private void initContextMenu() {

		MenuManager menuManager = new MenuManager(POPUP_MENU_ID, getClass().getName() + POPUP_MENU_POSTFIX);
		menuManager.setRemoveAllWhenShown(true);
		/*
		 * Copy to clipboard
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						tableViewer.copyToClipboard(titles);
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		/*
		 * Delete selected targets
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						deleteSelectedTargets();
					}
				};
				action.setText("Delete selected targets");
				manager.add(action);
			}
		});
		Menu menu = menuManager.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
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
			Table table = tableViewer.getTable();
			int[] indices = table.getSelectionIndices();
			/*
			 * Delete the selected targets.
			 */
			Object input = tableViewer.getInput();
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

		label.setText("Mass Spectra: " + massSpectraSize + " (Filtered Mass Spectra: " + tableViewer.getTable().getItemCount() + " [" + text.getText().trim() + "])");
	}
}