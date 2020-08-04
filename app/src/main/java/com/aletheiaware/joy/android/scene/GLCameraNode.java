/*
 * Copyright 2018 Stuart Scott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aletheiaware.joy.android.scene;

import android.opengl.GLES20;

import com.aletheiaware.joy.scene.Matrix;
import com.aletheiaware.joy.scene.Scene;
import com.aletheiaware.joy.scene.SceneGraphNode;
import com.aletheiaware.joy.scene.Vector;

public class GLCameraNode extends SceneGraphNode {

    public final String programName;
    public final String cameraName;

    public GLCameraNode(String programName) {
        this(programName, "camera");
    }

    public GLCameraNode(String programName, String cameraName) {
        super();
        this.programName = programName;
        this.cameraName = cameraName;
    }

    public void setupView(Scene scene, int[] viewport, Vector eye, Vector lookAt, Vector up) {
        // Set the viewport
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

        // Set the view matrix. This matrix can be said to represent the camera position.
        Matrix view = scene.getMatrix("view");
        android.opengl.Matrix.setLookAtM(view.get(), 0, eye.getX(), eye.getY(), eye.getZ(), lookAt.getX(), lookAt.getY(), lookAt.getZ(), up.getX(), up.getY(), up.getZ());

        // Try set u_CameraEye
        try {
            GLProgram program = ((GLScene) scene).getProgramNode(programName).getProgram();
            int eyeHandle = program.getUniformLocation("u_CameraEye");
            GLES20.glUniform3f(eyeHandle, eye.getX(), eye.getY(), eye.getZ());
        } catch (Exception e) {
            // Ignored
        }
    }

    public void setupProjection(Scene scene, float width, float height, float[] frustum) {
        // Create a new perspective projection matrix.
        float left;
        float right;
        float bottom;
        float top;
        if (width > height) {
            float ratio = width / height;
            // The height will stay the same while the width will vary as per aspect ratio.
            left = -ratio;
            right = ratio;
            bottom = -1;
            top = 1;
        } else {
            float ratio = height / width;
            // The width will stay the same while the height will vary as per aspect ratio.
            left = -1;
            right = 1;
            bottom = -ratio;
            top = ratio;
        }
        float near = frustum[0];
        float far = frustum[1];
        // System.out.println("width: " + width);
        // System.out.println("height: " + height);
        // System.out.println("left: " + left);
        // System.out.println("right: " + right);
        // System.out.println("bottom: " + bottom);
        // System.out.println("top: " + top);
        // System.out.println("near: " + near);
        // System.out.println("far: " + far);
        Matrix projection = scene.getMatrix("projection");
        android.opengl.Matrix.frustumM(projection.get(), 0, left, right, bottom, top, near, far);
    }

    @Override
    public void before(Scene scene) {
        int[] viewport = scene.getIntArray(cameraName + "-viewport");
        Vector eye = scene.getVector(cameraName + "-eye");
        Vector lookAt = scene.getVector(cameraName + "-look-at");
        Vector up = scene.getVector(cameraName + "-up");
        float[] frustum = scene.getFloatArray(cameraName + "-frustum");
        setupView(scene, viewport, eye, lookAt, up);
        setupProjection(scene, viewport[2], viewport[3], frustum);
        GLUtils.checkError("GLCameraNode.before");
    }

    @Override
    public void after(Scene scene) {
        GLUtils.checkError("GLCameraNode.after");
    }
}
