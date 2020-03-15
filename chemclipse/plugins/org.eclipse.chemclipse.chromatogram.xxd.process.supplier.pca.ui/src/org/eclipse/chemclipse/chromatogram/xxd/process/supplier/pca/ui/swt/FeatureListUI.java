/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.FeatureComparator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider.FeatureLabelProvider;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class FeatureListUI extends ExtendedTableViewer {

	private static final String[] TITLES = FeatureLabelProvider.TITLES;
	private static final int[] BOUNDS = FeatureLabelProvider.BOUNDS;
	private final ITableLabelProvider labelProvider = new FeatureLabelProvider();
	private final ViewerComparator comparator = new FeatureComparator();

	public FeatureListUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		if(evaluationPCA != null) {
			ISamplesPCA<? extends IVariable, ? extends ISample> samples = evaluationPCA.getSamples();
			if(samples != null) {
				// TODO create Feature Matrix
				super.setInput(samples.getSampleList());
			}
		} else {
			super.setInput(null);
		}
	}

	private void createColumns() {

		createColumns(TITLES, BOUNDS);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
	}
}
