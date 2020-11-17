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
 * Christoph Läubrich - adjust to API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.converter.model.reports.ISequence;
import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSequences;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.ChromatogramTypeSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IExtendedPartUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ISettingsHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.SequenceListUI;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ExtendedSequenceListUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedSequenceListUI.class);
	//
	private static final String DESCRIPTION = "Sequence Editor";
	//
	private Text dataPath;
	private Composite toolbarSearch;
	private Composite toolbarDataPath;
	private Composite toolbarMethod;
	private SearchSupportUI searchSupportUI;
	private MethodSupportUI methodSupportUI;
	private SequenceListUI sequenceListUI;
	//
	private String initialDataPath = "";
	private ISequence<? extends ISequenceRecord> sequence;
	private final ChromatogramTypeSupportUI chromatogramTypeSupport = new ChromatogramTypeSupportUI(new DataType[]{DataType.CSD, DataType.MSD, DataType.WSD});
	private final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private final ProcessSupplierContext processSupplierContext = new ProcessTypeSupport();

	public ExtendedSequenceListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(ISequence<? extends ISequenceRecord> sequence) {

		this.sequence = sequence;
		if(sequence != null) {
			initialDataPath = sequence.getDataPath();
		}
		updateDataSequenceData();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		toolbarSearch = createToolbarSearch(this);
		toolbarDataPath = createToolbarDataPath(this);
		toolbarMethod = createToolbarMethod(this);
		createSequenceList(this);
		//
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		PartSupport.setCompositeVisibility(toolbarDataPath, false);
		PartSupport.setCompositeVisibility(toolbarMethod, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleToolbarSearch(composite);
		createButtonToggleToolbarDataPath(composite);
		createButtonToggleToolbarMethod(composite);
		createBatchOpenButton(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarSearch(Composite parent) {

		searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				sequenceListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private Composite createToolbarDataPath(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(4, false));
		//
		createDataPathLabel(composite);
		createDataPathText(composite);
		createSetDataPathButton(composite);
		createSelectDataPathButton(composite);
		//
		return composite;
	}

	private Composite createToolbarMethod(Composite parent) {

		methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void execute(IProcessMethod processMethod, IProgressMonitor monitor) {

				IProcessingInfo<IChromatogramSelection<?, ?>> processingInfo = new ProcessingInfo<>();
				//
				TableItem[] tableItems = sequenceListUI.getTable().getItems();
				//
				for(TableItem tableItem : tableItems) {
					Object object = tableItem.getData();
					if(object instanceof ISequenceRecord) {
						/*
						 * Get the file.
						 */
						ISequenceRecord sequenceRecord = (ISequenceRecord)object;
						String pathChromatogram = sequence.getDataPath() + File.separator + sequenceRecord.getDataFile();
						IProcessingInfo<IChromatogramSelection<?, ?>> processingInfoChromatogram = chromatogramTypeSupport.getChromatogramSelection(pathChromatogram, monitor);
						if(!processingInfoChromatogram.hasErrorMessages()) {
							IChromatogramSelection<?, ?> chromatogramSelection = processingInfoChromatogram.getProcessingResult();
							addSequenceRecordInformation(sequenceRecord, chromatogramSelection.getChromatogram());
							ProcessingInfo<?> processorInfo = new ProcessingInfo<>();
							ProcessEntryContainer.applyProcessEntries(processMethod, new ProcessExecutionContext(monitor, processorInfo, processSupplierContext), IChromatogramSelectionProcessSupplier.createConsumer(chromatogramSelection));
							if(processorInfo.hasErrorMessages()) {
								processingInfo.addMessages(processorInfo);
							} else {
								processingInfo.addInfoMessage(DESCRIPTION, "Success processing file: " + pathChromatogram);
							}
						} else {
							processingInfo.addErrorMessage(DESCRIPTION, "Failed to process the file: " + pathChromatogram);
						}
					}
				}
				//
				ProcessingInfoViewSupport.updateProcessingInfo(methodSupportUI.getDisplay(), processingInfo, true);
			}
		});
		//
		return methodSupportUI;
	}

	private void addSequenceRecordInformation(ISequenceRecord sequenceRecord, IMeasurement measurement) {

		if(sequenceRecord != null && measurement != null) {
			measurement.putHeaderData("Sequence Description", sequenceRecord.getDescription());
			measurement.putHeaderData("Sequence Method", sequenceRecord.getProcessMethod());
			measurement.putHeaderData("Sequence Sample Name", sequenceRecord.getSampleName());
			measurement.putHeaderData("Sequence Substance", sequenceRecord.getSubstance());
			measurement.putHeaderData("Sequence Multiplier", Double.toString(sequenceRecord.getMultiplier()));
			measurement.putHeaderData("Sequence Vial", Integer.toString(sequenceRecord.getVial()));
		}
	}

	private void createDataPathLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Data Path:");
	}

	private void createDataPathText(Composite parent) {

		dataPath = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		dataPath.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		dataPath.setLayoutData(gridData);
	}

	private Button createSetDataPathButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Set the data path folder.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FILE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(sequence != null) {
					File file = new File(sequence.getCanonicalPath());
					if(file.exists()) {
						sequence.setDataPath(file.getParent());
						updateDataSequenceData();
					}
				}
			}
		});
		//
		return button;
	}

	private Button createSelectDataPathButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select the data path folder.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(sequence != null) {
					DirectoryDialog directoryDialog = new DirectoryDialog(DisplayUtils.getShell(button));
					directoryDialog.setText("Sequence Folder");
					directoryDialog.setMessage("Select the sequence root folder.");
					IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
					directoryDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_DIALOG_FOLDER));
					String directory = directoryDialog.open();
					if(directory != null) {
						preferenceStore.setValue(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_DIALOG_FOLDER, directory);
						sequence.setDataPath(directory);
						updateDataSequenceData();
					}
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

	private Button createButtonToggleToolbarDataPath(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the data path toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarDataPath);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_ACTIVE, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT_DEFAULT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleToolbarMethod(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the method toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarMethod);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createBatchOpenButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the selected chromatograms");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = sequenceListUI.getTable();
				int[] indices = table.getSelectionIndices();
				List<File> files = new ArrayList<>();
				//
				String chromatogramProcessMethod = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_LOAD_PROCESS_METHOD);
				if(!"".equals(chromatogramProcessMethod)) {
					// MessageDialog.openWarning(e.display.getActiveShell(), "Batch Open", "A default chromatogram processing method is currently set.");
				}
				//
				for(int index : indices) {
					Object object = table.getItem(index).getData();
					if(object instanceof ISequenceRecord) {
						ISequenceRecord sequenceRecord = (ISequenceRecord)object;
						files.add(new File(sequence.getDataPath() + File.separator + sequenceRecord.getDataFile()));
					}
				}
				//
				try {
					chromatogramTypeSupport.openFiles(files);
				} catch(Exception e1) {
					showDataPathWarningMessage(e.display.getActiveShell());
					logger.warn(e1);
				}
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the sequence");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageSequences.class, PreferencePageChromatogram.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		sequenceListUI.setComparator();
		searchSupportUI.reset();
		updateDataSequenceData();
	}

	private void reset() {

		searchSupportUI.reset();
		if(sequence != null) {
			sequence.setDataPath(initialDataPath);
		}
		updateDataSequenceData();
	}

	private void createSequenceList(Composite parent) {

		sequenceListUI = new SequenceListUI(parent, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = sequenceListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				Object object = sequenceListUI.getStructuredSelection().getFirstElement();
				if(object instanceof ISequenceRecord) {
					ISequenceRecord sequenceRecord = (ISequenceRecord)object;
					List<File> files = new ArrayList<>();
					files.add(new File(sequence.getDataPath() + File.separator + sequenceRecord.getDataFile()));
					try {
						chromatogramTypeSupport.openFiles(files);
					} catch(Exception e1) {
						showDataPathWarningMessage(e.display.getActiveShell());
						logger.warn(e1);
					}
				}
			}
		});
	}

	private void updateDataSequenceData() {

		if(sequence != null) {
			dataPath.setText(sequence.getDataPath());
			sequenceListUI.setInput(sequence);
		} else {
			dataPath.setText("");
			sequenceListUI.setInput(null);
		}
	}

	private void showDataPathWarningMessage(Shell shell) {

		MessageDialog.openWarning(shell, "Open Chromatogram", "The file doesn't exist. Please check that the data path is set correctly.");
	}
}
