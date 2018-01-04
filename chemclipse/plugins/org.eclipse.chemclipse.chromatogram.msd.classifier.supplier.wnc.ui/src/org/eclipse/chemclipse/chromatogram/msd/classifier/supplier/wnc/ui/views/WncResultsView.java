/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result.IChromatogramResultWNC;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider.WncResultsContentProvider;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider.WncResultsLabelProvider;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.internal.provider.WncResultsTableComparator;
import org.eclipse.chemclipse.model.core.IChromatogramResult;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.msd.ui.views.AbstractChromatogramSelectionMSDView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class WncResultsView extends AbstractChromatogramSelectionMSDView {

	private static final String POPUP_MENU_ID = "org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.ui.views.wncResultsView.popup";
	@Inject
	private Composite parent;
	private ExtendedTableViewer tableViewer;
	private WncResultsTableComparator wncResultsTableComparator;
	private String[] titles = {"Name", "Ion", "Percentage Sum Intensity [%]", "Percentage Max Intensity [%]"};
	private int bounds[] = {100, 100, 100, 100};

	@Inject
	public WncResultsView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new WncResultsContentProvider());
		tableViewer.setLabelProvider(new WncResultsLabelProvider());
		/*
		 * Sorting the table.
		 */
		wncResultsTableComparator = new WncResultsTableComparator();
		tableViewer.setComparator(wncResultsTableComparator);
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
			Object object = chromatogramSelection.getChromatogram().getChromatogramResult(IChromatogramResultWNC.IDENTIFIER);
			if(object instanceof IChromatogramResult) {
				IChromatogramResult chromatogramResult = (IChromatogramResult)object;
				Object result = chromatogramResult.getResult();
				if(result instanceof IWncIons) {
					IWncIons wncIons = (IWncIons)result;
					update(wncIons, forceReload);
				}
			}
		}
	}

	public void update(IWncIons wncIons, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(isPartVisible() && wncIons != null) {
			tableViewer.setInput(wncIons);
		}
	}
}
