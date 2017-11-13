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
import org.eclipse.chemclipse.csd.model.core.identifier.chromatogram.IChromatogramTargetCSD;
import org.eclipse.chemclipse.csd.model.core.identifier.scan.IScanTargetCSD;
import org.eclipse.chemclipse.csd.model.implementation.ChromatogramTargetCSD;
import org.eclipse.chemclipse.csd.model.implementation.ScanTargetCSD;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.chemclipse.model.targets.PeakTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramTargetMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuCategories;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PeakSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargets;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.identifier.chromatogram.IChromatogramTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.identifier.scan.IScanTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ChromatogramTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanTargetWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

public class ExtendedTargetsUI {

	private Label labelInfo;
	private Composite toolbarInfo;
	private Composite toolbarEdit;
	private Combo comboSubstanceName;
	private Text textCasNumber;
	private TargetsListUI targetsListUI;
	/*
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

		updateTargets();
	}

	public void update(Object object) {

		this.object = object;
		updateTargets();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarEdit = createToolbarEdit(parent);
		createTargetsTable(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, false);
		PartSupport.setCompositeVisibility(toolbarEdit, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarEdit(composite);
		createSettingsButton(composite);
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DISABLED, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarEdit);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DISABLED, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePageTargets();
				preferencePage.setTitle("Target Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
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

		comboSubstanceName = new Combo(parent, SWT.NONE);
		comboSubstanceName.setToolTipText("Substance Name");
		comboSubstanceName.setText("");
		// comboSubstanceName.setItems(new String[]{"Phenol", "Styrol", "Benzol"}); // Settings
		comboSubstanceName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createTextCasNumber(Composite parent) {

		textCasNumber = new Text(parent, SWT.BORDER);
		textCasNumber.setToolTipText("CAS Number (can be empty)");
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

				addTarget();
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

				deleteTargets();
			}
		});
	}

	private void createTargetsTable(Composite parent) {

		targetsListUI = new TargetsListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		targetsListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Add the delete targets support.
		 */
		ITableSettings tableSettings = targetsListUI.getTableSettings();
		addDeleteMenuEntry(tableSettings);
		addDeleteKeyEventProcessor(tableSettings);
		targetsListUI.applySettings(tableSettings);
	}

	private void addDeleteMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Target(s)";
			}

			@Override
			public String getCategory() {

				return ITableMenuCategories.STANDARD_OPERATION;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteTargets();
			}
		});
	}

	private void addDeleteKeyEventProcessor(ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					deleteTargets();
				}
			}
		});
	}

	private void applySettings() {

	}

	private void updateTargets() {

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

	@SuppressWarnings("rawtypes")
	private void deleteTargets() {

		MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText("Delete Targets");
		messageBox.setMessage("Would you like to delete the selected targets?");
		if(messageBox.open() == SWT.YES) {
			/*
			 * Delete Target
			 */
			Iterator iterator = targetsListUI.getStructuredSelection().iterator();
			while(iterator.hasNext()) {
				Object object = iterator.next();
				if(object instanceof ITarget) {
					deleteTarget((ITarget)object);
				}
			}
			updateTargets();
		}
	}

	private void deleteTarget(ITarget target) {

		if(object instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)object;
			scanMSD.removeTarget((IMassSpectrumTarget)target);
		} else if(object instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)object;
			scanCSD.removeTarget((IScanTargetCSD)target);
		} else if(object instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)object;
			scanWSD.removeTarget((IScanTargetWSD)target);
		} else if(object instanceof IPeak) {
			IPeak peak = (IPeak)object;
			peak.removeTarget((IPeakTarget)target);
		} else if(object instanceof IChromatogramMSD) {
			IChromatogramMSD chromatogramMSD = (IChromatogramMSD)object;
			chromatogramMSD.removeTarget((IChromatogramTargetMSD)target);
		} else if(object instanceof IChromatogramCSD) {
			IChromatogramCSD chromatogramCSD = (IChromatogramCSD)object;
			chromatogramCSD.removeTarget((IChromatogramTargetCSD)target);
		} else if(object instanceof IChromatogramWSD) {
			IChromatogramWSD chromatogramWSD = (IChromatogramWSD)object;
			chromatogramWSD.removeTarget((IChromatogramTargetWSD)target);
		}
	}

	private void addTarget() {

		String substanceName = comboSubstanceName.getText().trim();
		String casNumber = textCasNumber.getText().trim();
		//
		if("".equals(substanceName)) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Add Target", "The substance name must be not empty.");
		} else {
			/*
			 * Add a new entry.
			 */
			ILibraryInformation libraryInformation = new LibraryInformation();
			libraryInformation.setName(substanceName);
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
			} else if(object instanceof IPeak) {
				IPeak peak = (IPeak)object;
				peak.addTarget(new PeakTarget(libraryInformation, comparisonResult));
			} else if(object instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)object;
				chromatogramMSD.addTarget(new ChromatogramTargetMSD(libraryInformation, comparisonResult));
			} else if(object instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)object;
				chromatogramCSD.addTarget(new ChromatogramTargetCSD(libraryInformation, comparisonResult));
			} else if(object instanceof IChromatogramWSD) {
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)object;
				chromatogramWSD.addTarget(new ChromatogramTargetWSD(libraryInformation, comparisonResult));
			}
			//
			comboSubstanceName.setText("");
			textCasNumber.setText("");
			//
			update(object);
		}
	}
}
