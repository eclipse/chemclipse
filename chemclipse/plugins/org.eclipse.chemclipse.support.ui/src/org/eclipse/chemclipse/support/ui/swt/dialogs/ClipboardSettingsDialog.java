/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.settings.ValueDelimiter;
import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.support.CopyColumnsSupport;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

public class ClipboardSettingsDialog extends Dialog {

	public static final int DEFAULT_WIDTH = 300;
	public static final int DEFAULT_HEIGHT = 500;
	//
	private ExtendedTableViewer extendedTableViewer;
	private List<Button> columnCheckBoxes = new ArrayList<>();
	//
	private boolean copyHeader = true;
	private ValueDelimiter valueDelimiter = ValueDelimiter.TAB;
	private Set<Integer> columnSelections = new HashSet<>();

	public ClipboardSettingsDialog(Shell shell) {

		super(shell);
	}

	public boolean isCopyHeader() {

		return copyHeader;
	}

	public ValueDelimiter getValueDelimiter() {

		return valueDelimiter;
	}

	public String getColumnsSelection() {

		/*
		 * If the viewer is null or all columns are selected
		 * then return null, which means print all columns.
		 */
		if(extendedTableViewer != null) {
			if(!columnSelections.isEmpty()) {
				int numberColumns = getNumberColumns();
				if(columnSelections.size() < numberColumns) {
					return CopyColumnsSupport.getColumns(columnSelections);
				}
			}
		}
		//
		return PreferenceSupplier.DEF_CLIPBOARD_COPY_COLUMNS;
	}

	public void setExtendedTableViewer(ExtendedTableViewer extendedTableViewer) {

		this.extendedTableViewer = extendedTableViewer;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(SupportMessages.clipboardSettings);
	}

	@Override
	protected Point getInitialSize() {

		return new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	protected boolean isResizable() {

		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, true));
		//
		if(extendedTableViewer != null) {
			initialize();
			createSettingsSection(composite);
		} else {
			createInfoSection(composite);
		}
		//
		return composite;
	}

	private void initialize() {

		copyHeader = extendedTableViewer.isCopyHeaderToClipboard();
		valueDelimiter = extendedTableViewer.getCopyValueDelimiterClipboard();
	}

	private void createInfoSection(Composite parent) {

		createLabel(parent, SupportMessages.noClipboardSettingsAvailable);
	}

	private void createSettingsSection(Composite parent) {

		createButtonCopyHeader(parent);
		createToolbarMain(parent);
		createTableColumnsSection(parent);
	}

	private void createButtonCopyHeader(Composite parent) {

		createButtonCheck(parent, SupportMessages.copyHeaderToClipboard, SupportMessages.copyHeaderToolTip, copyHeader, new Consumer<Boolean>() {

			@Override
			public void accept(Boolean selection) {

				copyHeader = selection;
			}
		});
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(4, false));
		//
		createLabel(composite, SupportMessages.tableColumns);
		createButtonSelectAll(composite);
		createButtonDeselectAll(composite);
		createComboViewerDelimiter(composite);
	}

	private void createTableColumnsSection(Composite parent) {

		List<TableViewerColumn> tableViewerColumns = extendedTableViewer.getTableViewerColumns();
		int numberColumns = tableViewerColumns.size();
		//
		for(int i = 0; i < numberColumns; i++) {
			/*
			 * Columns
			 */
			TableViewerColumn tableViewerColumn = tableViewerColumns.get(i);
			TableColumn tableColumn = tableViewerColumn.getColumn();
			String title = tableColumn.getText();
			String tooltip = tableColumn.getToolTipText();
			/*
			 * Selection
			 */
			final int index = i;
			boolean selected = isColumnSelected(i);
			if(selected) {
				columnSelections.add(i);
			}
			//
			Button button = createButtonCheck(parent, title, tooltip, selected, new Consumer<Boolean>() {

				@Override
				public void accept(Boolean selection) {

					if(selection) {
						columnSelections.add(index);
					} else {
						columnSelections.remove(index);
					}
				}
			});
			columnCheckBoxes.add(button);
		}
	}

	private boolean isColumnSelected(int i) {

		int[] copyColumnsToClipboard = CopyColumnsSupport.getColumns(extendedTableViewer.getCopyColumnsToClipboard());
		if(copyColumnsToClipboard != null) {
			for(int j : copyColumnsToClipboard) {
				if(j == i) {
					return true;
				}
			}
		} else {
			return true;
		}
		//
		return false;
	}

	private Button createButtonCheck(Composite parent, String text, String tooltip, boolean selection, Consumer<Boolean> consumer) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText(text);
		button.setToolTipText(tooltip);
		button.setSelection(selection);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				consumer.accept(button.getSelection());
			}
		});
		//
		return button;
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return label;
	}

	private Button createButtonSelectAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(SupportMessages.selectAll);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHECK_ALL, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				int numberColumns = getNumberColumns();
				for(int i = 0; i < numberColumns; i++) {
					columnSelections.add(i);
				}
				updateButtons(true);
			}
		});
		//
		return button;
	}

	private Button createButtonDeselectAll(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(SupportMessages.deselectAll);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_UNCHECK_ALL, IApplicationImage.SIZE_16x16));
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				columnSelections.clear();
				updateButtons(false);
			}
		});
		//
		return button;
	}

	private ComboViewer createComboViewerDelimiter(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ValueDelimiter valueDelimiter) {
					return valueDelimiter.label();
				}
				return null;
			}
		});
		//
		combo.setToolTipText(SupportMessages.valueDelimiterToolTip);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(comboViewer.getStructuredSelection().getFirstElement() instanceof ValueDelimiter delimiter) {
					valueDelimiter = delimiter;
				}
			}
		});
		//
		comboViewer.setInput(ValueDelimiter.values());
		comboViewer.setSelection(new StructuredSelection(valueDelimiter));
		//
		return comboViewer;
	}

	private void updateButtons(boolean selected) {

		for(Button button : columnCheckBoxes) {
			button.setSelection(selected);
		}
	}

	private int getNumberColumns() {

		if(extendedTableViewer != null) {
			List<TableViewerColumn> tableViewerColumns = extendedTableViewer.getTableViewerColumns();
			return tableViewerColumns.size();
		}
		//
		return -1;
	}
}