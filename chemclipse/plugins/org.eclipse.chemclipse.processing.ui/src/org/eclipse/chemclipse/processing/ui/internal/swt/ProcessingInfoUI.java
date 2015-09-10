/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 * Janos Binder - cleanup
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.internal.swt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.internal.provider.ProcessingInfoContentProvider;
import org.eclipse.chemclipse.processing.ui.internal.provider.ProcessingInfoLabelProvider;
import org.eclipse.chemclipse.processing.ui.internal.provider.ProcessingInfoTableComparator;
import org.eclipse.chemclipse.support.ui.swt.viewers.ExtendedTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class ProcessingInfoUI {

	private ExtendedTableViewer tableViewer;
	private String[] titles = {"Type", "Description", "Message", "Date"};
	private int bounds[] = {100, 100, 100, 100};
	private IProcessingInfo processingInfo;

	public ProcessingInfoUI(Composite parent, int style) {

		parent.setLayout(new FillLayout());
		Map<Long, String> substances = new HashMap<Long, String>();
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		setTableProvider(substances);
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					tableViewer.copyToClipboard(titles);
				}
			}
		});
	}

	public void setFocus() {

		tableViewer.getControl().setFocus();
		if(processingInfo != null) {
			tableViewer.setInput(processingInfo);
		}
	}

	public void update(IProcessingInfo processingInfo) {

		this.processingInfo = processingInfo;
	}

	public ExtendedTableViewer getTableViewer() {

		return tableViewer;
	}

	public String[] getTitles() {

		return titles;
	}

	// -----------------------------------------private methods
	private void setTableProvider(Map<Long, String> substances) {

		/*
		 * Set the provider.
		 */
		tableViewer.setContentProvider(new ProcessingInfoContentProvider());
		tableViewer.setLabelProvider(new ProcessingInfoLabelProvider());
		tableViewer.setComparator(new ProcessingInfoTableComparator());
	}
}
