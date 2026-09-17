import csv
import math
import webcolors

# ICC PCS / CIELAB D50 reference white.
# XYZ values are normalized so Y = 1.0.
D50 = (0.964212, 1.00000, 0.825188)

# Bradford adaptation matrix, D65 -> D50.
D65_TO_D50 = (
    ( 1.0479297925449969,  0.022946870601609652, -0.05019226628920524),
    ( 0.02962780877005599,  0.9904344267538799, -0.017073799063418826),
    (-0.009243040646204504,  0.015055191490298152, 0.7518742814281371),
)

# sRGB, linear RGB -> XYZ D65.
SRGB_TO_XYZ_D65 = (
    (506752.0 / 1228815, 87881.0 / 245763, 12673.0 /   70218),
    (87098.0 /  409605, 175762.0 / 245763, 12673.0 /  175545),
    (7918.0 /  409605, 87881.0 / 737289, 1001167.0 / 1053270),
)


def srgb_to_linear(value):
    """Convert an 8-bit sRGB channel to linear RGB."""
    value /= 255.0
    if value <= 0.04045:
        return value / 12.92
    return ((value + 0.055) / 1.055) ** 2.4


def multiply_matrix_vector(matrix, vector):
    return tuple(
        sum(matrix[row][col] * vector[col] for col in range(3))
        for row in range(3)
    )


def rgb_to_xyz_d65(rgb):
    linear_rgb = tuple(srgb_to_linear(channel) for channel in rgb)
    return multiply_matrix_vector(SRGB_TO_XYZ_D65, linear_rgb)


def xyz_d65_to_xyz_d50(xyz_d65):
    return multiply_matrix_vector(D65_TO_D50, xyz_d65)


def lab_f(value):
    epsilon = 216 / 24389
    kappa = 24389 / 27

    if value > epsilon:
        return value ** (1 / 3)
    return (kappa * value + 16) / 116


def xyz_d50_to_lab(xyz_d50):
    x, y, z = xyz_d50
    xn, yn, zn = D50

    fx = lab_f(x / xn)
    fy = lab_f(y / yn)
    fz = lab_f(z / zn)

    L = 116 * fy - 16
    a = 500 * (fx - fy)
    b = 200 * (fy - fz)

    return L, a, b


def css_rgb_to_lab_d50(rgb):
    xyz_d65 = rgb_to_xyz_d65(rgb)
    xyz_d50 = xyz_d65_to_xyz_d50(xyz_d65)
    return xyz_d50_to_lab(xyz_d50)


def hex_to_rgb(hex_value):
    hex_value = hex_value.lstrip("#")
    return tuple(
        int(hex_value[i:i + 2], 16)
        for i in (0, 2, 4)
    )


rows = []

for name in webcolors.names():
    hex_value = webcolors.name_to_hex(name)
    rgb = hex_to_rgb(hex_value)
    L, a, b = css_rgb_to_lab_d50(rgb)

    rows.append({
        "name": name,
        "hex": hex_value.upper(),
        "sRGB_R": rgb[0],
        "sRGB_G": rgb[1],
        "sRGB_B": rgb[2],
        "Lab_D50_L": f"{L:.3f}",
        "Lab_D50_a": f"{a:.3f}",
        "Lab_D50_b": f"{b:.3f}",
    })

with open("css_named_colors_lab_d50.csv", "w", newline="") as file:
    writer = csv.DictWriter(file, fieldnames=rows[0].keys())
    writer.writeheader()
    writer.writerows(rows)

print(f"Wrote {len(rows)} colors to css_named_colors_lab_d50.csv")
