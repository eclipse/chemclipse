/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IPeakIntegrationResults;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider.PeakIntegrationResultsContentProvider;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider.PeakIntegrationResultsLabelProvider;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.internal.provider.PeakIntegrationResultsTableComparator;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;

public class PeakIntegrationResultsUI extends Composite {

	public static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt.peakIntegrationResultsListUI.popup";
	private ExtendedTableViewer tableViewer;
	private PeakIntegrationResultsTableComparator integrationResultTableComparator;
	private String[] titles = {"Start RT (minutes)", "Stop RT", "Integrated Area", "ion (TIC = 0)"};
	private int bounds[] = {150, 100, 100, 100};

	public PeakIntegrationResultsUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void update(IPeakIntegrationResults peakIntegrationResults) {

		tableViewer.setInput(peakIntegrationResults);
	}

	// -----------------------------------------private methods
	/**
	 * Initializes the widget.
	 */
	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		GridData gridData;
		// ------------------------------------------------------------------------------------------TabelViewer
		tableViewer = new ExtendedTableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		tableViewer.getTable().setLayoutData(gridData);
		tableViewer.setContentProvider(new PeakIntegrationResultsContentProvider());
		tableViewer.setLabelProvider(new PeakIntegrationResultsLabelProvider());
		tableViewer.getTable().addKeyListener(new KeyListener() {

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

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		/*
		 * Sorting the table.
		 */
		integrationResultTableComparator = new PeakIntegrationResultsTableComparator();
		tableViewer.setComparator(integrationResultTableComparator);
		initContextMenu();
	}

	/*
	 * Initialize a context menu.
	 */
	private void initContextMenu() {

		MenuManager menuManager = new MenuManager("#PopUpMenu", POPUP_MENU_ID);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						tableViewer.copyToClipboard(titles);
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}
}
