#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec3 Normal;

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;

const float VIEW_SHRINK = 1.0;
const mat4 VIEW_SCALE = mat4(
    VIEW_SHRINK, 0.0, 0.0, 0.0,
    0.0, VIEW_SHRINK, 0.0, 0.0,
    0.0, 0.0, VIEW_SHRINK, 0.0,
    0.0, 0.0, 0.0, 1.0
);

void main() {
    vec4 viewPosStart = ModelViewMat * vec4(Position, 1.0);
    vec4 viewPosEnd = ModelViewMat * vec4(Position + Normal, 1.0);

    vec4 linePosStart = ProjMat * viewPosStart;
    vec4 linePosEnd = ProjMat * viewPosEnd;

    vec3 ndc1 = linePosStart.xyz / linePosStart.w;
    vec3 ndc2 = linePosEnd.xyz / linePosEnd.w;

    vec2 dir = ndc2.xy - ndc1.xy;
    dir.x *= ScreenSize.x / ScreenSize.y; // Correct for aspect ratio
    vec2 lineScreenDirection = normalize(dir + 0.0001);
    vec2 lineOffset = vec2(-lineScreenDirection.y, lineScreenDirection.x) * 10 / ScreenSize;

    float forceFrontZ = -0.9999;

    if (gl_VertexID % 2 == 0) {
        gl_Position = vec4((vec3(ndc1.xy + lineOffset, forceFrontZ)) * linePosStart.w, linePosStart.w);
    } else {
        gl_Position = vec4((vec3(ndc1.xy - lineOffset, forceFrontZ)) * linePosStart.w, linePosStart.w);
    }

    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
    vertexColor = Color;
}
