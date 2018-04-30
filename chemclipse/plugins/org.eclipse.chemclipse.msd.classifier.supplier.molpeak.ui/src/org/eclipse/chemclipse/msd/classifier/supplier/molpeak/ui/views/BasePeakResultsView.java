/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.views;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.model.ILigninRatios;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.results.IChromatogramResultBasePeak;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.internal.provider.BasePeakResultsContentProvider;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.internal.provider.BasePeakResultsLabelProvider;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.ui.internal.provider.BasePeakResultsTableComparator;
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

public class BasePeakResultsView extends AbstractChromatogramSelectionMSDView {

	@Inject
	private Composite parent;
	private ExtendedTableViewer tableViewer;
	private String[] titles = {"Type", "Precentage [%]", "Note"};
	private int bounds[] = {100, 100, 100};

	@Inject
	public BasePeakResultsView(EPartService partService, MPart part, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		tableViewer = new ExtendedTableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new BasePeakResultsContentProvider());
		tableViewer.setLabelProvider(new BasePeakResultsLabelProvider());
		/*
		 * Sorting the table.
		 */
		BasePeakResultsTableComparator wncResultsTableComparator = new BasePeakResultsTableComparator();
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
			Object object = chromatogramSelection.getChromatogram().getMeasurementResult(IChromatogramResultBasePeak.IDENTIFIER);
			if(object instanceof IMeasurementResult) {
				IMeasurementResult chromatogramResult = (IMeasurementResult)object;
				Object result = chromatogramResult.getResult();
				if(result instanceof ILigninRatios) {
					ILigninRatios ligninRatios = (ILigninRatios)result;
					update(ligninRatios, forceReload);
				}
			}
		}
	}

	public void update(ILigninRatios ligninRatios, boolean forceReload) {

		/*
		 * Update the ui only if the actual view part is visible and the
		 * selection is not null.
		 */
		if(isPartVisible() && ligninRatios != null) {
			tableViewer.setInput(ligninRatios);
		}
	}
}
