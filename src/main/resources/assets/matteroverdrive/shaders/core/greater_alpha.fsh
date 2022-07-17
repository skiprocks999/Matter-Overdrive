#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float AlphaCutoff;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 texColor = texture(Sampler0, texCoord0);
    if(texColor.a <= AlphaCutoff) {
        discard;
    }

    fragColor = vertexColor * ColorModulator;
}
