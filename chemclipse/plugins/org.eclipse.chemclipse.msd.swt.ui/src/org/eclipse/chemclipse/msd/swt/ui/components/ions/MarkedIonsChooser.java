/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.ions;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
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
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.swt.ui.internal.components.ions.MarkedIonsChooserContentProvider;
import org.eclipse.chemclipse.msd.swt.ui.internal.components.ions.MarkedIonsChooserLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;

/**
 * This class can be used to show and edit instances of {@link IMarkedIons}.<br/>
 * Use it for example to show and edit {@link ExcludedIons} or {@link MarkedIons}.
 * 
 * @author eselmeister
 */
public class MarkedIonsChooser {

	private IMarkedIons markedIons;
	private ExtendedTableViewer tableViewer;
	private Label label;
	private String labelText;
	private Text textIonToAdd;
	private Text textMagnification;
	private NumberFormat numberFormat;
	//
	private String[] titles = {"m/z", "Magnification"};
	private int bounds[] = {100, 100};

	/**
	 * Creates a composite to edit marked ion instances.
	 * 
	 * @param parent
	 * @param style
	 */
	public MarkedIonsChooser(Composite parent, int style) {
		numberFormat = NumberFormat.getInstance();
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
	 * Returns the list of marked ions.
	 * 
	 * @return IMarkedIons
	 */
	public IMarkedIons getMarkedIons() {

		return markedIons;
	}

	/**
	 * Sets the instance of marked ions.
	 * 
	 * @param markedIons
	 */
	public void setMarkedIons(IMarkedIons markedIons) {

		if(markedIons != null) {
			this.markedIons = markedIons;
			if(tableViewer != null) {
				tableViewer.setInput(markedIons);
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
		label1.setText("m/z");
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
		tableViewer.setContentProvider(new MarkedIonsChooserContentProvider());
		tableViewer.setLabelProvider(new MarkedIonsChooserLabelProvider());
		if(markedIons != null) {
			tableViewer.setInput(markedIons);
		}
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					tableViewer.copyToClipboard(titles);
				}
			}
		});
		/*
		 * Add Ion Text
		 */
		textIonToAdd = new Text(compositeMarkedIonChooser, SWT.BORDER);
		textIonToAdd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textIonToAdd.addKeyListener(new KeyListener() {

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
					addIon();
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
		Button addIon = new Button(compositeMarkedIonChooser, SWT.NONE);
		addIon.setText("Add Ion");
		addIon.setLayoutData(gridDataButton);
		addIon.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Add the selected ion.
				 */
				addIon();
			}
		});
		/*
		 * 
		 */
		Button removeIon = new Button(compositeMarkedIonChooser, SWT.NONE);
		removeIon.setText("Remove Selected Ion(s)");
		removeIon.setLayoutData(gridDataButton);
		removeIon.addSelectionListener(new SelectionAdapter() {

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

	// ----------------------------------private methods
	/**
	 * Adds a ion to the markedIons instance and updates the
	 * view.
	 * 
	 * @param String
	 */
	private void addIon() {

		String ion = textIonToAdd.getText();
		String magnification = textMagnification.getText();
		//
		if(ion != null && !ion.equals("") && magnification != null && !magnification.equals("")) {
			try {
				/*
				 * Ion, Magnification
				 */
				double parsedIon = numberFormat.parse(ion).doubleValue();
				int parsedMagnification = numberFormat.parse(magnification).intValue();
				//
				if(markedIons != null && parsedIon > 0 && parsedMagnification >= 0) {
					textIonToAdd.setText("");
					markedIons.add(new MarkedIon(parsedIon, parsedMagnification));
					tableViewer.refresh();
				} else {
					textIonToAdd.setText("TIC (0) can't be added.");
				}
			} catch(ParseException e) {
				textIonToAdd.setText("Please type in a valid ion.");
			}
		} else {
			textIonToAdd.setText("The ion field must be not null.");
		}
	}

	/**
	 * Removes the ion from the markedIons instance and
	 * updates the view.
	 * 
	 * @param ion
	 */
	private void removeMassFragement(Object object) {

		if(object instanceof IMarkedIon) {
			IMarkedIon markedIon = (IMarkedIon)object;
			if(markedIons != null) {
				markedIons.remove(markedIon);
				tableViewer.refresh();
			}
		}
	}
}
