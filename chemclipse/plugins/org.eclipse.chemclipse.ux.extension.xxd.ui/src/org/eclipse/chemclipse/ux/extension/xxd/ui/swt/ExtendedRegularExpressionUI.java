/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class ExtendedRegularExpressionUI extends Composite implements IExtendedPartUI {

	private static final String LINE_DELIMITER = OperatingSystemUtils.getLineDelimiter();
	private static final String IMAGE_SHORTCUTS = IApplicationImage.IMAGE_QUESTION;
	private static final String TOOLTIP_SHORTCUTS = "the regular expression shortcuts toolbar.";
	//
	private AtomicReference<Button> buttonToolbarInfo = new AtomicReference<>();
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<Button> buttonToolbarShortcuts = new AtomicReference<>();
	private AtomicReference<RegexShortcutsUI> toolbarShortcuts = new AtomicReference<>();
	private AtomicReference<Text> regexControl = new AtomicReference<>();
	private AtomicReference<Spinner> groupControl = new AtomicReference<>();
	private AtomicReference<Button> processControl = new AtomicReference<>();
	private AtomicReference<StyledText> contentControl = new AtomicReference<>();

	public ExtendedRegularExpressionUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void updateInput() {

		applyRegularExpression();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createToolbarShortcuts(this);
		createTextSection(this);
		//
		initialize();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(6, false));
		//
		createButtonToggleToolbarInfo(composite);
		createTextRegularExpression(composite);
		createSpinnerGroup(composite);
		createButtonExecute(composite);
		createButtonToggleToolbarShortcuts(composite);
		createButtonSettings(composite);
	}

	private void createButtonToggleToolbarInfo(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarInfo.set(button);
	}

	private void createButtonToggleToolbarShortcuts(Composite parent) {

		Button button = createButtonToggleToolbar(parent, toolbarShortcuts, IMAGE_SHORTCUTS, TOOLTIP_SHORTCUTS);
		buttonToolbarShortcuts.set(button);
	}

	private void createTextRegularExpression(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Type in the regular expression here.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(isEnterPressed(e.keyCode)) {
					updateInput();
				}
			}
		});
		//
		regexControl.set(text);
	}

	private void createSpinnerGroup(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(0);
		spinner.setMaximum(50);
		spinner.setPageIncrement(1);
		spinner.setSelection(0);
		spinner.setToolTipText("Specify the selected group to be highlighted.");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		spinner.setLayoutData(gridData);
		//
		spinner.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(isEnterPressed(e.keyCode)) {
					updateInput();
				}
			}
		});
		//
		spinner.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				if(e.button == 1) {
					updateInput();
				}
			}
		});
		//
		groupControl.set(spinner);
	}

	private void createButtonExecute(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Apply the regular expression on the content.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImageProvider.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateInput();
			}
		});
		//
		processControl.set(button);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarShortcuts(Composite parent) {

		RegexShortcutsUI regexShortcutsUI = new RegexShortcutsUI(parent, SWT.NONE);
		regexShortcutsUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarShortcuts.set(regexShortcutsUI);
	}

	private void createTextSection(Composite parent) {

		StyledText styledText = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		styledText.setText("");
		styledText.setToolTipText("Copy & Paste the content to validate here.");
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		contentControl.set(styledText);
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo.get(), IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarShortcuts, buttonToolbarShortcuts.get(), IMAGE_SHORTCUTS, TOOLTIP_SHORTCUTS, false);
		applySettings();
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applyRegularExpression() {

		String message = null;
		String regex = regexControl.get().getText().trim();
		if(!regex.isEmpty()) {
			String content = contentControl.get().getText().trim();
			if(!content.isEmpty()) {
				try {
					/*
					 * Validate the regular expression.
					 * Check each line
					 */
					StringBuilder builder = new StringBuilder();
					List<StyleRange> styleRanges = new ArrayList<>();
					Pattern pattern = Pattern.compile(regex);
					int offset = 0;
					//
					for(String line : content.split(LINE_DELIMITER)) {
						Matcher matcher = pattern.matcher(line);
						int group = groupControl.get().getSelection();
						int groups = matcher.groupCount();
						boolean isMatchPartly = (group > 0);
						//
						if(group <= groups && matcher.find()) {
							String keyword = matcher.group(group);
							offset = append(builder, styleRanges, offset, line, keyword);
							if(!isMatchPartly) {
								if(!keyword.equals(line)) {
									message = "At least one line is not fully matched.";
								}
							}
						} else {
							message = "At least one line can't be matched.";
							builder.append(line);
							builder.append(LINE_DELIMITER);
						}
					}
					/*
					 * Highlight the matches
					 */
					contentControl.get().setText(builder.toString());
					contentControl.get().setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
				} catch(Exception e) {
					message = "The regular expression is invalid.";
				}
			} else {
				message = "Please type in content to test.";
			}
		} else {
			message = "Please specify a regular expression.";
		}
		/*
		 * Display feedback.
		 */
		if(message != null) {
			toolbarInfo.get().setBackground(Colors.getColor(Colors.LIGHT_YELLOW));
			toolbarInfo.get().setText(message);
		} else {
			toolbarInfo.get().setBackground(Colors.getColor(Colors.LIGHT_GREEN));
			toolbarInfo.get().setText("The content was matched successfully.");
		}
	}

	private int append(StringBuilder builder, List<StyleRange> styleRanges, int offset, String line, String keyword) {

		builder.append(line);
		builder.append(LINE_DELIMITER);
		//
		if(line.contains(keyword)) {
			int length = keyword.length();
			int fromIndex = 0;
			boolean findKeyword = true;
			while(findKeyword) {
				int index = line.indexOf(keyword, fromIndex);
				if(index > -1) {
					StyleRange styleRange = getStyleRange(index, length, offset);
					if(styleRange != null) {
						styleRanges.add(styleRange);
					}
					fromIndex = index + length;
				} else {
					findKeyword = false;
				}
			}
		}
		//
		return offset + line.length() + LINE_DELIMITER.length();
	}

	private StyleRange getStyleRange(int index, int length, int offset) {

		StyleRange styleRange = null;
		if(index > -1 && length > 0) {
			styleRange = new StyleRange();
			styleRange.start = offset + index;
			styleRange.length = length;
			styleRange.fontStyle = SWT.BOLD;
			styleRange.background = Colors.getColor(Colors.LIGHT_YELLOW);
			styleRange.foreground = Colors.BLACK;
		}
		//
		return styleRange;
	}

	private boolean isEnterPressed(int keyCode) {

		return keyCode == SWT.LF || keyCode == SWT.CR || keyCode == SWT.KEYPAD_CR;
	}

	private void applySettings() {

		updateInput();
	}
}