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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

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
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumIonsListUI;
import org.eclipse.chemclipse.support.ui.swt.viewers.ExtendedTableViewer;

public class PeakMassSpectrumIonsListView extends AbstractChromatogramSelectionMSDView {

	private static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.msd.ui.perspective.views.peakMassSpectrumIonsListView.popup";
	@Inject
	private Composite parent;
	private MassSpectrumIonsListUI massSpectrumIonsListUI;

	@Inject
	public PeakMassSpectrumIonsListView(EPartService partService, MPart part, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		massSpectrumIonsListUI = new MassSpectrumIonsListUI(parent, SWT.NONE);
		final ExtendedTableViewer tableViewer = massSpectrumIonsListUI.getTableViewer();
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
					tableViewer.copyToClipboard(massSpectrumIonsListUI.getTitles());
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

		massSpectrumIonsListUI.getTableViewer().getControl().setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			IScanMSD massSpectrum = chromatogramSelection.getSelectedPeak().getPeakModel().getPeakMassSpectrum();
			massSpectrumIonsListUI.update(massSpectrum, forceReload);
		}
	}

	@Override
	public boolean doUpdate(IChromatogramSelection chromatogramSelection) {

		if(super.doUpdate(chromatogramSelection)) {
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				return (((IChromatogramSelectionMSD)chromatogramSelection).getSelectedScan() != null) ? true : false;
			}
		}
		return false;
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
						massSpectrumIonsListUI.getTableViewer().copyToClipboard(massSpectrumIonsListUI.getTitles());
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		TableViewer tableViewer = massSpectrumIonsListUI.getTableViewer();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}
}
