/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - migrate from InputPart to Part
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.views;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.support.PropertiesUtil;
import org.eclipse.chemclipse.logging.ui.editors.LogFileEditor;
import org.eclipse.chemclipse.logging.ui.internal.support.LogFileContentProvider;
import org.eclipse.chemclipse.logging.ui.internal.support.LogFileLabelProvider;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.ui.di.Focus;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class LogFileView {

	@Inject
	private EPartService partService;
	@Inject
	private EModelService modelService;
	@Inject
	private MApplication application;
	private final TableViewer tableViewer;

	@Inject
	public LogFileView(Composite parent) {

		/*
		 * Show the log files.
		 */
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new LogFileContentProvider());
		tableViewer.setLabelProvider(new LogFileLabelProvider());
		tableViewer.setInput(new File(PropertiesUtil.getLogFilePath()));
		/*
		 * Open the log file in a text editor.
		 */
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				Object data = getSelectedObject();
				if(data != null && data instanceof File) {
					File file = (File)data;
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
		});
		/*
		 * Key listener to delete files.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * Delete the log file.
				 */
				if(e.keyCode == 127) {
					Object[] elements = getSelectedObjects();
					deleteLogFiles(elements);
				}
			}
		});
	}

	@Focus
	public void setFocus() {

		tableViewer.getControl().setFocus();
	}

	/**
	 * Deletes the given log files.
	 * 
	 * @param elements
	 */
	private void deleteLogFiles(Object[] elements) {

		if(elements != null && elements.length > 0) {
			/*
			 * Delete all selected elements.
			 */
			MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			messageBox.setText("Delete Log File(s)?");
			messageBox.setMessage("Do you really want to delete the selected log file(s)?");
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
		return data;
	}

	/**
	 * Returns the selected objects.
	 * 
	 * @return Object[]
	 */
	private Object[] getSelectedObjects() {

		Object[] data = null;
		/*
		 * Try to get the items.
		 */
		Table table = tableViewer.getTable();
		TableItem[] selectedItems = table.getSelection();
		int items = selectedItems.length;
		if(items > 0) {
			data = new Object[items];
			for(int item = 0; item < items; item++) {
				data[item] = selectedItems[item].getData();
			}
		}
		return data;
	}
}
