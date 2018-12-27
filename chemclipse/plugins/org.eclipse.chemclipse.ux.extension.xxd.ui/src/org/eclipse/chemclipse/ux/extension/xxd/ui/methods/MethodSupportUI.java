/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageMethods;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class MethodSupportUI extends Composite {

	private static final Logger logger = Logger.getLogger(MethodSupportUI.class);
	//
	private ComboViewer comboViewerMethods;
	private Button buttonEditMethod;
	private Button buttonExecuteMethod;
	//
	private IMethodListener methodListener;
	private ISupplierEditorSupport supplierEditorSupport = new EditorSupportFactory(DataType.MTH).getInstanceEditorSupport();
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public MethodSupportUI(Composite parent, int style) {
		super(parent, style);
		createControl();
	}

	public void setMethodListener(IMethodListener methodListener) {

		this.methodListener = methodListener;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		comboViewerMethods = createComboMethod(composite);
		createButtonAddMethod(composite);
		buttonEditMethod = createButtonEditMethod(composite);
		buttonExecuteMethod = createButtonExecuteMethod(composite);
		createButtonSettings(composite);
		//
		computeMethodComboItems();
	}

	private ComboViewer createComboMethod(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ListContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof File) {
					File file = (File)element;
					return file.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a process method.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof File) {
					File file = (File)object;
					preferenceStore.putValue(PreferenceConstants.P_SELECTED_METHOD_NAME, file.getName());
				}
				enableMethodEditButton();
			}
		});
		//
		return comboViewer;
	}

	private Button createButtonAddMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Creates and adds a new method.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = e.widget.getDisplay().getActiveShell();
				String directoryPath = preferenceStore.getString(PreferenceConstants.P_METHOD_EXPLORER_PATH_ROOT_FOLDER);
				File directory = new File(directoryPath);
				if(directory.exists()) {
					createNewMethod(shell);
				} else {
					if(selectMethodDirectory(shell)) {
						createNewMethod(shell);
					} else {
						MessageDialog.openError(shell, "Method Editor", "Please select a directory via the settings where your methods are located.");
					}
				}
			}
		});
		//
		return button;
	}

	private boolean selectMethodDirectory(Shell shell) {

		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText("Method Directory");
		//
		String directoryPath = directoryDialog.open();
		if(directoryPath != null && !directoryPath.equals("")) {
			preferenceStore.putValue(PreferenceConstants.P_METHOD_EXPLORER_PATH_ROOT_FOLDER, directoryPath);
			return true;
		} else {
			return false;
		}
	}

	private void createNewMethod(Shell shell) {

		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setOverwrite(true);
		fileDialog.setText("Process Method");
		fileDialog.setFileName(MethodConverter.DEFAULT_METHOD_FILE_NAME);
		fileDialog.setFilterExtensions(MethodConverter.DEFAULT_METHOD_FILE_EXTENSIONS);
		fileDialog.setFilterNames(MethodConverter.DEFAULT_METHOD_FILE_NAMES);
		fileDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_METHOD_EXPLORER_PATH_ROOT_FOLDER));
		//
		String filePath = fileDialog.open();
		if(filePath != null && !filePath.equals("")) {
			File file = new File(filePath);
			ProcessMethod processMethod = new ProcessMethod();
			processMethod.setOperator(UserManagement.getCurrentUser());
			processMethod.setDescription("This is an empty process method. Please modify.");
			//
			IProcessingInfo processingInfo = MethodConverter.convert(file, processMethod, MethodConverter.DEFAULT_MÉTHOD_CONVERTER_ID, new NullProgressMonitor());
			if(!processingInfo.hasErrorMessages()) {
				preferenceStore.putValue(PreferenceConstants.P_SELECTED_METHOD_NAME, file.getName());
				computeMethodComboItems();
				supplierEditorSupport.openEditor(file);
			}
		}
	}

	private Button createButtonEditMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Edit the selected method.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewerMethods.getStructuredSelection().getFirstElement();
				if(object instanceof File) {
					File file = (File)object;
					supplierEditorSupport.openEditor(file);
				}
			}
		});
		//
		return button;
	}

	private Button createButtonExecuteMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Apply the method to the selected chromatogram(s).");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewerMethods.getStructuredSelection().getFirstElement();
				if(object instanceof File) {
					File file = (File)object;
					if(file.exists()) {
						runMethod(file);
					}
				}
			}
		});
		//
		return button;
	}

	private Button createButtonSettings(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageProcessing = new PreferencePage();
				preferencePageProcessing.setTitle("Processing");
				//
				IPreferencePage preferencePageMethods = new PreferencePageMethods();
				preferencePageMethods.setTitle("Methods");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageProcessing));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageMethods));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(DisplayUtils.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.widget.getDisplay().getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
		//
		return button;
	}

	private void applySettings() {

		computeMethodComboItems();
	}

	private void computeMethodComboItems() {

		String methodRootFolder = preferenceStore.getString(PreferenceConstants.P_METHOD_EXPLORER_PATH_ROOT_FOLDER);
		//
		File directory = new File(methodRootFolder);
		if(directory.exists() && directory.isDirectory()) {
			comboViewerMethods.setInput(getMethodFiles(directory));
			if(comboViewerMethods.getCombo().getItemCount() > 0) {
				setSelectedMethod();
			}
		} else {
			comboViewerMethods.setInput(null);
		}
		//
		enableMethodEditButton();
	}

	private List<File> getMethodFiles(File directory) {

		List<File> methodFiles = new ArrayList<>();
		try {
			String[] extensions = MethodConverter.getMethodConverterSupport().getFilterExtensions();
			for(File file : directory.listFiles()) {
				for(String extension : extensions) {
					if(file.getName().endsWith(extension)) {
						if(!methodFiles.contains(file)) {
							methodFiles.add(file);
						}
					}
				}
			}
		} catch(NoConverterAvailableException e) {
			logger.warn(e);
		}
		//
		Collections.sort(methodFiles);
		return methodFiles;
	}

	private void setSelectedMethod() {

		String selectedMethodName = preferenceStore.getString(PreferenceConstants.P_SELECTED_METHOD_NAME);
		Combo combo = comboViewerMethods.getCombo();
		//
		exitloop:
		for(int i = 0; i < combo.getItemCount(); i++) {
			if(combo.getItem(i).equals(selectedMethodName)) {
				combo.select(i);
				break exitloop;
			}
		}
		//
		if(combo.getSelectionIndex() == -1) {
			combo.select(0);
			preferenceStore.putValue(PreferenceConstants.P_SELECTED_METHOD_NAME, combo.getItem(0));
		}
	}

	private void enableMethodEditButton() {

		buttonEditMethod.setEnabled(false);
		buttonExecuteMethod.setEnabled(false);
		//
		Object object = comboViewerMethods.getStructuredSelection().getFirstElement();
		if(object instanceof File) {
			File file = (File)object;
			if(file.exists()) {
				buttonEditMethod.setEnabled(true);
				buttonExecuteMethod.setEnabled(true);
			}
		}
	}

	private void runMethod(File file) {

		if(methodListener != null && file != null) {
			try {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
				dialog.run(false, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						IProcessingInfo processingInfo = MethodConverter.convert(file, monitor);
						if(!processingInfo.hasErrorMessages()) {
							try {
								ProcessMethod processMethod = processingInfo.getProcessingResult(ProcessMethod.class);
								methodListener.execute(processMethod, monitor);
							} catch(Exception e) {
								logger.warn(e);
							}
						}
					}
				});
			} catch(InvocationTargetException e) {
				logger.warn(e);
			} catch(InterruptedException e) {
				logger.warn(e);
			}
		}
	}
}
