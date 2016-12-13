/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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

import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ExplorerListSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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
	private TreeViewer treeViewer;
	private List<IChromatogramEditorSupport> chromatogramEditorSupportList;

	public AbstractSupplierFileExplorer(Composite parent, IChromatogramEditorSupport chromatogramEditorSupport) {
		this(parent, ExplorerListSupport.getChromatogramEditorSupportList(chromatogramEditorSupport));
	}

	public AbstractSupplierFileExplorer(Composite parent, List<IChromatogramEditorSupport> chromatogramEditorSupportList) {
		//
		this.chromatogramEditorSupportList = chromatogramEditorSupportList;
		/*
		 * Create the tree viewer.
		 */
		treeViewer = new TreeViewer(parent, SWT.NONE);
		treeViewer.setContentProvider(new ChromatogramFileExplorerContentProvider(chromatogramEditorSupportList));
		treeViewer.setLabelProvider(new ChromatogramFileExplorerLabelProvider(chromatogramEditorSupportList));
		Display.getCurrent().asyncExec(new Runnable() {

			@Override
			public void run() {

				treeViewer.setInput(EFS.getLocalFileSystem());
				expandLastDirectoryPath();
			}
		});
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
				treeViewer.refresh(file);
			}
			/*
			 * Don't use a else here, cause chromatogram can be
			 * stored also as directories.
			 */
			if(isNewFile(file)) {
				exitloop:
				for(IChromatogramEditorSupport chromatogramEditorSupport : chromatogramEditorSupportList) {
					if(chromatogramEditorSupport.isMatchMagicNumber(file)) {
						/*
						 * Show the first overview only.
						 */
						chromatogramEditorSupport.openOverview(file, eventBroker);
						break exitloop;
					}
				}
			}
		}
	}

	private void openEditor(File file) {

		if(file != null) {
			for(IChromatogramEditorSupport chromatogramEditorSupport : chromatogramEditorSupportList) {
				/*
				 * Open the supplier file.
				 */
				if(chromatogramEditorSupport.isMatchMagicNumber(file)) {
					saveDirectoryPath(file);
					chromatogramEditorSupport.openEditor(file, modelService, application, partService);
				}
			}
		}
	}

	@Focus
	private void setFocus() {

		treeViewer.getTree().setFocus();
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

		String directoryPath;
		if(file.isFile()) {
			directoryPath = file.getParent();
		} else {
			directoryPath = file.getAbsolutePath();
		}
		PreferenceSupplier.setLastDirectoryPath(directoryPath);
	}

	private void expandLastDirectoryPath() {

		String directoryPath = PreferenceSupplier.getLastDirectoryPath();
		File elementOrTreePath = new File(directoryPath);
		if(elementOrTreePath.exists()) {
			treeViewer.expandToLevel(elementOrTreePath, 1);
		}
	}
}
