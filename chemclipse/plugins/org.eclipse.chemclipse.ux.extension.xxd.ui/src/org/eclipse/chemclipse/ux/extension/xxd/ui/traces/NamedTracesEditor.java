/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.traces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.traces.NamedTrace;
import org.eclipse.chemclipse.model.traces.NamedTraces;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;

public class NamedTracesEditor extends Composite {

	private NamedTracesUI namedTracesUI;
	private NamedTracesListUI namedTracesListUI;
	//
	private NamedTraces namedTraces;
	//
	private IUpdateListener updateListener = null;

	public NamedTracesEditor(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(NamedTraces namedTraces) {

		this.namedTraces = namedTraces;
		updateNamedTracesUI();
		updateNamedTracesTable();
		fireUpdate();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public NamedTrace getNamedTrace() {

		return namedTracesUI.getNamedTrace();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		namedTracesUI = createNamedTracesUI(this);
		namedTracesListUI = createNamedTracesListUI(this);
	}

	private NamedTracesUI createNamedTracesUI(Composite parent) {

		NamedTracesUI namedTracesUI = new NamedTracesUI(parent, SWT.NONE);
		namedTracesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		namedTracesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateNamedTracesTable();
				fireUpdate();
			}
		});
		return namedTracesUI;
	}

	private NamedTracesListUI createNamedTracesListUI(Composite parent) {

		NamedTracesListUI namedTracesListUI = new NamedTracesListUI(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		Table table = namedTracesListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		namedTracesListUI.setEditEnabled(false);
		namedTracesListUI.setSortEnabled(true);
		/*
		 * Selection
		 */
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = namedTracesListUI.getStructuredSelection().getFirstElement();
				if(object instanceof NamedTrace) {
					NamedTrace namedTrace = (NamedTrace)object;
					String[] items = namedTracesUI.getItems();
					exitloop:
					for(int i = 0; i < items.length; i++) {
						if(items[i].equals(namedTrace.getIdentifier())) {
							namedTracesUI.select(i);
							break exitloop;
						}
					}
				}
			}
		});
		/*
		 * Delete items
		 */
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(namedTraces != null) {
					if(e.keyCode == SWT.DEL) {
						MessageBox messageBox = new MessageBox(e.display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messageBox.setText("Delete trace compounds");
						messageBox.setMessage("Would you like to delete the selected trace compounds?");
						if(messageBox.open() == SWT.YES) {
							/*
							 * Collect
							 */
							List<NamedTrace> deleteItems = new ArrayList<>();
							for(Object object : namedTracesListUI.getStructuredSelection().toList()) {
								if(object instanceof NamedTrace) {
									NamedTrace namedTrace = (NamedTrace)object;
									deleteItems.add(namedTrace);
								}
							}
							/*
							 * Delete
							 */
							delete(deleteItems);
							/*
							 * Update
							 */
							updateNamedTracesUI();
							updateNamedTracesTable();
							fireUpdate();
						}
					}
				}
			}
		});
		//
		return namedTracesListUI;
	}

	private void delete(List<NamedTrace> deleteItems) {

		if(namedTraces != null) {
			for(NamedTrace deleteItem : deleteItems) {
				namedTraces.remove(deleteItem.getIdentifier());
			}
		}
	}

	private void updateNamedTracesUI() {

		namedTracesUI.setInput(namedTraces);
	}

	private void updateNamedTracesTable() {

		if(namedTraces != null) {
			List<NamedTrace> list = new ArrayList<>(namedTraces.values());
			Collections.sort(list, (t1, t2) -> t1.getIdentifier().compareTo(t2.getIdentifier()));
			namedTracesListUI.setInput(list);
		} else {
			namedTracesListUI.setInput(null);
		}
	}

	private void fireUpdate() {

		if(updateListener != null) {
			getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					updateListener.update();
				}
			});
		}
	}
}