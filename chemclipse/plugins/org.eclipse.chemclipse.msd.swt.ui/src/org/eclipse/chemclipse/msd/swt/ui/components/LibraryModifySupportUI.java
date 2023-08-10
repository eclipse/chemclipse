/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverterSupport;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.RegularLibraryMassSpectrum;
import org.eclipse.chemclipse.msd.swt.ui.Activator;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectrumListUI;
import org.eclipse.chemclipse.msd.swt.ui.internal.runnables.LibraryImportRunnable;
import org.eclipse.chemclipse.msd.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.files.ExtendedFileDialog;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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

public class LibraryModifySupportUI extends Composite {

	private static final Logger logger = Logger.getLogger(LibraryModifySupportUI.class);
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_IMPORT = "ACTION_IMPORT";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	private static final String ACTION_ADD = "ACTION_ADD";
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
	private MassSpectrumListUI massSpectrumListUI;
	private SearchSupportUI searchSupportUI;
	private IMassSpectra massSpectra;

	public LibraryModifySupportUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IMassSpectra massSpectra) {

		this.massSpectra = massSpectra;
		notify(massSpectra);
		if(massSpectrumListUI != null) {
			massSpectrumListUI.setInput(massSpectra);
		}
	}

	private void notify(IMassSpectra massSpectra) {

		IEventBroker eventBroker = Activator.getDefault().getEventBroker();
		if(eventBroker != null) {
			eventBroker.send(IChemClipseEvents.TOPIC_LIBRARY_MSD_UPDATE, massSpectra);
		}
	}

	public void setReferencedComposites(MassSpectrumListUI massSpectrumListUI, SearchSupportUI searchSupportUI) {

		this.searchSupportUI = searchSupportUI;
		this.massSpectrumListUI = massSpectrumListUI;
		this.massSpectrumListUI.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_SELECT);
			}
		});
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridLayout gridLayout = new GridLayout(8, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		createLibraryPathText(composite);
		createSelectLibraryButton(composite);
		createMergeLibraryButton(composite);
		createCancelLibraryButton(composite);
		createDeleteLibraryButton(composite);
		createAddLibraryButton(composite);
		createImportLibraryButton(composite);
		createResetSearchButton(composite);
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createLibraryPathText(Composite parent) {

		textLibraryPath = new Text(parent, SWT.BORDER);
		textLibraryPath.setToolTipText("Absolute path to your library.");
		textLibraryPath.setText("");
		textLibraryPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createSelectLibraryButton(Composite parent) {

		buttonSelectLibrary = new Button(parent, SWT.PUSH);
		buttonSelectLibrary.setToolTipText("Select a library");
		buttonSelectLibrary.setText("");
		buttonSelectLibrary.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16));
		buttonSelectLibrary.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DatabaseConverterSupport databaseConverterSupport = DatabaseConverter.getDatabaseConverterSupport();
				try {
					String[] extensions = databaseConverterSupport.getFilterExtensions();
					String[] names = databaseConverterSupport.getFilterNames();
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
						FileDialog fileDialog = ExtendedFileDialog.create(shell, SWT.READ_ONLY);
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
		buttonMergeLibrary.setText("");
		buttonMergeLibrary.setToolTipText("Merge the selected library");
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
					LibraryImportRunnable runnable = new LibraryImportRunnable(file);
					ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
					try {
						/*
						 * Use true, true ... instead of false, true ... if the progress bar
						 * should be shown in action.
						 */
						monitor.run(true, true, runnable);
					} catch(InvocationTargetException e1) {
						logger.warn(e1);
						logger.warn(e1.getCause());
					} catch(InterruptedException e1) {
						logger.warn(e1);
					}
					//
					IMassSpectra massSpectraImport = runnable.getMassSpectra();
					if(massSpectraImport != null) {
						textLibraryPath.setText("");
						massSpectra.addMassSpectra(massSpectraImport.getList());
						massSpectra.setDirty(true);
						update(massSpectra);
						resetSearch();
					}
					enableButtonFields(ACTION_INITIALIZE);
				}
			}
		});
	}

	private void createCancelLibraryButton(Composite parent) {

		buttonCancel = new Button(parent, SWT.PUSH);
		buttonCancel.setToolTipText("Cancel operation");
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
		buttonDelete.setText("");
		buttonDelete.setToolTipText("Delete the library entry.");
		buttonDelete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(massSpectrumListUI != null) {
					Table table = massSpectrumListUI.getTable();
					int index = table.getSelectionIndex();
					if(index >= 0) {
						MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
						messageBox.setText("Delete library entries?");
						messageBox.setMessage("Would you like to delete the library entries?");
						if(messageBox.open() == SWT.OK) {
							//
							enableButtonFields(ACTION_DELETE);
							TableItem[] tableItems = table.getSelection();
							for(TableItem tableItem : tableItems) {
								Object object = tableItem.getData();
								if(object instanceof IScanMSD) {
									IScanMSD massSpectrum = (IScanMSD)object;
									massSpectra.removeMassSpectrum(massSpectrum);
									massSpectra.setDirty(true);
								}
							}
							update(massSpectra);
							resetSearch();
						}
					}
				}
			}
		});
	}

	private void createAddLibraryButton(Composite parent) {

		buttonAdd = new Button(parent, SWT.PUSH);
		buttonAdd.setToolTipText("Add a library entry.");
		buttonAdd.setText("");
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
						//
						libraryMassSpectrum.addIon(new Ion(18.0d, 600.0f));
						libraryMassSpectrum.addIon(new Ion(28.0d, 1000.0f));
						libraryMassSpectrum.addIon(new Ion(32.0d, 250.0f));
						/*
						 * Add to library and update the list.
						 */
						massSpectra.addMassSpectrum(libraryMassSpectrum);
						massSpectra.setDirty(true);
						update(massSpectra);
						if(searchSupportUI != null) {
							searchSupportUI.setSearchText(name);
						}
						//
					} catch(Exception exception) {
						logger.warn(exception);
					}
				}
				//
				enableButtonFields(ACTION_INITIALIZE);
			}
		});
	}

	private void createImportLibraryButton(Composite parent) {

		buttonImport = new Button(parent, SWT.PUSH);
		buttonImport.setToolTipText("Import a library.");
		buttonImport.setText("");
		buttonImport.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		buttonImport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_IMPORT);
			}
		});
	}

	private void createResetSearchButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the search settings.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				resetSearch();
			}
		});
	}

	private void resetSearch() {

		if(searchSupportUI != null) {
			searchSupportUI.setSearchText("");
		}
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
				if(massSpectrumListUI != null && massSpectrumListUI.getTable().getSelectionIndex() >= 0) {
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
}
