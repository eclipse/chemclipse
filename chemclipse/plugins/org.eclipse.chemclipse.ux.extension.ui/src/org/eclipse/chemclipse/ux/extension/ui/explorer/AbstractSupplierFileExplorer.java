/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - performance optimization and cleanup
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.DataExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class AbstractSupplierFileExplorer {

	private static final Logger logger = Logger.getLogger(AbstractSupplierFileExplorer.class);
	//
	@Inject
	private IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
	//
	private TabItem tabDrives;
	private TreeViewer treeViewerDrives;
	//
	private TabItem tabHome;
	private TreeViewer treeViewerHome;
	//
	private TabItem tabUserLocation;
	private TreeViewer treeViewerUserLocation;
	/*
	 * Contains no data initially.
	 */
	private List<ISupplierFileEditorSupport> supplierFileEditorSupportList = new ArrayList<ISupplierFileEditorSupport>();
	//

	public AbstractSupplierFileExplorer(Composite parent) {
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		createDrivesTreeViewer(tabFolder);
		createHomeTreeViewer(tabFolder);
		createUserLocationTreeViewer(tabFolder);
	}

	public void setSupplierFileEditorSupportList(List<ISupplierFileEditorSupport> supplierFileEditorSupportList) {

		this.supplierFileEditorSupportList = supplierFileEditorSupportList;
		//
		setTreeViewerProvider(treeViewerDrives);
		setTreeViewerProvider(treeViewerHome);
		setTreeViewerProvider(treeViewerUserLocation);
		//
		setTreeViewerContent(treeViewerDrives, File.listRoots());
		setTreeViewerContent(treeViewerHome, new File[]{new File(UserManagement.getUserHome())});
		setTreeViewerContent(treeViewerUserLocation, new File[]{getUserLocation()});
	}

	private void createDrivesTreeViewer(TabFolder tabFolder) {

		tabDrives = new TabItem(tabFolder, SWT.NONE);
		tabDrives.setText("Drives");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		//
		treeViewerDrives = createTreeViewer(composite);
		addBatchOpenButton(composite, treeViewerDrives);
		//
		tabDrives.setControl(composite);
	}

	private void createHomeTreeViewer(TabFolder tabFolder) {

		tabHome = new TabItem(tabFolder, SWT.NONE);
		tabHome.setText("Home");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		//
		treeViewerHome = createTreeViewer(composite);
		addBatchOpenButton(composite, treeViewerHome);
		//
		tabHome.setControl(composite);
	}

	private void createUserLocationTreeViewer(TabFolder tabFolder) {

		tabUserLocation = new TabItem(tabFolder, SWT.NONE);
		tabUserLocation.setText("User Location");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		//
		addUserLocationButton(composite, treeViewerUserLocation);
		treeViewerUserLocation = createTreeViewer(composite);
		addBatchOpenButton(composite, treeViewerUserLocation);
		//
		tabUserLocation.setControl(composite);
	}

	private File getUserLocation() {

		String userLocationPath = PreferenceSupplier.getUserLocationPath();
		File userLocation = new File(userLocationPath);
		if(!userLocation.exists()) {
			userLocation = new File(UserManagement.getUserHome());
		}
		return userLocation;
	}

	private TreeViewer createTreeViewer(Composite parent) {

		TreeViewer treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.VIRTUAL);
		treeViewer.setUseHashlookup(true);
		treeViewer.setExpandPreCheckFilters(true);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		setTreeViewerProvider(treeViewer);
		/*
		 * Register single (selection changed)/double click listener here.<br/>
		 * OK, it's not the best way, but it still works at beginning.
		 */
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				File file = (File)((IStructuredSelection)event.getSelection()).getFirstElement();
				openOverview(file);
			}
		});
		/*
		 * Open the editor.
		 */
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				File file = (File)((IStructuredSelection)event.getSelection()).getFirstElement();
				openEditor(file, false);
			}
		});
		createContextMenu(treeViewer);
		return treeViewer;
	}

	private void createContextMenu(Viewer viewer) {

		MenuManager contextMenu = new MenuManager("#ViewerMenu"); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager mgr) {

				File file = (File)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				List<ISupplierFileEditorSupport> activeFileSupplierList = getActiveFileSupplier(file);
				for(ISupplierFileEditorSupport activeFileSupplier : activeFileSupplierList) {
					contextMenu.add(new Action("Open as: " + activeFileSupplier.getType()) {

						@Override
						public void run() {

							if(file != null) {
								Iterator<?> it = ((IStructuredSelection)viewer.getSelection()).iterator();
								while(it.hasNext()) {
									File f = (File)it.next();
									if(f != null && f.isFile() && activeFileSupplier.isMatchMagicNumber(f)) {
										DisplayUtils.getDisplay().asyncExec(new Runnable() {

											@Override
											public void run() {

												openEditor(f, activeFileSupplier, true);
											}
										});
									}
								}
							}
						}
					});
				}
			}
		});
		Menu menu = contextMenu.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	void setTreeViewerProvider(TreeViewer treeViewer) {

		treeViewer.setContentProvider(new DataExplorerContentProvider(supplierFileEditorSupportList));
		treeViewer.setLabelProvider(new DataExplorerLabelProvider(supplierFileEditorSupportList));
	}

	private void addUserLocationButton(Composite parent, TreeViewer treeViewer) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Select User Location");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog directoryDialog = new DirectoryDialog(DisplayUtils.getShell(), SWT.READ_ONLY);
				directoryDialog.setText("Select a directory.");
				String pathname = directoryDialog.open();
				if(pathname != null) {
					File directory = new File(pathname);
					if(directory.exists()) {
						setTreeViewerContent(treeViewerUserLocation, directory);
						PreferenceSupplier.setUserLocationPath(directory.getAbsolutePath());
					}
				}
			}
		});
	}

	private void addBatchOpenButton(Composite parent, TreeViewer treeViewer) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Open Selected Measurements");
		button.setToolTipText("Try to open all selected files. Handle with care.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = treeViewer.getStructuredSelection();
				Iterator iterator = structuredSelection.iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					if(object instanceof File) {
						DisplayUtils.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {

								File file = (File)object;
								openEditor(file, true);
							}
						});
					}
				}
			}
		});
	}

	private void openOverview(File file) {

		if(file != null) {
			/*
			 * Update the directories content, until there is
			 * actual no way to monitor the file system outside
			 * of the workbench without using operating system
			 * specific function via e.g. JNI.
			 */
			if(file.isDirectory()) {
				if(tabDrives.getControl().isVisible()) {
					treeViewerDrives.refresh(file);
				} else if(tabHome.getControl().isVisible()) {
					treeViewerHome.refresh(file);
				} else if(tabUserLocation.getControl().isVisible()) {
					treeViewerUserLocation.refresh(file);
				}
			}
			/*
			 * Supplier files can be stored also as a directory.
			 */
			boolean isSupported = false;
			exitloop:
			for(ISupplierFileEditorSupport supplierFileEditorSupport : supplierFileEditorSupportList) {
				if(supplierFileEditorSupport.isMatchMagicNumber(file)) {
					/*
					 * Show the first overview only.
					 */
					supplierFileEditorSupport.openOverview(file);
					isSupported = true;
					break exitloop;
				}
			}
			//
			if(!isSupported) {
				eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE, null);
			}
		}
	}

	private void openEditor(File file, boolean batch) {

		if(file != null) {
			/*
			 * Get the settings and the
			 * responsible file supplier list.
			 */
			boolean openFirstDataMatchOnly = PreferenceSupplier.isOpenFirstDataMatchOnly();
			List<ISupplierFileEditorSupport> activeFileSupplierList = getActiveFileSupplier(file);
			/*
			 * Log the editor support info.
			 */
			logger.info("Modus Open First Data Match Only: " + openFirstDataMatchOnly);
			for(ISupplierFileEditorSupport activeFileSupplier : activeFileSupplierList) {
				logger.info(file.getName() + " Supplier Found: " + activeFileSupplier.getType());
			}
			/*
			 * Open the file(s).
			 */
			batch = (!openFirstDataMatchOnly && activeFileSupplierList.size() > 1) ? true : batch; // Prevent SWT thread deadlocks
			exitloop:
			for(ISupplierFileEditorSupport activeFileSupplier : activeFileSupplierList) {
				boolean success = openEditor(file, activeFileSupplier, batch);
				if(success && openFirstDataMatchOnly) {
					break exitloop;
				}
			}
		}
	}

	private boolean openEditor(File file, ISupplierFileEditorSupport activeFileSupplier, boolean batch) {

		saveDirectoryPath(file);
		return activeFileSupplier.openEditor(file, batch);
	}

	private List<ISupplierFileEditorSupport> getActiveFileSupplier(File file) {

		List<ISupplierFileEditorSupport> activeFileSupplierList = new ArrayList<>();
		for(ISupplierFileEditorSupport supplierFileEditorSupport : supplierFileEditorSupportList) {
			if(supplierFileEditorSupport.isMatchMagicNumber(file)) {
				activeFileSupplierList.add(supplierFileEditorSupport);
			}
		}
		return activeFileSupplierList;
	}

	@Focus
	private void setFocus() {

		if(tabDrives.getControl().isVisible()) {
			treeViewerDrives.getTree().setFocus();
		} else if(tabHome.getControl().isVisible()) {
			treeViewerHome.getTree().setFocus();
		} else if(tabUserLocation.getControl().isVisible()) {
			treeViewerUserLocation.getTree().setFocus();
		}
	}

	private void saveDirectoryPath(File file) {

		String directoryPath = "";
		if(file.isFile()) {
			/*
			 * Sometimes the data is stored
			 * in nested directories.
			 */
			File directory = file.getParentFile();
			if(directory != null) {
				File directoryRoot = directory.getParentFile();
				if(getNumberOfChildDirectories(directoryRoot) <= 1) {
					directoryPath = directoryRoot.getAbsolutePath();
				} else {
					directoryPath = directory.getAbsolutePath();
				}
			}
		} else {
			directoryPath = file.getAbsolutePath();
		}
		/*
		 * Store the specific directory path.
		 */
		if(!directoryPath.equals("")) {
			if(tabDrives.getControl().isVisible()) {
				PreferenceSupplier.setSelectedDrivePath(directoryPath);
			} else if(tabHome.getControl().isVisible()) {
				PreferenceSupplier.setSelectedHomePath(directoryPath);
			} else if(tabUserLocation.getControl().isVisible()) {
				PreferenceSupplier.setSelectedUserLocationPath(directoryPath);
			}
		}
	}

	private int getNumberOfChildDirectories(File directory) {

		int counter = 0;
		if(directory != null) {
			for(File file : directory.listFiles()) {
				if(file.isDirectory()) {
					counter++;
				}
			}
		}
		return counter;
	}

	private void setTreeViewerContent(TreeViewer treeViewer, Object input) {

		DisplayUtils.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {

				treeViewer.setInput(input);
				expandLastDirectoryPath(treeViewer);
			}
		});
	}

	private void expandLastDirectoryPath(TreeViewer treeViewer) {

		/*
		 * Get the specific directory path.
		 */
		String directoryPath = "";
		if(treeViewer == treeViewerDrives) {
			directoryPath = PreferenceSupplier.getSelectedDrivePath();
		} else if(treeViewer == treeViewerHome) {
			directoryPath = PreferenceSupplier.getSelectedHomePath();
		} else if(treeViewer == treeViewerUserLocation) {
			directoryPath = PreferenceSupplier.getSelectedUserLocationPath();
		}
		//
		File elementOrTreePath = new File(directoryPath);
		if(elementOrTreePath.exists()) {
			treeViewer.expandToLevel(elementOrTreePath, 1);
		}
	}
}
