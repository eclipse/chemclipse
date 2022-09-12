/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class SamplesEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private final ExtendedTableViewer tableViewer;
	private final String column;

	public SamplesEditingSupport(ExtendedTableViewer tableViewer, String column) {

		super(tableViewer);
		this.column = column;
		if(SamplesLabelProvider.USE.equals(column)) {
			this.cellEditor = new CheckboxCellEditor(tableViewer.getTable());
		} else if(SamplesLabelProvider.COLOR.equals(column)) {
			this.cellEditor = new ColorCellEditor(tableViewer.getTable());
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

		return true;
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof ISample sample) {
			switch(column) {
				case SamplesLabelProvider.USE:
					return sample.isSelected();
				case SamplesLabelProvider.COLOR:
					return Colors.getColor(sample.getRGB()).getRGB();
				case SamplesLabelProvider.GROUP_NAME:
					return sample.getGroupName() != null ? sample.getGroupName() : "";
				case SamplesLabelProvider.CLASSIFICATION:
					return sample.getClassification() != null ? sample.getClassification() : "";
				case SamplesLabelProvider.DESCRIPTION:
					return sample.getDescription() != null ? sample.getDescription() : "";
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof ISample sample) {
			switch(column) {
				case SamplesLabelProvider.USE:
					sample.setSelected((boolean)value);
					break;
				case SamplesLabelProvider.COLOR:
					if(value instanceof RGB rgb) {
						Color color = Colors.getColor(rgb);
						String colorRGB = Colors.getColor(color);
						sample.setRGB(colorRGB);
					}
					break;
				case SamplesLabelProvider.GROUP_NAME:
					sample.setGroupName((String)value);
					break;
				case SamplesLabelProvider.CLASSIFICATION:
					sample.setClassification((String)value);
					break;
				case SamplesLabelProvider.DESCRIPTION:
					sample.setDescription((String)value);
					break;
			}
			tableViewer.refresh(element);
		}
	}
}