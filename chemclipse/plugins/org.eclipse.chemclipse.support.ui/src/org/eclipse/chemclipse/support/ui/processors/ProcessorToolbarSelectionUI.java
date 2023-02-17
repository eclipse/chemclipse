/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support for sorting / icons
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ProcessorToolbarSelectionUI extends Composite {

	private Text textSearch;
	private ProcessSupplierListUI processorListAvailable;
	private ProcessSupplierListUI processorListActive;
	//
	private List<Processor> processors = new ArrayList<>();

	public ProcessorToolbarSelectionUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public List<Processor> getProcessors() {

		return Collections.unmodifiableList(processors);
	}

	public void setInput(List<Processor> processors) {

		this.processors.clear();
		if(processors != null) {
			this.processors.addAll(processors);
		}
		updateProcessorLists();
	}

	private void createControl() {

		setLayout(new GridLayout(3, false));
		//
		createToolbarTop(this);
		createListSection(this);
		createToolbarBottom(this);
	}

	private void createToolbarTop(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		textSearch = createTextSearch(composite);
		createButtonSearch(composite);
	}

	private Text createTextSearch(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL | SWT.ICON_SEARCH);
		text.setText(""); //$NON-NLS-1$
		text.setToolTipText(SupportMessages.searchAvailableProcessorItems);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		/*
		 * Listen to search key event.
		 */
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				runSearch();
			}
		});
		/*
		 * Click on the icons.
		 */
		text.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				if(e.detail == SWT.ICON_CANCEL) {
					text.setText(""); //$NON-NLS-1$
					runSearch();
				} else if(e.detail == SWT.ICON_SEARCH) {
					runSearch();
				}
			}
		});
		//
		return text;
	}

	private Button createButtonSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText(""); //$NON-NLS-1$
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				runSearch();
			}
		});
		//
		return button;
	}

	private void createListSection(Composite parent) {

		processorListAvailable = createProcessSupplierListUI(parent, true);
		createToolbarSelect(parent);
		processorListActive = createProcessSupplierListUI(parent, false);
	}

	private ProcessSupplierListUI createProcessSupplierListUI(Composite parent, boolean enableSorting) {

		ProcessSupplierListUI processSupplierListUI = new ProcessSupplierListUI(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 300;
		gridData.widthHint = 100;
		processSupplierListUI.enableSorting(enableSorting);
		Table table = processSupplierListUI.getTable();
		table.setLayoutData(gridData);
		//
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				Object object = processSupplierListUI.getStructuredSelection().getFirstElement();
				if(object instanceof Processor) {
					Processor processor = (Processor)object;
					processor.setActive(!processor.isActive());
					updateProcessorLists();
				}
			}
		});
		//
		return processSupplierListUI;
	}

	private void createToolbarSelect(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.VERTICAL | SWT.FLAT);
		//
		createToolItemAdd(toolBar);
		createToolItemRemove(toolBar);
	}

	private void createToolItemAdd(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		//
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifySelectedProcessors(processorListAvailable, true);
				updateProcessorLists();
			}
		});
	}

	private void createToolItemRemove(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		//
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifySelectedProcessors(processorListActive, false);
				updateProcessorLists();
			}
		});
	}

	private void modifySelectedProcessors(ProcessSupplierListUI listUI, boolean show) {

		for(Object object : listUI.getStructuredSelection().toArray()) {
			if(object instanceof Processor) {
				Processor processor = (Processor)object;
				processor.setActive(show);
			}
		}
	}

	private void createToolbarBottom(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = SWT.END;
		toolBar.setLayoutData(gridData);
		//
		createToolItemImage(toolBar);
		createToolItemMoveUp(toolBar);
		createToolItemMoveDown(toolBar);
	}

	private void createToolItemImage(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_BITMAP_DOCUMENT, IApplicationImage.SIZE_16x16));
		//
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = processorListActive.getStructuredSelection().getFirstElement();
				if(object instanceof Processor) {
					Processor processor = (Processor)object;
					ImageDialog imageDialog = new ImageDialog(e.display.getActiveShell());
					if(imageDialog.open() == Dialog.OK) {
						String imageFileName = imageDialog.getImageFileName();
						if(imageFileName != null) {
							processor.setImageFileName(imageFileName);
							updateProcessorLists();
						}
					}
				}
			}
		});
	}

	private void createToolItemMoveUp(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		//
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchProcessor(true);
				updateProcessorLists();
			}
		});
	}

	private void createToolItemMoveDown(ToolBar toolBar) {

		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		//
		toolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchProcessor(false);
				updateProcessorLists();
			}
		});
	}

	private void switchProcessor(boolean moveUp) {

		Object object = processorListActive.getStructuredSelection().getFirstElement();
		if(object instanceof Processor) {
			Processor processorActive = (Processor)object;
			ProcessorSupport.switchProcessor(processors, processorActive, moveUp);
		}
	}

	private void runSearch() {

		String searchText = textSearch.getText().trim();
		processorListAvailable.setSearchText(searchText, false);
	}

	private void updateProcessorLists() {

		if(processors != null) {
			processorListAvailable.setInput(processors.stream().filter(p -> !p.isActive()).collect(Collectors.toList()));
			processorListActive.setInput(processors.stream().filter(p -> p.isActive()).collect(Collectors.toList()));
		} else {
			processorListAvailable.setInput(null);
			processorListActive.setInput(null);
		}
	}
}
