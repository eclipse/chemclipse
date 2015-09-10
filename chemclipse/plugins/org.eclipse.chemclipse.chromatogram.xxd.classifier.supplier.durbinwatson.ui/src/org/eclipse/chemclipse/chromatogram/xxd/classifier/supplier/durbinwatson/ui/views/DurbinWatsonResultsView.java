/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.views;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IChromatogramResultDurbinWatson;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IDurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider.ClassifierResultContentProvider;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider.ClassifierResultLabelProvider;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider.ClassifierResultTableComparator;
import org.eclipse.chemclipse.model.core.IChromatogramResult;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.ui.swt.viewers.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.msd.ui.views.AbstractChromatogramSelectionMSDView;

public class DurbinWatsonResultsView extends AbstractChromatogramSelectionMSDView {

	private static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.views.durbinWatsonResultsView.popup";
	@Inject
	private Composite parent;
	private ExtendedTableViewer tableViewer;
	private ClassifierResultTableComparator classifierResultTableComparator;
	private String[] titles = {"Rating", "Derivative", "Order", "Width"};
	private int bounds[] = {100, 100, 100, 100};

	@Inject
	public DurbinWatsonResultsView(EPartService partService, MPart part, IEventBroker eventBroker) {

		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new ClassifierResultContentProvider());
		tableViewer.setLabelProvider(new ClassifierResultLabelProvider());
		/*
		 * Sorting the table.
		 */
		classifierResultTableComparator = new ClassifierResultTableComparator();
		tableViewer.setComparator(classifierResultTableComparator);
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
					tableViewer.copyToClipboard(titles);
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

		tableViewer.getControl().setFocus();
		update(getChromatogramSelection(), false);
	}

	@Override
	public void update(IChromatogramSelectionMSD chromatogramSelection, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(doUpdate(chromatogramSelection)) {
			Object object = chromatogramSelection.getChromatogram().getChromatogramResult(IChromatogramResultDurbinWatson.IDENTIFIER);
			if(object instanceof IChromatogramResult) {
				IChromatogramResult chromatogramResult = (IChromatogramResult)object;
				Object result = chromatogramResult.getResult();
				if(result instanceof IDurbinWatsonClassifierResult) {
					update((IDurbinWatsonClassifierResult)result, forceReload);
				}
			}
		}
	}

	public void update(IDurbinWatsonClassifierResult durbinWatsonClassifierResult, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(isPartVisible() && durbinWatsonClassifierResult != null) {
			tableViewer.setInput(durbinWatsonClassifierResult);
		}
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
