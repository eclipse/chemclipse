/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.IMassSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.wsd.identifier.wavespectrum.IWaveSpectrumIdentifierSupplier;
import org.eclipse.chemclipse.chromatogram.wsd.identifier.wavespectrum.IWaveSpectrumIdentifierSupport;
import org.eclipse.chemclipse.chromatogram.wsd.identifier.wavespectrum.WaveSpectrumIdentifier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.IUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.MassSpectrumIdentifierRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.WaveSpectrumIdentifierRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ScanIdentifierUI extends Composite {

	private static final Logger logger = Logger.getLogger(ScanIdentifierUI.class);
	private static final String KEY_MENU_DATA = "keyMenuData";
	//
	private Button button;
	private Menu menuMSD = null;
	private Menu menuWSD = null;
	//
	private List<IScan> scans = new ArrayList<>();
	private IUpdateListener updateListener = null;
	//
	private List<IMassSpectrumIdentifierSupplier> identifierSuppliersMSD = getIdentifierSuppliersMSD();
	private IMassSpectrumIdentifierSupplier massSpectrumIdentifierSupplier;
	List<IWaveSpectrumIdentifierSupplier> identifierSuppliersWSD = getIdentifierSuppliersWSD();
	private IWaveSpectrumIdentifierSupplier waveSpectrumIdentifierSupplier;
	//
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ScanIdentifierUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public static String[][] getScanIdentifierMSD() {

		List<IMassSpectrumIdentifierSupplier> identifierSuppliersMSD = getIdentifierSuppliersMSD();
		String[][] scanIdentifierMSD = new String[identifierSuppliersMSD.size()][2];
		for(int i = 0; i < identifierSuppliersMSD.size(); i++) {
			IMassSpectrumIdentifierSupplier identifierSupplier = identifierSuppliersMSD.get(i);
			scanIdentifierMSD[i] = new String[]{identifierSupplier.getIdentifierName(), identifierSupplier.getId()};
		}
		//
		return scanIdentifierMSD;
	}

	public static String[][] getScanIdentifierWSD() {

		List<IWaveSpectrumIdentifierSupplier> identifierSuppliersWSD = getIdentifierSuppliersWSD();
		String[][] scanIdentifierWSD = new String[identifierSuppliersWSD.size()][2];
		for(int i = 0; i < identifierSuppliersWSD.size(); i++) {
			IWaveSpectrumIdentifierSupplier identifierSupplier = identifierSuppliersWSD.get(i);
			scanIdentifierWSD[i] = new String[]{identifierSupplier.getIdentifierName(), identifierSupplier.getId()};
		}
		//
		return scanIdentifierWSD;
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		button.setEnabled(enabled);
	}

	public void setInput(IScan scan) {

		setInput(Arrays.asList(scan));
	}

	public void setInput(List<IScan> scans) {

		this.scans.clear();
		this.scans.addAll(scans);
		updateMenu();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void updateIdentifier() {

		updateMenu();
	}

	@Override
	protected void finalize() throws Throwable {

		if(menuMSD != null) {
			menuMSD.dispose();
		}
		//
		if(menuWSD != null) {
			menuWSD.dispose();
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		button = createButton(composite);
		//
		initialize();
	}

	private void initialize() {

		setEnabled(false);
	}

	private Button createButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IDENTIFY_MASS_SPECTRUM, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runIdentification(e.display);
			}
		});
		//
		return button;
	}

	private void updateMenu() {

		setEnabled(false);
		//
		if(!scans.isEmpty()) {
			/*
			 * Get the first scan to determine whether to use MSD or WSD.
			 */
			IScan scan = scans.get(0);
			if(scan instanceof IScanMSD) {
				/*
				 * MSD
				 */
				activateDefaultIdentifierMSD(identifierSuppliersMSD);
				if(menuMSD == null) {
					menuMSD = createMenuIdentifierMSD(button, identifierSuppliersMSD);
				}
				button.setMenu(menuMSD);
				setEnabled(true);
			} else if(scan instanceof IScanWSD) {
				/*
				 * WSD
				 */
				activateDefaultIdentifierWSD(identifierSuppliersWSD);
				if(menuWSD == null) {
					menuWSD = createMenuIdentifierWSD(button, identifierSuppliersWSD);
				}
				button.setMenu(menuWSD);
				setEnabled(true);
			}
		}
	}

	private void activateDefaultIdentifierMSD(List<IMassSpectrumIdentifierSupplier> identifierSuppliers) {

		/*
		 * Try to set the selected identifier.
		 */
		String id = preferenceStore.getString(PreferenceConstants.P_SCAN_IDENTIFER_MSD);
		if(!id.isEmpty()) {
			exitloop:
			for(IMassSpectrumIdentifierSupplier identifierSupplier : identifierSuppliers) {
				if(id.equals(identifierSupplier.getId())) {
					massSpectrumIdentifierSupplier = identifierSupplier;
					break exitloop;
				}
			}
		}
		/*
		 * Get a default identifier.
		 */
		if(massSpectrumIdentifierSupplier == null) {
			if(!identifierSuppliers.isEmpty()) {
				massSpectrumIdentifierSupplier = identifierSuppliers.get(0);
			}
		}
		/*
		 * Set the tooltip.
		 */
		if(massSpectrumIdentifierSupplier != null) {
			button.setToolTipText(massSpectrumIdentifierSupplier.getIdentifierName());
		}
	}

	private void activateDefaultIdentifierWSD(List<IWaveSpectrumIdentifierSupplier> identifierSuppliers) {

		/*
		 * Try to set the selected identifier.
		 */
		String id = preferenceStore.getString(PreferenceConstants.P_SCAN_IDENTIFER_WSD);
		if(!id.isEmpty()) {
			exitloop:
			for(IWaveSpectrumIdentifierSupplier identifierSupplier : identifierSuppliers) {
				if(id.equals(identifierSupplier.getId())) {
					waveSpectrumIdentifierSupplier = identifierSupplier;
					break exitloop;
				}
			}
		}
		/*
		 * Get a default identifier.
		 */
		if(waveSpectrumIdentifierSupplier == null) {
			if(!identifierSuppliers.isEmpty()) {
				waveSpectrumIdentifierSupplier = identifierSuppliers.get(0);
			}
		}
		/*
		 * Set the tooltip.
		 */
		if(waveSpectrumIdentifierSupplier != null) {
			button.setToolTipText(waveSpectrumIdentifierSupplier.getIdentifierName());
		}
	}

	private Menu createMenuIdentifierMSD(Button button, List<IMassSpectrumIdentifierSupplier> identifierSuppliers) {

		Menu menu = new Menu(button);
		menu.setData(KEY_MENU_DATA, DataType.MSD);
		//
		for(IMassSpectrumIdentifierSupplier identifierSupplier : identifierSuppliers) {
			/*
			 * Identifier Handler
			 */
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(identifierSupplier.getIdentifierName());
			menuItem.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					button.setToolTipText(identifierSupplier.getIdentifierName());
					preferenceStore.setValue(PreferenceConstants.P_SCAN_IDENTIFER_MSD, identifierSupplier.getId());
					massSpectrumIdentifierSupplier = identifierSupplier;
					runIdentification(e.display);
				}
			});
		}
		//
		return menu;
	}

	private Menu createMenuIdentifierWSD(Button button, List<IWaveSpectrumIdentifierSupplier> identifierSuppliers) {

		Menu menu = new Menu(button);
		menu.setData(KEY_MENU_DATA, DataType.WSD);
		//
		for(IWaveSpectrumIdentifierSupplier identifierSupplier : identifierSuppliers) {
			/*
			 * Identifier Handler
			 */
			MenuItem menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(identifierSupplier.getIdentifierName());
			menuItem.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					button.setToolTipText(identifierSupplier.getIdentifierName());
					preferenceStore.setValue(PreferenceConstants.P_SCAN_IDENTIFER_WSD, identifierSupplier.getId());
					waveSpectrumIdentifierSupplier = identifierSupplier;
					runIdentification(e.display);
				}
			});
		}
		//
		return menu;
	}

	private static List<IMassSpectrumIdentifierSupplier> getIdentifierSuppliersMSD() {

		IMassSpectrumIdentifierSupport massSpectrumIdentifierSupport = MassSpectrumIdentifier.getMassSpectrumIdentifierSupport();
		List<IMassSpectrumIdentifierSupplier> identifierSuppliers = new ArrayList<>(massSpectrumIdentifierSupport.getSuppliers());
		Collections.sort(identifierSuppliers, (s1, s2) -> s1.getIdentifierName().compareTo(s2.getIdentifierName()));
		//
		return identifierSuppliers;
	}

	private static List<IWaveSpectrumIdentifierSupplier> getIdentifierSuppliersWSD() {

		IWaveSpectrumIdentifierSupport waveSpectrumIdentifierSupport = WaveSpectrumIdentifier.getWaveSpectrumIdentifierSupport();
		List<IWaveSpectrumIdentifierSupplier> identifierSuppliers = new ArrayList<>(waveSpectrumIdentifierSupport.getSuppliers());
		Collections.sort(identifierSuppliers, (s1, s2) -> s1.getIdentifierName().compareTo(s2.getIdentifierName()));
		//
		return identifierSuppliers;
	}

	private void runIdentification(Display display) {

		Menu menu = button.getMenu();
		if(menu != null) {
			//
			DataType dataType = (DataType)menu.getData(KEY_MENU_DATA);
			List<IScanMSD> scansMSD = new ArrayList<>();
			List<IScanWSD> scansWSD = new ArrayList<>();
			//
			for(IScan scan : scans) {
				if(scan instanceof IScanMSD && DataType.MSD.equals(dataType)) {
					IScanMSD scanMSD = (IScanMSD)scan;
					IScanMSD optimizedMassSpectrum = scanMSD.getOptimizedMassSpectrum();
					IScanMSD massSpectrum = (optimizedMassSpectrum != null) ? optimizedMassSpectrum : scanMSD;
					scansMSD.add(massSpectrum);
				} else if(scan instanceof IScanWSD && DataType.WSD.equals(dataType)) {
					IScanWSD scanWSD = (IScanWSD)scan;
					scansWSD.add(scanWSD);
				}
			}
			//
			runIdentification(display, scansMSD, scansWSD, true);
		}
	}

	/**
	 * Identify the scan by calling the selected identifier.
	 * 
	 * @param display
	 * @param scanMSD
	 */
	private void runIdentification(Display display, List<IScanMSD> massSpectra, List<IScanWSD> waveSpectra, boolean update) {

		if(!massSpectra.isEmpty()) {
			if(massSpectrumIdentifierSupplier != null) {
				IRunnableWithProgress runnable = new MassSpectrumIdentifierRunnable(massSpectra, massSpectrumIdentifierSupplier.getId());
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
				try {
					monitor.run(true, true, runnable);
					if(update) {
						fireUpdate(display);
					}
				} catch(InvocationTargetException e) {
					logger.warn(e);
				} catch(InterruptedException e) {
					logger.warn(e);
				}
			}
		}
		//
		if(!waveSpectra.isEmpty()) {
			if(waveSpectrumIdentifierSupplier != null) {
				IRunnableWithProgress runnable = new WaveSpectrumIdentifierRunnable(waveSpectra, waveSpectrumIdentifierSupplier.getId());
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(display.getActiveShell());
				try {
					monitor.run(true, true, runnable);
					if(update) {
						fireUpdate(display);
					}
				} catch(InvocationTargetException e) {
					logger.warn(e);
				} catch(InterruptedException e) {
					logger.warn(e);
				}
			}
		}
	}

	private void fireUpdate(Display display) {

		if(updateListener != null) {
			updateListener.update(display);
		}
	}
}
