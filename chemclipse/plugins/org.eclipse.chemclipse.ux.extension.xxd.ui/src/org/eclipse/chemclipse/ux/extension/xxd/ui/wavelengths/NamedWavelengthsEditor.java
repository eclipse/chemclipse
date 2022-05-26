/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wavelengths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.model.wavelengths.NamedWavelength;
import org.eclipse.chemclipse.model.wavelengths.NamedWavelengths;
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

public class NamedWavelengthsEditor extends Composite {

	private NamedWavelengthsUI namedWavelengthsUI;
	private NamedWavelengthsListUI namedWavelengthsListUI;
	//
	private NamedWavelengths namedWavelengths;
	//
	private IUpdateListener updateListener = null;

	public NamedWavelengthsEditor(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(NamedWavelengths namedWavelengths) {

		this.namedWavelengths = namedWavelengths;
		updateNamedWavelengthsUI();
		updateNamedWavelengthsTable();
		fireUpdate();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public NamedWavelength getNamedWavelength() {

		return namedWavelengthsUI.getNamedWavelength();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		namedWavelengthsUI = createNamedWavelengthsUI(this);
		namedWavelengthsListUI = createNamedWavelengthsListUI(this);
	}

	private NamedWavelengthsUI createNamedWavelengthsUI(Composite parent) {

		NamedWavelengthsUI namedWavelengthsUI = new NamedWavelengthsUI(parent, SWT.NONE);
		namedWavelengthsUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		namedWavelengthsUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateNamedWavelengthsTable();
				fireUpdate();
			}
		});
		return namedWavelengthsUI;
	}

	private NamedWavelengthsListUI createNamedWavelengthsListUI(Composite parent) {

		NamedWavelengthsListUI namedWavelengthsListUI = new NamedWavelengthsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		Table table = namedWavelengthsListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		namedWavelengthsListUI.setEditEnabled(false);
		namedWavelengthsListUI.setSortEnabled(true);
		/*
		 * Selection
		 */
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = namedWavelengthsListUI.getStructuredSelection().getFirstElement();
				if(object instanceof NamedWavelength) {
					NamedWavelength NamedWavelength = (NamedWavelength)object;
					String[] items = namedWavelengthsUI.getItems();
					exitloop:
					for(int i = 0; i < items.length; i++) {
						if(items[i].equals(NamedWavelength.getIdentifier())) {
							namedWavelengthsUI.select(i);
							break exitloop;
						}
					}
				}
			}
		});
		/*
		 * Delete item(s)
		 */
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(namedWavelengths != null) {
					if(e.keyCode == SWT.DEL) {
						MessageBox messageBox = new MessageBox(e.display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messageBox.setText("Delete named wavelength(s)");
						messageBox.setMessage("Would you like to delete the selected named wavelength(s)?");
						if(messageBox.open() == SWT.YES) {
							/*
							 * Collect
							 */
							List<NamedWavelength> deleteItems = new ArrayList<>();
							for(Object object : namedWavelengthsListUI.getStructuredSelection().toList()) {
								if(object instanceof NamedWavelength) {
									NamedWavelength NamedWavelength = (NamedWavelength)object;
									deleteItems.add(NamedWavelength);
								}
							}
							/*
							 * Delete
							 */
							delete(deleteItems);
							/*
							 * Update
							 */
							updateNamedWavelengthsUI();
							updateNamedWavelengthsTable();
							fireUpdate();
						}
					}
				}
			}
		});
		//
		return namedWavelengthsListUI;
	}

	private void delete(List<NamedWavelength> deleteItems) {

		if(namedWavelengths != null) {
			for(NamedWavelength deleteItem : deleteItems) {
				namedWavelengths.remove(deleteItem.getIdentifier());
			}
		}
	}

	private void updateNamedWavelengthsUI() {

		namedWavelengthsUI.setInput(namedWavelengths);
	}

	private void updateNamedWavelengthsTable() {

		if(namedWavelengths != null) {
			List<NamedWavelength> list = new ArrayList<>(namedWavelengths.values());
			Collections.sort(list, (t1, t2) -> t1.getIdentifier().compareTo(t2.getIdentifier()));
			namedWavelengthsListUI.setInput(list);
		} else {
			namedWavelengthsListUI.setInput(null);
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
