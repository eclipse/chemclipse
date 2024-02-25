/*******************************************************************************
 * Copyright (c) 2012, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.chemclipse.rcp.app.ui.provider.PerspectiveSwitcherContentProvider;
import org.eclipse.chemclipse.rcp.app.ui.provider.PerspectiveSwitcherLabelProvider;
import org.eclipse.chemclipse.rcp.app.ui.provider.PerspectiveSwitcherViewerFilter;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class PerspectiveSwitcherDialog extends Dialog implements ISelectionChangedListener {

	/*
	 * Initial table height and weight
	 */
	private static final int LIST_HEIGHT = 300;
	private static final int LIST_WIDTH = 300;
	/*
	 * The SWT elements
	 */
	private AtomicReference<TableViewer> tableViewerControl = new AtomicReference<>();
	private AtomicReference<Text> textControl = new AtomicReference<>();
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
	@Inject
	private IEventBroker eventBroker;
	/*
	 * Store the previous selected perspectives
	 */
	@Inject
	@Preference(nodePath = "org.eclipse.chemclipse.rcp.app.ui.dialogs.PerspectiveSwitcherDialog")
	private IEclipsePreferences preferences;
	//
	private PerspectiveSwitcherViewerFilter perspectiveSwitcherViewerFilter = new PerspectiveSwitcherViewerFilter();
	private List<MPerspective> perspectives = new ArrayList<>();
	private MPerspective selectedPerspective = null;

	@Inject
	public PerspectiveSwitcherDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {

		super(shell);
		setShellStyle(getShellStyle() | SWT.SHEET);
	}

	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText("Select Perspective");
		//
		initialize();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		/*
		 * Set the perspective selection.
		 */
		if(tableViewerControl.get().getStructuredSelection().getFirstElement() instanceof MPerspective perspective) {
			selectedPerspective = perspective;
		}
		//
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
		createPerspectivesSearchTextField(composite);
		createPerspectivesList(composite);
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
		switchPerspective();
	}

	private void initialize() {

		perspectives.addAll(modelService.findElements(application, null, MPerspective.class, null));
		Collections.sort(perspectives, (p1, p2) -> p1.getLabel().compareTo(p2.getLabel()));
		perspectiveSwitcherViewerFilter.setCaseInsensitive(true);
	}

	private void switchPerspective() {

		if(selectedPerspective != null) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					partService.switchPerspective(selectedPerspective);
					if(eventBroker != null) {
						eventBroker.send(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, selectedPerspective.getLabel());
					}
				}
			});
		}
	}

	/**
	 * Creates a text field to search the list of perspectives.
	 * 
	 * @param parent
	 */
	private void createPerspectivesSearchTextField(Composite parent) {

		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setText("");
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				perspectiveSwitcherViewerFilter.setSearchPattern(text.getText());
				tableViewerControl.get().refresh();
				validateSelection();
			}
		});
		//
		text.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				perspectiveSwitcherViewerFilter.setSearchPattern(text.getText());
				tableViewerControl.get().refresh();
				validateSelection();
			}
		});
		//
		textControl.set(text);
	}

	/**
	 * Creates the list of available perspectives.
	 * 
	 * @param parent
	 */
	private void createPerspectivesList(Composite parent) {

		TableViewer tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = LIST_WIDTH;
		gridData.heightHint = LIST_HEIGHT;
		Control control = tableViewer.getControl();
		control.setLayoutData(gridData);
		/*
		 * Label and content provider
		 */
		tableViewer.setLabelProvider(ContextInjectionFactory.make(PerspectiveSwitcherLabelProvider.class, eclipseContext));
		tableViewer.setContentProvider(new PerspectiveSwitcherContentProvider());
		tableViewer.setInput(perspectives);
		tableViewer.addFilter(perspectiveSwitcherViewerFilter);
		tableViewer.addSelectionChangedListener(this);
		/*
		 * Select the perspective in double click.
		 */
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {

				okPressed();
			}
		});
		//
		tableViewerControl.set(tableViewer);
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
		Table table = tableViewerControl.get().getTable();
		int index = table.getSelectionIndex();
		if(index >= 0) {
			TableItem item = table.getItem(index);
			if(item.getData() instanceof MPerspective) {
				buttonOK.setEnabled(true);
			} else {
				buttonOK.setEnabled(false);
			}
		} else {
			buttonOK.setEnabled(false);
		}
	}
}
