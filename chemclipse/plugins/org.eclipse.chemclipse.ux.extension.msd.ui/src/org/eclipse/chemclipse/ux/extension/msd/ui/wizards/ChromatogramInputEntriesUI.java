/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved.
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.wizards;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.support.ui.wizards.TreeViewerFilesystemSupport;
import org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.ChromatogramFileExplorerLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramInputEntriesUI extends Composite {

	private static final Logger logger = Logger.getLogger(ChromatogramInputEntriesUI.class);
	private TreeViewer chromatogramViewer;
	private IChromatogramWizardElements chromatogramWizardElements = new ChromatogramWizardElements();

	public ChromatogramInputEntriesUI(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		chromatogramViewer = new TreeViewer(this, SWT.MULTI);
		chromatogramViewer.setLabelProvider(new ChromatogramFileExplorerLabelProvider(ChromatogramSupport.getInstanceIdentifier()));
		chromatogramViewer.setContentProvider(new ChromatogramFileExplorerContentProvider(ChromatogramSupport.getInstanceIdentifier()));
		chromatogramViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				chromatogramWizardElements.clearSelectedChromatograms();
				ISelection selection = chromatogramViewer.getSelection();
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				for(Object element : structuredSelection.toList()) {
					chromatogramWizardElements.addSelectedChromatogram(element.toString());
				}
			}
		});
		TreeViewerFilesystemSupport.retrieveAndSetLocalFileSystem(chromatogramViewer);
	}

	public File getCurrentDirectory() {

		File result = null;
		ISelection selection = chromatogramViewer.getSelection();
		IStructuredSelection structuredSelection = (IStructuredSelection)selection;
		for(Object element : structuredSelection.toList()) {
			File file = new File(element.toString());
			if(file.isFile())
				result = file.getParentFile();
			else
				result = file;
		}
		return result;
	}

	/**
	 * The given directory will be expanded if available.
	 * 
	 * @param directoryPath
	 */
	public void expandTree(String directoryPath) {

		if(directoryPath != null) {
			try {
				File elementOrTreePath = new File(directoryPath);
				chromatogramViewer.expandToLevel(elementOrTreePath, 1);
			} catch(Exception e) {
				logger.warn(e.getLocalizedMessage(), e);
			}
		}
	}

	public IChromatogramWizardElements getChromatogramWizardElements() {

		return chromatogramWizardElements;
	}

	public ChromatogramInputEntriesUI setChromatogramWizardElements(IChromatogramWizardElements chromatogramWizardElements) {

		this.chromatogramWizardElements = chromatogramWizardElements;
		return this;
	}
}
