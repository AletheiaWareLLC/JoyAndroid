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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class GLUtils {

    private GLUtils() {
    }

    public static void checkError(String source) {
        int err = GLES20.glGetError();
        if (err != GLES20.GL_NO_ERROR) {
            System.err.println(source + " GL Error " + err);
        }
    }

    public static void dumpGLInfo() {
        System.out.println("GL_VENDOR: " + GLES20.glGetString(GLES20.GL_VENDOR));
        System.out.println("GL_RENDERER: " + GLES20.glGetString(GLES20.GL_RENDERER));
        System.out.println("GL_VERSION: " + GLES20.glGetString(GLES20.GL_VERSION));
    }

    public static int loadProgram(String vertexSource, String fragmentSource, Map<String, Integer> attributes, Map<String, Integer> uniforms) throws IOException {
        int vertexShader = loadShader(vertexSource, GLES20.GL_VERTEX_SHADER);
        int fragmentShader = loadShader(fragmentSource, GLES20.GL_FRAGMENT_SHADER);
        int programId = GLES20.glCreateProgram();// create empty OpenGL ES Program
        GLES20.glAttachShader(programId, vertexShader);// add the vertex shader to program
        GLES20.glAttachShader(programId, fragmentShader);// add the fragment shader to program
        GLES20.glLinkProgram(programId);// creates OpenGL ES program executables
        // Get the link status.
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        // If the link failed, delete the program.
        if (linkStatus[0] == 0) {
            System.err.println(GLES20.glGetProgramInfoLog(programId));
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragmentShader);
            GLES20.glDeleteProgram(programId);
            programId = 0;
        } else {
            for (String a : attributes.keySet()) {
                int i = GLES20.glGetAttribLocation(programId, a);
                attributes.put(a, i);
                System.out.println("Attribute " + a + " " + i);
            }
            for (String u : uniforms.keySet()) {
                int i = GLES20.glGetUniformLocation(programId, u);
                uniforms.put(u, i);
                System.out.println("Uniform " + u + " " + i);
            }
        }
        return programId;
    }

    private static int loadShader(String source, int type) throws IOException {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        if (shader > 0) {
            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                System.err.println(GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int[] loadTexture(InputStream in) throws IOException {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;// No pre-scaling
            options.inPremultiplied = false;// No pre-multiplying
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            final Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set wrapping
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Generate mipmaps
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        } else {
            textureHandle[0] = -1;
        }
        checkError("loadTexture");
        return textureHandle;
    }
}
