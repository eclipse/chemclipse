/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.swt;

import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantSubstance;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.internal.editingsupport.SampleQuantCheckBoxEditingSupport;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.internal.provider.SampleQuantLabelProvider;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.internal.provider.SampleQuantTableComparator;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;

public class SampleQuantTableViewerUI extends ExtendedTableViewer {

	public static final int INDEX_TYPE = 7;
	public static final int INDEX_MIN_MATCH_QUALITY = 8;
	public static final int INDEX_MATCH_QUALITY = 9;
	public static final int INDEX_OK = 10;
	//
	private String[] titles = {"ID", "CAS#", "Name", "Max Scan", "Concentration", "Unit", "Misc", "Type", "Min Match Quality", "Match Quality", "OK"};
	private int[] bounds = {60, 110, 150, 50, 50, 50, 100, 30, 50, 50, 30};
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public SampleQuantTableViewerUI(Composite parent) {
		super(parent);
		createColumns();
	}

	public SampleQuantTableViewerUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new SampleQuantLabelProvider());
		setContentProvider(new ListContentProvider());
		setComparator(new SampleQuantTableComparator());
		setCellColorProvider();
		setEditingSupport();
	}

	private void setCellColorProvider() {

		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		TableViewerColumn tableViewerColumn = tableViewerColumns.get(INDEX_MATCH_QUALITY); // Match Quality
		if(tableViewerColumn != null) {
			tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {

				@Override
				public void update(ViewerCell cell) {

					if(cell != null) {
						ISampleQuantSubstance sampleQuantSubstance = (ISampleQuantSubstance)cell.getElement();
						if(!sampleQuantSubstance.getType().equals(ISampleQuantSubstance.TYPE_ISTD)) {
							double matchQuality = sampleQuantSubstance.getMatchQuality();
							cell.setText(decimalFormat.format(matchQuality));
							//
							if(!sampleQuantSubstance.getType().equals(ISampleQuantSubstance.TYPE_ISTD)) {
								if(!sampleQuantSubstance.isValidated()) {
									cell.setBackground(Colors.YELLOW);
									cell.setForeground(Colors.BLACK);
								} else {
									cell.setBackground(Colors.GREEN);
									cell.setForeground(Colors.BLACK);
								}
							}
						}
						super.update(cell);
					}
				}
			});
		}
	}

	private void setEditingSupport() {

		TableViewerColumn tableViewerColumn;
		List<TableViewerColumn> tableViewerColumns = getTableViewerColumns();
		//
		tableViewerColumn = tableViewerColumns.get(INDEX_OK); // Check/Uncheck
		tableViewerColumn.setEditingSupport(new SampleQuantCheckBoxEditingSupport(this));
	}
}
