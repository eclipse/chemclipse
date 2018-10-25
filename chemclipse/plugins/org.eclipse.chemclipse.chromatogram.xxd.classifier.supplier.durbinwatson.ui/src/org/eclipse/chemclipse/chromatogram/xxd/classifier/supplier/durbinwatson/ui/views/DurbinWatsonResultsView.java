/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IChromatogramResultDurbinWatson;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.result.IDurbinWatsonClassifierResult;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider.ClassifierResultContentProvider;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider.ClassifierResultLabelProvider;
import org.eclipse.chemclipse.chromatogram.xxd.classifier.supplier.durbinwatson.ui.internal.provider.ClassifierResultTableComparator;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
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

public class DurbinWatsonResultsView extends AbstractChromatogramSelectionMSDView {

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
			Object object = chromatogramSelection.getChromatogram().getMeasurementResult(IChromatogramResultDurbinWatson.IDENTIFIER);
			if(object instanceof IMeasurementResult) {
				IMeasurementResult chromatogramResult = (IMeasurementResult)object;
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
}
