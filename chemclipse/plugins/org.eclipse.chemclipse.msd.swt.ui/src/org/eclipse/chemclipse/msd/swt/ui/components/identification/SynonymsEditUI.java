/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.identification;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SynonymsEditUI extends Composite {

	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private SynonymsListUI synonymsListUI;
	//
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	//
	private Text textSynonym;
	private Button buttonSynonymAdd;
	//
	private ILibraryInformation libraryInformation;

	public SynonymsEditUI(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public void update(ILibraryInformation libraryInformation, boolean forceReload) {

		if(libraryInformation != null) {
			this.libraryInformation = libraryInformation;
			synonymsListUI.setInput(libraryInformation.getSynonyms());
		} else {
			libraryInformation = null;
		}
	}

	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonField(composite);
		createTableField(composite);
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createButtonField(Composite composite) {

		/*
		 * Text synonym
		 */
		textSynonym = new Text(composite, SWT.BORDER);
		textSynonym.setText("");
		textSynonym.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		buttonSynonymAdd = new Button(composite, SWT.PUSH);
		buttonSynonymAdd.setText("Add");
		buttonSynonymAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		buttonSynonymAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				String synonym = textSynonym.getText().trim();
				if("".equals(synonym)) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("Add synonym");
					messageBox.setMessage("Please type in a new synonym.");
					messageBox.open();
				} else {
					if(libraryInformation.getSynonyms().contains(synonym)) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
						messageBox.setText("Add synonym");
						messageBox.setMessage("The synonym already exists.");
						messageBox.open();
					} else {
						libraryInformation.getSynonyms().add(synonym);
						textSynonym.setText("");
						synonymsListUI.update(libraryInformation, true);
						enableButtonFields(ACTION_INITIALIZE);
					}
				}
			}
		});
		/*
		 * Buttons
		 */
		Composite compositeButtons = new Composite(composite, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(3, true));
		GridData gridDataComposite = new GridData();
		gridDataComposite.horizontalAlignment = SWT.RIGHT;
		compositeButtons.setLayoutData(gridDataComposite);
		//
		buttonCancel = new Button(compositeButtons, SWT.PUSH);
		buttonCancel.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CANCEL, IApplicationImage.SIZE_16x16));
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_CANCEL);
			}
		});
		//
		buttonDelete = new Button(compositeButtons, SWT.PUSH);
		buttonDelete.setEnabled(false);
		buttonDelete.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		buttonDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Table table = synonymsListUI.getTable();
				int index = table.getSelectionIndex();
				if(index >= 0) {
					MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					messageBox.setText("Delete synonym(s)?");
					messageBox.setMessage("Would you like to delete the synonym(s)?");
					if(messageBox.open() == SWT.OK) {
						//
						enableButtonFields(ACTION_DELETE);
						TableItem[] tableItems = table.getSelection();
						for(TableItem tableItem : tableItems) {
							Object object = tableItem.getData();
							if(object instanceof String) {
								libraryInformation.getSynonyms().remove(object);
							}
						}
						synonymsListUI.update(libraryInformation, true);
					}
				}
			}
		});
		//
		buttonAdd = new Button(compositeButtons, SWT.PUSH);
		buttonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		buttonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_ADD);
			}
		});
	}

	private void createTableField(Composite composite) {

		Composite compositeTable = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		compositeTable.setLayoutData(gridData);
		compositeTable.setLayout(new FillLayout());
		//
		synonymsListUI = new SynonymsListUI(compositeTable, SWT.BORDER | SWT.MULTI);
		synonymsListUI.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				enableButtonFields(ACTION_SELECT);
			}
		});
	}

	private void enableButtonFields(String action) {

		enableFields(false);
		switch(action) {
			case ACTION_INITIALIZE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_CANCEL:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_ADD:
				buttonCancel.setEnabled(true);
				textSynonym.setEnabled(true);
				buttonSynonymAdd.setEnabled(true);
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				//
				if(synonymsListUI.getTable().getSelectionIndex() >= 0) {
					buttonDelete.setEnabled(true);
				} else {
					buttonDelete.setEnabled(false);
				}
				break;
		}
	}

	private void enableFields(boolean enabled) {

		buttonCancel.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		//
		textSynonym.setEnabled(enabled);
		buttonSynonymAdd.setEnabled(enabled);
	}
}
