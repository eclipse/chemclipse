/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanTargetCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumTarget;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSelectedScan;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanTargetWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExtendedTargetsUI {

	private Label labelInfo;
	private Composite toolbarInfo;
	private Composite toolbarEdit;
	private Combo comboSubstance;
	private Text textCasNumber;
	private TargetsListUI targetsListUI;
	/*
	 * This could be:
	 * IScan,
	 * IPeak,
	 * IChromatogram
	 */
	private Object object;

	@Inject
	public ExtendedTargetsUI(Composite parent, MPart part) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		update(object);
	}

	public void update(Object object) {

		this.object = object;
		//
		if(object instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)object;
			labelInfo.setText(ScanSupport.getScanLabel(scanMSD));
			targetsListUI.setInput(scanMSD.getTargets());
		} else if(object instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)object;
			labelInfo.setText(ScanSupport.getScanLabel(scanCSD));
			targetsListUI.setInput(scanCSD.getTargets());
		} else if(object instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)object;
			labelInfo.setText(ScanSupport.getScanLabel(scanWSD));
			targetsListUI.setInput(scanWSD.getTargets());
		} else if(object instanceof IPeak) {
			IPeak peak = (IPeak)object;
			labelInfo.setText(PeakSupport.getPeakLabel(peak));
			targetsListUI.setInput(peak.getTargets());
		} else if(object instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)object;
			labelInfo.setText(ChromatogramSupport.getChromatogramLabel(chromatogramMSD));
			targetsListUI.setInput(chromatogramMSD.getTargets());
		} else if(object instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)object;
			labelInfo.setText(ChromatogramSupport.getChromatogramLabel(chromatogramCSD));
			targetsListUI.setInput(chromatogramCSD.getTargets());
		} else if(object instanceof IChromatogramWSD) {
			IChromatogramWSD chromatogramWSD = (IChromatogramWSD)object;
			labelInfo.setText(ChromatogramSupport.getChromatogramLabel(chromatogramWSD));
			targetsListUI.setInput(chromatogramWSD.getTargets());
		} else {
			targetsListUI.clear();
		}
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarEdit = createToolbarEdit(parent);
		createTable(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarEdit, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(5, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarEdit(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_INACTIVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_INACTIVE, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarEdit(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle edit toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_INACTIVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarEdit);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TOOLBAR_INACTIVE, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				update(object);
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageSelectedScan();
				preferencePage.setTitle("Scan Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						//
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createComboSubstance(composite);
		createTextCasNumber(composite);
		createButtonAdd(composite);
		createButtonDelete(composite);
		//
		return composite;
	}

	private void createComboSubstance(Composite parent) {

		comboSubstance = new Combo(parent, SWT.NONE);
		comboSubstance.setToolTipText("Substance Name");
		comboSubstance.setText("");
		comboSubstance.setItems(new String[]{"Phenol", "Styrol", "Benzol"});
		comboSubstance.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createTextCasNumber(Composite parent) {

		textCasNumber = new Text(parent, SWT.BORDER);
		textCasNumber.setToolTipText("CAS Number");
		textCasNumber.setText("");
		textCasNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String name = comboSubstance.getText().trim();
				String casNumber = textCasNumber.getText().trim();
				//
				if("".equals(name)) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Add Target", "The substance name must be not empty.");
				} else {
					/*
					 * Add a new entry.
					 */
					ILibraryInformation libraryInformation = new LibraryInformation();
					libraryInformation.setName(name);
					libraryInformation.setCasNumber(casNumber);
					IComparisonResult comparisonResult = ComparisonResult.createBestMatchComparisonResult();
					//
					if(object instanceof IScanMSD) {
						IScanMSD scanMSD = (IScanMSD)object;
						scanMSD.addTarget(new MassSpectrumTarget(libraryInformation, comparisonResult));
					} else if(object instanceof IScanCSD) {
						IScanCSD scanCSD = (IScanCSD)object;
						scanCSD.addTarget(new ScanTargetCSD(libraryInformation, comparisonResult));
					} else if(object instanceof IScanWSD) {
						IScanWSD scanWSD = (IScanWSD)object;
						scanWSD.addTarget(new ScanTargetWSD(libraryInformation, comparisonResult));
					}
					//
					comboSubstance.setText("");
					textCasNumber.setText("");
					//
					update(object);
				}
			}
		});
	}

	private void createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				@SuppressWarnings("rawtypes")
				Iterator iterator = targetsListUI.getStructuredSelection().iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					// System.out.println(object);
				}
			}
		});
	}

	private void createTable(Composite parent) {

		targetsListUI = new TargetsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		targetsListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
