#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    color *= vertexColor * ColorModulator;

    float brightness = clamp(1-(pow((color.r + color.g + color.b) / 3.0, 3)), 0, 1);
    color.a = brightness;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}