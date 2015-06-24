/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.notifier.PeakSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.peak.PeakListUI;
import org.eclipse.chemclipse.swt.ui.viewers.ExtendedTableViewer;

/**
 * @author Philip (eselmeister) Wenig
 * 
 */
public class PeakIdentificationResultsPage implements IMultiEditorPage {

	private FormToolkit toolkit;
	private int pageIndex;
	@SuppressWarnings("unused")
	private IPeakIdentificationBatchJob peakIdentificationBatchJob;
	private PeakListUI peakListUI;
	private SelectionUpdateListener selectionUpdateListener;
	private PeakIdentificationBatchProcessEditor editorPart;

	public PeakIdentificationResultsPage(PeakIdentificationBatchProcessEditor editorPart, Composite container) {

		createPage(editorPart, container);
		this.editorPart = editorPart;
		selectionUpdateListener = new PeakIdentificationResultsPage.SelectionUpdateListener();
		selectionUpdateListener.setParent(this);
	}

	@Override
	public void setFocus() {

		/*
		 * It could be that the focus get lost on long running operations, hence load the list on focus.
		 */
		IPeaks peaks = selectionUpdateListener.getPeaks();
		if(peaks != null) {
			update(peaks, true);
		}
	}

	@Override
	public int getPageIndex() {

		return pageIndex;
	}

	@Override
	public void dispose() {

		if(toolkit != null) {
			toolkit.dispose();
		}
	}

	@Override
	public void setPeakIdentificationBatchJob(IPeakIdentificationBatchJob peakIdentificationBatchJob) {

		if(peakIdentificationBatchJob != null) {
			this.peakIdentificationBatchJob = peakIdentificationBatchJob;
		}
	}

	public void update(IPeaks peaks, boolean forceReload) {

		if(editorPart.getActivePage() == getPageIndex() && peaks != null) {
			peakListUI.update(peaks, forceReload);
		}
	}

	public void clear() {

		peakListUI.clear();
	}

	// ---------------------------------------private methods
	/**
	 * Creates the page.
	 * 
	 */
	private void createPage(PeakIdentificationBatchProcessEditor editorPart, Composite container) {

		/*
		 * Create the parent composite.
		 */
		container.setLayout(new FillLayout());
		Composite parent = new Composite(container, SWT.NONE);
		parent.setLayout(new FillLayout());
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * Create the peak list view.
		 */
		createPeakListView(parent);
		/*
		 * Get the page index.
		 */
		pageIndex = editorPart.addPage(parent);
	}

	private void createPeakListView(Composite parent) {

		peakListUI = new PeakListUI(parent, SWT.NONE);
		final ExtendedTableViewer tableViewer = peakListUI.getTableViewer();
		/*
		 * Add a selection listener, to update peaks on click.
		 */
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				/*
				 * Is the selection a structured selection?
				 */
				ISelection selection = event.getSelection();
				if(selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection)selection;
					Object element = structuredSelection.getFirstElement();
					/*
					 * Is the element an instance of IPeakMSD?
					 */
					if(element instanceof IPeakMSD) {
						/*
						 * Update View : Mass Spectrum
						 */
						IPeakMSD peakMSD = (IPeakMSD)element;
						PeakSelectionUpdateNotifier.fireUpdateChange(peakMSD, true);
					}
				}
			}
		});
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					tableViewer.copyToClipboard(peakListUI.getTitles());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
	}

	// -----------------------------------------inner class to retrieve updates
	public static class SelectionUpdateListener {

		private static PeakIdentificationResultsPage parentWidget;
		private static IPeaks evaluatedPeaks = null;

		public void setParent(IMultiEditorPage parent) {

			if(parent instanceof PeakIdentificationResultsPage) {
				parentWidget = (PeakIdentificationResultsPage)parent;
			}
		}

		public void update(IPeaks peaks, boolean forceReload) {

			/*
			 * Set the actual chromatogram selection.
			 */
			evaluatedPeaks = peaks;
			if(parentWidget != null) {
				parentWidget.update(peaks, forceReload);
			}
		}

		public void clear() {

			if(parentWidget != null) {
				parentWidget.clear();
			}
		}

		public IPeaks getPeaks() {

			return evaluatedPeaks;
		}
	}
}