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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.MassSpectrumIdentifierRunnable;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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

public class ScanIdentifierUI extends Composite {

	private static final Logger logger = Logger.getLogger(ScanIdentifierUI.class);
	//
	private ComboViewer comboViewer;
	private Button button;
	//
	private IScan scan = null;
	private IUpdateListener updateListener = null;

	public ScanIdentifierUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	/**
	 * Identify the scan by calling the selected identifier.
	 * 
	 * @param display
	 * @param scanMSD
	 */
	public void runIdentification(Display display, IScanMSD scanMSD, boolean update) {

		if(scanMSD != null) {
			/*
			 * Get mass spectrum.
			 */
			IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
			IScanMSD massSpectrum = (optimizedMassSpectrum != null) ? optimizedMassSpectrum : scanMSD;
			/*
			 * Identification
			 */
			Object object = comboViewer.getStructuredSelection().getFirstElement();
			if(object instanceof IMassSpectrumIdentifierSupplier) {
				IMassSpectrumIdentifierSupplier supplier = (IMassSpectrumIdentifierSupplier)object;
				IRunnableWithProgress runnable = new MassSpectrumIdentifierRunnable(massSpectrum, supplier.getId());
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
				try {
					monitor.run(true, true, runnable);
					if(update) {
						fireUpdate(display);
					}
				} catch(InvocationTargetException e1) {
					logger.warn(e1);
				} catch(InterruptedException e1) {
					logger.warn(e1);
				}
			}
		}
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
		IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
		Collection<IMassSpectrumIdentifierSupplier> suppliers = massSpectrumIdentifierSupport.getSuppliers();
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

				if(element instanceof IMassSpectrumIdentifierSupplier) {
					IMassSpectrumIdentifierSupplier supplier = (IMassSpectrumIdentifierSupplier)element;
					return supplier.getIdentifierName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a scan identifier.");
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
		button.setToolTipText("Identify the currently selected scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(scan instanceof IScanMSD) {
					runIdentification(e.display, (IScanMSD)scan, true);
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
