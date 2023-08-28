/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.splash.SplashFactory;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumListUI;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public class LibraryTextEditingSupport extends EditingSupport {

	private TextCellEditor cellEditor;
	private ExtendedTableViewer tableViewer;
	private String columnLabel;
	//
	private DecimalFormat decimalFormat;

	public LibraryTextEditingSupport(ExtendedTableViewer tableViewer, String columnLabel) {

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

		return tableViewer.isEditEnabled();
	}

	@Override
	protected Object getValue(Object element) {

		Object object = null;
		if(element instanceof IRegularLibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			//
			switch(columnLabel) {
				case MassSpectrumListUI.NAME:
					object = libraryInformation.getName();
					break;
				case MassSpectrumListUI.RETENTION_TIME:
					object = decimalFormat.format(libraryMassSpectrum.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case MassSpectrumListUI.RELATIVE_RETENTION_TIME:
					object = decimalFormat.format(libraryMassSpectrum.getRelativeRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case MassSpectrumListUI.RETENTION_INDEX:
					object = decimalFormat.format(libraryMassSpectrum.getRetentionIndex());
					break;
				case MassSpectrumListUI.CAS:
					object = libraryInformation.getCasNumber();
					break;
				case MassSpectrumListUI.FORMULA:
					object = libraryInformation.getFormula();
					break;
				case MassSpectrumListUI.SMILES:
					object = libraryInformation.getSmiles();
					break;
				case MassSpectrumListUI.INCHI:
					object = libraryInformation.getInChI();
					break;
				case MassSpectrumListUI.REFERENCE_IDENTIFIER:
					object = libraryInformation.getReferenceIdentifier();
					break;
				case MassSpectrumListUI.COMMENTS:
					object = libraryInformation.getComments();
					break;
				case MassSpectrumListUI.SPLASH:
					object = new SplashFactory(libraryMassSpectrum).getSplash();
					break;
			}
		}
		return object;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(element instanceof IRegularLibraryMassSpectrum libraryMassSpectrum) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			//
			switch(columnLabel) {
				case MassSpectrumListUI.NAME:
					libraryInformation.setName(value.toString());
					break;
				case MassSpectrumListUI.RETENTION_TIME:
					libraryMassSpectrum.setRetentionTime((int)(Double.parseDouble(value.toString()) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					break;
				case MassSpectrumListUI.RELATIVE_RETENTION_TIME:
					libraryMassSpectrum.setRelativeRetentionTime((int)(Double.parseDouble(value.toString()) * AbstractChromatogram.MINUTE_CORRELATION_FACTOR));
					break;
				case MassSpectrumListUI.RETENTION_INDEX:
					libraryMassSpectrum.setRetentionIndex(Float.parseFloat(value.toString()));
					break;
				case MassSpectrumListUI.CAS:
					libraryInformation.setCasNumber(value.toString());
					break;
				case MassSpectrumListUI.FORMULA:
					libraryInformation.setFormula(value.toString());
					break;
				case MassSpectrumListUI.SMILES:
					libraryInformation.setSmiles(value.toString());
					break;
				case MassSpectrumListUI.INCHI:
					libraryInformation.setInChI(value.toString());
					break;
				case MassSpectrumListUI.REFERENCE_IDENTIFIER:
					libraryInformation.setReferenceIdentifier(value.toString());
					break;
				case MassSpectrumListUI.COMMENTS:
					libraryInformation.setComments(value.toString());
					break;
				case MassSpectrumListUI.SPLASH:
					// calculated property
					break;
			}
			tableViewer.refresh();
		}
	}
}
