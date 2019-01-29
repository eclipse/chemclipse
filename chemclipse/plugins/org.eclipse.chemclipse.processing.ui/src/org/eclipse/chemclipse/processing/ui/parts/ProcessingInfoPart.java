/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Janos Binder - cleanup
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.swt.ProcessingInfoUI;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class ProcessingInfoPart implements EventHandler {

	public static String ID = "org.eclipse.chemclipse.processing.ui.parts.ProcessingInfoPart";
	//
	private static final String POPUP_MENU_ID = "#PopUpMenu"; // $NON-NLS-1$
	private static final String POPUP_MENU_POSTFIX = "PopUpMenu"; // $NON-NLS-1$
	//
	@Inject
	private Composite parent;
	@Inject
	private EPartService partService;
	@Inject
	private MPart part;
	/*
	 * The info is static to display it on focus.
	 */
	private static IProcessingInfo processingInfo;
	private ProcessingInfoUI processingInfoUI;

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		processingInfoUI = new ProcessingInfoUI(parent, SWT.NONE);
		TableViewer tableViewer = processingInfoUI.getTableViewer();
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
					processingInfoUI.copyToClipboard();
				}
			}
		});
		initContextMenu();
	}

	@PreDestroy
	private void preDestroy() {

	}

	@Focus
	public void setFocus() {

		update(getProcessingInfo());
	}

	public void update(IProcessingInfo processingInfo) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(processingInfo)) {
			processingInfoUI.update(processingInfo);
			processingInfoUI.setFocus();
		}
	}

	public IProcessingInfo getProcessingInfo() {

		return processingInfo;
	}

	public void setProcessingInfo(IProcessingInfo processingInfoNew) {

		processingInfo = processingInfoNew;
	}

	public boolean doUpdate(IProcessingInfo processingInfo) {

		if(isPartVisible() && processingInfo != null) {
			return true;
		}
		return false;
	}

	public boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	/*
	 * Initialize a context menu.
	 */
	private void initContextMenu() {

		MenuManager menuManager = new MenuManager(POPUP_MENU_ID, getClass().getName() + POPUP_MENU_POSTFIX);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						processingInfoUI.copyToClipboard();
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		TableViewer tableViewer = processingInfoUI.getTableViewer();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}

	@Override
	public void handleEvent(Event event) {

		Object object = event.getProperty(IChemClipseEvents.PROPERTY_PROCESSING_INFO);
		if(object instanceof IProcessingInfo) {
			processingInfo = (IProcessingInfo)object;
			update(processingInfo);
		}
	}
}
