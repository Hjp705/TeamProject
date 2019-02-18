package com.myst.world.map.rendering;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL20.*;


public class Shader {
    private int program;
    private int vs;
    private int fs;

    public Shader(String filepath){
        program = glCreateProgram();

//        vs is the shader id
        vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, readFile(filepath + ".vs"));
        glCompileShader(vs);
        if(glGetShaderi(vs,GL_COMPILE_STATUS) != 1){
            System.err.println(glGetShaderInfoLog(vs));
        }

        fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, readFile(filepath + ".fs"));
        glCompileShader(fs);
        if(glGetShaderi(fs,GL_COMPILE_STATUS) != 1){
            System.err.println(glGetShaderInfoLog(fs));
        }

        glAttachShader(program, vs);
        glAttachShader(program, fs);


//        vertices is the name you refer to in the shader.vs and fs files
        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");

        glLinkProgram(program);
        if( glGetProgrami(program,GL_LINK_STATUS)!= 1){
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }
        glValidateProgram(program);
        if( glGetProgrami(program,GL_VALIDATE_STATUS)!= 1){
            System.err.println(glGetProgramInfoLog(program));
            System.exit(1);
        }

    }

    public void bind(){
        glUseProgram(program);
    }

    public void setUniform(String name, int value){
        int location = glGetUniformLocation(program, name);
        if (location != -1){
            glUniform1i(location, value);
        }
    }

    public void setUniform(String name, Matrix4f value){
        int location = glGetUniformLocation(program, name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        if (location != -1){
            glUniformMatrix4fv(location,false,buffer);
        }
    }

    private String readFile(String filepath){
        StringBuilder string = new StringBuilder();
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(new File(filepath)));

            String line;
            while((line = br.readLine()) != null){
                string.append(line);
                string.append("\n");
            }
            br.close();
        } catch (IOException e){
            e.printStackTrace();

        }
        return string.toString();
    }
}

