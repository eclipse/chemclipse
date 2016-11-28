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
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

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
import org.eclipse.chemclipse.msd.swt.ui.internal.runnables.ImportLibraryRunnable;
import org.eclipse.chemclipse.rcp.app.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
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
	//
	private MassSpectrumSearchListUI massSpectrumSearchListUI;
	private IEventBroker eventBroker;
	private IComparisonResult comparisonResult;
	//
	private Text textLibraryPath;
	private Button buttonSelectLibrary;
	private Button buttonMergeLibrary;
	//
	private Button buttonEditLibrary;
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	private Button buttonImport;
	//
	private IMassSpectra massSpectra;

	public MassSpectrumLibraryUI(Composite parent, int style) {
		super(parent, style);
		eventBroker = ModelSupportAddon.getEventBroker();
		comparisonResult = ComparisonResult.createBestMatchComparisonResult();
		initialize();
	}

	public void update(IMassSpectra massSpectra, boolean forceReload) {

		this.massSpectra = massSpectra;
		massSpectrumSearchListUI.update(massSpectra, true);
	}

	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonField(composite);
		createTableField(composite);
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createButtonField(Composite composite) {

		/*
		 * Spacer Label
		 */
		textLibraryPath = new Text(composite, SWT.BORDER);
		textLibraryPath.setText("");
		textLibraryPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		buttonSelectLibrary = new Button(composite, SWT.PUSH);
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
		//
		Shell shell = Display.getCurrent().getActiveShell();
		buttonMergeLibrary = new Button(composite, SWT.PUSH);
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
						massSpectrumSearchListUI.update(massSpectra, true);
					}
					enableButtonFields(ACTION_INITIALIZE);
				}
			}
		});
		/*
		 * Buttons
		 */
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(5, true));
		GridData gridDataComposite = new GridData();
		gridDataComposite.horizontalAlignment = SWT.RIGHT;
		compositeButtons.setLayoutData(gridDataComposite);
		//
		buttonEditLibrary = new Button(compositeButtons, SWT.PUSH);
		if(org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceSupplier.isChangePerspectivesAutomatically()) {
			buttonEditLibrary.setSelection(false);
			buttonEditLibrary.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DISABLED, IApplicationImage.SIZE_16x16));
		} else {
			buttonEditLibrary.setSelection(true);
			buttonEditLibrary.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		}
		buttonEditLibrary.setToolTipText("Edit Library");
		buttonEditLibrary.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean selection = org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceSupplier.isChangePerspectivesAutomatically();
				org.eclipse.chemclipse.rcp.app.ui.preferences.PreferenceSupplier.setChangePerspectivesAutomatically(!selection);
				//
				if(selection) {
					buttonEditLibrary.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DISABLED, IApplicationImage.SIZE_16x16));
				} else {
					buttonEditLibrary.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		buttonCancel = new Button(compositeButtons, SWT.PUSH);
		buttonCancel.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_CANCEL);
			}
		});
		//
		buttonDelete = new Button(compositeButtons, SWT.PUSH);
		buttonDelete.setEnabled(false);
		buttonDelete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = massSpectrumSearchListUI.getTableViewer().getTable();
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
						massSpectrumSearchListUI.update(massSpectra, true);
					}
				}
			}
		});
		//
		buttonAdd = new Button(compositeButtons, SWT.PUSH);
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

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
						massSpectrumSearchListUI.setSearchSelection(name);
						//
					} catch(Exception e1) {
						logger.warn(e1);
					}
				}
			}
		});
		//
		buttonImport = new Button(compositeButtons, SWT.PUSH);
		buttonImport.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		buttonImport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_IMPORT);
			}
		});
	}

	private void createTableField(Composite composite) {

		Composite compositeTable = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		compositeTable.setLayoutData(gridData);
		compositeTable.setLayout(new FillLayout());
		//
		Composite compositeInner = new Composite(compositeTable, SWT.NONE);
		compositeInner.setLayout(new GridLayout(1, true));
		massSpectrumSearchListUI = new MassSpectrumSearchListUI(compositeInner, SWT.BORDER | SWT.MULTI);
		massSpectrumSearchListUI.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object firstElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if(firstElement != null && firstElement instanceof IScanMSD) {
					/*
					 * Activate the views.
					 */
					List<String> viewIds = new ArrayList<String>();
					viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM);
					viewIds.add(IPerspectiveAndViewIds.VIEW_MASS_SPECTRUM_TARGETS);
					PerspectiveSwitchHandler.focusPerspectiveAndView(IPerspectiveAndViewIds.PERSPECTIVE_MS_LIBRARY, viewIds);
					/*
					 * Fire an update if an identified scan has been selected.
					 */
					IScanMSD massSpectrum = (IScanMSD)firstElement;
					MassSpectrumSelectionUpdateNotifier.fireUpdateChange(massSpectrum, true);
					//
					IIdentificationTarget identificationTarget = getIdentificationTarget(massSpectrum);
					eventBroker.send(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, identificationTarget);
					/*
					 * It's important to set the focus here.
					 * The PerspectiveSwitchHandler.focusPerspectiveAndView activates other views and sets the
					 * focus there. But when trying to press "DEL", the focus would be on the other views.
					 * Hence, it needs to be set back to this list.
					 */
					massSpectrumSearchListUI.setFocus();
				}
			}
		});
		massSpectrumSearchListUI.getTableViewer().getTable().addSelectionListener(new SelectionAdapter() {

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
				if(massSpectrumSearchListUI.getTableViewer().getTable().getSelectionIndex() >= 0) {
					buttonDelete.setEnabled(true);
				} else {
					buttonDelete.setEnabled(false);
				}
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
}
