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
package org.eclipse.chemclipse.ux.extension.ui.explorer;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.ExplorerListSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerLabelProvider;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class AbstractSupplierFileExplorer {

	@Inject
	private EPartService partService;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication application;
	//
	private File lastClickedFile;
	private List<ISupplierFileEditorSupport> supplierFileEditorSupportList;
	//
	private TabItem drivesTab;
	private TreeViewer drivesTreeViewer;
	//
	private TreeViewer homeTreeViewer;
	private TabItem homeTab;
	//
	private TreeViewer userLocationTreeViewer;
	private TabItem userLocationTab;

	public AbstractSupplierFileExplorer(Composite parent, ISupplierFileEditorSupport supplierFileEditorSupport) {
		this(parent, ExplorerListSupport.getChromatogramEditorSupportList(supplierFileEditorSupport));
	}

	public AbstractSupplierFileExplorer(Composite parent, List<ISupplierFileEditorSupport> supplierFileEditorSupportList) {
		/*
		 * The supplier editor support list is used to show
		 * the preview and open the editor on demand.
		 */
		this.supplierFileEditorSupportList = supplierFileEditorSupportList;
		/*
		 * Create the tree viewer.
		 */
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createDrivesTreeViewer(tabFolder);
		createHomeTreeViewer(tabFolder);
		createUserLocationTreeViewer(tabFolder);
	}

	private void createDrivesTreeViewer(TabFolder tabFolder) {

		drivesTab = new TabItem(tabFolder, SWT.NONE);
		drivesTab.setText("Drives");
		drivesTreeViewer = createTreeViewer(tabFolder);
		drivesTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		if(OperatingSystemUtils.isWindows()) {
			/*
			 * Windows, try to get C:\\
			 */
			File root = null;
			exitloop:
			for(File file : File.listRoots()) {
				if(file.getAbsolutePath().startsWith("C:")) {
					root = file;
					break exitloop;
				}
			}
			/*
			 * Show at least the home directory, if C:\\ was not found.
			 */
			if(root == null) {
				root = new File(UserManagement.getUserHome());
			}
			setTreeViewerContent(drivesTreeViewer, root);
		} else {
			/*
			 * Mac OS X, Linux
			 */
			setTreeViewerContent(drivesTreeViewer, EFS.getLocalFileSystem());
		}
		drivesTab.setControl(drivesTreeViewer.getControl());
	}

	private void createHomeTreeViewer(TabFolder tabFolder) {

		homeTab = new TabItem(tabFolder, SWT.NONE);
		homeTab.setText("Home");
		homeTreeViewer = createTreeViewer(tabFolder);
		homeTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		setTreeViewerContent(homeTreeViewer, new File(UserManagement.getUserHome()));
		homeTab.setControl(homeTreeViewer.getControl());
	}

	private void createUserLocationTreeViewer(TabFolder tabFolder) {

		userLocationTab = new TabItem(tabFolder, SWT.NONE);
		userLocationTab.setText("User Location");
		Composite compositeUserLocation = new Composite(tabFolder, SWT.NONE);
		compositeUserLocation.setLayoutData(new GridData(GridData.FILL_BOTH));
		compositeUserLocation.setLayout(new GridLayout());
		//
		Button buttonSelectUserLocation = new Button(compositeUserLocation, SWT.PUSH);
		buttonSelectUserLocation.setText("Select User Location");
		buttonSelectUserLocation.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16));
		buttonSelectUserLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonSelectUserLocation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.READ_ONLY);
				directoryDialog.setText("Select a directory.");
				String pathname = directoryDialog.open();
				if(pathname != null) {
					File directory = new File(pathname);
					if(directory.exists()) {
						userLocationTreeViewer.setInput(directory);
						PreferenceSupplier.setUserLocationPath(directory.getAbsolutePath());
					}
				}
			}
		});
		//
		String userLocationPath = PreferenceSupplier.getUserLocationPath();
		File userLocation = new File(userLocationPath);
		if(!userLocation.exists()) {
			userLocation = new File(UserManagement.getUserHome());
		}
		userLocationTreeViewer = createTreeViewer(compositeUserLocation);
		userLocationTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		setTreeViewerContent(userLocationTreeViewer, userLocation);
		userLocationTab.setControl(compositeUserLocation);
	}

	private TreeViewer createTreeViewer(Composite parent) {

		TreeViewer treeViewer = new TreeViewer(parent, SWT.NONE);
		treeViewer.setContentProvider(new SupplierFileExplorerContentProvider(supplierFileEditorSupportList));
		treeViewer.setLabelProvider(new SupplierFileExplorerLabelProvider(supplierFileEditorSupportList));
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
				openEditor(file);
			}
		});
		//
		return treeViewer;
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
				if(drivesTab.getControl().isVisible()) {
					drivesTreeViewer.refresh(file);
				} else if(homeTab.getControl().isVisible()) {
					homeTreeViewer.refresh(file);
				} else if(userLocationTab.getControl().isVisible()) {
					userLocationTreeViewer.refresh(file);
				}
			}
			/*
			 * Supplier files can be stored also as a directory.
			 */
			if(isNewFile(file)) {
				boolean isSupported = false;
				exitloop:
				for(ISupplierFileEditorSupport supplierFileEditorSupport : supplierFileEditorSupportList) {
					if(supplierFileEditorSupport.isMatchMagicNumber(file)) {
						/*
						 * Show the first overview only.
						 */
						supplierFileEditorSupport.openOverview(file, eventBroker);
						isSupported = true;
						break exitloop;
					}
				}
				//
				if(!isSupported) {
					eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE, file);
				}
			}
		}
	}

	private void openEditor(File file) {

		if(file != null) {
			for(ISupplierFileEditorSupport supplierFileEditorSupport : supplierFileEditorSupportList) {
				/*
				 * Open the supplier file.
				 */
				if(supplierFileEditorSupport.isMatchMagicNumber(file)) {
					saveDirectoryPath(file);
					supplierFileEditorSupport.openEditor(file, modelService, application, partService);
				}
			}
		}
	}

	@Focus
	private void setFocus() {

		if(drivesTab.getControl().isVisible()) {
			drivesTreeViewer.getTree().setFocus();
		} else if(homeTab.getControl().isVisible()) {
			homeTreeViewer.getTree().setFocus();
		} else if(userLocationTab.getControl().isVisible()) {
			userLocationTreeViewer.getTree().setFocus();
		}
	}

	/*
	 * Checks if the file has already been loaded in overview mode.
	 */
	private boolean isNewFile(File file) {

		if(lastClickedFile == null || lastClickedFile != file) {
			lastClickedFile = file;
			return true;
		} else {
			return false;
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
			if(drivesTab.getControl().isVisible()) {
				PreferenceSupplier.setSelectedDrivePath(directoryPath);
			} else if(homeTab.getControl().isVisible()) {
				PreferenceSupplier.setSelectedHomePath(directoryPath);
			} else if(userLocationTab.getControl().isVisible()) {
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

		Display.getCurrent().asyncExec(new Runnable() {

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
		if(treeViewer == drivesTreeViewer) {
			directoryPath = PreferenceSupplier.getSelectedDrivePath();
		} else if(treeViewer == homeTreeViewer) {
			directoryPath = PreferenceSupplier.getSelectedHomePath();
		} else if(treeViewer == userLocationTreeViewer) {
			directoryPath = PreferenceSupplier.getSelectedUserLocationPath();
		}
		//
		File elementOrTreePath = new File(directoryPath);
		if(elementOrTreePath.exists()) {
			treeViewer.expandToLevel(elementOrTreePath, 1);
		}
	}
}
