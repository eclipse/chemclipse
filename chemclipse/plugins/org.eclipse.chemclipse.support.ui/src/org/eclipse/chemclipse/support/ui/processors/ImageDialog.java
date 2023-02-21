/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.l10n.SupportMessages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ImageDialog extends Dialog {

	public static final int DEFAULT_WIDTH = 400;
	public static final int DEFAULT_HEIGHT = 450;
	//
	private static final String FILE_NAME = "FileName"; //$NON-NLS-1$
	private static final String EXTENSION_GIF = ".gif"; //$NON-NLS-1$
	private static final String EXTENSION_PNG = ".png"; //$NON-NLS-1$
	//
	private Text textSearch;
	private Table tableImages;
	private String imageFileName = null;
	//
	private List<String> images = new ArrayList<>();

	public ImageDialog(Shell parent) {

		super(parent);
	}

	public String getImageFileName() {

		return imageFileName;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);
		shell.setText(SupportMessages.processorImage);
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
		composite.setLayout(new GridLayout(2, false));
		//
		createToolbarTop(composite);
		tableImages = createTableImages(composite);
		//
		initialize();
		updateTable(""); //$NON-NLS-1$
		//
		return composite;
	}

	private void initialize() {

		images.addAll(ApplicationImageFactory.getInstance().listImages(IApplicationImage.SIZE_16x16));
		Collections.sort(images);
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
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImageProvider.SIZE_16x16));
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

	private Table createTableImages(Composite parent) {

		Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		//
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		table.setLayoutData(gridData);
		//
		TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
		tableColumn.setWidth(DEFAULT_WIDTH - 50);
		//
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectImage();
			}
		});
		//
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				selectImage();
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				if(selectImage()) {
					closeDialog();
				}
			}
		});
		//
		return table;
	}

	private void runSearch() {

		String searchText = textSearch.getText().trim();
		updateTable(searchText);
	}

	private void updateTable(String searchTerm) {

		tableImages.clearAll();
		for(TableItem tableItem : tableImages.getItems()) {
			tableItem.dispose();
		}
		//
		for(String image : images) {
			if(isValidImage(image)) {
				if(imageMatchesSearch(image, searchTerm)) {
					TableItem tableItem = new TableItem(tableImages, SWT.NONE);
					tableItem.setData(FILE_NAME, image);
					tableItem.setText(image.replace(EXTENSION_GIF, "").replace(EXTENSION_PNG, "").toLowerCase()); //$NON-NLS-1$ //$NON-NLS-2$
					tableItem.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImageProvider.SIZE_16x16));
				}
			}
		}
	}

	private boolean isValidImage(String image) {

		return (image.toLowerCase().endsWith(EXTENSION_GIF) || image.toLowerCase().endsWith(EXTENSION_PNG));
	}

	private boolean imageMatchesSearch(String image, String searchTerm) {

		if(searchTerm == null || searchTerm.isEmpty()) {
			return true;
		}
		//
		return image.toLowerCase().contains(searchTerm.toLowerCase());
	}

	private boolean selectImage() {

		int index = tableImages.getSelectionIndex();
		TableItem tableItem = tableImages.getItem(index);
		if(tableItem != null) {
			imageFileName = tableItem.getData(FILE_NAME).toString();
			return true;
		} else {
			imageFileName = null;
			return false;
		}
	}

	private void closeDialog() {

		super.okPressed();
	}
}