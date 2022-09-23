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
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;
in float scanLine;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < 0.1) {
        discard;
    }
    color *= vertexColor * ColorModulator;

    float timer = (sin(GameTime * 2400 * 0.5) + 1) / 4 + 0.7;

    float scanLineOffset = scanLine * 50.0;

    float brightness = (pow((color.r + color.g + color.b) / 3.0, 3) * 3.5 + 0.1) * timer + clamp((sin(scanLineOffset + GameTime* 4400) * 0.8) - 0.2, 0, 0.3);
    color.rgb = mix(overlayColor.rgb, color.rgb * 3, clamp(overlayColor.a, 0.0, 1.0));
    fragColor = color * linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor) * vec4(vec3(1.0), brightness);//  * mod(GameTime * 5.0, 1.0)
}
