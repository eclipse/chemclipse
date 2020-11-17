/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make helper method public static to read all configured methods, set processinginfo to the results view
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.converter.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.ListProcessEntryContainer;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.settings.UserManagement;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.PreferencesConfig;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageChromatogramExport;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageReportExport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class MethodSupportUI extends Composite implements PreferencesConfig {

	private static final Logger logger = Logger.getLogger(MethodSupportUI.class);
	private final ISupplierEditorSupport supplierEditorSupport = new EditorSupportFactory(DataType.MTH, () -> Activator.getDefault().getEclipseContext()).getInstanceEditorSupport();
	//
	private ComboViewer comboViewerMethods;
	private Button buttonAddMethod;
	private Button buttonCopyMethod;
	private Button buttonEditMethod;
	private Button buttonExecuteMethod;
	private Button buttonDeleteMethod;
	private Button buttonMethodDirectory;
	//
	private IMethodListener methodListener = null;

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
		GridLayout gridLayout = new GridLayout(7, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		comboViewerMethods = createComboMethod(composite);
		buttonAddMethod = createButtonAddMethod(composite);
		buttonEditMethod = createButtonEditMethod(composite);
		buttonCopyMethod = createButtonCopyMethod(composite);
		buttonDeleteMethod = createButtonDeleteMethod(composite);
		buttonMethodDirectory = createButtonMethodDirectory(composite);
		buttonExecuteMethod = createButtonExecuteMethod(composite);
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

				if(element instanceof IProcessMethod) {
					return ((IProcessMethod)element).getName();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a process method.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IProcessMethod) {
					PreferenceSupplier.setSelectedMethodName(((IProcessMethod)object).getName());
				}
				enableWidgets();
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

				Shell shell = e.display.getActiveShell();
				File directory = MethodConverter.getUserMethodDirectory();
				if(directory.exists()) {
					createNewMethod(shell, true);
				} else {
					if(selectMethodDirectory(shell)) {
						createNewMethod(shell, true);
					} else {
						MessageDialog.openError(shell, "Method Editor", "Please select a directory via the settings where your methods are located.");
					}
				}
			}
		});
		//
		return button;
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
				File file = getProcessMethodFile(object);
				if(file != null) {
					//
					// supplierEditorSupport.openEditor(file);
					//
					IEclipseContext eclipseContext = Activator.getDefault().getEclipseContext();
					OpenSnippetHandler.openSnippet(ProcessMethodEditor.SNIPPET_ID, eclipseContext, new BiFunction<IEclipseContext, MPart, Runnable>() {

						@Override
						public Runnable apply(IEclipseContext context, MPart part) {

							Set<DataCategory> dataCategories = new HashSet<>();
							dataCategories.add(DataCategory.CSD);
							dataCategories.add(DataCategory.MSD);
							dataCategories.add(DataCategory.WSD);
							context.set(File.class, file);
							return null;
						}
					});
				}
			}
		});
		//
		return button;
	}

	private Button createButtonCopyMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Copy the selected method.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD_COPY, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object objectSource = comboViewerMethods.getStructuredSelection().getFirstElement();
				if(objectSource instanceof IProcessMethod) {
					/*
					 * Container
					 */
					IProcessMethod processMethod = (IProcessMethod)objectSource;
					if(processMethod instanceof ListProcessEntryContainer) {
						if(((ListProcessEntryContainer)processMethod).isReadOnly()) {
							MessageDialog.openInformation(e.display.getActiveShell(), "Copy Method", "You can't copy this method because it is a method container.");
							return;
						}
					}
					/*
					 * Single Method
					 */
					File fileSource = getProcessMethodFile(objectSource);
					if(fileSource != null && fileSource.exists()) {
						if(MessageDialog.openQuestion(e.display.getActiveShell(), "Copy Method", "Do you want to copy the method: " + fileSource.getName() + "?")) {
							/*
							 * Process Method (Source)
							 */
							try (InputStream inputStreamSource = new FileInputStream(fileSource)) {
								IProcessingInfo<IProcessMethod> processingInfoSource = MethodConverter.load(inputStreamSource, fileSource.getAbsolutePath(), null);
								IProcessMethod processMethodSource = processingInfoSource.getProcessingResult();
								/*
								 * Process Method (Sink)
								 */
								File fileSink = createNewMethod(e.display.getActiveShell(), false);
								if(fileSink != null && fileSink.exists()) {
									try (InputStream inputStreamSink = new FileInputStream(fileSink)) {
										IProcessingInfo<IProcessMethod> processingInfoSink = MethodConverter.load(inputStreamSink, fileSource.getAbsolutePath(), null);
										if(processingInfoSink.getProcessingResult() instanceof ProcessMethod) {
											/*
											 * Copy the entries.
											 */
											ProcessMethod processMethodSink = (ProcessMethod)processingInfoSink.getProcessingResult();
											for(IProcessEntry processEntry : processMethodSource) {
												processMethodSink.addProcessEntry(processEntry);
											}
											/*
											 * Save the new method.
											 */
											IProcessingInfo<?> processingInfo = MethodConverter.convert(fileSink, processMethodSink, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
											if(!processingInfo.hasErrorMessages()) {
												/*
												 * Open the editor
												 */
												computeMethodComboItems();
												supplierEditorSupport.openEditor(fileSink);
											}
										}
									} catch(IOException e1) {
										logger.warn(e1);
									}
								}
							} catch(IOException e1) {
								logger.warn(e1);
							}
						}
						return;
					}
					//
					MessageDialog.openInformation(e.display.getActiveShell(), "Copy Method", "Can't determine the file for copying, maybe it was already deleted or you don't have sufficient rights?");
				}
			}
		});
		//
		return button;
	}

	private Button createButtonDeleteMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Delete the selected method.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD_DELETE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewerMethods.getStructuredSelection().getFirstElement();
				if(object instanceof IProcessMethod) {
					IProcessMethod processMethod = (IProcessMethod)object;
					if(processMethod instanceof ListProcessEntryContainer) {
						if(((ListProcessEntryContainer)processMethod).isReadOnly()) {
							MessageDialog.openInformation(e.display.getActiveShell(), "Delete Method", "You can't delete this method because it is read only");
							return;
						}
					}
					File file = getProcessMethodFile(object);
					if(file != null && file.exists()) {
						if(MessageDialog.openQuestion(e.display.getActiveShell(), "Delete Method", "Do you want to delete the method: " + file.getName() + "?")) {
							file.delete();
							PreferenceSupplier.setSelectedMethodName("");
							computeMethodComboItems();
						}
						return;
					}
					MessageDialog.openInformation(e.display.getActiveShell(), "Delete Method", "Can't determine the file for deletion, maybe it was already deleted or you don't have sufficient rights?");
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
				if(object instanceof IProcessMethod) {
					runMethod((IProcessMethod)object, e.display.getActiveShell());
				}
			}
		});
		//
		return button;
	}

	private Button createButtonMethodDirectory(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select the method directory.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectMethodDirectory(e.display.getActiveShell());
				applySettings();
			}
		});
		//
		return button;
	}

	private boolean selectMethodDirectory(Shell shell) {

		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText("Method Directory");
		directoryDialog.setFilterPath(MethodConverter.getUserMethodDirectory().getAbsolutePath());
		//
		String directoryPath = directoryDialog.open();
		if(directoryPath != null && !directoryPath.equals("")) {
			MethodConverter.setUserMethodDirectory(new File(directoryPath));
			return true;
		} else {
			return false;
		}
	}

	private File createNewMethod(Shell shell, boolean openEditor) {

		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setOverwrite(true);
		fileDialog.setText("Process Method");
		fileDialog.setFileName(MethodConverter.DEFAULT_METHOD_FILE_NAME);
		fileDialog.setFilterExtensions(MethodConverter.DEFAULT_METHOD_FILE_EXTENSIONS);
		fileDialog.setFilterNames(MethodConverter.DEFAULT_METHOD_FILE_NAMES);
		fileDialog.setFilterPath(MethodConverter.getUserMethodDirectory().getAbsolutePath());
		//
		File file = null;
		String filePath = fileDialog.open();
		if(filePath != null && !filePath.equals("")) {
			/*
			 * Select a file where the process method shall be stored.
			 */
			file = new File(filePath);
			ProcessMethod processMethod = new ProcessMethod(ProcessMethod.CHROMATOGRAPHY);
			processMethod.setOperator(UserManagement.getCurrentUser());
			processMethod.setDescription("Process Method");
			//
			IProcessingInfo<?> processingInfo = MethodConverter.convert(file, processMethod, MethodConverter.DEFAULT_METHOD_CONVERTER_ID, new NullProgressMonitor());
			if(!processingInfo.hasErrorMessages()) {
				PreferenceSupplier.setSelectedMethodName(file.getName());
				if(openEditor) {
					computeMethodComboItems();
					supplierEditorSupport.openEditor(file);
				}
			} else {
				file = null;
			}
		}
		//
		return file;
	}

	private void computeMethodComboItems() {

		List<IProcessMethod> methods = new ArrayList<IProcessMethod>(MethodConverter.getUserMethods());
		if(methods.size() > 0) {
			Collections.sort(methods, new Comparator<IProcessMethod>() {

				@Override
				public int compare(IProcessMethod o1, IProcessMethod o2) {

					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			comboViewerMethods.setInput(methods);
			if(comboViewerMethods.getCombo().getItemCount() > 0) {
				setSelectedMethod();
			}
		} else {
			comboViewerMethods.setInput(null);
		}
		//
		enableWidgets();
	}

	private void setSelectedMethod() {

		String selectedMethodName = PreferenceSupplier.getSelectedMethodName();
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
			PreferenceSupplier.setSelectedMethodName(combo.getItem(0));
		}
	}

	private void runMethod(IProcessMethod processMethod, Shell shell) {

		if(methodListener != null && processMethod != null) {
			try {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				dialog.run(false, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						methodListener.execute(processMethod, monitor);
					}
				});
			} catch(InvocationTargetException e) {
				ProcessingInfoViewSupport.updateProcessingInfoError(processMethod.getName(), "Execution failed", e.getCause());
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void enableWidgets() {

		buttonAddMethod.setEnabled(true);
		buttonEditMethod.setEnabled(false);
		buttonCopyMethod.setEnabled(false);
		buttonDeleteMethod.setEnabled(false);
		buttonExecuteMethod.setEnabled(false);
		buttonMethodDirectory.setEnabled(true); // Always true
		//
		Object object = comboViewerMethods.getStructuredSelection().getFirstElement();
		if(object instanceof IProcessMethod) {
			boolean editable = getProcessMethodFile(object) != null;
			buttonEditMethod.setEnabled(editable);
			buttonCopyMethod.setEnabled(editable);
			buttonDeleteMethod.setEnabled(editable);
			buttonExecuteMethod.setEnabled(true);
		}
	}

	private File getProcessMethodFile(Object object) {

		if(object instanceof ProcessMethod) {
			return ((ProcessMethod)object).getSourceFile();
		}
		return null;
	}

	@Override
	public IPreferencePage[] getPreferencePages() {

		IPreferencePage preferencePageReportExport = new PreferencePageReportExport();
		preferencePageReportExport.setTitle("Report Export");
		IPreferencePage preferencePageChromatogramExport = new PreferencePageChromatogramExport();
		preferencePageChromatogramExport.setTitle("Chromatogram Export");
		return new IPreferencePage[]{preferencePageReportExport, preferencePageChromatogramExport};
	}

	@Override
	public void applySettings() {

		computeMethodComboItems();
	}
}
