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
import android.opengl.GLSurfaceView;

import com.aletheiaware.joy.scene.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLScene extends Scene implements GLSurfaceView.Renderer {

    private final int[] viewport = new int[4];
    private final List<String> programNames = new ArrayList<>();
    private final Map<String, GLProgramNode> programNodes = new HashMap<>();
    private final Map<String, GLVertexMesh> vertexMeshes = new HashMap<>();
    private final Map<String, GLVertexNormalMesh> vertexNormalMeshes = new HashMap<>();
    //private final Map<String, GLVertexNormalTextureMesh> vertexNormalTextureMeshes = new HashMap<>();

    public void putProgramNode(String name, GLProgramNode program) {
        programNames.add(name);
        programNodes.put(name, program);
    }

    public GLProgramNode getProgramNode(String name) {
        return programNodes.get(name);
    }

    public GLVertexMesh getVertexMesh(String name) {
        return vertexMeshes.get(name);
    }

    public void putVertexMesh(String name, GLVertexMesh mesh) {
        vertexMeshes.put(name, mesh);
    }

    public GLVertexNormalMesh getVertexNormalMesh(String name) {
        return vertexNormalMeshes.get(name);
    }

    public void putVertexNormalMesh(String name, GLVertexNormalMesh mesh) {
        vertexNormalMeshes.put(name, mesh);
    }

    public int[] getViewport() {
        return viewport;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        System.out.println("GLScene.onSurfaceCreated: " + config);
        GLUtils.dumpGLInfo();
        getMatrix("model").makeIdentity();
        getMatrix("view").makeIdentity();
        getMatrix("projection").makeIdentity();
        GLUtils.checkError("GLScene.onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        System.out.println("GLScene.onSurfaceChanged: " + width + " x " + height);
        viewport[0] = 0;
        viewport[1] = 0;
        viewport[2] = width;
        viewport[3] = height;
        for (int i = 0; i < programNames.size(); i++) {
            programNodes.get(programNames.get(i)).getProgram().reshape(0, 0, width, height);
        }
        GLUtils.checkError("GLScene.onSurfaceChanged");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glFrontFace(GLES20.GL_CCW);
        for (int i = 0; i < programNames.size(); i++) {
            programNodes.get(programNames.get(i)).draw(this);
        }
        GLUtils.checkError("GLScene.onDrawFrame");
    }
}
