/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.identification;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.SynonymsListContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.SynonymsListLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.provider.SynonymsListTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;

public class SynonymsListUI {

	private ExtendedTableViewer tableViewer;
	private SynonymsListTableComparator synonymListTableComparator;
	private String[] titles = {"Synonym"};
	private int bounds[] = {300};

	public SynonymsListUI(Composite parent, int style) {
		parent.setLayout(new FillLayout());
		/*
		 * E.g. Scan
		 */
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new SynonymsListContentProvider());
		tableViewer.setLabelProvider(new SynonymsListLabelProvider());
		/*
		 * Sorting the table.
		 */
		synonymListTableComparator = new SynonymsListTableComparator();
		tableViewer.setComparator(synonymListTableComparator);
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
	}

	public void update(ILibraryInformation libraryInformation, boolean forceReload) {

		if(libraryInformation != null) {
			tableViewer.setInput(libraryInformation.getSynonyms());
		}
	}

	public void clear() {

		tableViewer.setInput(null);
	}

	public ExtendedTableViewer getTableViewer() {

		return tableViewer;
	}

	public String[] getTitles() {

		return titles;
	}
}
