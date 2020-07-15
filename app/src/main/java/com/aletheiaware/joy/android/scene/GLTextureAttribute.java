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

import com.aletheiaware.joy.scene.TextureAttribute;
import com.aletheiaware.joy.scene.Scene;

import java.util.Arrays;

public abstract class GLTextureAttribute extends TextureAttribute {

    private final String programName;

    public GLTextureAttribute(String programName, String textureName) {
        super(textureName);
        this.programName = programName;
    }

    public abstract void load();

    @Override
    public void set(Scene scene) {
        // Try set u_Texture and u_TextureEnabled
        try {
            GLScene glScene = ((GLScene) scene);
            GLProgram program = glScene.getProgramNode(programName).getProgram();
            int textureHandle = program.getUniformLocation("u_Texture");
            int textureEnabledHandle = program.getUniformLocation("u_TextureEnabled");
            if (textureHandle >= 0 && textureEnabledHandle >= 0) {
                int[] textureId = getTexture(scene);
                if (textureId == null || textureId.length == 0 || textureId[0] == -1) {
                    load();
                    textureId = getTexture(scene);
                }
                if (textureId != null && textureId.length > 0) {
                    // Set the active texture unit to texture unit 0.
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                    // Bind the texture to this unit.
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
                    // Set the texture uniform sampler to texture unit 0.
                    GLES20.glUniform1i(textureHandle, 0);
                    // Enable texturing.
                    GLES20.glUniform1i(textureEnabledHandle, 1);
                } else {
                    // Disable texturing.
                    GLES20.glUniform1i(textureEnabledHandle, 0);
                }
            }
        } catch (Exception e) {
            // Ignored
        }
        GLUtils.checkError("GLTextureAttribute.set");
    }

    @Override
    public void unset(Scene scene) {
        // Try unset u_TextureEnabled
        try {
            GLScene glScene = ((GLScene) scene);
            GLProgram program = glScene.getProgramNode(programName).getProgram();
            int textureEnabledHandle = program.getUniformLocation("u_TextureEnabled");
            if (textureEnabledHandle >= 0) {
                // Disable texturing.
                GLES20.glUniform1i(textureEnabledHandle, 0);
            }
        } catch (Exception e) {
            // Ignored
        }
        GLUtils.checkError("GLTextureAttribute.unset");
    }
}