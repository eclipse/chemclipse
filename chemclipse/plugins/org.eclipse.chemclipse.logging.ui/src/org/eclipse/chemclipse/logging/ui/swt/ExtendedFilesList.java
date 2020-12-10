/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.swt;

import java.io.File;

import org.eclipse.chemclipse.logging.support.PropertiesUtil;
import org.eclipse.chemclipse.logging.ui.Activator;
import org.eclipse.chemclipse.logging.ui.editors.LogFileEditor;
import org.eclipse.chemclipse.logging.ui.internal.support.LogFileContentProvider;
import org.eclipse.chemclipse.logging.ui.internal.support.LogFileLabelProvider;
import org.eclipse.chemclipse.logging.ui.support.FilesSupport;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ExtendedFilesList extends Composite {

	private Label labelInfo;
	private Button buttonDelete;
	private Button buttonDeleteAll;
	private TableViewer tableViewer;
	//
	private EPartService partService;
	private EModelService modelService;
	private MApplication application;

	public ExtendedFilesList(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(EPartService partService, EModelService modelService, MApplication application) {

		this.partService = partService;
		this.modelService = modelService;
		this.application = application;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		labelInfo = createLabel(composite);
		tableViewer = createTableViewer(composite);
		//
		initialize();
	}

	private void initialize() {

		buttonDelete.setEnabled(false);
		buttonDeleteAll.setEnabled(false);
		updateInput();
		updateLabel();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(4, false));
		//
		buttonDelete = createButtonDelete(composite);
		buttonDeleteAll = createButtonDeleteAll(composite);
		createButtonReset(composite);
		createButtonExplorer(composite);
	}

	private Label createLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return label;
	}

	private Button createButtonDelete(Composite parent) {

		Button button = new Button(parent, SWT.NONE);
		button.setText("");
		button.setToolTipText("Delete selected log files.");
		button.setImage(Activator.getDefault().getImage(Activator.ICON_DELETE));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteFiles(getSelectedObjects(), e.display);
			}
		});
		//
		return button;
	}

	private Button createButtonDeleteAll(Composite parent) {

		Button button = new Button(parent, SWT.NONE);
		button.setText("");
		button.setToolTipText("Delete all log files.");
		button.setImage(Activator.getDefault().getImage(Activator.ICON_DELETE_ALL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteFiles(getAllObjects(), e.display);
			}
		});
		//
		return button;
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.NONE);
		button.setText("");
		button.setToolTipText("Reload all files of the directory.");
		button.setImage(Activator.getDefault().getImage(Activator.ICON_RESET));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateInput();
			}
		});
		//
		return button;
	}

	private Button createButtonExplorer(Composite parent) {

		Button button = new Button(parent, SWT.NONE);
		button.setText("");
		button.setToolTipText("Open the log directory with the system file explorer.");
		button.setImage(Activator.getDefault().getImage(Activator.ICON_FOLDER_OPENED));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Program.launch(PropertiesUtil.getLogFilePath());
			}
		});
		//
		return button;
	}

	private TableViewer createTableViewer(Composite parent) {

		TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new LogFileContentProvider());
		tableViewer.setLabelProvider(new LogFileLabelProvider());
		//
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Open the log file in a text editor.
		 */
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				Object data = getSelectedObject();
				if(data != null && data instanceof File) {
					File file = (File)data;
					if(modelService != null && partService != null && application != null) {
						/*
						 * Get the editor part stack.
						 */
						MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
						/*
						 * Create the input part and prepare it.
						 */
						MPart part = MBasicFactory.INSTANCE.createPart();
						part.setElementId(LogFileEditor.ID);
						part.setContributionURI(LogFileEditor.CONTRIBUTION_URI);
						part.setObject(file.getAbsolutePath());
						part.setIconURI(LogFileEditor.ICON_URI);
						part.setLabel(file.getName());
						part.setTooltip(LogFileEditor.TOOLTIP);
						part.setCloseable(true);
						/*
						 * Add it to the stack and show it.
						 */
						partStack.getChildren().add(part);
						partService.showPart(part, PartState.ACTIVATE);
					}
				}
			}
		});
		/*
		 * Key listener to delete files.
		 */
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.DEL) {
					Object[] elements = getSelectedObjects();
					deleteFiles(elements, e.display);
				}
			}
		});
		//
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				buttonDelete.setEnabled(getSelectedObjects() != null);
				buttonDeleteAll.setEnabled(tableViewer.getTable().getItemCount() > 0);
			}
		});
		//
		return tableViewer;
	}

	/**
	 * Deletes the given log files.
	 * 
	 * @param elements
	 */
	private void deleteFiles(Object[] elements, Display display) {

		if(elements != null && elements.length > 0) {
			/*
			 * Delete all selected elements.
			 */
			MessageBox messageBox = new MessageBox(display.getActiveShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			messageBox.setText("Log File(s)?");
			messageBox.setMessage("Do you really want to delete the log file(s)?");
			/*
			 * If the user agreed to delete the log files.
			 */
			if(messageBox.open() == SWT.YES) {
				for(Object element : elements) {
					if(element instanceof File) {
						File file = (File)element;
						file.delete();
						tableViewer.remove(element);
					}
				}
			}
			//
			updateLabel();
		}
	}

	/**
	 * The method returns the selected table object or null, if none is selected.
	 * 
	 * @return Object
	 */
	private Object getSelectedObject() {

		Object data = null;
		/*
		 * Try to get the table item.
		 */
		Object[] items = getSelectedObjects();
		if(items != null) {
			data = items[0];
		}
		//
		return data;
	}

	/**
	 * Returns the selected objects.
	 * 
	 * @return Object[]
	 */
	private Object[] getSelectedObjects() {

		return getObjects(tableViewer.getTable().getSelection());
	}

	private Object[] getAllObjects() {

		return getObjects(tableViewer.getTable().getItems());
	}

	/**
	 * Returns the selected objects.
	 * 
	 * @return Object[]
	 */
	private Object[] getObjects(TableItem[] tableItems) {

		Object[] data = null;
		//
		int items = tableItems.length;
		if(items > 0) {
			data = new Object[items];
			for(int item = 0; item < items; item++) {
				data[item] = tableItems[item].getData();
			}
		}
		//
		return data;
	}

	private void updateLabel() {

		labelInfo.setText("Used space on disk: " + FilesSupport.getUsedSpaceInMegabytes() + " MB");
	}

	private void updateInput() {

		tableViewer.setInput(FilesSupport.getLogDirectory());
	}
}
