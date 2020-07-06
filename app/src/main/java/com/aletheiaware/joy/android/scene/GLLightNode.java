/*
 * Copyright 2019 Aletheia Ware LLC
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

import com.aletheiaware.joy.scene.LightNode;
import com.aletheiaware.joy.scene.Matrix;
import com.aletheiaware.joy.scene.Scene;

public class GLLightNode extends LightNode {

    private final float[] lightWorld = new float[4];
    private final float[] lightEye = new float[4];
    private final String programName;
    private boolean initialized = false;

    public GLLightNode(String programName, String lightName) {
        super(lightName);
        this.programName = programName;
    }

    @Override
    public void before(Scene scene) {
        if (!initialized) {
            initialized = true;
            // Try set u_LightPos
            try {
                GLProgram program = ((GLScene) scene).getProgramNode(programName).getProgram();
                int lightHandle = program.getUniformLocation("u_LightPos");
                float[] light = scene.getFloatArray(getLightName());
                Matrix model = scene.getMatrix("model");
                Matrix view = scene.getMatrix("view");

                // Model to world coords
                android.opengl.Matrix.multiplyMV(lightWorld, 0, model.get(), 0, light, 0);
                // World to eye coords
                android.opengl.Matrix.multiplyMV(lightEye, 0, view.get(), 0, lightWorld, 0);

                // Pass in the light information
                GLES20.glUniform3fv(lightHandle, 1, lightEye, 0);
        } catch (Exception e) {
            // Ignored
        }
            GLUtils.checkError("GLLightNode.before");
        }
    }
}
