/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WelcomeTile extends Composite {

	private Color colorInactive = Colors.getColor(74, 142, 142);
	private Color colorActive = Colors.getColor(5, 100, 100);
	//
	private ISelectionHandler selectionHandler;
	//
	private Label labelImage;
	private Text textSection;
	private Text textDesciption;

	public WelcomeTile(Composite parent, int style) {
		super(parent, style);
		initialize();
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

	private Text addTextSection(Composite parent) {

		Text text = new Text(parent, SWT.NONE);
		text.setForeground(Colors.WHITE);
		text.setBackground(colorInactive);
		Font font = new Font(Display.getDefault(), "Arial", 18, SWT.BOLD);
		text.setFont(font);
		text.setText("");
		text.setLayoutData(getGridData(SWT.BEGINNING, SWT.END, 1));
		font.dispose();
		addControlListener(text);
		return text;
	}

	private Text addTextDescription(Composite parent) {

		Text text = new Text(parent, SWT.CENTER | SWT.WRAP);
		text.setBackground(colorInactive);
		text.setText("");
		text.setLayoutData(getGridData(SWT.CENTER, SWT.BEGINNING, 2));
		addControlListener(text);
		return text;
	}

	private GridData getGridData(int horizontalAlignment, int verticalAlignment, int horizontalSpan) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = horizontalAlignment;
		gridData.verticalAlignment = verticalAlignment;
		gridData.horizontalSpan = horizontalSpan;
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
		this.layout(false);
		this.redraw();
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

		if(selectionHandler != null) {
			selectionHandler.handleEvent();
		}
	}
}
