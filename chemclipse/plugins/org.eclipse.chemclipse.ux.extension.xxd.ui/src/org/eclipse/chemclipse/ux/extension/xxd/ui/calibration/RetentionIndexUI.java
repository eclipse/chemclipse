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
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.ISeparationColumnIndices;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class RetentionIndexUI extends Composite implements IExtendedPartUI {

	private AtomicReference<SearchSupportUI> toolbarSearch = new AtomicReference<>();
	private AtomicReference<CalibrationEditUI> toolbarEdit = new AtomicReference<>();
	private AtomicReference<RetentionIndexTableViewerUI> retentionIndexListUI = new AtomicReference<>();
	//
	private IUpdateListener updateListener = null;
	private ISeparationColumnIndices separationColumnIndices = null;
	//
	private IEventBroker eventBroker = Activator.getDefault().getEventBroker();

	public RetentionIndexUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void addRetentionIndexEntries(List<IRetentionIndexEntry> retentionIndexEntries) {

		toolbarEdit.get().addRetentionIndexEntries(retentionIndexEntries);
	}

	public void setInput(ISeparationColumnIndices separationColumnIndices) {

		this.separationColumnIndices = separationColumnIndices;
		updateInput();
	}

	public RetentionIndexTableViewerUI getRetentionIndexTableViewerUI() {

		return retentionIndexListUI.get();
	}

	public void setSearchVisibility(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarSearch.get(), visible);
	}

	public boolean toggleSearchVisibility() {

		boolean visible = !toolbarSearch.get().isVisible();
		setSearchVisibility(visible);
		return visible;
	}

	public void setEditVisibility(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarEdit.get(), visible);
	}

	public boolean toggleEditVisibility() {

		boolean visible = !toolbarEdit.get().isVisible();
		setEditVisibility(visible);
		return visible;
	}

	public void toggleTableEdit() {

		boolean editEnabled = !retentionIndexListUI.get().isEditEnabled();
		retentionIndexListUI.get().setEditEnabled(editEnabled);
	}

	public void enableTableEdit(boolean editEnabled) {

		retentionIndexListUI.get().setEditEnabled(editEnabled);
	}

	public String getSearchText() {

		return toolbarSearch.get().getSearchText();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		createToolbarSearch(composite);
		createToolbarEdit(composite);
		createTableField(composite);
		//
		initialize();
	}

	private void initialize() {

		setSearchVisibility(false);
		setEditVisibility(false);
	}

	private void updateInput() {

		retentionIndexListUI.get().setInput(separationColumnIndices);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				retentionIndexListUI.get().setSearchText(searchText, caseSensitive);
				fireUpdate(Display.getDefault());
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createToolbarEdit(Composite parent) {

		CalibrationEditUI calibrationEditUI = new CalibrationEditUI(parent, SWT.NONE);
		calibrationEditUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		calibrationEditUI.setCalibrationEditListener(new ICalibrationEditListener() {

			@Override
			public void delete() {

				if(separationColumnIndices != null) {
					Table table = retentionIndexListUI.get().getTable();
					int index = table.getSelectionIndex();
					if(index >= 0) {
						if(MessageDialog.openQuestion(calibrationEditUI.getShell(), ExtensionMessages.deleteReferences, ExtensionMessages.shallDeleteReferences)) {
							List<Integer> keysToRemove = new ArrayList<>();
							TableItem[] tableItems = table.getSelection();
							for(TableItem tableItem : tableItems) {
								Object object = tableItem.getData();
								if(object instanceof Map.Entry entry) {
									if(entry.getValue() instanceof IRetentionIndexEntry retentionIndexEntry) {
										keysToRemove.add(retentionIndexEntry.getRetentionTime());
									}
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
							updateInput();
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
					updateInput();
					fireUpdate(Display.getDefault());
				}
			}
		});
		//
		toolbarEdit.set(calibrationEditUI);
	}

	private void createTableField(Composite composite) {

		RetentionIndexTableViewerUI tableViewer = new RetentionIndexTableViewerUI(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tableViewer.setUpdateListener(new IUpdateListener() {

			@Override
			public void update(Display display) {

				notifyLibraryUpdate();
			}
		});
		//
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				toolbarEdit.get().selectRetentionIndices();
				fireUpdate(e.display);
			}
		});
		//
		retentionIndexListUI.set(tableViewer);
	}

	private void fireUpdate(Display display) {

		if(updateListener != null) {
			updateListener.update(display);
		}
	}

	private void notifyLibraryUpdate() {

		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_RI_LIBRARY_UPDATE, new Object[]{"", separationColumnIndices});
		}
	}
}