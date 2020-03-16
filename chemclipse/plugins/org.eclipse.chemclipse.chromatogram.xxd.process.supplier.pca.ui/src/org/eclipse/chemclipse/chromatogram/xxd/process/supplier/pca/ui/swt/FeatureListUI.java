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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.FeatureDataMatrix;
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
		createColumnsDefault();
	}

	public void setInput(EvaluationPCA evaluationPCA) {

		boolean createDefault = true;
		if(evaluationPCA != null) {
			ISamplesPCA<? extends IVariable, ? extends ISample> samples = evaluationPCA.getSamples();
			if(samples != null) {
				createDefault = false;
				FeatureDataMatrix featureDataMatrix = new FeatureDataMatrix(samples);
				createColumnsSpecific(featureDataMatrix);
				super.setInput(featureDataMatrix.getFeatures());
			}
		}
		//
		if(createDefault) {
			super.setInput(null);
			createColumnsDefault();
		}
	}

	private void createColumnsDefault() {

		createColumns(TITLES, BOUNDS, labelProvider, comparator);
	}

	private void createColumnsSpecific(FeatureDataMatrix featureDataMatrix) {

		/*
		 * Labels and bounds
		 */
		String variableName = featureDataMatrix.getVariableName();
		List<String> sampleNames = featureDataMatrix.getSampleNames();
		//
		List<String> titleList = new ArrayList<>();
		List<Integer> boundList = new ArrayList<>();
		/*
		 * Standards Labels
		 */
		for(int i = 0; i < TITLES.length; i++) {
			/*
			 * Replace the variable name.
			 */
			String title = TITLES[i];
			if(FeatureLabelProvider.VARIABLE.equals(title)) {
				title = variableName;
			}
			//
			titleList.add(title);
			boundList.add(BOUNDS[i]);
		}
		/*
		 * Sample labels
		 */
		for(String sampleName : sampleNames) {
			titleList.add(sampleName);
			boundList.add(FeatureLabelProvider.BOUND_SAMPLE);
		}
		//
		String[] titles = titleList.toArray(new String[titleList.size()]);
		int size = boundList.size();
		int[] bounds = new int[size];
		for(int i = 0; i < size; i++) {
			bounds[i] = boundList.get(i);
		}
		//
		super.setInput(null);
		createColumns(titles, bounds, labelProvider, comparator);
	}

	private void createColumns(String[] titles, int[] bounds, ITableLabelProvider labelProvider, ViewerComparator comparator) {

		createColumns(titles, bounds);
		setLabelProvider(labelProvider);
		setContentProvider(new ListContentProvider());
		setComparator(comparator);
	}
}
