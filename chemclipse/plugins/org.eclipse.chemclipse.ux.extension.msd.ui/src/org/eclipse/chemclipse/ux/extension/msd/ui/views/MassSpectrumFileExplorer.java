/*******************************************************************************
 * Copyright (c) 2013, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.MassSpectrumEditor;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.MassSpectrumIdentifier;
import org.eclipse.chemclipse.ux.extension.msd.ui.provider.MassSpectrumFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.provider.MassSpectrumFileExplorerLabelProvider;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumFileExplorer {

	private TreeViewer treeViewer;
	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication application;

	@Inject
	public MassSpectrumFileExplorer(Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new MassSpectrumFileExplorerContentProvider());
		treeViewer.setLabelProvider(new MassSpectrumFileExplorerLabelProvider());
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
				}
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				File file = (File)((IStructuredSelection)event.getSelection()).getFirstElement();
				openMassSpectrumEditor(file);
			}
		});
	}

	@Focus
	private void setFocus() {

		treeViewer.getTree().setFocus();
	}

	// --------------------------------------------private methods
	private void openMassSpectrumEditor(final File file) {

		/*
		 * Check that the selected file or directory is a valid chromatogram.
		 */
		if(MassSpectrumIdentifier.isMassSpectrum(file) || MassSpectrumIdentifier.isMassSpectrumDirectory(file)) {
			/*
			 * Get the editor part stack.
			 */
			MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
			/*
			 * Create the input part and prepare it.
			 */
			MPart part = MBasicFactory.INSTANCE.createInputPart();
			part.setElementId(MassSpectrumEditor.ID);
			part.setContributionURI(MassSpectrumEditor.CONTRIBUTION_URI);
			part.setObject(file.getAbsolutePath());
			part.setIconURI(MassSpectrumEditor.ICON_URI);
			part.setLabel(file.getName());
			part.setTooltip(MassSpectrumEditor.TOOLTIP);
			part.setCloseable(true);
			/*
			 * Add it to the stack and show it.
			 */
			partStack.getChildren().add(part);
			partService.showPart(part, PartState.ACTIVATE);
		}
	}
	// --------------------------------------------private methods
}
