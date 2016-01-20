/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Rafael Aguayo - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import javafx.embed.swt.FXCanvas;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class ScorePlot3dPage {

	private PcaEditor pcaEditor;

	public ScorePlot3dPage(PcaEditor pcaEditor, TabFolder tabFolder, FormToolkit formToolkit) {
		//
		this.pcaEditor = pcaEditor;
		initialize(tabFolder, formToolkit);
	}

	public void update() {

		IPcaResults pcaResults = pcaEditor.getPcaResults();
		for(Map.Entry<ISample, IPcaResult> entry : pcaResults.getPcaResultMap().entrySet()) {
			IPcaResult pcaResult = entry.getValue();
			double[] eigenSpace = pcaResult.getEigenSpace();
			/*
			 * Place each sample coordinates in a 3D view.
			 */
			String name = entry.getKey().getName();
			double x = eigenSpace[0]; // PC1
			double y = eigenSpace[1]; // PC2
			double z = eigenSpace[2]; // PC3
			System.out.println(name + " -> " + x + "," + y + "," + z);
		}
	}

	private void initialize(TabFolder tabFolder, FormToolkit formToolkit) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("3D View");
		//
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new FillLayout());
		//
		Composite parent = new Composite(composite, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(GridData.FILL_BOTH);
		parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		/*
		 * JavaFX
		 */
		FXCanvas fxCanvas = new FXCanvas(parent, SWT.NONE) {

			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {

				getScene().getWindow().sizeToScene();
				int width = (int)getScene().getWidth();
				int height = (int)getScene().getHeight();
				return new Point(width, height);
			}
		};
		Group group = new Group();
		Button javafxButton = new Button("Curious?");
		javafxButton.setId("pcaButton");
		group.getChildren().add(javafxButton);
		Scene scene = new Scene(group, Color.rgb(parent.getBackground().getRed(), parent.getBackground().getGreen(), parent.getBackground().getBlue()));
		scene.getStylesheets().add("css/pca.css");
		fxCanvas.setScene(scene);
		javafxButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				if(javafxButton.getText().equals("Curious?")) {
					javafxButton.setText("A 3D view will be available soon!");
				} else {
					javafxButton.setText("Curious?");
				}
				parent.layout();
			}
		});
		//
		tabItem.setControl(composite);
	}
}
