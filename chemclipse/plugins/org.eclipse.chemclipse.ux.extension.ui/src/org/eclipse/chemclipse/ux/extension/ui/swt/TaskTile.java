/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class TaskTile extends Composite {

	public static final int LARGE_TITLE = (1 << 1);
	public static final int HIGHLIGHT = (1 << 2);
	private Color colorInactive = Colors.getColor(74, 142, 142);
	private Color colorActive = Colors.getColor(5, 100, 100);
	//
	private ISelectionHandler selectionHandler;
	//
	private Label labelImage;
	private Label textSection;
	private Label textDesciption;
	//
	private Cursor handCursor;
	private Cursor waitCursor;

	public TaskTile(Composite parent, int style) {
		super(parent, SWT.NONE);
		initialize();
		waitCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_WAIT);
		handCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		updateStyle(style);
	}

	@Override
	public void dispose() {

		super.dispose();
		handCursor.dispose();
		waitCursor.dispose();
	}

	public void setColors(Color colorActive, Color colorInactive) {

		this.colorActive = colorActive;
		this.colorInactive = colorInactive;
		//
		setBackground(colorInactive);
		textSection.setBackground(colorInactive);
		textDesciption.setBackground(colorInactive);
	}

	public void setSelectionHandler(ISelectionHandler selectionHandler) {

		this.selectionHandler = selectionHandler;
	}

	public void setContent(Image image, String section, String description) {

		labelImage.setImage(image);
		if(image == null) {
			modifyLabelImage(true);
		} else {
			modifyLabelImage(false);
		}
		textSection.setText(section);
		textDesciption.setText(description);
		this.layout(true);
		this.redraw();
	}

	public void setActive() {

		setBackgroundColor(colorActive);
	}

	public void setInactive() {

		setBackgroundColor(colorInactive);
	}

	private void initialize() {

		setLayout(new GridLayout(1, true));
		setBackground(colorInactive);
		//
		setLayout(new GridLayout(2, true));
		addControlListener(this);
		//
		labelImage = addLabelImage(this);
		textSection = addTextSection(this);
		textDesciption = addTextDescription(this);
	}

	private Label addLabelImage(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(getGridData(SWT.END, SWT.END, 1));
		addControlListener(label);
		return label;
	}

	private Label addTextSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setForeground(Colors.WHITE);
		label.setBackground(colorInactive);
		label.setText("");
		label.setLayoutData(getGridData(SWT.BEGINNING, SWT.END, 1));
		addControlListener(label);
		return label;
	}

	private Label addTextDescription(Composite parent) {

		Label label = new Label(parent, SWT.CENTER | SWT.WRAP);
		label.setBackground(colorInactive);
		label.setText("");
		label.setLayoutData(getGridData(SWT.CENTER, SWT.BEGINNING, 2));
		addControlListener(label);
		return label;
	}

	private GridData getGridData(int horizontalAlignment, int verticalAlignment, int horizontalSpan) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = horizontalAlignment;
		gridData.verticalAlignment = verticalAlignment;
		gridData.horizontalSpan = horizontalSpan;
		gridData.grabExcessHorizontalSpace = true;
		return gridData;
	}

	private void setBackgroundColor(Color color) {

		setBackground(color);
		labelImage.setBackground(color);
		textSection.setBackground(color);
		textDesciption.setBackground(color);
	}

	private void modifyLabelImage(boolean exclude) {

		GridData gridDataLabel = (GridData)labelImage.getLayoutData();
		gridDataLabel.exclude = exclude;
		labelImage.setVisible(!exclude);
		GridData gridDataText = (GridData)textSection.getLayoutData();
		gridDataText.horizontalAlignment = (exclude) ? SWT.CENTER : SWT.BEGINNING;
		gridDataText.horizontalSpan = (exclude) ? 2 : 1;
	}

	private void addControlListener(Control control) {

		control.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				handleSelection();
			}
		});
		control.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				handleSelection();
			}
		});
	}

	private void handleSelection() {

		Cursor oldCursor = getCursor();
		try {
			setCursor(waitCursor);
			if(selectionHandler != null) {
				selectionHandler.handleEvent();
			}
		} finally {
			setCursor(oldCursor);
		}
	}

	public void updateStyle(int style) {

		if((style & HIGHLIGHT) != 0) {
			setCursor(handCursor);
			colorActive = Colors.getColor(5, 100, 100);
		} else {
			setCursor(null);
			colorActive = colorInactive;
		}
		int fontSize;
		if((style & LARGE_TITLE) != 0) {
			fontSize = 40;
		} else {
			fontSize = 18;
		}
		Font font = new Font(DisplayUtils.getDisplay(), "Arial", fontSize, SWT.BOLD);
		textSection.setFont(font);
		font.dispose();
	}
}
