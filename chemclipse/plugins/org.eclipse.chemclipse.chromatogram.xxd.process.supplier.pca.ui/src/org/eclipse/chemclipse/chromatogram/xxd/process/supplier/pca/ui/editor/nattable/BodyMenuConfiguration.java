/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.Set;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class BodyMenuConfiguration extends AbstractUiBindingConfiguration {

	private Menu bodyMenu;

	public BodyMenuConfiguration(PeakListNatTable peakListNatTable) {
		this.bodyMenu = createBodyMenu(peakListNatTable).build();
	}

	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {

		if(this.bodyMenu != null) {
			uiBindingRegistry.registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, null, MouseEventMatcher.RIGHT_BUTTON), new PopupMenuAction(this.bodyMenu));
		}
	}

	protected PopupMenuBuilder createBodyMenu(final PeakListNatTable peakListNatTable) {

		return new PopupMenuBuilder(peakListNatTable.natTable).withMenuItemProvider(new IMenuItemProvider() {

			@Override
			public void addMenuItem(final NatTable natTable, Menu popupMenu) {

				MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
				menuItem.setText("Check Selected Variables");
				menuItem.setEnabled(true);
				menuItem.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent event) {

						Set<Range> ranges = peakListNatTable.selectionLayer.getSelectedRowPositions();
						for(Range range : ranges) {
							for(int index : range.getMembers()) {
								int variableIndex = peakListNatTable.sortModel.getOrderRow().get(index);
								peakListNatTable.tableData.getVariables().get(variableIndex).setSelected(true);
							}
						}
					}
				});
				menuItem = new MenuItem(popupMenu, SWT.PUSH);
				menuItem.setText("Uncheck Selected Variables");
				menuItem.setEnabled(true);
				menuItem.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent event) {

						Set<Range> ranges = peakListNatTable.selectionLayer.getSelectedRowPositions();
						for(Range range : ranges) {
							for(int index : range.getMembers()) {
								int variableIndex = peakListNatTable.sortModel.getOrderRow().get(index);
								peakListNatTable.tableData.getVariables().get(variableIndex).setSelected(false);
							}
						}
					}
				});
				menuItem = new MenuItem(popupMenu, SWT.SEPARATOR);
				menuItem = new MenuItem(popupMenu, SWT.PUSH);
				menuItem.setText("Check All Variables");
				menuItem.setEnabled(true);
				menuItem.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent event) {

						peakListNatTable.tableData.getVariables().forEach(v -> v.setSelected(true));
					}
				});
				menuItem = new MenuItem(popupMenu, SWT.PUSH);
				menuItem.setText("Uncheck All Variables");
				menuItem.setEnabled(true);
				menuItem.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent event) {

						peakListNatTable.tableData.getVariables().forEach(v -> v.setSelected(false));
					}
				});
				menuItem = new MenuItem(popupMenu, SWT.SEPARATOR);
				menuItem = new MenuItem(popupMenu, SWT.PUSH);
				menuItem.setText("Export Table");
				menuItem.setEnabled(true);
				menuItem.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent event) {

						peakListNatTable.exportTableDialog(Display.getDefault());
					}
				});
			}
		});
	}
}
