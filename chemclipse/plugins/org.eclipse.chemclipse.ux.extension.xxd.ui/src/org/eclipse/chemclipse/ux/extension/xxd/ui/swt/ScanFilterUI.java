/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.IMassSpectrumFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum.MassSpectrumFilter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.IUpdateListener;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ScanFilterUI extends Composite {

	private static final Logger logger = Logger.getLogger(ScanFilterUI.class);
	//
	private ComboViewer comboViewer;
	private Button button;
	//
	private IScan scan = null;
	private IUpdateListener updateListener = null;

	public ScanFilterUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(IScan scan) {

		this.scan = scan;
		//
		boolean enabled = scan instanceof IScanMSD;
		comboViewer.getCombo().setEnabled(enabled);
		button.setEnabled(enabled);
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		comboViewer = createComboViewer(composite);
		button = createButton(composite);
		//
		IMassSpectrumFilterSupport massSpectrumFilterSupport = MassSpectrumFilter.getMassSpectrumFilterSupport();
		Collection<IMassSpectrumFilterSupplier> suppliers = massSpectrumFilterSupport.getSuppliers();
		comboViewer.setInput(suppliers);
		if(suppliers.size() > 0) {
			comboViewer.getCombo().select(0);
		}
	}

	private ComboViewer createComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMassSpectrumFilterSupplier) {
					IMassSpectrumFilterSupplier supplier = (IMassSpectrumFilterSupplier)element;
					return supplier.getFilterName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a scan filter.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 200;
		combo.setLayoutData(gridData);
		//
		return comboViewer;
	}

	private Button createButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Filter the currently selected scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/*
				 * Filter
				 */
				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMassSpectrumFilterSupplier) {
					IMassSpectrumFilterSupplier supplier = (IMassSpectrumFilterSupplier)object;
					if(scan instanceof IScanMSD) {
						/*
						 * Get or create an optimized scan.
						 */
						IScanMSD scanMSD = (IScanMSD)scan;
						IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
						if(optimizedMassSpectrum == null) {
							try {
								optimizedMassSpectrum = scanMSD.makeDeepCopy();
								scanMSD.setOptimizedMassSpectrum(optimizedMassSpectrum);
							} catch(CloneNotSupportedException e1) {
								logger.warn(e1);
							}
						}
						//
						if(optimizedMassSpectrum != null) {
							/*
							 * Clear all identification results and
							 * then run apply the filter.
							 */
							optimizedMassSpectrum.getTargets().clear();
							MassSpectrumFilter.applyFilter(optimizedMassSpectrum, supplier.getId(), new NullProgressMonitor());
							fireUpdate(e.display);
						}
					}
				}
			}
		});
		return button;
	}

	private void fireUpdate(Display display) {

		if(updateListener != null) {
			updateListener.update(display);
		}
	}
}
