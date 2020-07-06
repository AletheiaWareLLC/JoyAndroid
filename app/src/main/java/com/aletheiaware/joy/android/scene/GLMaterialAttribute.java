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

import com.aletheiaware.joy.scene.MaterialAttribute;
import com.aletheiaware.joy.scene.Scene;

import java.util.Arrays;

public class GLMaterialAttribute extends MaterialAttribute {

    private final String programName;

    public GLMaterialAttribute(String programName, String materialName) {
        super(materialName);
        this.programName = programName;
    }

    @Override
    public void set(Scene scene) {
        // Try set u_Material
        try {
            GLProgram program = ((GLScene) scene).getProgramNode(programName).getProgram();
            int materialHandle = program.getUniformLocation("u_Material");
            float[] material = getMaterial(scene);
            if (materialHandle >= 0 && material != null) {
                // Pass in the material information
                GLES20.glUniform3fv(materialHandle, 1, material, 0);
            }
        } catch (Exception e) {
            // Ignored
        }
        GLUtils.checkError("GLMaterialAttribute.set");
    }
}
