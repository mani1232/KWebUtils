package cc.worldmandia.kwebutils.presentation.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cc.worldmandia.kwebutils.core.OrbitCamera
import cc.worldmandia.kwebutils.domain.model.ProjectFile
import cc.worldmandia.kwebutils.presentation.common.MainFont
import cc.worldmandia.kwebutils.presentation.feature.dashboard.WebBackButton
import cc.worldmandia.kwebutils.presentation.theme.BrandColorOption
import cc.worldmandia.kwebutils.presentation.theme.availableBrandColors
import com.zakgof.korender.Korender
import com.zakgof.korender.math.ColorRGB.Companion.white
import com.zakgof.korender.math.ColorRGBA
import com.zakgof.korender.math.FloatMath.PIdiv2
import com.zakgof.korender.math.Transform.Companion.scale
import com.zakgof.korender.math.Vec3
import com.zakgof.korender.math.y
import com.zakgof.korender.math.z
import kwebutils.config_editor.generated.resources.Res

@Composable
fun FileUploadCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Icon(
                Icons.Default.UploadFile,
                "Add new file",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally).size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text("Drop file here", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.CustomDashBoardAppBar(
    onAmoledClick: (Boolean) -> Unit,
    onColorClick: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBrandColor by remember { mutableStateOf(availableBrandColors.first()) }
    var isAmoledEnabled by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth(),
        shape = RoundedCornerShape(25),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WebBackButton()
            }

            Text(
                text = "Config Editor | Files",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = MainFont,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.width(4.dp))

                TooltipBox(
                    positionProvider =
                        TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Left),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                "${
                                    if (isAmoledEnabled) "Disable" else "Enable"
                                } amoled"
                            )
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    Switch(
                        checked = isAmoledEnabled,
                        onCheckedChange = {
                            isAmoledEnabled = it
                            onAmoledClick(isAmoledEnabled)
                        },
                        modifier = Modifier.scale(0.95f)
                    )
                }

                Spacer(Modifier.width(8.dp))

                VerticalDivider(
                    modifier = Modifier
                        .height(32.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )

                Spacer(Modifier.width(4.dp))

                ThemePaletteDropdownButton(selectedOption = selectedBrandColor) { newColor ->
                    selectedBrandColor = newColor
                    onColorClick(selectedBrandColor.color)
                }
            }
        }
    }
}

@Composable
fun FileCard(file: ProjectFile, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium, fontFamily = MainFont)
                Text(file.format.name, style = MaterialTheme.typography.bodySmall)
            }
            FilledTonalButton(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors()
            ) {
                Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Edit")
            }
        }
    }
}

@Composable
fun GltfExample(surface: Color) = Korender(appResourceLoader = { Res.readBytes(it) }) {
    val orbitCamera = OrbitCamera(20.z, 3.y)
    OnTouch { orbitCamera.touch(it) }
    Frame {
        base(ColorRGBA.Green)
        background = with(surface) {
            ColorRGBA(red, green, blue, 1f)
        }
        camera = orbitCamera.run { camera() }
        DirectionalLight(Vec3(1.0f, -1.0f, -1.0f), white(3f))
        AmbientLight(white(0.6f))
        Gltf(
            resource = "models/dress.glb",
            transform = scale(0.03f)//.rotate(1.y, frameInfo.time)
        )
    }
}

@Composable
fun TestChangeColor(surface: Color) {
    Korender(appResourceLoader = { Res.readBytes(it) }) {
        val orbitCamera = OrbitCamera(20.z, 3.y)
        OnTouch { orbitCamera.touch(it) }
        Frame {
            camera = orbitCamera.run { camera() }
            background = with(surface) {
                ColorRGBA(red, green, blue, 1f)
            }
            DirectionalLight(Vec3(1.0f, -1.0f, -1.0f), white(3f))
            Gltf(
                resource = "models/dress.glb",
                transform = scale(0.0025f).rotate(Vec3(1f, 0f, 0f), -1.57f).rotate(Vec3(0f, 1f, 0f), frameInfo.time),
            )
        }
    }
}

@Composable
fun ObjFileExample(surface: Color) {
    Korender(appResourceLoader = { Res.readBytes(it) }) {
        val orbitCamera = OrbitCamera(20.z, 0.z)
        OnTouch { orbitCamera.touch(it) }
        Frame {
            background = with(surface) {
                ColorRGBA(red, green, blue, 1f)
            }
            DirectionalLight(Vec3(1.0f, -1.0f, -1.0f), white(3f))
            camera = orbitCamera.run { camera() }
            Renderable(
                base(colorTexture = texture("models/head.jpg"), metallicFactor = 0.3f, roughnessFactor = 0.5f),
                mesh = obj("models/head.obj"),
                transform = scale(7.0f).rotate(1.y, -PIdiv2),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePaletteDropdownButton(
    modifier: Modifier = Modifier,
    selectedOption: BrandColorOption,
    onOptionSelected: (BrandColorOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Left),
        tooltip = { PlainTooltip { Text("Change color") } },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = { expanded = true }) {
            ColorSwatch(selectedOption.color, modifier = Modifier.size(32.dp))
        }
    }

    DropdownMenu(
        modifier = modifier,
        expanded = expanded, onDismissRequest = { expanded = false }
    ) {
        availableBrandColors.forEach { option ->
            DropdownMenuItem(
                text = { Text(option.name) },
                leadingIcon = {
                    ColorSwatch(option.color, modifier = Modifier.size(24.dp))
                },
                onClick = {
                    onOptionSelected(option)
                    expanded = false
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
        }
    }
}

@Composable
fun ColorSwatch(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color)
    )
}