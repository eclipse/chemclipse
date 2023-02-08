/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.calibration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class RetentionIndexUI extends Composite {

	private SearchSupportUI searchSupportUI;
	private CalibrationEditUI calibrationEditUI;
	private RetentionIndexTableViewerUI retentionIndexListUI;
	//
	private IUpdateListener updateListener = null;
	private ISeparationColumnIndices separationColumnIndices = null;

	public RetentionIndexUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void addRetentionIndexEntries(List<IRetentionIndexEntry> retentionIndexEntries) {

		calibrationEditUI.addRetentionIndexEntries(retentionIndexEntries);
	}

	public void setInput(ISeparationColumnIndices separationColumnIndices) {

		this.separationColumnIndices = separationColumnIndices;
		retentionIndexListUI.setInput(separationColumnIndices);
	}

	public RetentionIndexTableViewerUI getRetentionIndexTableViewerUI() {

		return retentionIndexListUI;
	}

	public void setSearchVisibility(boolean visible) {

		PartSupport.setCompositeVisibility(searchSupportUI, visible);
	}

	public boolean toggleSearchVisibility() {

		return PartSupport.toggleCompositeVisibility(searchSupportUI);
	}

	public void setEditVisibility(boolean visible) {

		PartSupport.setCompositeVisibility(calibrationEditUI, visible);
	}

	public boolean toggleEditVisibility() {

		return PartSupport.toggleCompositeVisibility(calibrationEditUI);
	}

	public void toggleTableEdit() {

		boolean editEnabled = !retentionIndexListUI.isEditEnabled();
		retentionIndexListUI.setEditEnabled(editEnabled);
	}

	public void enableTableEdit(boolean editEnabled) {

		retentionIndexListUI.setEditEnabled(editEnabled);
	}

	public String getSearchText() {

		return searchSupportUI.getSearchText();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		searchSupportUI = createToolbarSearch(composite);
		calibrationEditUI = createToolbarEdit(composite);
		retentionIndexListUI = createTableField(composite);
	}

	private SearchSupportUI createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				retentionIndexListUI.setSearchText(searchText, caseSensitive);
				fireUpdate(Display.getDefault());
			}
		});
		//
		return searchSupportUI;
	}

	private CalibrationEditUI createToolbarEdit(Composite parent) {

		CalibrationEditUI calibrationEditUI = new CalibrationEditUI(parent, SWT.NONE);
		calibrationEditUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		calibrationEditUI.setCalibrationEditListener(new ICalibrationEditListener() {

			@Override
			public void delete() {

				if(separationColumnIndices != null) {
					Table table = retentionIndexListUI.getTable();
					int index = table.getSelectionIndex();
					if(index >= 0) {
						MessageBox messageBox = new MessageBox(calibrationEditUI.getShell(), SWT.ICON_WARNING);
						messageBox.setText(ExtensionMessages.deleteReferences);
						messageBox.setMessage(ExtensionMessages.shallDeleteReferences);
						if(messageBox.open() == SWT.OK) {
							List<Integer> keysToRemove = new ArrayList<>();
							TableItem[] tableItems = table.getSelection();
							for(TableItem tableItem : tableItems) {
								Object object = tableItem.getData();
								if(object instanceof IRetentionIndexEntry retentionIndexEntry) {
									keysToRemove.add(retentionIndexEntry.getRetentionTime());
								}
							}
							/*
							 * Remove
							 */
							for(int key : keysToRemove) {
								separationColumnIndices.remove(key);
							}
							separationColumnIndices.setDirty(true);
							notifyLibraryUpdate();
							retentionIndexListUI.setInput(separationColumnIndices);
							fireUpdate(Display.getDefault());
						}
					}
				}
			}

			@Override
			public void add(IRetentionIndexEntry retentionIndexEntry) {

				if(retentionIndexEntry != null && separationColumnIndices != null) {
					separationColumnIndices.put(retentionIndexEntry);
					separationColumnIndices.setDirty(true);
					notifyLibraryUpdate();
					retentionIndexListUI.setInput(separationColumnIndices);
					fireUpdate(Display.getDefault());
				}
			}
		});
		//
		return calibrationEditUI;
	}

	private RetentionIndexTableViewerUI createTableField(Composite composite) {

		RetentionIndexTableViewerUI tableViewer = new RetentionIndexTableViewerUI(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.setUpdateListener(new IUpdateListener() {

			@Override
			public void update(Display display) {

				notifyLibraryUpdate();
			}
		});
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				calibrationEditUI.selectRetentionIndices();
				fireUpdate(e.display);
			}
		});
		//
		return tableViewer;
	}

	private void fireUpdate(Display display) {

		if(updateListener != null) {
			updateListener.update(display);
		}
	}

	private void notifyLibraryUpdate() {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_RI_LIBRARY_UPDATE, new Object[]{"", separationColumnIndices});
		}
	}
}
