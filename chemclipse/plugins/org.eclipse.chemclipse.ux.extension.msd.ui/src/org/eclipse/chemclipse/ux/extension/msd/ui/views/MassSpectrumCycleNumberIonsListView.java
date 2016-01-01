/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumCycleNumberIonsListUI;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.e4.core.services.events.IEventBroker;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class MassSpectrumCycleNumberIonsListView extends AbstractChromatogramSelectionMSDView {

	private static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.msd.ui.perspective.views.massSpectrumCycleNumberIonsListView.popup";
	@Inject
	private Composite parent;
	private MassSpectrumCycleNumberIonsListUI massSpectrumCycleNumberIonsListUI;

	@Inject
	public MassSpectrumCycleNumberIonsListView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		massSpectrumCycleNumberIonsListUI = new MassSpectrumCycleNumberIonsListUI(parent, SWT.NONE);
		final ExtendedTableViewer tableViewer = massSpectrumCycleNumberIonsListUI.getTableViewer();
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					tableViewer.copyToClipboard(massSpectrumCycleNumberIonsListUI.getTitles());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		initContextMenu();
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Focus
	public void setFocus() {

		massSpectrumCycleNumberIonsListUI.getTableViewer().getControl().setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			massSpectrumCycleNumberIonsListUI.update(chromatogramSelection, forceReload);
		}
	}

	/*
	 * Initialize a context menu.
	 */
	private void initContextMenu() {

		MenuManager menuManager = new MenuManager("#PopUpMenu", POPUP_MENU_ID);
		menuManager.setRemoveAllWhenShown(true);
		/*
		 * Copy to clipboard
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						massSpectrumCycleNumberIonsListUI.getTableViewer().copyToClipboard(massSpectrumCycleNumberIonsListUI.getTitles());
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		TableViewer tableViewer = massSpectrumCycleNumberIonsListUI.getTableViewer();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}
}
