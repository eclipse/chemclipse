/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("rawtypes")
public class ReferencedChromatogramDialog extends TitleAreaDialog {

	private IChromatogram chromatogram;
	private List<Button> checkBoxList;
	private List<IChromatogram> selectedChromatograms;

	public ReferencedChromatogramDialog(Shell parentShell, IChromatogram chromatogram) {
		super(parentShell);
		this.chromatogram = chromatogram;
		checkBoxList = new ArrayList<Button>();
		selectedChromatograms = new ArrayList<IChromatogram>();
	}

	public List<IChromatogram> getSelectedChromatograms() {

		return selectedChromatograms;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Control createDialogArea(Composite parent) {

		setTitle("Open Referenced Chromatograms");
		setMessage("Select additional chromatograms.", IMessageProvider.INFORMATION);
		Composite container = (Composite)super.createDialogArea(parent);
		/*
		 * There could be several chromatograms. Hence, use a scrolled composite.
		 */
		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.V_SCROLL);
		scrolledComposite.setLayout(new GridLayout(1, true));
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Set the elements
		 */
		Composite elementComposite = new Composite(scrolledComposite, SWT.NONE);
		elementComposite.setLayout(new GridLayout(1, true));
		/*
		 * Select / Deselect All
		 */
		Composite selectDeselectAllComposite = new Composite(elementComposite, SWT.NONE);
		selectDeselectAllComposite.setLayout(new GridLayout(2, true));
		Button buttonSelectAll = new Button(selectDeselectAllComposite, SWT.PUSH);
		buttonSelectAll.setText("Select All");
		buttonSelectAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setCheckBoxSelection(true);
			}
		});
		Button buttonDeselectAll = new Button(selectDeselectAllComposite, SWT.PUSH);
		buttonDeselectAll.setText("Deselect All");
		buttonDeselectAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setCheckBoxSelection(false);
			}
		});
		/*
		 * Referenced Chromatograms
		 */
		Composite referencedChromatogramsComposite = new Composite(elementComposite, SWT.NONE);
		referencedChromatogramsComposite.setLayout(new GridLayout(1, true));
		List<IChromatogram> references = chromatogram.getReferencedChromatograms();
		int counter = 1;
		for(IChromatogram chromatogram : references) {
			/*
			 * Enables to mark the referenced chromatogram to be opened in a new editor.
			 */
			Button button = new Button(referencedChromatogramsComposite, SWT.CHECK);
			button.setText("Chromatogram #" + counter++);
			button.setData(chromatogram);
			checkBoxList.add(button);
		}
		//
		elementComposite.pack(true);
		scrolledComposite.setMinSize(elementComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setContent(elementComposite);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(false);
		scrolledComposite.setAlwaysShowScrollBars(true);
		//
		return container;
	}

	private void setCheckBoxSelection(boolean selected) {

		for(Button button : checkBoxList) {
			button.setSelection(selected);
		}
	}

	@Override
	protected void okPressed() {

		saveSelection();
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {

		return true;
	}

	@Override
	protected Point getInitialSize() {

		return new Point(400, 300);
	}

	/**
	 * Call this method only once before pressing the ok button.
	 */
	private void saveSelection() {

		/*
		 * Get the selected chromatograms.
		 */
		for(Button button : checkBoxList) {
			/*
			 * Get only the selected elements.
			 */
			if(button.getSelection() == true) {
				IChromatogram chromatogram = (IChromatogram)button.getData();
				selectedChromatograms.add(chromatogram);
			}
			/*
			 * Remove the chromatogram from the button data field.
			 */
			button.setData(null);
		}
	}
}
