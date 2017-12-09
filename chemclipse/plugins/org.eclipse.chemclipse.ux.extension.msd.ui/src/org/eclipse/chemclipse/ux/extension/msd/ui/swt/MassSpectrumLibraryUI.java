/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.swt;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverterSupport;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.MassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.RegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.notifier.MassSpectrumSelectionUpdateNotifier;
import org.eclipse.chemclipse.msd.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.msd.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumListUI;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.msd.ui.internal.runnables.ImportLibraryRunnable;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class MassSpectrumLibraryUI extends Composite {

	private static final Logger logger = Logger.getLogger(MassSpectrumLibraryUI.class);
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_IMPORT = "ACTION_IMPORT";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	private static final String ACTION_ADD = "ACTION_ADD";
	//
	private MassSpectrumListUI massSpectrumListUI;
	private IComparisonResult comparisonResult;
	//
	private Composite toolbarInfo;
	private Composite toolbarSearch;
	private Composite toolbarEdit;
	//
	private Label labelInfo;
	private SearchSupportUI searchSupportUI;
	//
	private Text textLibraryPath;
	private Button buttonSelectLibrary;
	private Button buttonMergeLibrary;
	//
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	private Button buttonImport;
	//
	private IMassSpectra massSpectra;

	public MassSpectrumLibraryUI(Composite parent, int style) {
		super(parent, style);
		comparisonResult = ComparisonResult.createBestMatchComparisonResult();
		createControl();
	}

	public void update(IMassSpectra massSpectra) {

		this.massSpectra = massSpectra;
		if(massSpectra != null) {
			massSpectrumListUI.setInput(massSpectra);
			updateLabel();
		} else {
			clear();
		}
	}

	public void clear() {

		massSpectrumListUI.setInput(null);
		updateLabel();
	}

	private void createControl() {

		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		createToolbarMain(composite);
		toolbarInfo = createToolbarInfo(composite);
		toolbarSearch = createToolbarSearch(composite);
		toolbarEdit = createToolbarEdit(composite);
		createLibraryTable(composite);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarEdit, false);
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarSearch(composite);
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

	private Button createButtonToggleToolbarSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle search toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSearch);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarEdit);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
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

				IPreferencePage preferencePage = new PreferencePage();
				preferencePage.setTitle("Target Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						// applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, true));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setBackground(Colors.WHITE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarSearch(Composite parent) {

		searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setBackground(Colors.WHITE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				massSpectrumListUI.setSearchText(searchText, caseSensitive);
				updateLabel();
			}
		});
		//
		return searchSupportUI;
	}

	private Composite createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createLibraryPathText(composite);
		createSelectLibraryButton(composite);
		createMergeLibraryButton(composite);
		createCancelLibraryButton(composite);
		createDeleteLibraryButton(composite);
		createAddLibraryButton(composite);
		createImportLibraryButton(composite);
		//
		return composite;
	}

	private void createLibraryPathText(Composite parent) {

		textLibraryPath = new Text(parent, SWT.BORDER);
		textLibraryPath.setText("");
		textLibraryPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createSelectLibraryButton(Composite parent) {

		buttonSelectLibrary = new Button(parent, SWT.PUSH);
		buttonSelectLibrary.setText("Select Library");
		buttonSelectLibrary.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				MassSpectrumConverterSupport massSpectrumConverterSupport = MassSpectrumConverter.getMassSpectrumConverterSupport();
				try {
					String[] extensions = massSpectrumConverterSupport.getFilterExtensions();
					String[] names = massSpectrumConverterSupport.getFilterNames();
					if(extensions.length == names.length) {
						String[] filterExtensions = new String[extensions.length + 1];
						String[] filterNames = new String[extensions.length + 1];
						//
						filterExtensions[0] = "*.*";
						filterNames[0] = "All files";
						//
						for(int i = 0; i < extensions.length; i++) {
							filterExtensions[i + 1] = extensions[i].replace(".", "*.");
							filterNames[i + 1] = names[i];
						}
						//
						Shell shell = Display.getCurrent().getActiveShell();
						FileDialog fileDialog = new FileDialog(shell, SWT.READ_ONLY);
						fileDialog.setText("Select Library");
						fileDialog.setFilterExtensions(filterExtensions);
						fileDialog.setFilterNames(filterNames);
						fileDialog.setFilterPath(PreferenceSupplier.getPathMassSpectrumLibraries());
						String pathname = fileDialog.open();
						if(pathname != null) {
							PreferenceSupplier.setPathMassSpectrumLibraries(fileDialog.getFilterPath());
							textLibraryPath.setText(pathname);
						}
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
	}

	private void createMergeLibraryButton(Composite parent) {

		Shell shell = Display.getCurrent().getActiveShell();
		buttonMergeLibrary = new Button(parent, SWT.PUSH);
		buttonMergeLibrary.setText("Merge Library");
		buttonMergeLibrary.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		buttonMergeLibrary.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String pathLibrary = textLibraryPath.getText().trim();
				if("".equals(pathLibrary)) {
					/*
					 * No library selected.
					 */
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("Merge Library");
					messageBox.setMessage("Please select a library.");
					messageBox.open();
				} else {
					/*
					 * Merge library.
					 */
					File file = new File(textLibraryPath.getText().trim());
					ImportLibraryRunnable runnable = new ImportLibraryRunnable(file);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
					try {
						/*
						 * Use true, true ... instead of false, true ... if the progress bar
						 * should be shown in action.
						 */
						monitor.run(true, true, runnable);
					} catch(InvocationTargetException e1) {
						logger.warn(e1);
					} catch(InterruptedException e1) {
						logger.warn(e1);
					}
					//
					IMassSpectra massSpectraImport = runnable.getMassSpectra();
					if(massSpectraImport != null) {
						textLibraryPath.setText("");
						massSpectra.addMassSpectra(massSpectraImport.getList());
						update(massSpectra);
					}
					enableButtonFields(ACTION_INITIALIZE);
				}
			}
		});
	}

	private void createCancelLibraryButton(Composite parent) {

		buttonCancel = new Button(parent, SWT.PUSH);
		buttonCancel.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_CANCEL);
			}
		});
	}

	private void createDeleteLibraryButton(Composite parent) {

		buttonDelete = new Button(parent, SWT.PUSH);
		buttonDelete.setEnabled(false);
		buttonDelete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = massSpectrumListUI.getTable();
				int index = table.getSelectionIndex();
				if(index >= 0) {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					messageBox.setText("Delete library entrie(s)?");
					messageBox.setMessage("Would you like to delete the library entrie(s)?");
					if(messageBox.open() == SWT.OK) {
						//
						enableButtonFields(ACTION_DELETE);
						TableItem[] tableItems = table.getSelection();
						for(TableItem tableItem : tableItems) {
							Object object = tableItem.getData();
							if(object instanceof IScanMSD) {
								IScanMSD massSpectrum = (IScanMSD)object;
								massSpectra.removeMassSpectrum(massSpectrum);
							}
						}
						update(massSpectra);
					}
				}
			}
		});
	}

	private void createAddLibraryButton(Composite parent) {

		buttonAdd = new Button(parent, SWT.PUSH);
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_ADD);
				//
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL);
				messageBox.setText("Add library entry?");
				messageBox.setMessage("Would you like to add a new library entry?");
				if(messageBox.open() == SWT.OK) {
					try {
						/*
						 * Create a standard library mass spectrum.
						 */
						String name = "Add a name";
						IRegularLibraryMassSpectrum libraryMassSpectrum = new RegularLibraryMassSpectrum();
						ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
						libraryInformation.setName(name);
						libraryInformation.setComments("This library entry has been added manually");
						libraryInformation.setMiscellaneous(ValueFormat.getDateFormatEnglish().format(new Date()));
						libraryMassSpectrum.addIon(new Ion(18.0d, 600.0f));
						libraryMassSpectrum.addIon(new Ion(28.0d, 1000.0f));
						libraryMassSpectrum.addIon(new Ion(32.0d, 250.0f));
						/*
						 * Add to library and update the list.
						 */
						massSpectra.addMassSpectrum(libraryMassSpectrum);
						searchSupportUI.setSearchText(name);
						//
					} catch(Exception e1) {
						logger.warn(e1);
					}
				}
				//
				enableButtonFields(ACTION_INITIALIZE);
			}
		});
	}

	private void createImportLibraryButton(Composite parent) {

		buttonImport = new Button(parent, SWT.PUSH);
		buttonImport.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		buttonImport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_IMPORT);
			}
		});
	}

	private void createLibraryTable(Composite parent) {

		massSpectrumListUI = new MassSpectrumListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		massSpectrumListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		massSpectrumListUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(firstElement != null && firstElement instanceof IScanMSD) {
					/*
					 * Activate the views if the Mass Spectrum Library Perspective is active.
					 */
					if(PerspectiveSwitchHandler.isActivePerspective(IPerspectiveAndViewIds.PERSPECTIVE_MS_LIBRARY)) {
						List<String> viewIds = new ArrayList<String>();
						viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM);
						viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM_TARGETS);
						PerspectiveSwitchHandler.focusViews(viewIds);
					}
					/*
					 * Fire an update if an identified scan has been selected.
					 */
					IScanMSD massSpectrum = (IScanMSD)firstElement;
					MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
					//
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					IIdentificationTarget identificationTarget = getIdentificationTarget(massSpectrum);
					eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, identificationTarget);
					/*
					 * It's important to set the focus here.
					 * The PerspectiveSwitchHandler.focusPerspectiveAndView activates other views and sets the
					 * focus there. But when trying to press "DEL", the focus would be on the other views.
					 * Hence, it needs to be set back to this list.
					 */
					massSpectrumListUI.getTable().setFocus();
				}
			}
		});
		massSpectrumListUI.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_SELECT);
			}
		});
	}

	private void enableButtonFields(String action) {

		enableFields(false);
		switch(action) {
			case ACTION_INITIALIZE:
				buttonAdd.setEnabled(true);
				buttonImport.setEnabled(true);
				break;
			case ACTION_CANCEL:
				buttonAdd.setEnabled(true);
				buttonImport.setEnabled(true);
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_IMPORT:
				buttonCancel.setEnabled(true);
				textLibraryPath.setEnabled(true);
				buttonSelectLibrary.setEnabled(true);
				buttonMergeLibrary.setEnabled(true);
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				buttonImport.setEnabled(true);
				//
				if(massSpectrumListUI.getTable().getSelectionIndex() >= 0) {
					buttonDelete.setEnabled(true);
				} else {
					buttonDelete.setEnabled(false);
				}
				break;
			case ACTION_ADD:
				buttonCancel.setEnabled(true);
				break;
		}
	}

	private void enableFields(boolean enabled) {

		buttonCancel.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		buttonImport.setEnabled(enabled);
		//
		textLibraryPath.setEnabled(enabled);
		buttonSelectLibrary.setEnabled(enabled);
		buttonMergeLibrary.setEnabled(enabled);
	}

	private IIdentificationTarget getIdentificationTarget(IScanMSD scanMSD) {

		IIdentificationTarget identificationTarget = null;
		//
		ILibraryInformation libraryInformation = null;
		if(scanMSD instanceof IRegularLibraryMassSpectrum) {
			IRegularLibraryMassSpectrum libraryMassSpectrum = (IRegularLibraryMassSpectrum)scanMSD;
			libraryInformation = libraryMassSpectrum.getLibraryInformation();
			if(libraryInformation != null) {
				try {
					identificationTarget = new MassSpectrumTarget(libraryInformation, comparisonResult);
				} catch(ReferenceMustNotBeNullException e) {
					logger.warn(e);
				}
			}
		}
		//
		return identificationTarget;
	}

	private void updateLabel() {

		labelInfo.setText("Stored Mass Spectra: " + getItemSize());
	}

	private int getItemSize() {

		return massSpectrumListUI.getTable().getItemCount();
	}
}
