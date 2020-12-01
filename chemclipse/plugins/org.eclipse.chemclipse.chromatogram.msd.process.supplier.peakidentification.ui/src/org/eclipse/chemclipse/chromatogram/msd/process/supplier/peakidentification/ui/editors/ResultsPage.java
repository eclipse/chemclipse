/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.ui.editors;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model.IPeakIdentificationBatchJob;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.peak.PeakListUI;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ResultsPage implements IMultiEditorPage {

	private FormToolkit toolkit;
	private int pageIndex;
	@SuppressWarnings("unused")
	private IPeakIdentificationBatchJob peakIdentificationBatchJob;
	private PeakListUI peakListUI;
	private SelectionUpdateListener selectionUpdateListener;
	private BatchProcessEditor editorPart;

	public ResultsPage(BatchProcessEditor editorPart, Composite container) {

		createPage(editorPart, container);
		this.editorPart = editorPart;
		selectionUpdateListener = new ResultsPage.SelectionUpdateListener();
		selectionUpdateListener.setParent(this);
	}

	@Override
	public void setFocus() {

		IPeaks<?> peaks = selectionUpdateListener.getPeaks();
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

	public void update(IPeaks<?> peaks, boolean forceReload) {

		if(editorPart.getActivePage() == getPageIndex() && peaks != null) {
			peakListUI.update(peaks, forceReload);
		}
	}

	public void clear() {

		peakListUI.clear();
	}

	private void createPage(BatchProcessEditor editorPart, Composite container) {

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
					//
					if(element instanceof IPeakMSD) {
						IPeakMSD peakMSD = (IPeakMSD)element;
						UpdateNotifierUI.update(Display.getDefault(), peakMSD);
					}
				}
			}
		});
	}

	public static class SelectionUpdateListener {

		private static ResultsPage parentWidget;
		private static IPeaks<?> evaluatedPeaks = null;

		public void setParent(IMultiEditorPage parent) {

			if(parent instanceof ResultsPage) {
				parentWidget = (ResultsPage)parent;
			}
		}

		public void update(IPeaks<?> peaks, boolean forceReload) {

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

		public IPeaks<?> getPeaks() {

			return evaluatedPeaks;
		}
	}
}