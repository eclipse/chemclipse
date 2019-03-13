/*******************************************************************************
 * Copyright (c) 2015, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.menu.TableMenuEntryComparator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class ExtendedTableViewer extends TableViewer implements IExtendedTableViewer {

	private static final String MENU_TEXT = "Table PopUp Menu";
	//
	private ITableSettings tableSettings;
	private List<TableViewerColumn> tableViewerColumns;
	private Map<String, Set<ITableMenuEntry>> categoryMenuEntriesMap;
	private Map<String, ITableMenuEntry> menuEntryMap;
	private Map<String, MenuManager> menuManagerMap;
	private Set<KeyListener> userDefinedKeyListeners;
	private List<IColumnMoveListener> columnMoveListeners;
	private boolean editEnabled;

	public ExtendedTableViewer(Composite parent) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	public ExtendedTableViewer(Composite parent, int style) {
		super(parent, style);
		tableSettings = new TableSettings();
		tableViewerColumns = new ArrayList<TableViewerColumn>();
		categoryMenuEntriesMap = new HashMap<String, Set<ITableMenuEntry>>();
		menuEntryMap = new HashMap<String, ITableMenuEntry>();
		menuManagerMap = new HashMap<String, MenuManager>();
		userDefinedKeyListeners = new HashSet<KeyListener>();
		columnMoveListeners = new ArrayList<>();
		applySettings(tableSettings);
		editEnabled = true;
		registerMenuListener();
	}

	@Override
	public ITableSettings getTableSettings() {

		return tableSettings;
	}

	@Override
	public void applySettings(ITableSettings tableSettings) {

		/*
		 * Add the key listeners.
		 */
		if(tableSettings == null) {
			this.tableSettings = new TableSettings();
		} else {
			this.tableSettings = tableSettings;
		}
		//
		createKeyListener();
	}

	public void addColumnMoveListener(IColumnMoveListener columnMoveListener) {

		columnMoveListeners.add(columnMoveListener);
	}

	public void removeColumnMoveListener(IColumnMoveListener columnMoveListener) {

		columnMoveListeners.remove(columnMoveListener);
	}

	private void registerMenuListener() {

		getTable().addListener(SWT.MenuDetect, new Listener() {

			@Override
			public void handleEvent(Event event) {

				/*
				 * Create the menu if requested.
				 */
				if(tableSettings.isCreateMenu()) {
					createPopupMenu();
				} else {
					Menu menu = getTable().getMenu();
					if(menu != null) {
						getTable().setMenu(null);
						menu.dispose();
					}
				}
			}
		});
	}

	@Override
	public void createColumns(String[] titles, int[] bounds) {

		/*
		 * Clear the table and all existing columns.
		 */
		Table table = getTable();
		table.setRedraw(false);
		//
		tableViewerColumns.clear();
		table.clearAll();
		while(table.getColumnCount() > 0) {
			table.getColumns()[0].dispose();
		}
		table.removeAll();
		/*
		 * A label provider must be available.
		 */
		if(getLabelProvider() != null) {
			/*
			 * Set the columns.
			 */
			for(int i = 0; i < titles.length; i++) {
				/*
				 * Column sort.
				 */
				final int index = i;
				final TableViewerColumn tableViewerColumn = new TableViewerColumn(this, SWT.NONE);
				tableViewerColumns.add(tableViewerColumn);
				final TableColumn tableColumn = tableViewerColumn.getColumn();
				tableColumn.setText(titles[i]);
				tableColumn.setWidth(bounds[i]);
				tableColumn.setResizable(true);
				tableColumn.setMoveable(true);
				tableColumn.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						ViewerComparator viewerComparator = getComparator();
						if(viewerComparator instanceof IRecordTableComparator) {
							/*
							 * Only sort if a record table sorter has been set.
							 */
							IRecordTableComparator recordTableComparator = (IRecordTableComparator)viewerComparator;
							recordTableComparator.setColumn(index);
							int direction = table.getSortDirection();
							if(table.getSortColumn() == tableColumn) {
								/*
								 * Toggle the sort direction
								 */
								direction = (direction == SWT.UP) ? SWT.DOWN : SWT.UP;
							} else {
								direction = SWT.UP;
							}
							table.setSortDirection(direction);
							table.setSortColumn(tableColumn);
							refresh();
						}
					}
				});
				/*
				 * Column Move Listener
				 */
				tableColumn.addListener(SWT.Move, new Listener() {

					@Override
					public void handleEvent(Event event) {

						fireColumnMoved();
					}
				});
			}
		}
		//
		table.setRedraw(true);
		refresh();
		/*
		 * Set header and lines visible.
		 */
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void fireColumnMoved() {

		for(IColumnMoveListener columnMoveListener : columnMoveListeners) {
			columnMoveListener.handle();
		}
	}

	public List<TableViewerColumn> getTableViewerColumns() {

		return Collections.unmodifiableList(tableViewerColumns);
	}

	/**
	 * Returns the table viewer column with the given label or null
	 * if none was matched.
	 * 
	 * @param label
	 * @return {@link TableViewerColumn}
	 */
	public TableViewerColumn getTableViewerColumn(String label) {

		for(TableViewerColumn tableViewerColumn : tableViewerColumns) {
			if(tableViewerColumn.getColumn().getText().equals(label)) {
				return tableViewerColumn;
			}
		}
		return null;
	}

	@Override
	public boolean isEditEnabled() {

		return editEnabled;
	}

	@Override
	public void setEditEnabled(boolean editEnabled) {

		this.editEnabled = editEnabled;
	}

	private void createKeyListener() {

		/*
		 * Remove existing listeners.
		 */
		Table table = getTable();
		for(KeyListener keyListener : userDefinedKeyListeners) {
			table.removeKeyListener(keyListener);
		}
		userDefinedKeyListeners.clear();
		/*
		 * Add new listeners.
		 */
		ExtendedTableViewer extendedTableViewer = this;
		for(IKeyEventProcessor keyEventProcessor : tableSettings.getKeyEventProcessors()) {
			KeyListener keyListener = new KeyAdapter() {

				@Override
				public void keyReleased(KeyEvent e) {

					keyEventProcessor.handleEvent(extendedTableViewer, e);
				}
			};
			table.addKeyListener(keyListener);
			userDefinedKeyListeners.add(keyListener);
		}
	}

	private void createPopupMenu() {

		/*
		 * Clear the existing entries.
		 */
		Table table = getTable();
		table.setMenu(null);
		//
		categoryMenuEntriesMap.clear();
		menuEntryMap.clear();
		menuManagerMap.clear();
		//
		String menuId = getClass().getCanonicalName();
		MenuManager menuManager = new MenuManager(MENU_TEXT, menuId);
		menuManager.setRemoveAllWhenShown(true); // Seems to remove sub menus. Solution needed!
		//
		addMenuItemsFromChartSettings();
		createMenuItems(menuManager);
		//
		Menu menu = menuManager.createContextMenu(table);
		table.setMenu(menu);
	}

	private void addMenuItemsFromChartSettings() {

		for(ITableMenuEntry menuEntry : tableSettings.getMenuEntries()) {
			addMenuEntry(menuEntry);
		}
	}

	private void addMenuEntry(ITableMenuEntry menuEntry) {

		if(menuEntry != null) {
			String category = menuEntry.getCategory();
			Set<ITableMenuEntry> menuEntries = categoryMenuEntriesMap.get(category);
			/*
			 * Create set if not existent.
			 */
			if(menuEntries == null) {
				menuEntries = new HashSet<ITableMenuEntry>();
				categoryMenuEntriesMap.put(category, menuEntries);
			}
			/*
			 * Add the entry.
			 */
			menuEntries.add(menuEntry);
			menuEntryMap.put(menuEntry.getName(), menuEntry);
		}
	}

	private void createMenuItems(MenuManager menuManager) {

		List<String> categories = new ArrayList<String>(categoryMenuEntriesMap.keySet());
		Collections.sort(categories);
		Iterator<String> iterator = categories.iterator();
		while(iterator.hasNext()) {
			String category = iterator.next();
			createMenuCategory(menuManager, category, categoryMenuEntriesMap.get(category));
			if(iterator.hasNext()) {
				menuManager.add(new Separator());
			}
		}
	}

	private void createMenuCategory(MenuManager menuManager, String category, Set<ITableMenuEntry> tableMenuEntries) {

		List<ITableMenuEntry> menuEntries = new ArrayList<ITableMenuEntry>(tableMenuEntries);
		Collections.sort(menuEntries, new TableMenuEntryComparator());
		//
		for(ITableMenuEntry menuEntry : menuEntries) {
			menuManager.addMenuListener(new IMenuListener() {

				@Override
				public void menuAboutToShow(IMenuManager manager) {

					if("".equals(category)) {
						/*
						 * Top level
						 */
						manager.add(getAction(menuEntry));
					} else {
						/*
						 * Sub menu
						 */
						MenuManager subMenu = menuManagerMap.get(category);
						if(subMenu == null) {
							subMenu = new MenuManager(category, null);
							menuManagerMap.put(category, subMenu);
							manager.add(subMenu);
						}
						subMenu.add(getAction(menuEntry));
					}
				}
			});
		}
	}

	private IAction getAction(ITableMenuEntry menuEntry) {

		ExtendedTableViewer extendedTableViewer = this;
		IAction action = new Action() {

			@Override
			public void run() {

				menuEntry.execute(extendedTableViewer);
			}
		};
		action.setText(menuEntry.getName());
		return action;
	}
}
