/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.SequenceFileRunnable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSequences;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

public class ExtendedSequenceExplorerUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedSequenceExplorerUI.class);
	//
	private Composite toolbarSearch;
	private ComboViewer rootFolderComboViewer;
	private ComboViewer subFolderComboViewer;
	private SequenceFilesUI sequenceFilesUI;
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private ISupplierEditorSupport supplierEditorSupport = new SupplierEditorSupport(DataType.SEQ, () -> Activator.getDefault().getEclipseContext());

	public ExtendedSequenceExplorerUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public boolean setFocus() {

		sequenceFilesUI.getTable().setFocus();
		return true;
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		setBackground(DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//
		createToolbarMain(this);
		toolbarSearch = createToolbarSearch(this);
		createSequenceSelection(this);
		//
		PartSupport.setCompositeVisibility(toolbarSearch, false);
		applySettings();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleToolbarSearch(composite);
		createResetButton(composite);
		createSettingsButton(composite);
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

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the sequence file editor.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				//
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageSequences.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private Composite createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				sequenceFilesUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private void createSequenceSelection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		//
		rootFolderComboViewer = createRootFolderComboViewer(composite);
		createRootFolderButton(composite);
		subFolderComboViewer = createSubFolderComboViewer(composite);
		sequenceFilesUI = createSequenceFileList(composite);
		createBatchOpenButton(composite);
	}

	private ComboViewer createRootFolderComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
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
		combo.setToolTipText("Select a folder.");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof File) {
					//
					File file = (File)object;
					preferenceStore.setValue(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_PARENT_FOLDER, file.getAbsolutePath());
					if(preferenceStore.getBoolean(PreferenceConstants.P_SEQUENCE_EXPLORER_USE_SUBFOLDER)) {
						List<File> files = getDirectories(file);
						setSubFolderContent(files);
					} else {
						setSequenceListContent(file);
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private Button createRootFolderButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select the root folder.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				DirectoryDialog directoryDialog = new DirectoryDialog(DisplayUtils.getShell(button));
				directoryDialog.setText("Sequence Folder");
				directoryDialog.setMessage("Select the sequence root folder.");
				directoryDialog.setFilterPath(preferenceStore.getString(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER));
				String directory = directoryDialog.open();
				if(directory != null) {
					List<File> files = getDirectories(new File(directory));
					preferenceStore.setValue(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER, directory);
					setRootFolderContent(files);
				}
			}
		});
		//
		return button;
	}

	private ComboViewer createSubFolderComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
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
		combo.setToolTipText("Select a sub folder.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof File) {
					File file = (File)object;
					if(file.isDirectory()) {
						preferenceStore.setValue(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_SUB_FOLDER, file.getAbsolutePath());
						setSequenceListContent(file);
					}
				}
			}
		});
		//
		return comboViewer;
	}

	private SequenceFilesUI createSequenceFileList(Composite parent) {

		SequenceFilesUI sequenceFilesUI = new SequenceFilesUI(parent, SWT.BORDER | SWT.MULTI);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		Table table = sequenceFilesUI.getTable();
		table.setLayoutData(gridData);
		//
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				Object object = sequenceFilesUI.getStructuredSelection().getFirstElement();
				if(object instanceof File) {
					File file = (File)object;
					supplierEditorSupport.openEditor(file, false);
				}
			}
		});
		//
		return sequenceFilesUI;
	}

	private Button createBatchOpenButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("Open Sequence(s)");
		button.setToolTipText("Open the selected sequences.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_IMPORT, IApplicationImage.SIZE_16x16));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = sequenceFilesUI.getTable();
				int[] indices = table.getSelectionIndices();
				for(int index : indices) {
					Object object = table.getItem(index).getData();
					if(object instanceof File) {
						File file = (File)object;
						supplierEditorSupport.openEditor(file, true);
					}
				}
			}
		});
		//
		return button;
	}

	private List<File> getDirectories(File file) {

		List<File> files = new ArrayList<>();
		if(file.isDirectory()) {
			for(File subFile : file.listFiles()) {
				if(subFile.isDirectory()) {
					files.add(subFile);
				}
			}
		}
		return files;
	}

	private void setRootFolderContent(List<File> files) {

		Collections.sort(files);
		rootFolderComboViewer.setInput(files);
		//
		if(files.size() > 0) {
			int index = getSelectedDirectoryIndex(files, preferenceStore.getString(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_PARENT_FOLDER));
			File file = files.get(index);
			rootFolderComboViewer.getCombo().select(index);
			//
			List<File> subfiles = getDirectories(file);
			setSubFolderContent(subfiles);
		}
	}

	private void setSubFolderContent(List<File> files) {

		Collections.sort(files);
		subFolderComboViewer.setInput(files);
		//
		if(files.size() > 0) {
			int index = getSelectedDirectoryIndex(files, preferenceStore.getString(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_SUB_FOLDER));
			File file = files.get(index);
			subFolderComboViewer.getCombo().select(index);
			setSequenceListContent(file);
		}
	}

	private int getSelectedDirectoryIndex(List<File> files, String absolutePathDirectory) {

		int index = 0;
		exitloop:
		for(int i = 0; i < files.size(); i++) {
			if(files.get(i).getAbsolutePath().equals(absolutePathDirectory)) {
				index = i;
				break exitloop;
			}
		}
		return index;
	}

	private void setSequenceListContent(File file) {

		if(file.isDirectory()) {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(DisplayUtils.getShell());
			SequenceFileRunnable runnable = new SequenceFileRunnable(file);
			try {
				dialog.run(true, true, runnable);
				List<File> files = runnable.getSequenceFiles();
				sequenceFilesUI.setInput(files);
			} catch(Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} else {
			sequenceFilesUI.setInput(null);
		}
	}

	private void applySettings() {

		File file = new File(preferenceStore.getString(PreferenceConstants.P_SEQUENCE_EXPLORER_PATH_ROOT_FOLDER));
		if(file.exists() && file.isDirectory()) {
			List<File> files = getDirectories(file);
			setRootFolderContent(files);
		}
		//
		if(preferenceStore.getBoolean(PreferenceConstants.P_SEQUENCE_EXPLORER_USE_SUBFOLDER)) {
			subFolderComboViewer.getCombo().setEnabled(true);
		} else {
			subFolderComboViewer.getCombo().setEnabled(false);
		}
	}
}
