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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.dialogs.QuantitationCompoundEditDialog;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.dialogs.QuantitationCompoundEntryEdit;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageQuantitation;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.QuantCompoundListUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class ExtendedQuantCompoundListUI {

	private static final Logger logger = Logger.getLogger(ExtendedQuantCompoundListUI.class);
	private static final String DESCRIPTION = "Quantitation Compound(s)";
	//
	private Composite toolbarSearch;
	private SearchSupportUI searchSupportUI;
	private QuantCompoundListUI quantCompundListUI;
	//
	private IQuantitationDatabase quantitationDatabase = null;

	public ExtendedQuantCompoundListUI(Composite parent) {
		initialize(parent);
	}

	public void update(IQuantitationDatabase quantitationDatabase) {

		this.quantitationDatabase = quantitationDatabase;
		setQuantitationDatabase();
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarSearch = createToolbarSearch(parent);
		createTable(parent);
		//
		PartSupport.setCompositeVisibility(toolbarSearch, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createAddButton(composite);
		createEditButton(composite);
		createButtonToggleToolbarSearch(composite);
		createResetButton(composite);
		createSettingsButton(composite);
	}

	private Button createAddButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Add a new entry.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(quantitationDatabase != null) {
					QuantitationCompoundEntryEdit quantitationCompoundEntryEdit = new QuantitationCompoundEntryEdit();
					QuantitationCompoundEditDialog dialog = new QuantitationCompoundEditDialog(e.display.getActiveShell(), quantitationCompoundEntryEdit, "Create a new quantitation compound.", quantitationDatabase, true);
					if(dialog.open() == IDialogConstants.OK_ID) {
						try {
							IQuantitationCompound quantitationCompound = quantitationCompoundEntryEdit.getQuantitationCompound();
							quantitationDatabase.add(quantitationCompound);
							setQuantitationDatabase();
						} catch(Exception e1) {
							logger.warn(e1);
							MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "The quantitation compound already exists in the database.");
						}
					}
				} else {
					MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "No valid database has been selected.");
				}
			}
		});
		//
		return button;
	}

	private Button createEditButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Edit an entry.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public void widgetSelected(SelectionEvent e) {

				IQuantitationCompound quantitationCompound = getSelectedQuantitationCompound();
				if(quantitationCompound != null) {
					if(quantitationDatabase != null) {
						/*
						 * Catch if the database is not available.
						 */
						Shell shell = Display.getCurrent().getActiveShell();
						QuantitationCompoundEntryEdit quantitationCompoundEntryEdit = new QuantitationCompoundEntryEdit();
						IQuantitationCompound quantitationCompoundOld = quantitationCompound;
						quantitationCompoundEntryEdit.setQuantitationCompoundMSD(quantitationCompoundOld);
						QuantitationCompoundEditDialog dialog = new QuantitationCompoundEditDialog(shell, quantitationCompoundEntryEdit, "Edit the quantitation compound.", quantitationDatabase, false);
						/*
						 * Yes, edit the document.
						 */
						if(dialog.open() == IDialogConstants.OK_ID) {
							/*
							 * Get the edited compound and set the quantitation
							 * signals and concentration response entries.
							 */
							IQuantitationCompound quantitationCompoundNew = quantitationCompoundEntryEdit.getQuantitationCompound();
							quantitationCompoundNew.updateQuantitationSignals(quantitationCompoundOld.getQuantitationSignals());
							quantitationCompoundNew.updateConcentrationResponseEntries(quantitationCompoundOld.getConcentrationResponseEntries());
							quantitationCompound.updateQuantitationCompound(quantitationCompoundNew);
							setQuantitationDatabase();
						}
					} else {
						MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "No valid database has been selected.");
					}
				} else {
					MessageDialog.openError(e.display.getActiveShell(), DESCRIPTION, "Please select a quantitation compound to edit.");
				}
			}
		});
		//
		return button;
	}

	private Composite createToolbarSearch(Composite parent) {

		searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				quantCompundListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
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
		button.setToolTipText("Reset");
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

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PreferencePageQuantitation()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		searchSupportUI.reset();
	}

	private void reset() {

		searchSupportUI.reset();
	}

	@SuppressWarnings("rawtypes")
	private void createTable(Composite parent) {

		quantCompundListUI = new QuantCompoundListUI(parent, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = quantCompundListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		quantCompundListUI.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = quantCompundListUI.getStructuredSelection().getFirstElement();
				if(object instanceof IQuantitationCompound) {
					IQuantitationCompound quantitationCompound = (IQuantitationCompound)object;
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					parent.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {

							eventBroker.send(IChemClipseEvents.TOPIC_QUANT_DB_COMPOUND_UPDATE, quantitationCompound);
						}
					});
				}
			}
		});
	}

	private void setQuantitationDatabase() {

		if(quantitationDatabase != null) {
			quantCompundListUI.setInput(quantitationDatabase);
		} else {
			quantCompundListUI.clear();
		}
	}

	@SuppressWarnings("rawtypes")
	private IQuantitationCompound getSelectedQuantitationCompound() {

		IQuantitationCompound quantitationCompound = null;
		Object element = quantCompundListUI.getStructuredSelection().getFirstElement();
		if(element instanceof IQuantitationCompound) {
			quantitationCompound = (IQuantitationCompound)element;
		}
		return quantitationCompound;
	}
}
