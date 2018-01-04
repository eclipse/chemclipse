/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * PLEASE ASK BEFORE CHANGING THIS CLASS.
 */
public abstract class AbstractExtendedEditorPage implements IExtendedEditorPage {

	protected static final int HORIZONTAL_INDENT = 15;
	//
	private FormToolkit toolkit;
	private Composite control;
	private ScrolledForm scrolledForm;

	/**
	 * Use fillBody == false if you want to call the fillBody(ScrolledForm scrolledForm) method
	 * after doing some extra initializations. Otherwise, the fillBody method is called directly
	 * by the constructor.
	 * 
	 * @param pageName
	 * @param container
	 * @param fillBody
	 */
	public AbstractExtendedEditorPage(String pageName, Composite container, boolean fillBody) {
		/*
		 * Create the parent composite.
		 */
		control = new Composite(container, SWT.NONE);
		control.setLayout(new FillLayout());
		control.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * Forms API
		 */
		toolkit = new FormToolkit(control.getDisplay());
		scrolledForm = toolkit.createScrolledForm(control);
		scrolledForm.setText(pageName);
		if(fillBody) {
			fillBody(scrolledForm);
		}
	}

	@Override
	public ScrolledForm getScrolledForm() {

		return scrolledForm;
	}

	@Override
	public FormToolkit getFormToolkit() {

		return toolkit;
	}

	@Override
	public Composite getControl() {

		return control;
	}

	@Override
	public void dispose() {

		if(toolkit != null) {
			toolkit.dispose();
		}
	}

	protected TableWrapLayout createFormTableWrapLayout(boolean makeColumnsEqualWidth, int numColumns) {

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		//
		tableWrapLayout.topMargin = 12;
		tableWrapLayout.bottomMargin = 12;
		tableWrapLayout.leftMargin = 6;
		tableWrapLayout.rightMargin = 6;
		tableWrapLayout.horizontalSpacing = 20;
		tableWrapLayout.verticalSpacing = 17;
		tableWrapLayout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		tableWrapLayout.numColumns = numColumns;
		//
		return tableWrapLayout;
	}

	protected Section createSection(Composite parent, int colspan) {

		Section section = toolkit.createSection(parent, Section.EXPANDED);
		//
		section.clientVerticalSpacing = 2;
		section.setLayout(createSectionTableWrapLayout(true, 1));
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		tableWrapData.colspan = colspan;
		section.setLayoutData(tableWrapData);
		//
		return section;
	}

	protected Composite createClientInfo(Section section) {

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		//
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.marginTop = 2;
		gridLayout.marginLeft = 6;
		client.setLayout(gridLayout);
		//
		return client;
	}

	protected Label createLabel(Composite client, String text) {

		Label label = toolkit.createLabel(client, text);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		label.setLayoutData(gridData);
		return label;
	}

	protected Text createText(Composite client, int style, String text) {

		Text textField = toolkit.createText(client, text, style);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		textField.setLayoutData(gridData);
		return textField;
	}

	protected CLabel createCLabel(Composite client, String text, Image image, int horizontalIndent) {

		CLabel label = new CLabel(client, SWT.LEFT);
		label.setText(text);
		label.setImage(image);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = horizontalIndent;
		label.setLayoutData(gridData);
		return label;
	}

	protected TableWrapLayout createSectionTableWrapLayout(boolean makeColumnsEqualWidth, int numColumns) {

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		//
		tableWrapLayout.topMargin = 2;
		tableWrapLayout.bottomMargin = 2;
		tableWrapLayout.leftMargin = 2;
		tableWrapLayout.rightMargin = 2;
		tableWrapLayout.horizontalSpacing = 0;
		tableWrapLayout.verticalSpacing = 0;
		tableWrapLayout.makeColumnsEqualWidth = makeColumnsEqualWidth;
		tableWrapLayout.numColumns = numColumns;
		//
		return tableWrapLayout;
	}

	protected Section createSection(Composite parent, int colspan, String text, String description) {

		Section section = toolkit.createSection(parent, Section.EXPANDED | Section.TWISTIE | Section.DESCRIPTION | Section.TITLE_BAR);
		//
		section.clientVerticalSpacing = 2;
		section.setText(text);
		section.setDescription(description);
		section.setLayout(createSectionTableWrapLayout(true, 1));
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		tableWrapData.colspan = colspan;
		section.setLayoutData(tableWrapData);
		//
		return section;
	}

	protected Composite createClient(Section section) {

		return createClient(section, 1);
	}

	protected Composite createClient(Section section, int numColumns) {

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		//
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 2;
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 15;
		client.setLayout(gridLayout);
		//
		return client;
	}
}
