/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.peak.detector.supplier.manual.ui.swt;

import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ManualDetectedPeakUI extends Composite {

	private IPeak peak;
	private IChromatogramSelection chromatogramSelection;
	private Button buttonAdd;
	private ManualExtendedPeakUI manualExtendedPeakUI;

	/**
	 * @param parent
	 * @param style
	 */
	public ManualDetectedPeakUI(Composite parent, int style) {
		super(parent, style);
		initialize(parent);
	}

	/**
	 * Updates and shows the peak.
	 * 
	 * @param peak
	 */
	public void update(IChromatogramSelection chromatogramSelection, IPeak peak) {

		this.chromatogramSelection = chromatogramSelection;
		this.peak = peak;
		manualExtendedPeakUI.setPeakAndSelection(peak, chromatogramSelection);
	}

	// -----------------------------------------private methods
	/**
	 * Initializes the widget.
	 */
	private void initialize(Composite parent) {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		GridLayout layout;
		layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 1;
		composite.setLayout(layout);
		// ------------------------------------------------------------------------------------------Buttons
		Composite buttonbar = new Composite(composite, SWT.FILL);
		buttonbar.setLayout(new FillLayout());
		/*
		 * Add the selected peak.
		 */
		buttonAdd = new Button(buttonbar, SWT.NONE);
		buttonAdd.setText("Add Peak");
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Set the peak.
				 */
				if(chromatogramSelection != null && peak != null) {
					if(chromatogramSelection instanceof IChromatogramSelectionMSD && peak instanceof IChromatogramPeakMSD) {
						/*
						 * MSD
						 */
						((IChromatogramSelectionMSD)chromatogramSelection).getChromatogramMSD().addPeak((IChromatogramPeakMSD)peak);
						if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
							/*
							 * Force an update to show the peak in the main editor.
							 */
							((ChromatogramSelectionMSD)chromatogramSelection).update(true);
						}
					} else if(chromatogramSelection instanceof IChromatogramSelectionCSD && peak instanceof IChromatogramPeakCSD) {
						/*
						 * FID
						 */
						((IChromatogramSelectionCSD)chromatogramSelection).getChromatogramCSD().addPeak((IChromatogramPeakCSD)peak);
						if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
							/*
							 * Force an update to show the peak in the main editor.
							 */
							((ChromatogramSelectionCSD)chromatogramSelection).update(true);
						}
					}
				}
			}
		});
		/*
		 * Peak
		 */
		manualExtendedPeakUI = new ManualExtendedPeakUI(composite, SWT.FILL | SWT.BORDER);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		manualExtendedPeakUI.setLayoutData(gridData);
	}
}
