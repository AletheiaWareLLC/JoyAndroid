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

import com.aletheiaware.joy.JoyProto.Mesh;
import com.aletheiaware.joy.scene.VertexNormalTextureMesh;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class GLVertexNormalTextureMesh extends VertexNormalTextureMesh {

    // TODO move to GLProgram
    public final int[] vertexBufferObject = new int[1];
    public final int[] normalBufferObject = new int[1];
    public final int[] textureBufferObject = new int[1];

    public GLVertexNormalTextureMesh(int vertices, FloatBuffer vb, FloatBuffer nb, FloatBuffer tb) {
        super(vertices, vb, nb, tb);
    }

    public GLVertexNormalTextureMesh(Mesh mesh) throws IOException {
        super(mesh);
    }

    public GLVertexNormalTextureMesh(byte[] data) throws IOException {
        super(data);
    }

    public GLVertexNormalTextureMesh(InputStream in) throws IOException {
        super(in);
    }

    public GLVertexNormalTextureMesh(DataInputStream in) throws IOException {
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

        // Normal
        if (normalBufferObject[0] == 0) {
            normalBuffer.position(0);
            GLES20.glGenBuffers(1, normalBufferObject, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalBufferObject[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normalBufferSize, normalBuffer, GLES20.GL_STATIC_DRAW);
            System.out.println("NormalBufferObject " + normalBufferObject[0]);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalBufferObject[0]);
        int normalHandle = program.getAttributeLocation("a_Normal");
        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        // Texture
        if (textureBufferObject[0] == 0) {
            textureBuffer.position(0);
            GLES20.glGenBuffers(1, textureBufferObject, 0);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureBufferObject[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureBufferSize, textureBuffer, GLES20.GL_STATIC_DRAW);
            System.out.println("TextureBufferObject " + textureBufferObject[0]);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureBufferObject[0]);
        int textureHandle = program.getAttributeLocation("a_TexCoord");
        GLES20.glEnableVertexAttribArray(textureHandle);
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        // Draw
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Clean up
        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLUtils.checkError("GLVertexNormalTextureMesh.draw");
    }
}
