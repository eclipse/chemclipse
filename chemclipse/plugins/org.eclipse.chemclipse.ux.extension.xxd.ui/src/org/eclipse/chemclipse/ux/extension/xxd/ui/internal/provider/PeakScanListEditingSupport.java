/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for name editing, improve classifier support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.targets.TargetSupport;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class PeakScanListEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private final ExtendedTableViewer tableViewer;
	private final String column;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
	private DecimalFormat integerFormat = ValueFormat.getDecimalFormatEnglish("0");

	public PeakScanListEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS.equals(column)) {
			this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
		} else {
			this.cellEditor = new TextCellEditor(tableViewer.getTable());
		}
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {

		if(column.equals(PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS)) {
			return (element instanceof IPeak);
		} else {
			return tableViewer.isEditEnabled();
		}
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof IPeak peak) {
			IPeakModel peakModel = peak.getPeakModel();
			switch(column) {
				case PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS:
					return peak.isActiveForAnalysis();
				case PeakScanListLabelProvider.RETENTION_TIME:
					return decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				case PeakScanListLabelProvider.RELATIVE_RETENTION_TIME:
					return decimalFormat.format(peakModel.getPeakMaximum().getRelativeRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				case PeakScanListLabelProvider.RETENTION_INDEX:
					if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
						return integerFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
					} else {
						return decimalFormat.format(peakModel.getPeakMaximum().getRetentionIndex());
					}
				case PeakScanListLabelProvider.AREA_TOTAL:
					if(PreferenceSupplier.showAreaWithoutDecimals()) {
						return integerFormat.format(peak.getIntegratedArea());
					} else {
						return decimalFormat.format(peak.getIntegratedArea());
					}
				case PeakScanListLabelProvider.START_RETENTION_TIME:
					return decimalFormat.format(peakModel.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				case PeakScanListLabelProvider.STOP_RETENTION_TIME:
					return decimalFormat.format(peakModel.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
				case PeakScanListLabelProvider.BEST_TARGET:
					return TargetSupport.getBestTargetLibraryField(peak);
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IPeak peak) {
			switch(column) {
				case PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS:
					peak.setActiveForAnalysis((boolean)value);
					break;
			}
			tableViewer.refresh(element);
		}
	}
}
