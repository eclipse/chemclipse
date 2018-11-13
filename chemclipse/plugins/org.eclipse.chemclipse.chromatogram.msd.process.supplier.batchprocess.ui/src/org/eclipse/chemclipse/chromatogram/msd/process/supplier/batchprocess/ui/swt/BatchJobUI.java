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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.ui.swt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.IBatchProcessJob;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.converter.model.ChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorSupportFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class BatchJobUI extends Composite {

	private static final Logger logger = Logger.getLogger(BatchJobUI.class);
	//
	private static final String DESCRIPTION = "Batch Job";
	//
	private FileListUI fileListUI;
	private List<ISupplierEditorSupport> supplierEditorSupportList = new ArrayList<>();
	private IBatchProcessJob batchProcessJob;

	public BatchJobUI(Composite parent, int style) {
		super(parent, style);
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.MSD).getInstanceEditorSupport());
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.CSD).getInstanceEditorSupport());
		supplierEditorSupportList.add(new EditorSupportFactory(DataType.WSD).getInstanceEditorSupport());
		createControl();
	}

	public void update(IBatchProcessJob batchProcessJob) {

		this.batchProcessJob = batchProcessJob;
		setProcessFiles();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(1, true));
		//
		createMethodExecutor(composite);
		createFileList(composite);
		createButtons(composite);
	}

	private void createMethodExecutor(Composite parent) {

		MethodSupportUI methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void execute(ProcessMethod processMethod, IProgressMonitor monitor) {

				IProcessingInfo processingInfo = new ProcessingInfo();
				//
				ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
				TableItem[] tableItems = fileListUI.getTable().getItems();
				//
				for(TableItem tableItem : tableItems) {
					Object object = tableItem.getData();
					if(object instanceof File) {
						/*
						 * Get the file.
						 */
						File file = (File)object;
						DataType dataType = detectDataType(file);
						if(dataType != null) {
							IChromatogramSelection chromatogramSelection = getChromatogramSelection(file, dataType, monitor);
							if(chromatogramSelection != null) {
								IProcessingInfo processorInfo = processTypeSupport.applyProcessor(chromatogramSelection, processMethod, monitor);
								if(processorInfo.hasErrorMessages()) {
									processingInfo.addMessages(processorInfo);
								} else {
									processingInfo.addInfoMessage(DESCRIPTION, "Success processing file: " + file);
								}
							} else {
								String message = "Chromatogram Selection is null: " + file + " " + dataType;
								processingInfo.addErrorMessage(DESCRIPTION, message);
								logger.warn(message);
							}
						} else {
							String message = "Could not detect data type of file: " + file;
							processingInfo.addErrorMessage(DESCRIPTION, message);
							logger.warn(message);
						}
					}
				}
				//
				ProcessingInfoViewSupport.updateProcessingInfo(methodSupportUI.getDisplay(), processingInfo, true);
			}
		});
	}

	private DataType detectDataType(File file) {

		String type = "";
		if(file.exists()) {
			exitloop:
			for(ISupplierEditorSupport supplierEditorSupport : supplierEditorSupportList) {
				if(isSupplierFile(supplierEditorSupport, file)) {
					type = supplierEditorSupport.getType();
					break exitloop;
				}
			}
		}
		//
		DataType dataType;
		switch(type) {
			case ISupplierFileIdentifier.TYPE_MSD:
				dataType = DataType.MSD;
				break;
			case ISupplierFileIdentifier.TYPE_CSD:
				dataType = DataType.CSD;
				break;
			case ISupplierFileIdentifier.TYPE_WSD:
				dataType = DataType.WSD;
				break;
			default:
				dataType = null;
				break;
		}
		//
		return dataType;
	}

	@SuppressWarnings("rawtypes")
	private IChromatogramSelection getChromatogramSelection(File file, DataType dataType, IProgressMonitor monitor) {

		IChromatogramSelection chromatogramSelection = null;
		boolean fireUpdate = false;
		//
		switch(dataType) {
			case MSD_NOMINAL:
			case MSD_TANDEM:
			case MSD_HIGHRES:
			case MSD:
				IProcessingInfo processingInfoMSD = ChromatogramConverterMSD.convert(file, monitor);
				IChromatogramMSD chromatogramMSD = processingInfoMSD.getProcessingResult(IChromatogramMSD.class);
				chromatogramSelection = new ChromatogramSelectionMSD(chromatogramMSD, fireUpdate);
				break;
			case CSD:
				IProcessingInfo processingInfoCSD = ChromatogramConverterCSD.convert(file, monitor);
				IChromatogramCSD chromatogramCSD = processingInfoCSD.getProcessingResult(IChromatogramCSD.class);
				chromatogramSelection = new ChromatogramSelectionCSD(chromatogramCSD, fireUpdate);
				break;
			case WSD:
				IProcessingInfo processingInfoWSD = ChromatogramConverterWSD.convert(file, monitor);
				IChromatogramWSD chromatogramWSD = processingInfoWSD.getProcessingResult(IChromatogramWSD.class);
				chromatogramSelection = new ChromatogramSelectionWSD(chromatogramWSD, fireUpdate);
				break;
			default:
				// No action
		}
		//
		return chromatogramSelection;
	}

	private boolean isSupplierFile(ISupplierEditorSupport supplierEditorSupport, File file) {

		if(file.isDirectory()) {
			if(supplierEditorSupport.isSupplierFileDirectory(file)) {
				return true;
			}
		} else {
			if(supplierEditorSupport.isSupplierFile(file)) {
				return true;
			}
		}
		return false;
	}

	private void createFileList(Composite parent) {

		fileListUI = new FileListUI(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		fileListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createButtons(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		createMoveUpButton(composite);
		createMoveDownButton(composite);
		createRemoveButton(composite);
		createAddButton(composite);
	}

	private void createMoveUpButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move import record(s) up");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = fileListUI.getTable().getSelectionIndex();
				if(index != -1) {
					//
					setProcessFiles();
				}
			}
		});
	}

	private void createMoveDownButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move import record(s) down");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int index = fileListUI.getTable().getSelectionIndex();
				if(index != -1) {
					//
					setProcessFiles();
				}
			}
		});
	}

	private void createRemoveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Remove the import record(s)");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REMOVE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TableItem[] tableItems = fileListUI.getTable().getSelection();
				for(TableItem tableItem : tableItems) {
					Object object = tableItem.getData();
					if(object instanceof File) {
						//
					}
				}
				setProcessFiles();
			}
		});
	}

	private void createAddButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add new chromatogram(s)");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				InputWizardSettings inputWizardSettings = new InputWizardSettings(new InputWizardSettings.DataType[]{InputWizardSettings.DataType.MSD_CHROMATOGRAM, InputWizardSettings.DataType.CSD_CHROMATOGRAM, InputWizardSettings.DataType.WSD_CHROMATOGRAM});
				inputWizardSettings.setTitle("Chromatogram");
				inputWizardSettings.setDescription("Select chromatogram(s) to analyze.");
				inputWizardSettings.setPathPreferences(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.P_FILTER_PATH_IMPORT_RECORDS);
				//
				InputEntriesWizard inputWizard = new InputEntriesWizard(inputWizardSettings);
				WizardDialog wizardDialog = new WizardDialog(e.widget.getDisplay().getActiveShell(), inputWizard);
				wizardDialog.setMinimumPageSize(InputWizardSettings.DEFAULT_WIDTH, InputWizardSettings.DEFAULT_HEIGHT);
				wizardDialog.create();
				//
				if(wizardDialog.open() == WizardDialog.OK) {
					IChromatogramWizardElements chromatogramWizardElements = inputWizard.getChromatogramWizardElements();
					for(String selectedChromatogram : chromatogramWizardElements.getSelectedChromatograms()) {
						File file = new File(selectedChromatogram);
						if(file.exists()) {
							if(batchProcessJob != null) {
								batchProcessJob.getChromatogramInputEntries().add(new ChromatogramInputEntry(file.getAbsolutePath()));
							}
						}
					}
					//
					setProcessFiles();
				}
			}
		});
	}

	private void setProcessFiles() {

		if(batchProcessJob == null) {
			fileListUI.setInput(null);
		} else {
			List<File> files = new ArrayList<>();
			for(IChromatogramInputEntry inputEntry : batchProcessJob.getChromatogramInputEntries()) {
				File file = new File(inputEntry.getInputFile());
				if(file.exists()) {
					files.add(file);
				}
			}
			fileListUI.setInput(files);
		}
	}
}
