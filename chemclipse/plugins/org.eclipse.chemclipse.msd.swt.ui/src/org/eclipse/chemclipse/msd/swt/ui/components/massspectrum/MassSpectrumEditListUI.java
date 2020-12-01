/*******************************************************************************
 * Copyright (c) 2016, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.components.massspectrum;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class MassSpectrumEditListUI extends Composite {

	private static final Logger logger = Logger.getLogger(MassSpectrumEditListUI.class);
	//
	private static final String ACTION_INITIALIZE = "ACTION_INITIALIZE";
	private static final String ACTION_CANCEL = "ACTION_CANCEL";
	private static final String ACTION_ADD = "ACTION_ADD";
	private static final String ACTION_DELETE = "ACTION_DELETE";
	private static final String ACTION_SELECT = "ACTION_SELECT";
	//
	private MassSpectrumIonsListUI massSpectrumIonsListUI;
	//
	private Button buttonCancel;
	private Button buttonDelete;
	private Button buttonAdd;
	//
	private Text textMz;
	private Text textIntensity;
	private Button buttonIonAdd;
	//
	private IScanMSD massSpectrum;

	public MassSpectrumEditListUI(Composite parent, int style) {

		super(parent, style);
		initialize();
	}

	public void update(IScanMSD massSpectrum, boolean forceReload) {

		this.massSpectrum = massSpectrum;
		if(massSpectrum != null) {
			massSpectrumIonsListUI.setInput(massSpectrum);
		} else {
			massSpectrumIonsListUI.setInput(null);
		}
	}

	private void initialize() {

		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(7, false));
		//
		createButtonField(composite);
		createTableField(composite);
		//
		enableButtonFields(ACTION_INITIALIZE);
	}

	private void createButtonField(Composite composite) {

		Label labelMz = new Label(composite, SWT.NONE);
		labelMz.setText("m/z");
		//
		textMz = new Text(composite, SWT.BORDER);
		textMz.setText("");
		textMz.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		Label labelIntensity = new Label(composite, SWT.NONE);
		labelIntensity.setText("abundance");
		//
		textIntensity = new Text(composite, SWT.BORDER);
		textIntensity.setText("");
		textIntensity.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		buttonIonAdd = new Button(composite, SWT.PUSH);
		buttonIonAdd.setText("Add");
		buttonIonAdd.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		buttonIonAdd.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				if(massSpectrum == null) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					messageBox.setText("Add ion");
					messageBox.setMessage("No mass spectrum has been selected.");
					messageBox.open();
				} else {
					try {
						double mz = Double.parseDouble(textMz.getText().trim());
						float intensity = Float.parseFloat(textIntensity.getText().trim());
						IIon ion = new Ion(mz, intensity);
						if(massSpectrum.getIons().contains(ion)) {
							MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
							messageBox.setText("Add ion");
							messageBox.setMessage("The ion exists already.");
							messageBox.open();
						} else {
							massSpectrum.addIon(ion);
							textMz.setText("");
							textIntensity.setText("");
							UpdateNotifier.update(massSpectrum);
							enableButtonFields(ACTION_INITIALIZE);
						}
					} catch(Exception e1) {
						logger.warn(e1);
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
						messageBox.setText("Add ion");
						messageBox.setMessage("Please check the m/z and abundance values.");
						messageBox.open();
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

				if(massSpectrum != null) {
					Table table = massSpectrumIonsListUI.getTable();
					int index = table.getSelectionIndex();
					if(index >= 0) {
						MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
						messageBox.setText("Delete ion(s)?");
						messageBox.setMessage("Would you like to delete the ion(s)?");
						if(messageBox.open() == SWT.OK) {
							//
							enableButtonFields(ACTION_DELETE);
							TableItem[] tableItems = table.getSelection();
							for(TableItem tableItem : tableItems) {
								Object object = tableItem.getData();
								if(object instanceof IIon) {
									IIon ion = (IIon)object;
									massSpectrum.removeIon(ion);
								}
							}
							massSpectrumIonsListUI.update(massSpectrum, true);
						}
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
		gridData.horizontalSpan = 7;
		compositeTable.setLayoutData(gridData);
		compositeTable.setLayout(new FillLayout());
		//
		massSpectrumIonsListUI = new MassSpectrumIonsListUI(compositeTable, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		massSpectrumIonsListUI.getTable().addSelectionListener(new SelectionAdapter() {

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
				textMz.setEnabled(true);
				textIntensity.setEnabled(true);
				buttonIonAdd.setEnabled(true);
				break;
			case ACTION_DELETE:
				buttonAdd.setEnabled(true);
				break;
			case ACTION_SELECT:
				buttonAdd.setEnabled(true);
				//
				if(massSpectrumIonsListUI.getTable().getSelectionIndex() >= 0) {
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
		textMz.setEnabled(enabled);
		textIntensity.setEnabled(enabled);
		buttonIonAdd.setEnabled(enabled);
	}
}
