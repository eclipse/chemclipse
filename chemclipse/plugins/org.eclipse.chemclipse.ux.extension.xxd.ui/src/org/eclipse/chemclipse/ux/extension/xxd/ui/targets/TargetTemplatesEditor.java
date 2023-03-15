/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.targets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.identifier.template.TargetTemplate;
import org.eclipse.chemclipse.model.identifier.template.TargetTemplates;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;

public class TargetTemplatesEditor extends Composite {

	private TargetTemplatesUI targetTemplatesUI;
	private TargetTemplateListUI targetTemplateListUI;
	//
	private TargetTemplates targetTemplates;

	public TargetTemplatesEditor(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(TargetTemplates targetTemplates) {

		this.targetTemplates = targetTemplates;
		updateTargetTemplatesUI();
		updateTargetTemplatesTable();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		targetTemplatesUI = createTargetTemplatesUI(this);
		targetTemplateListUI = createTargetTemplateListUI(this);
	}

	private TargetTemplatesUI createTargetTemplatesUI(Composite parent) {

		TargetTemplatesUI targetTemplatesUI = new TargetTemplatesUI(parent, SWT.NONE);
		targetTemplatesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		targetTemplatesUI.setUpdateListener(new IUpdateListener() {

			@Override
			public void update() {

				updateTargetTemplatesTable();
			}
		});
		//
		return targetTemplatesUI;
	}

	private TargetTemplateListUI createTargetTemplateListUI(Composite parent) {

		TargetTemplateListUI targetTemplateListUI = new TargetTemplateListUI(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		Table table = targetTemplateListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Target Template Selection
		 */
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = targetTemplateListUI.getStructuredSelection().getFirstElement();
				if(object instanceof TargetTemplate) {
					TargetTemplate targetTemplate = (TargetTemplate)object;
					String[] items = targetTemplatesUI.getItems();
					exitloop:
					for(int i = 0; i < items.length; i++) {
						if(items[i].equals(targetTemplate.getName())) {
							targetTemplatesUI.select(i);
							break exitloop;
						}
					}
				}
			}
		});
		/*
		 * Delete items
		 */
		table.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(targetTemplates != null) {
					if(e.keyCode == SWT.DEL) {
						MessageBox messageBox = new MessageBox(e.display.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						messageBox.setText("Delete target templates");
						messageBox.setMessage("Would you like to delete the selected target templates?");
						if(messageBox.open() == SWT.YES) {
							/*
							 * Collect
							 */
							List<TargetTemplate> deleteItems = new ArrayList<>();
							for(Object object : targetTemplateListUI.getStructuredSelection().toList()) {
								if(object instanceof TargetTemplate) {
									TargetTemplate targetTemplate = (TargetTemplate)object;
									deleteItems.add(targetTemplate);
								}
							}
							/*
							 * Delete
							 */
							delete(deleteItems);
							/*
							 * Update
							 */
							updateTargetTemplatesUI();
							updateTargetTemplatesTable();
						}
					}
				}
			}
		});
		//
		return targetTemplateListUI;
	}

	private void delete(List<TargetTemplate> deleteItems) {

		if(targetTemplates != null) {
			for(TargetTemplate deleteItem : deleteItems) {
				targetTemplates.remove(deleteItem.getName());
			}
		}
	}

	private void updateTargetTemplatesUI() {

		targetTemplatesUI.setInput(targetTemplates);
	}

	private void updateTargetTemplatesTable() {

		if(targetTemplates != null) {
			List<TargetTemplate> list = new ArrayList<>(targetTemplates.values());
			Collections.sort(list, (t1, t2) -> t1.getName().compareTo(t2.getName()));
			targetTemplateListUI.setInput(list);
		} else {
			targetTemplateListUI.setInput(null);
		}
	}
}