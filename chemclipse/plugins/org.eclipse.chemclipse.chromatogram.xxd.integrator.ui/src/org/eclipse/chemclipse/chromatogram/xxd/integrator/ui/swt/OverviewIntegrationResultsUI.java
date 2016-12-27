/*******************************************************************************
 * Copyright (c) 2011, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;

public class OverviewIntegrationResultsUI extends Composite {

	public static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.swt.overviewIntegrationResultsUI.popup";
	private List list;
	private Clipboard clipboard;

	public OverviewIntegrationResultsUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void update(double chromatogramArea, double backgroundArea, double peakArea) {

		list.removeAll();
		list.add("Chromatogram Area: " + chromatogramArea);
		list.add("Background Area: " + backgroundArea);
		list.add("Peak Area: " + peakArea);
	}

	private void initialize() {

		clipboard = new Clipboard(Display.getDefault());
		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		GridData gridData;
		list = new List(composite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		list.add("There are no integration results available.");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		list.setLayoutData(gridData);
		list.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					copyToClipboard();
				}
			}
		});
		initContextMenu();
	}

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
						copyToClipboard();
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		Menu menu = menuManager.createContextMenu(list);
		list.setMenu(menu);
	}

	private void copyToClipboard() {

		/*
		 * Copy the whole selection.
		 */
		StringBuilder builder = new StringBuilder();
		for(String selection : list.getSelection()) {
			builder.append(selection);
			builder.append(OperatingSystemUtils.getLineDelimiter());
		}
		/*
		 * Transfer the selected text (items) to the clipboard.
		 */
		TextTransfer textTransfer = TextTransfer.getInstance();
		Object[] data = new Object[]{builder.toString()};
		Transfer[] dataTypes = new Transfer[]{textTransfer};
		clipboard.setContents(data, dataTypes);
	}
}
