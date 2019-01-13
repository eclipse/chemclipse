/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class EigenvaluesCovarianceMatrixTable {

	private NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	private Table table;
	private String[] tableColumns = new String[]{"Num", "Eigenvalue", "Proportion (%)", "Comulative (%)"};

	public EigenvaluesCovarianceMatrixTable(Composite parent, Object layoutData) {
		table = new Table(parent, SWT.BORDER);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(layoutData);
		for(int i = 0; i < tableColumns.length; i++) {
			TableColumn tableColumn = new TableColumn(table, SWT.None);
			tableColumn.setText(tableColumns[i]);
		}
	}

	public void update(ISamples samples, double minProportion) {

		update(samples, Integer.MAX_VALUE, minProportion);
	}

	public void update(ISamples samples, int maxEigenvalues, double minProportion) {

		table.clearAll();
		table.removeAll();
		double[] eigenvalues = PcaUtils.getEigenValuesCovarianceMatrix(samples);
		double sum = Arrays.stream(eigenvalues).sum();
		double comul = 0;
		for(int i = 0; i < eigenvalues.length && i < maxEigenvalues; i++) {
			double eig = eigenvalues[i];
			double prop = eig / sum;
			comul = prop + comul;
			if(!(prop >= minProportion)) {
				break;
			}
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(0, Integer.toString(i + 1));
			tableItem.setText(1, Double.toString(eig));
			tableItem.setText(2, nf.format(prop * 100));
			tableItem.setText(3, nf.format(comul * 100));
		}
		TableColumn[] columns = table.getColumns();
		for(int i = 0; i < columns.length; i++) {
			columns[i].pack();
		}
	}
}
