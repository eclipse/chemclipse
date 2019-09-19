/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - refactor to use a TileDefinition as model
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.util.function.Consumer;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class TaskTile extends Composite {

	public static final int LARGE_TITLE = (1 << 1);
	public static final int HIGHLIGHT = (1 << 2);
	private Color colorInactive;
	private Color colorActive;
	//
	private Label labelImage;
	private Label textSection;
	private Label textDesciption;
	//
	private final Cursor handCursor;
	private final Cursor waitCursor;
	private final TileDefinition definition;
	private final Consumer<TileDefinition> definitionConsumer;
	private final Color[] colors;

	public TaskTile(Composite parent, int style, TileDefinition definition, Consumer<TileDefinition> definitionConsumer, Color[] colors) {
		super(parent, SWT.NONE);
		this.definition = definition;
		this.definitionConsumer = definitionConsumer;
		this.colors = colors;
		initialize();
		waitCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_WAIT);
		handCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		updateStyle(style);
		updateFromDefinition();
	}

	@Override
	public void dispose() {

		super.dispose();
		handCursor.dispose();
		waitCursor.dispose();
	}

	public TileDefinition getDefinition() {

		return definition;
	}

	private void setContent(Image image, String section, String description) {

		labelImage.setImage(image);
		if(image == null) {
			modifyLabelImage(true);
		} else {
			modifyLabelImage(false);
		}
		textSection.setText(section);
		textDesciption.setText(description == null ? "" : description);
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
		label.setText("");
		label.setLayoutData(getGridData(SWT.BEGINNING, SWT.END, 1));
		addControlListener(label);
		return label;
	}

	private Label addTextDescription(Composite parent) {

		Label label = new Label(parent, SWT.CENTER | SWT.WRAP);
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

			private boolean active;

			@Override
			public void mouseDown(MouseEvent e) {

				if(e.button == 1) {
					active = true;
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {

				if(active && matches(e)) {
					handleSelection();
				}
			}

			private boolean matches(MouseEvent e) {

				Point size = control.getSize();
				return e.x >= 0 && e.x <= size.x && e.y >= 0 && e.y <= size.y;
			}
		});
		control.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.CR) {
					handleSelection();
				}
			}
		});
	}

	private void handleSelection() {

		Cursor oldCursor = getCursor();
		try {
			setCursor(waitCursor);
			if(definition != null) {
				try {
					definitionConsumer.accept(definition);
				} catch(RuntimeException e) {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, "TaskTile", "invoke of consumer failed", e));
				}
			}
		} finally {
			setCursor(oldCursor);
		}
	}

	public void updateStyle(int style) {

		if((style & HIGHLIGHT) != 0) {
			setCursor(handCursor);
			colorActive = colors[0];
			colorInactive = colors[1];
		} else {
			setCursor(null);
			colorActive = colors[1];
			colorInactive = colors[1];
		}
		int fontSize;
		if((style & LARGE_TITLE) != 0) {
			fontSize = 40;
		} else {
			fontSize = 18;
		}
		Font font = new Font(getDisplay(), "Arial", fontSize, SWT.BOLD);
		textSection.setFont(font);
		font.dispose();
		setInactive();
	}

	public void updateFromDefinition() {

		if(definition != null) {
			setContent(definition.getIcon(), definition.getTitle(), definition.getDescription());
		}
	}
}
