/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - initial API and implementation
 * Christoph Läubrich - Enhance the handling of table columns
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.support.preferences.SupportPreferences;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.menu.TableMenuEntryComparator;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinition;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedTableViewer extends TableViewer implements IExtendedTableViewer {

	private static final Logger logger = LoggerFactory.getLogger(ExtendedTableViewer.class);
	private static final String MENU_TEXT = SupportMessages.tablePopUpMenu;
	//
	private ITableSettings tableSettings = new TableSettings();
	private final List<TableViewerColumn> tableViewerColumns = new ArrayList<>();
	private final Map<String, Set<ITableMenuEntry>> categoryMenuEntriesMap = new HashMap<>();
	private final Map<String, ITableMenuEntry> menuEntryMap = new HashMap<>();
	private final Map<String, MenuManager> menuManagerMap = new HashMap<>();
	private final Set<KeyListener> userDefinedKeyListeners = new HashSet<>();
	private final List<IColumnMoveListener> columnMoveListeners = new ArrayList<>();
	private boolean editEnabled = true;
	private boolean copyHeaderToClipboard = SupportPreferences.DEF_CLIPBOARD_COPY_HEADER;
	private String copyColumnsToClipboard = SupportPreferences.DEF_CLIPBOARD_COPY_COLUMNS;
	//
	private ControlListener controlListener = null;

	public ExtendedTableViewer(Composite parent) {

		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	public ExtendedTableViewer(Composite parent, int style) {

		super(parent, style);
		//
		applySettings(tableSettings);
		registerMenuListener();
		setContentProvider(ArrayContentProvider.getInstance());
		handleColumnSettings();
	}

	public void setColumnMoveWidthSupport(IPreferenceStore preferenceStore, String preferenceColumnOrder, String preferenceColumnWidth) {

		Table table = getTable();
		if(preferenceColumnOrder != null) {
			TableSupport.setColumnOrder(table, preferenceStore.getString(preferenceColumnOrder));
		}
		//
		if(preferenceColumnWidth != null) {
			TableSupport.setColumnWidth(table, preferenceStore.getString(preferenceColumnWidth));
		}
		//
		setControlListener(new ControlAdapter() {

			@Override
			public void controlMoved(ControlEvent e) {

				if(preferenceColumnOrder != null) {
					String columnOrder = TableSupport.getColumnOrder(table);
					preferenceStore.setValue(preferenceColumnOrder, columnOrder);
				}
			}

			@Override
			public void controlResized(ControlEvent e) {

				if(preferenceColumnWidth != null) {
					String columnWidth = TableSupport.getColumnWidth(table);
					preferenceStore.setValue(preferenceColumnWidth, columnWidth);
				}
			}
		});
	}

	public void setControlListener(ControlListener controlListener) {

		this.controlListener = controlListener;
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
		table.setRedraw(true);
		refresh();
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
				final TableViewerColumn tableViewerColumn = createTableColumn(titles[i], bounds[i]);
				final TableColumn tableColumn = tableViewerColumn.getColumn();
				//
				tableColumn.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						sortColumn(table, index, tableColumn);
					}
				});
				//
				tableColumn.addControlListener(new ControlAdapter() {

					@Override
					public void controlMoved(ControlEvent e) {

						if(controlListener != null) {
							controlListener.controlMoved(e);
						}
					}

					@Override
					public void controlResized(ControlEvent e) {

						if(controlListener != null) {
							controlListener.controlResized(e);
						}
					}
				});
				//
				tableViewerColumns.add(tableViewerColumn);
			}
		}
		/*
		 * Set header and lines visible.
		 */
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	@Override
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

	@Override
	public boolean isCopyHeaderToClipboard() {

		return copyHeaderToClipboard;
	}

	@Override
	public void setCopyHeaderToClipboard(boolean copyHeaderToClipboard) {

		this.copyHeaderToClipboard = copyHeaderToClipboard;
		String pClipboardCopyHeader = getPreferenceName(SupportPreferences.P_CLIPBOARD_COPY_HEADER);
		SupportPreferences.setClipboardCopyHeader(pClipboardCopyHeader, copyHeaderToClipboard);
	}

	@Override
	public String getCopyColumnsToClipboard() {

		return copyColumnsToClipboard;
	}

	@Override
	public void setCopyColumnsToClipboard(String copyColumnsToClipboard) {

		this.copyColumnsToClipboard = copyColumnsToClipboard;
		String pClipboardCopyColumns = getPreferenceName(SupportPreferences.P_CLIPBOARD_COPY_COLUMNS);
		SupportPreferences.setClipboardCopyColumns(pClipboardCopyColumns, copyColumnsToClipboard);
	}

	/**
	 * Checks if the cell of the given column was selected.
	 * ColumnIndex is 0 based.
	 * 
	 * @param event
	 * @param columnLabel
	 * @return boolean
	 */
	public boolean isColumnSelected(Event event, String columnLabel) {

		for(int i = 0; i < tableViewerColumns.size(); i++) {
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			String label = tableViewerColumn.getColumn().getText();
			if(label.endsWith(columnLabel)) {
				return isColumnSelected(event, i);
			}
		}
		//
		return false;
	}

	/**
	 * Checks if the cell of the given column was selected.
	 * ColumnIndex is 0 based.
	 * 
	 * @param event
	 * @param columnIndex
	 * @return boolean
	 */
	public boolean isColumnSelected(Event event, int columnIndex) {

		Table table = getTable();
		Rectangle clientArea = table.getClientArea();
		Point point = new Point(event.x, event.y);
		//
		int index = table.getTopIndex();
		while(index < table.getItemCount()) {
			boolean visible = false;
			TableItem item = table.getItem(index);
			for(int i = 0; i < table.getColumnCount(); i++) {
				Rectangle rectangle = item.getBounds(i);
				if(rectangle.contains(point)) {
					if(i == columnIndex) {
						return true;
					}
				}
				//
				if(!visible && rectangle.intersects(clientArea)) {
					visible = true;
				}
			}
			//
			if(!visible) {
				return false;
			}
			index++;
		}
		//
		return false;
	}

	@Override
	public void clearColumns() {

		tableViewerColumns.clear();
		Table table = getTable();
		for(TableColumn column : table.getColumns()) {
			column.dispose();
		}
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
	}

	@Override
	public <D, C> TableViewerColumn addColumn(ColumnDefinition<D, C> definition) {

		TableViewerColumn tableViewerColumn = createColumn(this, definition, editEnabled);
		tableViewerColumn.getColumn().setMoveable(true);
		tableViewerColumn.getColumn().addListener(SWT.Move, new Listener() {

			@Override
			public void handleEvent(Event event) {

				fireColumnMoved();
			}
		});
		tableViewerColumns.add(tableViewerColumn);
		Comparator<C> comparator = definition.getComparator();
		if(comparator != null) {
			TableColumn tableColumn = tableViewerColumn.getColumn();
			tableColumn.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					Table table = getTable();
					final int direction;
					if(table.getSortColumn() == tableColumn) {
						direction = (table.getSortDirection() == SWT.UP) ? SWT.DOWN : SWT.UP;
					} else {
						direction = SWT.UP;
					}
					setComparator(new ViewerComparator() {

						@Override
						public void sort(Viewer viewer, Object[] elements) {

							Comparator<Object> comparer = new Comparator<Object>() {

								@SuppressWarnings("unchecked")
								@Override
								public int compare(Object o1, Object o2) {

									try {
										C d1 = definition.apply((D)o1);
										C d2 = definition.apply((D)o2);
										if(d1 == null) {
											if(d2 == null) {
												return 0;
											} else {
												return 1;
											}
										} else if(d2 == null) {
											return -1;
										}
										return comparator.compare(d1, d2);
									} catch(ClassCastException e) {
										logger.warn("Inconsistent data items in respect to column definition: {}, sorting will be inconsistent!", e.toString()); //$NON-NLS-1$
										return 0;
									}
								}
							};
							if(direction == SWT.DOWN) {
								comparer = Collections.reverseOrder(comparer);
							}
							Arrays.sort(elements, comparer);
						}
					});
					table.setSortDirection(direction);
					table.setSortColumn(tableColumn);
					refresh();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
		}
		getTable().setHeaderVisible(true);
		getTable().setLinesVisible(true);
		return tableViewerColumn;
	}

	protected void sortColumn(Table table, final int index, final TableColumn tableColumn) {

		ViewerComparator viewerComparator = getComparator();
		IRecordTableComparator recordTableComparator = Adapters.adapt(viewerComparator, IRecordTableComparator.class);
		if(recordTableComparator != null) {
			/*
			 * Only sort if a record table sorter has been set.
			 */
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

	private void fireColumnMoved() {

		for(IColumnMoveListener columnMoveListener : columnMoveListeners) {
			columnMoveListener.handle();
		}
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
				menuEntries = new HashSet<>();
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

		List<String> categories = new ArrayList<>(categoryMenuEntriesMap.keySet());
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

		List<ITableMenuEntry> menuEntries = new ArrayList<>(tableMenuEntries);
		Collections.sort(menuEntries, new TableMenuEntryComparator());
		//
		for(ITableMenuEntry menuEntry : menuEntries) {
			menuManager.addMenuListener(new IMenuListener() {

				@Override
				public void menuAboutToShow(IMenuManager manager) {

					if(category.isEmpty()) {
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

	private void handleColumnSettings() {

		/*
		 * Handle the copy to clipboard settings
		 */
		String pClipboardCopyHeader = getPreferenceName(SupportPreferences.P_CLIPBOARD_COPY_HEADER);
		copyHeaderToClipboard = SupportPreferences.isClipboardCopyHeader(pClipboardCopyHeader);
		//
		String pClipboardCopyColumns = getPreferenceName(SupportPreferences.P_CLIPBOARD_COPY_COLUMNS);
		copyColumnsToClipboard = SupportPreferences.getClipboardCopyColumns(pClipboardCopyColumns);
	}

	private TableViewerColumn createTableColumn(String title, int width) {

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(this, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider());
		final TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setText(title);
		tableColumn.setWidth(width);
		tableColumn.setResizable(true);
		tableColumn.setMoveable(true);
		tableColumn.addListener(SWT.Move, new Listener() {

			@Override
			public void handleEvent(Event event) {

				fireColumnMoved();
			}
		});
		return tableViewerColumn;
	}

	private String getPreferenceName(String prefix) {

		String postfix = getClass().getName();
		return prefix + postfix;
	}
}