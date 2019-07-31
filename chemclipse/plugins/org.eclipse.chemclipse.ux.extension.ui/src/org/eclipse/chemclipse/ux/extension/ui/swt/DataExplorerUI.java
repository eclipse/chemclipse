/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - performance optimization and cleanup, refactor handling of Suppliers
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class DataExplorerUI extends MultiDataExplorerTreeUI {

	private IEventBroker eventBroker;

	public DataExplorerUI(Composite parent, IEventBroker eventBroker, IPreferenceStore preferenceStore) {
		super(parent, preferenceStore);
		this.eventBroker = eventBroker;
	}

	@Override
	protected void initTabComponent(Composite parent, DataExplorerTreeUI treeUI) {

		super.initTabComponent(parent, treeUI);
		createContextMenu(treeUI);
		addBatchOpenButton(parent, treeUI);
	}

	@Override
	protected void handleDoubleClick(File file, DataExplorerTreeUI treeUI) {

		openEditor(file, treeUI);
	}

	@Override
	protected void handleSelection(File[] files, DataExplorerTreeUI treeUI) {

		if(files.length > 0) {
			openOverview(files[0], treeUI);
		}
	}

	private void createContextMenu(DataExplorerTreeUI treeUI) {

		TreeViewer treeViewer = treeUI.getTreeViewer();
		MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager mgr) {

				DataExplorerContentProvider contentProvider = (DataExplorerContentProvider)treeViewer.getContentProvider();
				Object[] selection = treeViewer.getStructuredSelection().toArray();
				Set<ISupplierFileEditorSupport> suppliers = new LinkedHashSet<>();
				for(Object object : selection) {
					if(object instanceof File) {
						File file = (File)object;
						for(ISupplierFileIdentifier supplier : contentProvider.getSupplierFileIdentifier(file)) {
							if(supplier instanceof ISupplierFileEditorSupport) {
								suppliers.add((ISupplierFileEditorSupport)supplier);
							}
						}
					}
				}
				for(ISupplierFileEditorSupport activeFileSupplier : suppliers) {
					contextMenu.add(new Action("Open as: " + activeFileSupplier.getType()) {

						@Override
						public void run() {

							for(Object object : selection) {
								if(object instanceof File) {
									openEditorWithSupplier((File)object, activeFileSupplier);
								}
							}
						}
					});
				}
				if(selection.length == 1 && selection[0] instanceof File && ((File)selection[0]).isDirectory()) {
					contextMenu.add(new Action("Open all contained measurements in this folder") {

						@Override
						public void run() {

							openRecurse((File)selection[0], treeUI);
						}
					});
				}
			}
		});
		Menu menu = contextMenu.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
	}

	private boolean openRecurse(File file, DataExplorerTreeUI treeUI) {

		boolean opened = false;
		File[] listFiles = file.listFiles();
		if(listFiles != null) {
			for(File f : listFiles) {
				opened |= openEditor(f, treeUI);
			}
			if(!opened) {
				for(File f : listFiles) {
					if(f.isDirectory()) {
						// recurse into sub-directory...
						opened |= openRecurse(f, treeUI);
					}
				}
			}
		}
		return opened;
	};

	private void addBatchOpenButton(Composite parent, DataExplorerTreeUI treeUI) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Open Selected Measurements");
		button.setToolTipText("Try to open all selected files. Handle with care.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = treeUI.getTreeViewer().getStructuredSelection();
				Iterator iterator = structuredSelection.iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					if(object instanceof File) {
						e.display.asyncExec(new Runnable() {

							@Override
							public void run() {

								File file = (File)object;
								openEditor(file, treeUI);
							}
						});
					}
				}
			}
		});
	}

	private void openOverview(File file, DataExplorerTreeUI treeUI) {

		if(file != null) {
			DataExplorerContentProvider contentProvider = treeUI.getContentProvider();
			/*
			 * Update the directories content, until there is
			 * actual no way to monitor the file system outside
			 * of the workbench without using operating system
			 * specific function via e.g. JNI.
			 */
			if(file.isDirectory()) {
				contentProvider.refresh(file);
			}
			Collection<ISupplierFileIdentifier> identifiers = contentProvider.getSupplierFileIdentifier(file);
			for(ISupplierFileIdentifier identifier : identifiers) {
				if(identifier instanceof ISupplierFileEditorSupport) {
					ISupplierFileEditorSupport fileEditorSupport = (ISupplierFileEditorSupport)identifier;
					fileEditorSupport.openOverview(file);
					return;
				}
			}
			if(eventBroker != null) {
				eventBroker.post(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE, null);
			}
		}
	}

	private boolean openEditor(File file, DataExplorerTreeUI treeUI) {

		DataExplorerContentProvider contentProvider = (DataExplorerContentProvider)treeUI.getTreeViewer().getContentProvider();
		saveLastDirectoryPath();
		boolean success = false;
		if(file != null) {
			boolean openFirstDataMatchOnly = PreferenceSupplier.isOpenFirstDataMatchOnly();
			Collection<ISupplierFileIdentifier> identifiers = contentProvider.getSupplierFileIdentifier(file);
			for(ISupplierFileIdentifier identifier : identifiers) {
				if(identifier instanceof ISupplierFileEditorSupport) {
					success = success | openEditorWithSupplier(file, (ISupplierFileEditorSupport)identifier);
				}
				if(success && openFirstDataMatchOnly) {
					break;
				}
			}
		}
		return success;
	}

	public boolean openEditorWithSupplier(File file, ISupplierFileEditorSupport identifier) {

		return identifier.openEditor(file);
	}
}
