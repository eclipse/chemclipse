/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.dialogs;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.eclipse.chemclipse.rcp.app.ui.provider.SelectViewContentProvider;
import org.eclipse.chemclipse.rcp.app.ui.provider.SelectViewFilter;
import org.eclipse.chemclipse.rcp.app.ui.provider.SelectViewLabelProvider;

public class SelectViewDialog extends Dialog implements ISelectionChangedListener {

	/*
	 * Initial table height and weight
	 */
	private final static int LIST_HEIGHT = 300;
	private final static int LIST_WIDTH = 300;
	/*
	 * The SWT elements
	 */
	private TableViewer tableViewer;
	private SelectViewFilter selectViewFilter;
	private Text text;
	/*
	 * Context and services
	 */
	@Inject
	private IEclipseContext eclipseContext;
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	/*
	 * 
	 */
	private List<MPart> parts;
	private MPart selectedPart;

	@Inject
	public SelectViewDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.SHEET);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText("Select View");
		parts = modelService.findElements(application, null, MPart.class, null);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		/*
		 * Set the selected perspective.
		 */
		Table table = tableViewer.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem item = table.getItem(index);
			if(item.getData() instanceof MPart) {
				selectedPart = (MPart)item.getData();
			}
		}
		validateSelection();
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * 
	 * @param parent
	 *            the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(1, true));
		/*
		 * Create the SWT elements
		 */
		createViewSearchTextField(composite);
		createViewList(composite);
		/*
		 * Enable / disable the OK button.
		 */
		validateSelection();
		//
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {

		super.okPressed();
		selectAndActivatePart();
	}

	private void selectAndActivatePart() {

		if(selectedPart != null) {
			/*
			 * If the selected part is not in the list, create it.
			 */
			if(!partService.getParts().contains(selectedPart)) {
				partService.createPart(selectedPart.getElementId());
			}
			partService.showPart(selectedPart, PartState.ACTIVATE);
		}
	}

	/**
	 * Creates a text field to search the list of perspectives.
	 * 
	 * @param parent
	 */
	private void createViewSearchTextField(Composite parent) {

		text = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setText("");
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				selectViewFilter.setSearchPattern(text.getText());
				tableViewer.refresh();
				validateSelection();
			}
		});
		text.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				selectViewFilter.setSearchPattern(text.getText());
				tableViewer.refresh();
				validateSelection();
			}
		});
	}

	/**
	 * Creates the list of available perspectives.
	 * 
	 * @param parent
	 */
	private void createViewList(Composite parent) {

		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = LIST_WIDTH;
		gridData.heightHint = LIST_HEIGHT;
		Control control = tableViewer.getControl();
		control.setLayoutData(gridData);
		/*
		 * Label and content provider
		 */
		tableViewer.setLabelProvider(ContextInjectionFactory.make(SelectViewLabelProvider.class, eclipseContext));
		tableViewer.setContentProvider(new SelectViewContentProvider());
		tableViewer.setInput(parts);
		selectViewFilter = new SelectViewFilter();
		selectViewFilter.setCaseInsensitive(true);
		tableViewer.addFilter(selectViewFilter);
		tableViewer.addSelectionChangedListener(this);
		/*
		 * Select the perspective in double click.
		 */
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {

				okPressed();
			}
		});
	}

	/**
	 * Validates whether the OK button is enabled or not.
	 */
	private void validateSelection() {

		Button buttonOK = getButton(IDialogConstants.OK_ID);
		if(buttonOK == null) {
			return;
		}
		/*
		 * Check if an item has been selected and if it
		 * is an instance of MPerspective.
		 */
		Table table = tableViewer.getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem item = table.getItem(index);
			if(item.getData() instanceof MPart) {
				buttonOK.setEnabled(true);
			} else {
				buttonOK.setEnabled(false);
			}
		} else {
			buttonOK.setEnabled(false);
		}
	}
}
