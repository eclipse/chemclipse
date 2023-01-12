/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.FeatureDataMatrix;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.FeatureComparator;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.FeatureEditingSupport;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.FeatureLabelProvider;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider.FeatureListFilter;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;

public class FeatureListUI extends ExtendedTableViewer {

	private static final String[] TITLES = FeatureLabelProvider.TITLES;
	private static final int[] BOUNDS = FeatureLabelProvider.BOUNDS;
	//
	private final ITableLabelProvider labelProvider = new FeatureLabelProvider();
	private final ViewerComparator comparator = new FeatureComparator();
	private final FeatureListFilter listFilter = new FeatureListFilter();
	//
	private IUpdateListener updateListener = null;

	public FeatureListUI(Composite parent, int style) {

		super(parent, style);
		setContentProviders();
		createColumnsDefault();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	/**
	 * Fires and update if the listener is != null.
	 */
	public void updateContent() {

		if(updateListener != null) {
			updateListener.update();
		}
	}

	public void clear() {

		setInput(null);
	}

	public void setSearchText(String searchText, boolean caseSensitive) {

		listFilter.setSearchText(searchText, caseSensitive);
		refresh();
	}

	public void setInput(FeatureDataMatrix featureDataMatrix) {

		if(featureDataMatrix != null) {
			createColumnsSpecific(featureDataMatrix);
			super.setInput(featureDataMatrix.getFeatures());
		} else {
			super.setInput(null);
			createColumnsDefault();
		}
	}

	private void createColumnsDefault() {

		createColumns(TITLES, BOUNDS, labelProvider, comparator);
	}

	private void setContentProviders() {

		setContentProvider(new ListContentProvider());
		setUseHashlookup(true);
		setComparator(null);
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
		setFilters(listFilter);
		setEditingSupport();
	}

	private void setEditingSupport() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(isEditable(label)) {
				tableViewerColumn.setEditingSupport(new FeatureEditingSupport(this, label));
			}
		}
	}

	private boolean isEditable(String label) {

		if(FeatureLabelProvider.USE.equals(label)) {
			return true;
		} else if(FeatureLabelProvider.CLASSIFICATION.equals(label)) {
			return true;
		} else if(FeatureLabelProvider.DESCRIPTION.equals(label)) {
			return true;
		}
		return false;
	}
}
