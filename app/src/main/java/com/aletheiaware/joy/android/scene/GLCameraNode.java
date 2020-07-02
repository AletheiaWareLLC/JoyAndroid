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

    private final String programName;

    private float width = 0;
    private float height = 0;

    public GLCameraNode(String programName) {
        super();
        this.programName = programName;
    }

    public void setupView(Scene scene) {
        // Set the view matrix. This matrix can be said to represent the camera position.
        Vector cameraEye = scene.getVector("camera-eye");
        Vector cameraLookAt = scene.getVector("camera-look-at");
        Vector cameraUp = scene.getVector("camera-up");
        Matrix view = scene.getMatrix("view");
        android.opengl.Matrix.setLookAtM(view.get(), 0, cameraEye.getX(), cameraEye.getY(), cameraEye.getZ(), cameraLookAt.getX(), cameraLookAt.getY(), cameraLookAt.getZ(), cameraUp.getX(), cameraUp.getY(), cameraUp.getZ());

        GLProgram program = ((GLScene) scene).getProgramNode(programName).getProgram();
        int cameraEyeHandle = program.getUniformLocation("u_CameraEye");
        GLES20.glUniform3f(cameraEyeHandle, cameraEye.getX(), cameraEye.getY(), cameraEye.getZ());
    }

    public void setupProjection(Scene scene, int[] viewport) {
        // Create a new perspective projection matrix.
        width = viewport[2];
        height = viewport[3];
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
        float[] frustum = scene.getFloatArray("frustum");
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
        int[] viewport = ((GLScene) scene).getViewport();
        if (width != viewport[2] || height != viewport[3]) {
            setupView(scene);
            setupProjection(scene, viewport);
        }
        GLUtils.checkError("GLCameraNode.before");
    }

    @Override
    public void after(Scene scene) {
        GLUtils.checkError("GLCameraNode.after");
    }
}
