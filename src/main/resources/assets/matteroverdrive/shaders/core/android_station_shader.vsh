#version 150

#moj_import <light.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;

out float vertexDistance;
out vec4 vertexColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;
out float scanLine;

float rNoise(float x, float y)
{
    return fract(sin(dot(vec2(x, y), vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {

    float noiseSpeed = GameTime * 0.1 + length(ModelViewMat * vec4(Position, 1.0));

    gl_Position = ProjMat * ModelViewMat * (vec4(Position, 1.0));

    if(step(0.9985, rNoise(noiseSpeed * 100 + 100, 2.0)) == 1) {
        gl_Position += vec4(rNoise(noiseSpeed * 100 + 123, 2.0) * 0.1, rNoise(noiseSpeed * 100 + 125, 2.0) * 0.1, rNoise(noiseSpeed * 523 + 300, 2.0) * 0.1, 0.0);
    }

    vertexDistance = length((ModelViewMat * vec4(Position, 1.0)).xyz);
    vertexColor = Color; //minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    vertexColor *= 0.95 + (step(0.998, rNoise(noiseSpeed, 2.0)) * 0.2) + (step(0.999, rNoise(noiseSpeed + 100, 2.0)) * 0.15);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);

    vec4 holoComparison = (vec4(Position, 1.0));

    scanLine = holoComparison.y;
}