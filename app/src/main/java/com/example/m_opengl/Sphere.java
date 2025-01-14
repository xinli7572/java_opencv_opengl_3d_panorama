package com.example.m_opengl;


import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

public class Sphere {

    private static float[] VERTEX_DATA;
    private static float[] TEXCOORDS;
    private static short[] INDICES;
    private static int slices = 96;

    static {
        // 计算球体顶点和纹理坐标 加载数据
        generateSphere(slices, slices);
    }


    public static void generateSphere(int latitudeCount, int longitudeCount) {

        // 球体顶点坐标和纹理坐标
        int numVertices = (latitudeCount + 1) * (longitudeCount + 1);
        VERTEX_DATA = new float[numVertices * 3];  // 3个坐标x, y, z
        TEXCOORDS = new float[numVertices * 2];  // 2个纹理坐标u, v
        INDICES = new short[numVertices * 6];

        int vertexIndex = 0;
        int texCoordIndex = 0;
        int indiceIndex = 0;

        for (int lat = 0; lat <= latitudeCount; lat++) {
            double theta = Math.PI * lat / latitudeCount;
            for (int lon = 0; lon <= longitudeCount; lon++) {
                double phi = 2 * Math.PI * lon / longitudeCount;

                // 球面坐标转换为笛卡尔坐标
                float x = (float) (Math.sin(theta) * Math.cos(phi));
                float y = (float) Math.cos(theta);
                float z = (float) (Math.sin(theta) * Math.sin(phi));

                VERTEX_DATA[vertexIndex++] = x;
                VERTEX_DATA[vertexIndex++] = y;
                VERTEX_DATA[vertexIndex++] = z;

                //全景图 纹理
                TEXCOORDS[texCoordIndex++] = ((float) lon / longitudeCount);     
                TEXCOORDS[texCoordIndex++] = ((float) lat / latitudeCount);


                short topLeft = (short) (lat * (latitudeCount + 1) + lon);
                short topRight = (short) (lat * (latitudeCount + 1) + (lon + 1));
                short bottomLeft = (short) ((lat + 1) * (latitudeCount + 1) + lon);
                short bottomRight = (short) ((lat + 1) * (latitudeCount + 1) + (lon + 1));

                // 绘制两个三角形
                INDICES[indiceIndex++] = topLeft;
                INDICES[indiceIndex++] = bottomLeft;
                INDICES[indiceIndex++] = topRight;

                INDICES[indiceIndex++] = topRight;
                INDICES[indiceIndex++] = bottomLeft;
                INDICES[indiceIndex++] = bottomRight;
            }
        }
    }

    private final int programId;
    private final int positionHandle;
    private final int texCoordHandle;
    private final int textureHandle;
    private final int mvpMatrixHandle;
    private final int indexCount;

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texCoordBuffer;
    private final ShortBuffer indexBuffer;

    public Sphere() {

        //球体坐标
        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_DATA.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(VERTEX_DATA).position(0);

        //图片切割坐标
        texCoordBuffer = ByteBuffer.allocateDirect(TEXCOORDS.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        texCoordBuffer.put(TEXCOORDS).position(0);

        //贴图 索引 数据
        indexBuffer = ByteBuffer.allocateDirect(INDICES.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        indexBuffer.put(INDICES).position(0);


        // 编译和链接着色器
        String vertexShaderCode = "attribute vec4 aPosition;\n" +
                "attribute vec2 aTexCoord;\n" +
                "uniform mat4 uMVPMatrix;\n" +
                "varying vec2 vTexCoord;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = uMVPMatrix * aPosition;\n" +
                "    vTexCoord = aTexCoord;\n" +
                "}";

        String fragmentShaderCode = "precision mediump float;\n" +
                "uniform sampler2D uTexture;\n" +
                "varying vec2 vTexCoord;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_FragColor = texture2D(uTexture, vTexCoord);\n" +
                "}";

        // 编译着色器并创建程序
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        programId = GLES20.glCreateProgram();
        GLES20.glAttachShader(programId, vertexShader);
        GLES20.glAttachShader(programId, fragmentShader);
        GLES20.glLinkProgram(programId);
        GLES20.glUseProgram(programId);

        positionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
        texCoordHandle = GLES20.glGetAttribLocation(programId, "aTexCoord");
        textureHandle = GLES20.glGetUniformLocation(programId, "uTexture");
        mvpMatrixHandle = GLES20.glGetUniformLocation(programId, "uMVPMatrix");

        indexCount = INDICES.length;
    }

    //加载 着色器
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    //绑定 图片
    public void bindTexture(int textureId) {

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(textureHandle, 0);
    }

    float[] mvpMatrix = new float[16];


    public void draw(float[] viewMatrix, float[] projectionMatrix, float[] modelMatrix) {
        GLES20.glUseProgram(programId);

        // 设置矩阵
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // 绑定顶点数据
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        // 绑定纹理坐标数据
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);
        GLES20.glEnableVertexAttribArray(texCoordHandle);


        // 使用索引缓冲区绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    }

}
