/*
 * Copyright 2020 Aletheia Ware LLC
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

public class GLFogNode extends SceneGraphNode {

    private final String programName;

    public GLFogNode(String programName) {
        super();
        this.programName = programName;
    }

    @Override
    public void before(Scene scene) {
        // Try set u_FogColour and u_FogEnabled
        try {
            GLProgram program = ((GLScene) scene).getProgramNode(programName).getProgram();
            int fogColourHandle = program.getUniformLocation("u_FogColour");
            int fogEnabledHandle = program.getUniformLocation("u_FogEnabled");
            int fogIntensityHandle = program.getUniformLocation("u_FogIntensity");
            float[] fogColour = scene.getFloatArray("fog-colour");
            int[] fogEnabled = scene.getIntArray("fog-enabled");
            float[] fogIntensity = scene.getFloatArray("fog-intensity");
            // Pass in the fog information
            GLES20.glUniform4fv(fogColourHandle, 1, fogColour, 0);
            GLES20.glUniform1i(fogEnabledHandle, fogEnabled[0]);
            GLES20.glUniform1f(fogIntensityHandle, fogIntensity[0]);
        } catch (Exception e) {
            // Ignored
        }
        GLUtils.checkError("GLFogNode.before");
    }

    @Override
    public void after(Scene scene) {
        // Try unset u_FogEnabled
        try {
            GLProgram program = ((GLScene) scene).getProgramNode(programName).getProgram();
            int fogEnabledHandle = program.getUniformLocation("u_FogEnabled");
            // Pass in the fog information
            GLES20.glUniform1i(fogEnabledHandle, 0);
        } catch (Exception e) {
            // Ignored
        }
        GLUtils.checkError("GLFogNode.after");
    }
}
