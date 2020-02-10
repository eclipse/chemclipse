/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import java.util.Collection;
import java.util.Set;

import org.eclipse.chemclipse.model.core.AbstractPeak;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ClassifierCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class PeakScanListEditingSupport extends EditingSupport {

	private CellEditor cellEditor;
	private final ExtendedTableViewer tableViewer;
	private final String column;

	public PeakScanListEditingSupport(ExtendedTableViewer tableViewer, String column) {
		super(tableViewer);
		this.column = column;
		if(PeakScanListLabelProvider.CLASSIFIER.equals(column)) {
			this.cellEditor = new ClassifierCellEditor(tableViewer);
		} else if(PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS.equals(column)) {
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

		if(column == PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS) {
			return (element instanceof IPeak);
		} else if(PeakScanListLabelProvider.NAME.equals(column)) {
			return element instanceof AbstractPeak && tableViewer.isEditEnabled();
		} else {
			return tableViewer.isEditEnabled();
		}
	}

	@Override
	protected Object getValue(Object element) {

		if(element instanceof IPeak) {
			IPeak peak = (IPeak)element;
			switch(column) {
				case PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS:
					return peak.isActiveForAnalysis();
				case PeakScanListLabelProvider.CLASSIFIER:
					return peak;
				case PeakScanListLabelProvider.NAME:
					String name = peak.getName();
					if(name == null) {
						return "";
					}
					return name;
			}
		}
		return false;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IPeak) {
			IPeak peak = (IPeak)element;
			switch(column) {
				case PeakScanListLabelProvider.ACTIVE_FOR_ANALYSIS:
					peak.setActiveForAnalysis((boolean)value);
					break;
				case PeakScanListLabelProvider.NAME:
					if(peak instanceof AbstractPeak) {
						AbstractPeak abstractPeak = (AbstractPeak)peak;
						String name = (String)value;
						if(name != null && name.equals(abstractPeak.getName())) {
							return;
						}
						abstractPeak.setName(name);
					}
					break;
				case PeakScanListLabelProvider.CLASSIFIER:
					if(value instanceof Set<?>) {
						Set<?> set = (Set<?>)value;
						Collection<String> classifier = peak.getClassifier();
						// delete all removed ones
						for(String oldCLassifier : classifier.toArray(new String[0])) {
							if(!set.contains(oldCLassifier)) {
								peak.removeClassifier(oldCLassifier);
							}
						}
						// add new ones
						for(Object object : set) {
							if(!classifier.contains(object)) {
								peak.addClassifier(object.toString());
							}
						}
					}
			}
			tableViewer.refresh(element);
		}
	}
}
