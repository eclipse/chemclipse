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
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.internal.provider.PeakLabelProvider;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.LibraryMassSpectrumComparisonUI;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassValueDisplayPrecision;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.wizards.AbstractExtendedWizardPage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class PagePeakAssignment extends AbstractExtendedWizardPage {

	private IRetentionIndexWizardElements wizardElements;

	public PagePeakAssignment(IRetentionIndexWizardElements wizardElements) {
		//
		super(PagePeakAssignment.class.getName());
		setTitle("Peak Assigment");
		setDescription("Please assign the alkanes.");
		this.wizardElements = wizardElements;
	}

	@Override
	public boolean canFinish() {

		return true;
	}

	@Override
	public void setDefaultValues() {

	}

	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible) {
			System.out.println(wizardElements.getFileName());
			validateSelection();
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		//
		createLibraryComparisonField(composite);
		createPeakSpinnerField(composite);
		createAssignIndexField(composite);
		createPeakTargetsField(composite);
		//
		validateSelection();
		setControl(composite);
	}

	private void createLibraryComparisonField(Composite composite) {

		Composite parent = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 3;
		parent.setLayoutData(gridData);
		parent.setLayout(new FillLayout());
		LibraryMassSpectrumComparisonUI libraryMassSpectrumComparisonUI = new LibraryMassSpectrumComparisonUI(parent, SWT.NONE, MassValueDisplayPrecision.NOMINAL);
	}

	private void createPeakSpinnerField(Composite composite) {

		Button buttonPrevious = new Button(composite, SWT.PUSH);
		buttonPrevious.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PREVIOUS, IApplicationImage.SIZE_16x16));
		//
		Label label = new Label(composite, SWT.NONE);
		label.setText("C9 (Octane)");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Button buttonNext = new Button(composite, SWT.PUSH);
		buttonNext.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_NEXT, IApplicationImage.SIZE_16x16));
	}

	private void createAssignIndexField(Composite composite) {

		Button buttonAdd = new Button(composite, SWT.PUSH);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		buttonAdd.setLayoutData(gridData);
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				//
			}
		});
	}

	private void createPeakTargetsField(Composite composite) {

		String[] titles = {"RT", "S/N", "Peak Area"};
		int[] bounds = {200, 150, 150};
		//
		ExtendedTableViewer chromatogramTableViewer = new ExtendedTableViewer(composite, SWT.BORDER);
		chromatogramTableViewer.createColumns(titles, bounds);
		Table table = chromatogramTableViewer.getTable();
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 100;
		table.setLayoutData(gridData);
		chromatogramTableViewer.setLabelProvider(new PeakLabelProvider());
		chromatogramTableViewer.setContentProvider(new ListContentProvider());
		chromatogramTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				int index = chromatogramTableViewer.getTable().getSelectionIndex();
			}
		});
	}

	private void validateSelection() {

		String message = null;
		/*
		 * Updates the status
		 */
		updateStatus(message);
	}
}
