/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.chemclipse.support.text.ValueFormat;

import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Axes {

	private boolean addPlane = false;
	final private PhongMaterial cornMaterial = new PhongMaterial();
	private NumberFormat format;
	final private PhongMaterial gridaxisMaterial = new PhongMaterial();
	private double lableDistance;
	private double lableDistanceNameAxis;
	final private Group mainGroup = new Group();
	final private PhongMaterial planeMaterial = new PhongMaterial();
	private Chart3DSettings settings;
	private double tickLenght;
	private double widthCorn;

	public Axes(Chart3DSettings settings) {
		this.settings = settings;
		double absMaximum = Arrays.stream(new double[]{settings.getMinX(), settings.getMaxX(), settings.getMinY(), settings.getMaxY(), settings.getMinZ(), settings.getMaxZ()}).map(d -> Math.abs(d)).max().getAsDouble();
		double numberDigits = Math.floor(Math.log10(absMaximum));
		if(Math.abs(numberDigits) > 4) {
			format = new DecimalFormat("#.##E0", new DecimalFormatSymbols(Locale.US));
		} else {
			format = ValueFormat.getNumberFormatEnglish();
		}
		/*
		 *
		 */
		cornMaterial.setDiffuseColor(Color.BLACK);
		cornMaterial.setSpecularColor(Color.BLACK.brighter());
		/*
		 * set look plane
		 */
		gridaxisMaterial.setDiffuseColor(Color.GRAY);
		gridaxisMaterial.setSpecularColor(Color.GRAY.brighter());
		planeMaterial.setDiffuseColor(Color.LIGHTGRAY);
		planeMaterial.setSpecularColor(Color.LIGHTGRAY.brighter());
		/*
		 * set fix size
		 */
		tickLenght = 40;
		lableDistance = 70;
		lableDistanceNameAxis = 200;
		widthCorn = 4;
		mainGroup.getChildren().setAll(createXYPlane(), createYZPlane(), createXZPlane(), createCorns(), createXLabels(), createYLabels(), createZLanels());
	}

	private Node createCorn(double height, Point3D rotation, double rotate, double translationX, double translationY, double translationZ, double lengthening) {

		Box corn = new Box(height + lengthening, widthCorn, widthCorn);
		corn.setMaterial(cornMaterial);
		corn.setRotationAxis(rotation);
		corn.setRotate(rotate);
		corn.setTranslateX(translationX);
		corn.setTranslateY(translationY);
		corn.setTranslateZ(translationZ);
		return corn;
	}

	private Group createCorns() {

		double s = settings.getScale();
		Group group = new Group();
		Node corn = createCorn((settings.getAxisXlenght()) * s, Rotate.X_AXIS, 0, 0, settings.getAxisMinY() * s, settings.getAxisMinZ() * s, 500);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisXlenght()) * s, Rotate.X_AXIS, 0, 0, settings.getAxisMaxY() * s, settings.getAxisMinZ() * s, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisXlenght()) * s, Rotate.X_AXIS, 0, 0, settings.getAxisMinY() * s, settings.getAxisMaxZ() * s, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisXlenght()) * s, Rotate.X_AXIS, 0, 0, settings.getAxisMaxY() * s, settings.getAxisMaxZ() * s, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisYlenght()) * s, Rotate.Z_AXIS, 90, settings.getAxisMinX() * s, 0, settings.getAxisMinZ() * s, 500);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisYlenght()) * s, Rotate.Z_AXIS, 90, settings.getAxisMaxX() * s, 0, settings.getAxisMinZ() * s, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisYlenght()) * s, Rotate.Z_AXIS, 90, settings.getAxisMinX() * s, 0, settings.getAxisMaxZ() * s, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisYlenght()) * s, Rotate.Z_AXIS, 90, settings.getAxisMaxX() * s, 0, settings.getAxisMaxZ() * s, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisZlenght()) * s, Rotate.Y_AXIS, 90, settings.getAxisMinX() * s, settings.getAxisMinY() * s, 0, 500);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisZlenght()) * s, Rotate.Y_AXIS, 90, settings.getAxisMaxX() * s, settings.getAxisMinY() * s, 0, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisZlenght()) * s, Rotate.Y_AXIS, 90, settings.getAxisMinX() * s, settings.getAxisMaxY() * s, 0, 0);
		group.getChildren().add(corn);
		corn = createCorn((settings.getAxisZlenght()) * s, Rotate.Y_AXIS, 90, settings.getAxisMaxX() * s, settings.getAxisMaxY() * s, 0, 0);
		group.getChildren().add(corn);
		return group;
	}

	private Node createGridLine(Point3D origin, Point3D target) {

		Point3D yAxis = new Point3D(0, 1, 0);
		Point3D diff = target.subtract(origin);
		double height = diff.magnitude();
		Point3D mid = target.midpoint(origin);
		Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());
		Point3D axisOfRotation = diff.crossProduct(yAxis);
		double angle = Math.acos(diff.normalize().dotProduct(yAxis));
		Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
		Box line = new Box(2, height, 2);
		line.setMaterial(gridaxisMaterial);
		line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
		return line;
	}

	private Group createPlane(double sizeH, double sizeV) {

		Group group = new Group();
		if(addPlane) {
			Box plane = new Box(sizeH, sizeV, 2);
			plane.setMaterial(planeMaterial);
			group.getChildren().add(plane);
		}
		double startX = -sizeH / 2;
		double finishX = sizeH / 2;
		double startY = -sizeV / 2;
		double finishY = sizeV / 2;
		double lineSpacingScaled = settings.getLineSpacing() * settings.getScale();
		for(double x = startX; x <= finishX; x += lineSpacingScaled) {
			group.getChildren().add(createGridLine(new Point3D(x, startY, 0), new Point3D(x, finishY, 0)));
		}
		for(double y = startY; y <= finishY; y += lineSpacingScaled) {
			group.getChildren().add(createGridLine(new Point3D(startX, y, 0), new Point3D(finishX, y, 0)));
		}
		return group;
	}

	private Group createXLabels() {

		Group group = new Group();
		String nameAxis = settings.getLabelAxisX();
		Label name = new Label(nameAxis);
		name.setTranslateZ(lableDistanceNameAxis);
		name.setRotationAxis(Rotate.X_AXIS);
		name.setRotate(90);
		setLabelStyleAxis(name);
		group.getChildren().add(name);
		double lineSpacingScaled = settings.getLineSpacing() * settings.getScale();
		double minX = settings.getAxisMinX() * settings.getScale();
		double maxX = settings.getAxisMaxX() * settings.getScale();
		double minY = settings.getAxisMinY() * settings.getScale();
		double maxZ = settings.getAxisMaxZ() * settings.getScale();
		for(int i = (int)(minX / lineSpacingScaled) + 1; i < (int)(maxX / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * settings.getLineSpacing()));
			setLabelStyle(label);
			Rotate xRotate = new Rotate(90, 0, 0, 0, Rotate.X_AXIS);
			Rotate yRotate = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
			label.getTransforms().addAll(yRotate, xRotate);
			label.setTranslateX(i * lineSpacingScaled);
			label.setTranslateZ(2 * lableDistance);
			Node tick = createGridLine(new Point3D(i * lineSpacingScaled, 0, -tickLenght / 2), new Point3D(i * lineSpacingScaled, 0, tickLenght / 2));
			group.getChildren().add(tick);
			group.getChildren().add(label);
		}
		group.setTranslateX((maxX - minX) / 2 + minX);
		group.setTranslateY(minY);
		group.setTranslateZ(maxZ);
		return group;
	}

	private Group createXYPlane() {

		Group group = createPlane(settings.getAxisXlenght() * settings.getScale(), settings.getAxisYlenght() * settings.getScale());
		group.setTranslateZ(settings.getAxisMinZ() * settings.getScale());
		return group;
	}

	private Group createXZPlane() {

		Group group = createPlane(settings.getAxisXlenght() * settings.getScale(), settings.getAxisZlenght() * settings.getScale());
		group.setTranslateY(settings.getAxisMinY() * settings.getScale());
		group.setRotationAxis(Rotate.X_AXIS);
		group.setRotate(90);
		return group;
	}

	private Group createYLabels() {

		Group group = new Group();
		String axisName = settings.getLabelAxisY();
		Label name = new Label(axisName);
		Rotate zRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Z_AXIS);
		Rotate yRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS);
		name.getTransforms().addAll(yRotateAxis, zRotateAxis);
		name.setTranslateZ(lableDistanceNameAxis);
		setLabelStyleAxis(name);
		group.getChildren().add(name);
		double minY = settings.getAxisMinY() * settings.getScale();
		double maxY = settings.getAxisMaxY() * settings.getScale();
		double maxZ = settings.getAxisMaxZ() * settings.getScale();
		double minX = settings.getAxisMinX() * settings.getScale();
		double lineSpacingScaled = settings.getLineSpacing() * settings.getScale();
		for(int i = (int)(minY / lineSpacingScaled) + 1; i < (int)(maxY / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * settings.getLineSpacing()));
			setLabelStyle(label);
			Rotate xRotate = new Rotate(180, 0, 0, 0, Rotate.X_AXIS);
			Rotate yRotate = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
			label.getTransforms().addAll(yRotate, xRotate);
			label.setTranslateY(i * lineSpacingScaled);
			label.setTranslateZ(2 * lableDistance);
			Node tick = createGridLine(new Point3D(0, i * lineSpacingScaled, -tickLenght / 2), new Point3D(0, i * lineSpacingScaled, tickLenght / 2));
			group.getChildren().add(tick);
			group.getChildren().add(label);
		}
		group.setTranslateY((maxY - minY) / 2 + minY);
		group.setTranslateX(minX);
		group.setTranslateZ(maxZ);
		return group;
	}

	private Group createYZPlane() {

		Group group = createPlane(settings.getAxisZlenght() * settings.getScale(), settings.getAxisYlenght() * settings.getScale());
		group.setTranslateX(settings.getAxisMinX() * settings.getScale());
		group.setRotationAxis(Rotate.Y_AXIS);
		group.setRotate(90);
		return group;
	}

	private Group createZLanels() {

		Group group = new Group();
		String nameAxis = settings.getLabelAxisZ();
		Label name = new Label(nameAxis);
		Rotate yRotateAxis = new Rotate(90, 0, 0, 0, Rotate.Y_AXIS);
		Rotate zRotateAxis = new Rotate(-90, 0, 0, 0, Rotate.Z_AXIS);
		name.getTransforms().addAll(zRotateAxis, yRotateAxis);
		name.setTranslateX(lableDistanceNameAxis);
		setLabelStyleAxis(name);
		group.getChildren().add(name);
		double minZ = settings.getAxisMinZ() * settings.getScale();
		double maxZ = settings.getAxisMaxZ() * settings.getScale();
		double maxX = settings.getAxisMaxX() * settings.getScale();
		double minY = settings.getAxisMinY() * settings.getScale();
		double lineSpacingScaled = settings.getLineSpacing() * settings.getScale();
		for(int i = (int)(minZ / lineSpacingScaled) + 1; i < (int)(maxZ / lineSpacingScaled); i++) {
			Label label = new Label(format.format(i * settings.getLineSpacing()));
			setLabelStyle(label);
			label.setRotationAxis(Rotate.X_AXIS);
			label.setRotate(90);
			label.setTranslateZ(i * lineSpacingScaled);
			label.setTranslateX(lableDistance);
			Node tick = createGridLine(new Point3D(-tickLenght / 2, 0, i * lineSpacingScaled), new Point3D(tickLenght / 2, 0, i * lineSpacingScaled));
			group.getChildren().add(tick);
			group.getChildren().add(label);
		}
		group.setTranslateZ((maxZ - minZ) / 2 + minZ);
		group.setTranslateX(maxX);
		group.setTranslateY(minY);
		return group;
	}

	public Group getAxes() {

		return mainGroup;
	}

	private void setLabelStyle(Label label) {

		label.setAlignment(Pos.CENTER);
		label.setFont(new Font("Arial", 35));
	}

	private void setLabelStyleAxis(Label label) {

		label.setFont(new Font("Arial", 50));
	}
}
