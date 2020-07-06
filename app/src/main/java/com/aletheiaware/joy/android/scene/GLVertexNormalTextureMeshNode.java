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

public class GLVertexNormalTextureMeshNode extends SceneGraphNode {

    private final String programName;
    private final String meshName;

    public GLVertexNormalTextureMeshNode(String programName, String meshName) {
        super();
        this.programName = programName;
        this.meshName = meshName;
    }

    public String getMeshName() {
        return meshName;
    }

    @Override
    public void before(Scene scene) {
        GLScene glScene = ((GLScene) scene);
        GLProgram program = glScene.getProgramNode(programName).getProgram();

        // Set matricies
        Matrix model = scene.getMatrix("model");
        Matrix view = scene.getMatrix("view");
        Matrix projection = scene.getMatrix("projection");
        Matrix mv = scene.getMatrix("model-view");
        Matrix mvp = scene.getMatrix("model-view-projection");
        mv.makeMultiplication(view, model);
        mvp.makeMultiplication(projection, mv);

        // Try set u_MVMatrix
        try {
            GLES20.glUniformMatrix4fv(program.getUniformLocation("u_MVMatrix"), 1, false, mv.get(), 0);
        } catch (Exception e) {
            // Ignored
        }

        // Try set u_MVPMatrix
        try {
            GLES20.glUniformMatrix4fv(program.getUniformLocation("u_MVPMatrix"), 1, false, mvp.get(), 0);
        } catch (Exception e) {
            // Ignored
        }

        // Draw vertex normal mesh
        GLVertexNormalTextureMesh mesh = glScene.getVertexNormalTextureMesh(meshName);
        if (mesh != null) {
            mesh.draw(program, meshName);
        }

        GLUtils.checkError("GLVertexNormalTextureMeshNode.before");
    }

    @Override
    public void after(Scene scene) {
    }
}
