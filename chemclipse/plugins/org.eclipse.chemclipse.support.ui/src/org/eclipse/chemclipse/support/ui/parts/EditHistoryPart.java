/*******************************************************************************
 * Copyright (c) 2014, 2015 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.messages.ISupportMessages;
import org.eclipse.chemclipse.support.messages.SupportMessages;
import org.eclipse.chemclipse.support.settings.IOperatingSystemUtils;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.ui.internal.provider.EditHistoryContentProvider;
import org.eclipse.chemclipse.support.ui.internal.provider.EditHistoryLabelProvider;
import org.eclipse.chemclipse.support.ui.internal.provider.EditHistoryTableSorter;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class EditHistoryPart {

	private static final String POPUP_MENU_ID = "com.lablicate.xreport.supplier.ui.parts.editHistoryPart.popup"; // $NON-NLS-1$
	@Inject
	private Composite parent;
	private TableViewer tableViewer;
	private String COLUMN_DATE = SupportMessages.INSTANCE().getMessage(ISupportMessages.COLUMN_DATE);
	private String COLUMN_DESCRIPTION = SupportMessages.INSTANCE().getMessage(ISupportMessages.COLUMN_DESCRIPTION);
	private String COLUMN_EDITOR = SupportMessages.INSTANCE().getMessage(ISupportMessages.COLUMN_EDITOR);
	private String[] titles = {COLUMN_DATE, COLUMN_DESCRIPTION, COLUMN_EDITOR};
	private int[] bounds = {100, 100, 100};
	//
	private EPartService partService;
	private MPart part;
	private IEventBroker eventBroker;
	private EventHandler eventHandler;
	//
	private IEditHistory editHistory;
	//
	private EditHistoryTableSorter editHistoryTableSorter;
	//
	private Clipboard clipboard;
	private IOperatingSystemUtils operatingSystemUtils;

	@Inject
	public EditHistoryPart(EPartService partService, MPart part, IEventBroker eventBroker) {
		this.partService = partService;
		this.part = part;
		this.eventBroker = eventBroker;
		subscribe();
	}

	@PostConstruct
	private void createControl(IEventBroker eventBroker) {

		/*
		 * Clipboard
		 */
		clipboard = new Clipboard(Display.getDefault());
		operatingSystemUtils = new OperatingSystemUtils();
		//
		parent.setLayout(new FillLayout());
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		createColumns();
		tableViewer.setContentProvider(new EditHistoryContentProvider());
		tableViewer.setLabelProvider(new EditHistoryLabelProvider());
		editHistoryTableSorter = new EditHistoryTableSorter();
		tableViewer.setSorter(editHistoryTableSorter);
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == 99 && e.stateMask == 262144) {
					/*
					 * The selected content will be placed to the clipboard if
					 * the user is using "Function + c". "Function-Key" 262144
					 * (stateMask) + "c" 99 (keyCode)
					 */
					copyToClipboard();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		initContextMenu();
	}

	/*
	 * Initialize a context menu.
	 */
	private void initContextMenu() {

		MenuManager menuManager = new MenuManager("#PopUpMenu", POPUP_MENU_ID); // $NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);
		/*
		 * Copy to clipboard
		 */
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						copyToClipboard();
					}
				};
				action.setText(SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_COPY_SELECTION_CLIPBOARD));
				manager.add(action);
			}
		});
		//
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}

	@Focus
	public void setFocus() {

		tableViewer.getControl().setFocus();
		update();
	}

	public void setInput(IEditHistory editHistory) {

		if(doUpdate(editHistory)) {
			this.editHistory = editHistory;
			tableViewer.setInput(editHistory);
		}
	}

	private void update() {

		if(doUpdate(editHistory)) {
			tableViewer.setInput(editHistory);
		}
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	/**
	 * Create the columns.
	 */
	private void createColumns() {

		/*
		 * Set the titles and bounds.
		 */
		for(int i = 0; i < titles.length; i++) {
			/*
			 * Column sort.
			 */
			final int index = i;
			final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			final TableColumn tableColumn = tableViewerColumn.getColumn();
			tableColumn.setText(titles[i]);
			tableColumn.setWidth(bounds[i]);
			tableColumn.setResizable(true);
			tableColumn.setMoveable(true);
			tableColumn.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					editHistoryTableSorter.setColumn(index);
					int direction = tableViewer.getTable().getSortDirection();
					if(tableViewer.getTable().getSortColumn() == tableColumn) {
						/*
						 * Toggle the direction
						 */
						direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
					} else {
						direction = SWT.UP;
					}
					tableViewer.getTable().setSortDirection(direction);
					tableViewer.getTable().setSortColumn(tableColumn);
					tableViewer.refresh();
				}
			});
		}
		/*
		 * Set header and lines visible.
		 */
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	/**
	 * Copies the actual selection to the clipboard.
	 */
	private void copyToClipboard() {

		String DELIMITER = IOperatingSystemUtils.TAB;
		String END_OF_LINE = operatingSystemUtils.getLineDelimiter();
		StringBuilder builder = new StringBuilder();
		int size = titles.length;
		/*
		 * Header
		 */
		for(String title : titles) {
			builder.append(title);
			builder.append(DELIMITER);
		}
		builder.append(DELIMITER);
		/*
		 * Copy the selected items.
		 */
		TableItem selection;
		Table table = tableViewer.getTable();
		for(int index : table.getSelectionIndices()) {
			/*
			 * Get the nth selected item.
			 */
			selection = table.getItem(index);
			/*
			 * Dump all elements of the item, e.g. RT, Abundance, ... .
			 */
			for(int columnIndex = 0; columnIndex < size; columnIndex++) {
				builder.append(selection.getText(columnIndex));
				builder.append(DELIMITER);
			}
			builder.append(END_OF_LINE);
		}
		/*
		 * If the builder is empty, give a note that items needs to be selected.
		 */
		if(builder.length() == 0) {
			builder.append(SupportMessages.INSTANCE().getMessage(ISupportMessages.LABEL_SELECT_ENTRIES));
			builder.append(END_OF_LINE);
		}
		/*
		 * Transfer the selected text (items) to the clipboard.
		 */
		TextTransfer textTransfer = TextTransfer.getInstance();
		Object[] data = new Object[]{builder.toString()};
		Transfer[] dataTypes = new Transfer[]{textTransfer};
		clipboard.setContents(data, dataTypes);
	}

	/**
	 * Subscribes the selection update events.
	 */
	private void subscribe() {

		if(eventBroker != null) {
			/*
			 * Receives and handles chromatogram selection updates.
			 */
			eventHandler = new EventHandler() {

				public void handleEvent(Event event) {

					editHistory = (IEditHistory)event.getProperty(IChemClipseEvents.PROPERTY_EDIT_HISTORY);
					update();
				}
			};
			eventBroker.subscribe(IChemClipseEvents.TOPIC_EDIT_HISTORY_UPDATE, eventHandler);
		}
	}

	/**
	 * Unsubscribes this listener.
	 */
	private void unsubscribe() {

		if(eventBroker != null && eventHandler != null) {
			eventBroker.unsubscribe(eventHandler);
		}
	}

	private boolean isPartVisible() {

		if(partService != null && partService.isPartVisible(part)) {
			return true;
		}
		return false;
	}

	private boolean doUpdate(IEditHistory editHistory) {

		if(isPartVisible() && editHistory != null) {
			return true;
		}
		return false;
	}
}
