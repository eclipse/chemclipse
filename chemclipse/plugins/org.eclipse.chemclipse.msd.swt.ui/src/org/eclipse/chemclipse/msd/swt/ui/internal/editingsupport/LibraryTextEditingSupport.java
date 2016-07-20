/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.editingsupport;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class LibraryTextEditingSupport extends EditingSupport {

	private TextCellEditor cellEditor;
	private TableViewer tableViewer;
	private String columnLabel;
	//
	private DecimalFormat decimalFormat;

	public LibraryTextEditingSupport(TableViewer tableViewer, String columnLabel) {
		super(tableViewer);
		//
		this.tableViewer = tableViewer;
		this.columnLabel = columnLabel;
		this.decimalFormat = ValueFormat.getDecimalFormatEnglish();
		//
		cellEditor = new TextCellEditor(tableViewer.getTable());
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

		Object object = null;
		if(element instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)element;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			//
			switch(columnLabel) {
				case "Name":
					object = libraryInformation.getName();
					break;
				case "Retention Time":
					object = decimalFormat.format(libraryMassSpectrum.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case "Retention Index":
					object = decimalFormat.format(libraryMassSpectrum.getRetentionIndex());
					break;
				case "CAS":
					object = libraryInformation.getCasNumber();
					break;
				case "Formula":
					object = libraryInformation.getFormula();
					break;
				case "SMILES":
					object = libraryInformation.getSmiles();
					break;
				case "InChI":
					object = libraryInformation.getInChI();
					break;
				case "Reference Identifier":
					object = libraryInformation.getReferenceIdentifier();
					break;
				case "Comments":
					object = libraryInformation.getComments();
					break;
			}
		}
		return object;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)element;
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			//
			switch(columnLabel) {
				case "Name":
					libraryInformation.setName(value.toString());
					break;
				case "Retention Time":
					libraryMassSpectrum.setRetentionTime((int)(Double.parseDouble(value.toString()) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					break;
				case "Retention Index":
					libraryMassSpectrum.setRetentionIndex(Float.parseFloat(value.toString()));
					break;
				case "CAS":
					libraryInformation.setCasNumber(value.toString());
					break;
				case "Formula":
					libraryInformation.setFormula(value.toString());
					break;
				case "SMILES":
					libraryInformation.setSmiles(value.toString());
					break;
				case "InChI":
					libraryInformation.setInChI(value.toString());
					break;
				case "Reference Identifier":
					libraryInformation.setReferenceIdentifier(value.toString());
					break;
				case "Comments":
					libraryInformation.setComments(value.toString());
					break;
			}
			tableViewer.refresh();
		}
	}
}
