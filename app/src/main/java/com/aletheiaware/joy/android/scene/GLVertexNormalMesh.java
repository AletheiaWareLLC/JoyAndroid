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

import com.aletheiaware.joy.JoyProto.Mesh;
import com.aletheiaware.joy.scene.VertexNormalMesh;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class GLVertexNormalMesh extends VertexNormalMesh {

    // TODO move to GLProgram
    public final int[] vbo = new int[1];
    public final int[] nbo = new int[1];

    public GLVertexNormalMesh(int size, int vertices, FloatBuffer vb, FloatBuffer nb) {
        super(size, vertices, vb, nb);
    }

    public GLVertexNormalMesh(Mesh mesh) throws IOException {
        super(mesh);
    }

    public GLVertexNormalMesh(byte[] data) throws IOException {
        super(data);
    }

    public GLVertexNormalMesh(InputStream in) throws IOException {
        super(in);
    }

    public GLVertexNormalMesh(DataInputStream in) throws IOException {
        super(in);
    }

    public void draw(GLProgram program) {
        // Vertex
        if (vbo[0] == 0) {
            vertexBuffer.position(0);
            GLES20.glGenBuffers(1, vbo, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferSize, vertexBuffer, GLES20.GL_STATIC_DRAW);
            System.out.println("vbo " + vbo[0]);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        int vertexHandle = program.getAttributeLocation("a_Position");
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        // Normal
        if (nbo[0] == 0) {
            normalBuffer.position(0);
            GLES20.glGenBuffers(1, nbo, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, nbo[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferSize, normalBuffer, GLES20.GL_STATIC_DRAW);
            System.out.println("nbo " + nbo[0]);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, nbo[0]);
        int normalHandle = program.getAttributeLocation("a_Normal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        // Draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numVertices);

        // Clean up
        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLUtils.checkError("GLVertexNormalMesh.draw");
    }
}
