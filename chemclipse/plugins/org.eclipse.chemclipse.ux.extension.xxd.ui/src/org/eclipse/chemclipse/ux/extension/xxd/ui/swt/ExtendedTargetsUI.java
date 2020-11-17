/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - content-proposal support
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.chemclipse.model.updates.ITargetUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.IColumnMoveListener;
import org.eclipse.chemclipse.support.ui.swt.ITableSettings;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSystem;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.ListSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageLists;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageTargets;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.targets.ComboTarget;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swtchart.extensions.core.IKeyboardSupport;

public class ExtendedTargetsUI extends Composite implements IExtendedPartUI {

	private static final String MENU_CATEGORY_TARGETS = "Targets";
	private static final String TARGET_OPTION_AUTO = "Auto";
	private static final String TARGET_OPTION_CHROMATOGRAM = "Chromatogram";
	//
	private final Map<String, Object> map = new HashMap<String, Object>();
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarSearch;
	private AtomicReference<Composite> toolbarSearch = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	//
	private ComboViewer comboViewerTargetOption;
	private ComboTarget comboTarget;
	private Button buttonAddTarget;
	private Button buttonDeleteTarget;
	private Button buttonDeleteTargets;
	private TargetsListUI targetListUI;
	/*
	 * IScan,
	 * IPeak,
	 * IChromatogram,
	 * ITargetSupplier
	 */
	private Object object = null;
	private Object objectCacheChromatogram = null;
	private Object objectCacheOther = null;
	//
	private final ListSupport listSupport = new ListSupport();
	private ScanDataSupport scanDataSupport = new ScanDataSupport();
	private PeakDataSupport peakDataSupport = new PeakDataSupport();

	@Inject
	public ExtendedTargetsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public boolean setFocus() {

		updateTargets();
		return super.setFocus();
	}

	public void update(Object object) {

		/*
		 * Option: AUTO -> Collect IChromatogram
		 */
		if(!isTargetOptionChromatogram() && object instanceof IChromatogram) {
			this.objectCacheChromatogram = object;
		}
		/*
		 * Option: CHROMATOGRAM -> Collect Other
		 */
		if(isTargetOptionChromatogram() && !(object instanceof IChromatogram)) {
			this.objectCacheOther = object;
		}
		/*
		 * Various objects are updated here.
		 * Hence, a simple this.object = object won't work.
		 */
		boolean showChromatogramTargets = isTargetOptionChromatogram();
		if(object instanceof IChromatogram) {
			/*
			 * Chromatogram
			 */
			if(showChromatogramTargets) {
				this.object = object;
				updateTargets();
			}
		} else if(object != null) {
			/*
			 * Other (Peak, Scan, ITargetSupplier (General))
			 */
			if(!showChromatogramTargets) {
				this.object = object;
				updateTargets();
			}
		} else {
			/*
			 * Object must be null here.
			 */
			this.object = null;
			updateTargets();
		}
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarSearch(this);
		createToolbarEdit(this);
		targetListUI = createTargetTable(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IApplicationImage.IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarSearch, buttonToolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH, false);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		//
		targetListUI.setEditEnabled(false);
		comboViewerTargetOption.setInput(new String[]{TARGET_OPTION_AUTO, TARGET_OPTION_CHROMATOGRAM});
		comboViewerTargetOption.getCombo().select(0);
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		comboViewerTargetOption = createComboViewerTargetOption(composite);
		buttonToolbarSearch = createButtonToggleToolbar(composite, toolbarSearch, IMAGE_SEARCH, TOOLTIP_SEARCH);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		buttonDeleteTargets = createButtonDeleteAll(composite);
		createButtonSettings(composite);
	}

	private ComboViewer createComboViewerTargetOption(Composite composite) {

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof String) {
					return (String)element;
				}
				return null;
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Select the target display option.");
		GridData gridData = new GridData();
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object != null) {
					if(TARGET_OPTION_CHROMATOGRAM.equals(object)) {
						update(objectCacheChromatogram);
					} else {
						update(objectCacheOther);
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonToggleEditModus(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Enable/disable to edit the table.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean editEnabled = !targetListUI.isEditEnabled();
				targetListUI.setEditEnabled(editEnabled);
				button.setImage(ApplicationImageFactory.getInstance().getImage((editEnabled) ? IApplicationImage.IMAGE_EDIT_ENTRY_ACTIVE : IApplicationImage.IMAGE_EDIT_ENTRY_DEFAULT, IApplicationImage.SIZE_16x16));
				updateInput();
			}
		});
		//
		return button;
	}

	private Button createButtonDeleteAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Delete all target(s)");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE_ALL, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(e.display.getActiveShell(), "Target(s)", "Do you want to delete all target(s)?")) {
					deleteAllTargets();
					updateTargets();
				}
			}
		});
		//
		return button;
	}

	private void createButtonSettings(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageTargets()));
				preferenceManager.addToRoot(new PreferenceNode("2", new PreferencePageSystem()));
				preferenceManager.addToRoot(new PreferenceNode("3", new PreferencePageLists()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				targetListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		toolbarSearch.set(searchSupportUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(4, false));
		//
		comboTarget = createComboTarget(composite);
		buttonAddTarget = createButtonAdd(composite);
		buttonDeleteTarget = createButtonDelete(composite);
		createButtonToggleEditModus(composite);
		//
		toolbarEdit.set(composite);
	}

	private ComboTarget createComboTarget(Composite parent) {

		ComboTarget comboTarget = new ComboTarget(parent, SWT.NONE);
		comboTarget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboTarget.setTargetUpdateListener(new ITargetUpdateListener() {

			@Override
			public void update(IIdentificationTarget identificationTarget) {

				if(identificationTarget != null) {
					setTarget(identificationTarget);
				}
			}
		});
		return comboTarget;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the target.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IIdentificationTarget identificationTarget = comboTarget.createTarget();
				if(identificationTarget != null) {
					setTarget(identificationTarget);
				}
			}
		});
		return button;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected target(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteTargets(e.display.getActiveShell());
			}
		});
		return button;
	}

	private TargetsListUI createTargetTable(Composite parent) {

		TargetsListUI listUI = new TargetsListUI(parent, SWT.BORDER);
		listUI.setEditingSupport();
		Table table = listUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateWidgets();
			}
		});
		//
		listUI.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				propagateTarget();
			}
		});
		/*
		 * Set/Save the column order.
		 */
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		String preferenceName = PreferenceConstants.P_COLUMN_ORDER_TARGET_LIST;
		listSupport.setColumnOrder(table, preferenceStore.getString(preferenceName));
		listUI.addColumnMoveListener(new IColumnMoveListener() {

			@Override
			public void handle() {

				String columnOrder = listSupport.getColumnOrder(table);
				preferenceStore.setValue(preferenceName, columnOrder);
			}
		});
		/*
		 * Add the delete targets support.
		 */
		Shell shell = listUI.getTable().getShell();
		ITableSettings tableSettings = listUI.getTableSettings();
		addDeleteMenuEntry(shell, tableSettings);
		addVerifyTargetsMenuEntry(tableSettings);
		addUnverifyTargetsMenuEntry(tableSettings);
		addKeyEventProcessors(shell, tableSettings);
		listUI.applySettings(tableSettings);
		//
		return listUI;
	}

	private void addDeleteMenuEntry(Shell shell, ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Delete Target(s)";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				deleteTargets(shell);
			}
		});
	}

	private void addVerifyTargetsMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Verify Target(s) Check";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				verifyTargets(true);
			}
		});
	}

	private void addUnverifyTargetsMenuEntry(ITableSettings tableSettings) {

		tableSettings.addMenuEntry(new ITableMenuEntry() {

			@Override
			public String getName() {

				return "Verify Target(s) Uncheck";
			}

			@Override
			public String getCategory() {

				return MENU_CATEGORY_TARGETS;
			}

			@Override
			public void execute(ExtendedTableViewer extendedTableViewer) {

				verifyTargets(false);
			}
		});
	}

	private void addKeyEventProcessors(Shell shell, ITableSettings tableSettings) {

		tableSettings.addKeyEventProcessor(new IKeyEventProcessor() {

			@Override
			public void handleEvent(ExtendedTableViewer extendedTableViewer, KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					/*
					 * DEL
					 */
					deleteTargets(shell);
				} else if(e.keyCode == IKeyboardSupport.KEY_CODE_LC_I && (e.stateMask & SWT.CTRL) == SWT.CTRL) {
					if((e.stateMask & SWT.ALT) == SWT.ALT) {
						/*
						 * CTRL + ALT + I
						 */
						verifyTargets(false);
					} else {
						/*
						 * CTRL + I
						 */
						verifyTargets(true);
					}
				} else {
					propagateTarget();
				}
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void verifyTargets(boolean verified) {

		Iterator iterator = targetListUI.getStructuredSelection().iterator();
		while(iterator.hasNext()) {
			Object object = iterator.next();
			if(object instanceof IIdentificationTarget) {
				IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
				identificationTarget.setManuallyVerified(verified);
			}
		}
		updateTargets();
	}

	private void propagateTarget() {

		Table table = targetListUI.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem tableItem = table.getItem(index);
			Object object = tableItem.getData();
			if(object instanceof IIdentificationTarget) {
				/*
				 * Fire updates
				 */
				IEventBroker eventBroker = Activator.getDefault().getEventBroker();
				if(eventBroker != null) {
					IScanMSD massSpectrum = getMassSpectrum();
					IIdentificationTarget target = (IIdentificationTarget)object;
					//
					DisplayUtils.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							/*
							 * Molecule Part
							 */
							eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, target);
						}
					});
					//
					if(massSpectrum != null) {
						DisplayUtils.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {

								/*
								 * Send the identification target update to let e.g. the molecule renderer react on an update.
								 */
								map.clear();
								IIdentificationTarget identificationTarget = (IIdentificationTarget)object;
								map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN, massSpectrum);
								map.put(IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET_ENTRY, identificationTarget);
								eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE, map);
							}
						});
					}
				}
			}
		}
	}

	private void applySettings() {

		comboTarget.updateContentProposals();
	}

	private void updateTargets() {

		updateInput();
		updateWidgets();
		//
		targetListUI.sortTable();
		Table table = targetListUI.getTable();
		if(table.getItemCount() > 0) {
			table.setSelection(0);
			//
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			boolean propagateTargetOnUpdate = preferenceStore.getBoolean(PreferenceConstants.P_PROPAGATE_TARGET_ON_UPDATE);
			if(propagateTargetOnUpdate) {
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						propagateTarget();
					}
				});
			}
		}
	}

	private void updateInput() {

		if(object instanceof ITargetSupplier) {
			ITargetSupplier targetSupplier = (ITargetSupplier)object;
			targetListUI.setInput(targetSupplier.getTargets());
		} else {
			targetListUI.setInput(null);
			enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
		}
		//
		updateLabelInfo();
	}

	private void updateLabelInfo() {

		if(object instanceof ITargetSupplier) {
			String dataDescription;
			if(object instanceof IChromatogram) {
				dataDescription = ChromatogramDataSupport.getChromatogramLabel((IChromatogram<?>)object);
			} else if(object instanceof IPeak) {
				dataDescription = peakDataSupport.getPeakLabel((IPeak)object);
			} else if(object instanceof IScan) {
				dataDescription = scanDataSupport.getScanLabel((IScan)object);
			} else {
				dataDescription = "Target Supplier";
			}
			String editInformation = targetListUI.isEditEnabled() ? "Edit is enabled." : "Edit is disabled.";
			toolbarInfo.get().setText(dataDescription + " : " + editInformation);
		} else {
			toolbarInfo.get().setText("No target data has been selected yet.");
		}
	}

	private void updateWidgets() {

		boolean enabled = (object == null) ? false : true;
		comboTarget.setEnabled(enabled);
		buttonAddTarget.setEnabled(enabled);
		//
		if(object instanceof ITargetSupplier) {
			ITargetSupplier targetSupplier = (ITargetSupplier)object;
			buttonDeleteTarget.setEnabled(targetListUI.getTable().getSelectionIndex() >= 0);
			buttonDeleteTargets.setEnabled(targetSupplier.getTargets().size() > 0);
		} else {
			buttonDeleteTarget.setEnabled(false);
			buttonDeleteTargets.setEnabled(false);
		}
	}

	@SuppressWarnings("rawtypes")
	private void deleteTargets(Shell shell) {

		if(MessageDialog.openQuestion(shell, "Target(s)", "Would you like to delete the selected target(s)?")) {
			/*
			 * Delete Target
			 */
			Iterator iterator = targetListUI.getStructuredSelection().iterator();
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

		if(object instanceof ITargetSupplier) {
			ITargetSupplier targetSupplier = (ITargetSupplier)object;
			targetSupplier.getTargets().remove(target);
		}
		/*
		 * Don't do an table update here, cause this method could be called several times in a loop.
		 */
	}

	private void deleteAllTargets() {

		if(object instanceof ITargetSupplier) {
			ITargetSupplier targetSupplier = (ITargetSupplier)object;
			targetSupplier.getTargets().clear();
		}
	}

	private void setTarget(IIdentificationTarget identificationTarget) {

		if(object instanceof ITargetSupplier) {
			ITargetSupplier targetSupplier = (ITargetSupplier)object;
			targetSupplier.getTargets().add(identificationTarget);
		}
		//
		comboTarget.setText("");
		updateTargets();
	}

	/**
	 * May return null.
	 * 
	 * @return IScanMSD
	 */
	private IScanMSD getMassSpectrum() {

		if(object instanceof IScanMSD) {
			return (IScanMSD)object;
		} else if(object instanceof IPeakMSD) {
			IPeakMSD peakMSD = (IPeakMSD)object;
			return peakMSD.getExtractedMassSpectrum();
		} else {
			return null;
		}
	}

	private boolean isTargetOptionChromatogram() {

		Object object = comboViewerTargetOption.getStructuredSelection().getFirstElement();
		return TARGET_OPTION_CHROMATOGRAM.equals(object);
	}
}
