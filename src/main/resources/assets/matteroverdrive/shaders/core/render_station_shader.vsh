#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in vec3 Normal;

uniform sampler2D Sampler0;
uniform float GameTime;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;


float randomNoise(float x, float y)
{
    return fract(sin(dot(vec2(x, y), vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    float noiseSpeed = GameTime * 0.1;
    vertexDistance = fog_distance(ModelViewMat, pos, FogShape);
    vertexColor = Color; // * minecrafat_sample_lightmap(Sampler1, UV1);
    vertexColor *= 0.2 + (step(0.98, randomNoise(noiseSpeed, 2.0)) * 0.1) + (step(0.999, randomNoise(noiseSpeed + 100, 2.0)) * 0.15);
    texCoord0 = UV0;
    texCoord0.x += mod(GameTime * 1000.0, 1);
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}