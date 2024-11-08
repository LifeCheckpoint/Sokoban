#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float blurAmount;

void main() {
    vec4 color = vec4(0.0);
    float total = 0.0;

    // Sample pixels in a 5x5 grid
    for (float x = -3.0; x <= 3.0; x += 1.0) {
        for (float y = -3.0; y <= 3.0; y += 1.0) {
            vec4 sample = texture2D(u_texture, v_texCoords + vec2(x, y) * blurAmount);
            color += sample;
            total += 1.0;
        }
    }
    gl_FragColor = color / total;
}
