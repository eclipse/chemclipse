/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.swt.ui.components.chromatogram;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelength;
import org.eclipse.chemclipse.wsd.swt.ui.internal.provider.MarkedWavelengthsChooserContentProvider;
import org.eclipse.chemclipse.wsd.swt.ui.internal.provider.MarkedWavelengthsChooserLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MarkedWavelengthsChooser {

	private IMarkedWavelengths markedWavelengths;
	private ExtendedTableViewer tableViewer;
	private Label label;
	private String labelText;
	private Text textWavelengthToAdd;
	private Text textMagnification;
	private NumberFormat numberFormat;
	//
	private String[] titles = {"Wavelength", "Magnification"};
	private int bounds[] = {100, 100};

	/**
	 * Creates a composite to edit marked ion instances.
	 * 
	 * @param parent
	 * @param style
	 */
	public MarkedWavelengthsChooser(Composite parent, int style) {
		numberFormat = ValueFormat.getNumberFormatEnglish();
		initialize(parent);
	}

	/**
	 * Returns the label text.
	 * 
	 * @return String
	 */
	public String getLabelText() {

		return labelText;
	}

	/**
	 * Sets the label text.
	 * 
	 * @param labelText
	 */
	public void setLabelText(String labelText) {

		if(label != null) {
			this.labelText = labelText;
			label.setText(labelText);
		}
	}

	/**
	 * Returns the list of marked wavelengths.
	 * 
	 * @return IMarkedWavelengths
	 */
	public IMarkedWavelengths getMarkedIons() {

		return markedWavelengths;
	}

	/**
	 * Sets the instance of marked wavelengths.
	 * 
	 * @param markedWavelengths
	 */
	public void setMarkedWavelengths(IMarkedWavelengths markedWavelengths) {

		if(markedWavelengths != null) {
			this.markedWavelengths = markedWavelengths;
			if(tableViewer != null) {
				tableViewer.setInput(markedWavelengths);
			}
		}
	}

	// ----------------------------------private methods
	private void initialize(Composite parent) {

		Composite compositeMarkedIonChooser = new Composite(parent, SWT.NONE);
		compositeMarkedIonChooser.setLayout(new GridLayout(3, false));
		GridData gridDataComposite = new GridData(GridData.FILL_BOTH);
		gridDataComposite.grabExcessHorizontalSpace = true;
		gridDataComposite.grabExcessVerticalSpace = true;
		compositeMarkedIonChooser.setLayoutData(gridDataComposite);
		/*
		 * Label
		 */
		label = new Label(compositeMarkedIonChooser, SWT.NONE);
		labelText = "Edit marked ions.";
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label label1 = new Label(compositeMarkedIonChooser, SWT.NONE);
		label1.setText("Wavelength");
		label1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label label2 = new Label(compositeMarkedIonChooser, SWT.NONE);
		label2.setText("Magnification");
		label2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * 
		 */
		Composite compositeEditor = new Composite(compositeMarkedIonChooser, SWT.NONE);
		compositeEditor.setLayout(new FillLayout());
		GridData gridDataEditor = new GridData();
		gridDataEditor.horizontalAlignment = GridData.FILL;
		gridDataEditor.verticalAlignment = GridData.FILL;
		gridDataEditor.verticalSpan = 2;
		gridDataEditor.grabExcessHorizontalSpace = true;
		gridDataEditor.grabExcessVerticalSpace = true;
		gridDataEditor.heightHint = 230;
		gridDataEditor.widthHint = 150;
		compositeEditor.setLayoutData(gridDataEditor);
		/*
		 * 
		 */
		tableViewer = new ExtendedTableViewer(compositeEditor, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.createColumns(titles, bounds);
		tableViewer.setContentProvider(new MarkedWavelengthsChooserContentProvider());
		tableViewer.setLabelProvider(new MarkedWavelengthsChooserLabelProvider());
		if(markedWavelengths != null) {
			tableViewer.setInput(markedWavelengths);
		}
		/*
		 * Add Ion Text
		 */
		textWavelengthToAdd = new Text(compositeMarkedIonChooser, SWT.BORDER);
		textWavelengthToAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textWavelengthToAdd.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * If the enter key is pressed, the ion will be added
				 * to the list.
				 */
				if(e.keyCode == 13 || e.keyCode == 16777296) {
					addWavelength();
				}
			}
		});
		/*
		 * Magnification
		 */
		textMagnification = new Text(compositeMarkedIonChooser, SWT.BORDER);
		textMagnification.setText("1");
		textMagnification.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * Add ion (Button)
		 */
		GridData gridDataButton = new GridData(GridData.FILL_HORIZONTAL);
		gridDataButton.verticalAlignment = GridData.BEGINNING;
		//
		Button addWavelength = new Button(compositeMarkedIonChooser, SWT.NONE);
		addWavelength.setText("Add Wavelength");
		addWavelength.setLayoutData(gridDataButton);
		addWavelength.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Add the selected ion.
				 */
				addWavelength();
			}
		});
		/*
		 * 
		 */
		Button removeWavelength = new Button(compositeMarkedIonChooser, SWT.NONE);
		removeWavelength.setText("Remove Selected Wavelength(s)");
		removeWavelength.setLayoutData(gridDataButton);
		removeWavelength.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				/*
				 * Remove the selected ion(s).
				 */
				if(tableViewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.getSelection();
					@SuppressWarnings("unchecked")
					List<Object> ionList = structuredSelection.toList();
					for(Object ion : ionList) {
						removeMassFragement(ion);
					}
				}
			}
		});
	}

	private void addWavelength() {

		String wavelength = textWavelengthToAdd.getText();
		String magnification = textMagnification.getText();
		//
		if(wavelength != null && !wavelength.equals("") && magnification != null && !magnification.equals("")) {
			try {
				/*
				 * Wavelength, Magnification
				 */
				int parsedWavelength = numberFormat.parse(wavelength).intValue();
				int parsedMagnification = numberFormat.parse(magnification).intValue();
				//
				if(markedWavelengths != null && parsedWavelength > 0 && parsedMagnification >= 0) {
					textWavelengthToAdd.setText("");
					markedWavelengths.add(new MarkedWavelength(parsedWavelength, parsedMagnification));
					tableViewer.refresh();
				} else {
					textWavelengthToAdd.setText("Wavelength (0) can't be added.");
				}
			} catch(ParseException e) {
				textWavelengthToAdd.setText("Please type in a valid wavelength.");
			}
		} else {
			textWavelengthToAdd.setText("The wavelength field must be not null.");
		}
	}

	/**
	 * Removes the ion from the markedIons instance and
	 * updates the view.
	 * 
	 * @param ion
	 */
	private void removeMassFragement(Object object) {

		if(object instanceof IMarkedWavelength) {
			IMarkedWavelength markedWavelength = (IMarkedWavelength)object;
			if(markedWavelengths != null) {
				markedWavelengths.remove(markedWavelength);
				tableViewer.refresh();
			}
		}
	}
}
