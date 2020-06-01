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
import com.aletheiaware.joy.scene.VertexMesh;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class GLVertexMesh extends VertexMesh {

    public final int[] vertexBufferObject = new int[1];

    public GLVertexMesh(int vertices, FloatBuffer vb) {
        super(vertices, vb);
    }

    public GLVertexMesh(Mesh mesh) throws IOException {
        super(mesh);
    }

    public GLVertexMesh(byte[] data) throws IOException {
        super(data);
    }

    public GLVertexMesh(InputStream in) throws IOException {
        super(in);
    }

    public GLVertexMesh(DataInputStream in) throws IOException {
        super(in);
    }

    public void draw(GLProgram program) {
        // Vertex
        if (vertexBufferObject[0] == 0) {
            vertexBuffer.position(0);
            GLES20.glGenBuffers(1, vertexBufferObject, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObject[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBufferSize, vertexBuffer, GLES20.GL_STATIC_DRAW);
            System.out.println("VertexBufferObject " + vertexBufferObject[0]);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferObject[0]);
        int vertexHandle = program.getAttributeLocation("a_Position");
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        // Draw
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

        // Clean up
        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLUtils.checkError("GLVertexMesh.draw");
    }
}
