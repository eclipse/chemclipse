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
package org.eclipse.chemclipse.ux.extension.ui.explorer;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramIdentifier;
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

public abstract class AbstractChromatogramFileExplorer {

	private File lastClickedFile;
	private TreeViewer treeViewer;
	@Inject
	private EPartService partService;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication application;

	public AbstractChromatogramFileExplorer(Composite parent, IChromatogramIdentifier chromatogramIdentifier, final IChromatogramEditorSupport chromatogramEditorSupport) {
		treeViewer = new TreeViewer(parent, SWT.VIRTUAL);
		treeViewer.setContentProvider(new ChromatogramFileExplorerContentProvider(chromatogramIdentifier));
		treeViewer.setLabelProvider(new ChromatogramFileExplorerLabelProvider(chromatogramIdentifier));
		TreeViewerFilesystemSupport.retrieveAndSetLocalFileSystem(treeViewer);
		/*
		 * Register single (selection changed)/double click listener here.<br/>
		 * OK, it's not the best way, but it still works at beginning.
		 */
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				/*
				 * Check the first element if null otherwise an
				 * NullPointerException would be thrown if the
				 * firstElement is null.
				 */
				Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(firstElement != null) {
					File file = (File)firstElement;
					/*
					 * Update the directories content, until there is
					 * actual no way to monitor the file system outside
					 * of the workbench without using operating system
					 * specific function via e.g. JNI.
					 */
					if(file.isDirectory()) {
						treeViewer.refresh(firstElement);
					}
					/*
					 * Don't use a else here, cause chromatogram can be
					 * stored also as directories.
					 */
					if(isNewFile(file)) {
						chromatogramEditorSupport.openOverview(file, eventBroker);
					}
				}
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				File file = (File)((IStructuredSelection)event.getSelection()).getFirstElement();
				chromatogramEditorSupport.openEditor(file, modelService, application, partService);
			}
		});
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
}
