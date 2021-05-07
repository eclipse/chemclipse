/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make UI configurable, support selection of existing process methods, support for init with different datatypes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.autoComplete;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ProcessMethodHeader extends Composite {

	private Text textName;
	private Text textCategory;
	private Text textOperator;
	private Text textDescription;
	private Button buttonResume;
	private Button buttonFinalize;
	//
	private ProcessMethod processMethod = null;
	//
	private IUpdateListener updateListener = null;
	private IModificationHandler modificationHandler = null;
	private ProcessSupplierContext processingSupport = null;
	//
	private String[] knownCategories = new String[]{};

	public ProcessMethodHeader(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(ProcessMethod processMethod) {

		this.processMethod = processMethod;
		updateProcessMethod();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
	}

	public void setProcessingSupport(ProcessSupplierContext processingSupport) {

		this.processingSupport = processingSupport;
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		textName = createNameSection(this);
		textOperator = createOperatorSection(this);
		textDescription = createDescriptionSection(this);
		buttonResume = createSupportResumeSection(this);
		textCategory = createCategorySection(this);
		buttonFinalize = createFinalize(this);
	}

	private Text createNameSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Name:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The name of this method that is used for display");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setName(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Text createOperatorSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Operator:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The operator is the person who has created / currently manages this method");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setOperator(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Text createDescriptionSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Description:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Description");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setDescription(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Button createSupportResumeSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Enable Resume:");
		//
		Button button = new Button(parent, SWT.CHECK);
		button.setText("");
		button.setToolTipText("This option allows to resume the method at a selected entry.");
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					boolean resume = button.getSelection();
					processMethod.setSupportResume(resume);
					setDirty(true);
				}
			}
		});
		//
		return button;
	}

	private Text createCategorySection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Category:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The category groups similar methods under a common name");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setCategory(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		autoComplete(text, new IContentProposalProvider() {

			@Override
			public IContentProposal[] getProposals(String contents, int position) {

				List<ContentProposal> list = new ArrayList<>();
				if(contents != null) {
					String[] items = getItems();
					for(String item : items) {
						if(item.toLowerCase().contains(contents.toLowerCase())) {
							list.add(new ContentProposal(item));
						}
					}
				}
				return list.toArray(new IContentProposal[0]);
			}

			private String[] getItems() {

				if(knownCategories == null && processingSupport != null) {
					Set<String> categories = new TreeSet<>();
					processingSupport.visitSupplier(new Consumer<IProcessSupplier<?>>() {

						@Override
						public void accept(IProcessSupplier<?> supplier) {

							String category = supplier.getCategory();
							if(category != null && !category.isEmpty()) {
								categories.add(category);
							}
						}
					});
					knownCategories = categories.toArray(new String[0]);
				}
				//
				return knownCategories;
			}
		});
		//
		return text;
	}

	private Button createFinalize(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Finalized:");
		Button button = new Button(parent, SWT.CHECK);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openConfirm(parent.getShell(), "Finalize Process Method", "Finalize a method prevents further modifications to this method, are you sure?")) {
					updateProcessMethod();
					fireUpdate();
				} else {
					button.setSelection(false);
				}
			}
		});
		return button;
	}

	private void setDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}

	private void updateProcessMethod() {

		if(processMethod != null) {
			textOperator.setText(processMethod.getOperator());
			textDescription.setText(processMethod.getDescription());
			buttonResume.setSelection(processMethod.isSupportResume());
			textCategory.setText(processMethod.getCategory());
			textName.setText(processMethod.getName());
			boolean readOnly = buttonFinalize.getSelection();
			if(readOnly) {
				processMethod.setReadOnly(readOnly);
				textOperator.setEnabled(false);
				textDescription.setEnabled(false);
				buttonResume.setEnabled(false);
				textCategory.setEnabled(false);
				textName.setEnabled(false);
				buttonFinalize.setEnabled(false);
			}
		} else {
			textOperator.setText("");
			textDescription.setText("");
			buttonResume.setEnabled(false);
			textCategory.setText("");
			textName.setText("");
		}
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}
