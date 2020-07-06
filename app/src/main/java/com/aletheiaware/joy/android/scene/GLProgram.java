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

import com.aletheiaware.joy.JoyProto.Shader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GLProgram {

    private final Map<String, Integer> attributes = new HashMap<>();
    private final Map<String, Integer> uniforms = new HashMap<>();
    private final Map<String, Integer> buffers = new HashMap<>();
    private final String name;
    private final String vertexSource;
    private final String fragmentSource;
    private int program = -1;
    private long start;

    public GLProgram(Shader shader) {
        this(shader.getName(), shader.getVertexSource(), shader.getFragmentSource(), shader.getAttributesList(), shader.getUniformsList());
    }

    public GLProgram(String name, String vertexSource, String fragmentSource, List<String> attributes, List<String> uniforms) {
        this.name = name;
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
        for (String a : attributes) {
            this.attributes.put(a, -1);
        }
        for (String u : uniforms) {
            this.uniforms.put(u, -1);
        }
    }

    public String getName() {
        return name;
    }

    public int getAttributeLocation(String name) {
        Integer loc = attributes.get(name);
        if (loc == null || loc < 0) {
            throw new RuntimeException("GL Attribute Location not set for " + name);
        }
        return loc;
    }

    public int getUniformLocation(String name) {
        Integer loc = uniforms.get(name);
        if (loc == null || loc < 0) {
            throw new RuntimeException("GL Uniform Location not set for " + name);
        }
        return loc;
    }

    public int getBuffer(String name) {
        Integer id = buffers.get(name);
        if (id == null || id < 0) {
            throw new RuntimeException("GL Buffer not set for " + name);
        }
        return id;
    }

    public void putBuffer(String name, int id) {
        buffers.put(name, id);
    }

    public void reshape(int x, int y, int width, int height) {
        GLES20.glViewport(x, y, width, height);
        GLUtils.checkError("GLProgram.reshape");
    }

    public void before() {
        if (program < 0) {
            start = System.currentTimeMillis();

            try {
                program = GLUtils.loadProgram(vertexSource, fragmentSource, attributes, uniforms);
                System.out.println("Loaded " + name + " " + program);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        GLES20.glUseProgram(program);
        GLUtils.checkError("GLProgram.before");
    }

    public void after() {
        GLES20.glUseProgram(0);
        GLUtils.checkError("GLProgram.after");
    }

    @Override
    public String toString() {
        return name;
    }
}
