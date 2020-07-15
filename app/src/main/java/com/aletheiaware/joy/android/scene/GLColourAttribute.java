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

import com.aletheiaware.joy.scene.ColourAttribute;
import com.aletheiaware.joy.scene.Scene;

public class GLColourAttribute extends ColourAttribute {

    private final String programName;

    public GLColourAttribute(String programName, String colourName) {
        super(colourName);
        this.programName = programName;
    }

    @Override
    public void set(Scene scene) {
        GLProgram program = ((GLScene) scene).getProgramNode(programName).getProgram();
        int colourHandle = program.getUniformLocation("u_Colour");
        float[] colour = getColour(scene);
        if (colourHandle >= 0 && colour != null) {
            // Pass in the colour information
            GLES20.glUniform4fv(colourHandle, 1, colour, 0);
        }
    }

    @Override
    public void unset(Scene scene) {
        // Ignored
    }
}